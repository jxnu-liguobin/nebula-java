/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.InvalidConfigException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NebulaPool {
    private GenericObjectPool<SyncConnection> objectPool = null;
    private LoadBalancer loadBalancer;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    // the wait time to get idle connection, unit ms
    private int waitTime = 0;

    private List<HostAddress> hostToIp(List<HostAddress> addresses)
        throws UnknownHostException {
        List<HostAddress> newAddrs = new ArrayList<>();
        for (HostAddress addr : addresses) {
            String ip = InetAddress.getByName(addr.getHost()).getHostAddress();
            newAddrs.add(new HostAddress(ip, addr.getPort()));
        }
        return newAddrs;
    }

    private void checkConfig(NebulaPoolConfig config) {
        if (config.getIdleTime() < 0) {
            throw new InvalidConfigException(
                "Config idleTime:" + config.getIdleTime() + " is illegal");
        }

        if (config.getMaxConnSize() <= 0) {
            throw new InvalidConfigException(
                "Config maxConnSize:" + config.getMaxConnSize() + " is illegal");
        }

        if (config.getMinConnSize() < 0 || config.getMinConnSize() > config.getMaxConnSize()) {
            throw new InvalidConfigException(
                "Config minConnSize:" + config.getMinConnSize() + " is illegal");
        }

        if (config.getTimeout() < 0) {
            throw new InvalidConfigException(
                "Config timeout:" + config.getTimeout() + " is illegal");
        }

        if (config.getWaitTime() < 0) {
            throw new InvalidConfigException(
                "Config waitTime:" + config.getWaitTime() + " is illegal");
        }
    }

    /**
     * @param addresses the graphd services addresses
     * @param config the config for the pool
     * @return boolean if all graph services are ok, return true,
     *         if some of them broken return false
     * @throws UnknownHostException if host address is illegal
     * @throws InvalidConfigException if config is illegal
     */
    public boolean init(List<HostAddress> addresses, NebulaPoolConfig config)
        throws UnknownHostException, InvalidConfigException {
        checkConfig(config);
        this.waitTime = config.getWaitTime();
        List<HostAddress> newAddrs = hostToIp(addresses);
        this.loadBalancer = new RoundRobinLoadBalancer(newAddrs, config.getTimeout());
        ConnObjectPool objectPool = new ConnObjectPool(this.loadBalancer, config);
        this.objectPool = new GenericObjectPool<>(objectPool);
        GenericObjectPoolConfig objConfig = new GenericObjectPoolConfig();
        objConfig.setMinIdle(config.getMinConnSize());
        objConfig.setMaxIdle(config.getMaxConnSize());
        objConfig.setMaxTotal(config.getMaxConnSize());
        objConfig.setTimeBetweenEvictionRunsMillis(config.getIntervalIdle() <= 0
            ? BaseObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS
            : config.getIntervalIdle());
        objConfig.setSoftMinEvictableIdleTimeMillis(config.getIdleTime() <= 0
            ? BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS
            : config.getIdleTime());
        this.objectPool.setConfig(objConfig);

        AbandonedConfig abandonedConfig = new AbandonedConfig();
        abandonedConfig.setRemoveAbandonedOnBorrow(true);
        this.objectPool.setAbandonedConfig(abandonedConfig);
        return objectPool.init();
    }

    /**
     * close the pool, all connections will be closed
     */
    public void close() {
        this.loadBalancer.close();
        this.objectPool.close();
    }

    /**
     * get a session from the NebulaPool
     * @param userName the userName to authenticate with nebula-graph
     * @param password the password to authenticate with nebula-graph
     * @param reconnect whether to retry after the connection is disconnected
     * @return Session
     * @throws NotValidConnectionException if get connection failed
     * @throws IOErrorException if get unexpected exception
     * @throws AuthFailedException if authenticate failed
     */
    public Session getSession(String userName, String password, boolean reconnect)
            throws NotValidConnectionException, IOErrorException, AuthFailedException {
        try {
            SyncConnection connection = getConnection();
            AuthResult authResult = connection.authenticate(userName, password);
            return new Session(connection, authResult, this, reconnect);
        } catch (NotValidConnectionException | AuthFailedException | IOErrorException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw new NotValidConnectionException(e.getMessage());
        } catch (Exception e) {
            throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage());
        }
    }

    /**
     * Get the number of connections was used by users
     * @return the active connection number
     */
    public int getActiveConnNum() {
        return objectPool.getNumActive();
    }

    /**
     * Get the number of free connections in the pool
     * @return the idle connection number
     */
    public int getIdleConnNum() {
        return objectPool.getNumIdle();
    }

    /**
     * Get the number of waits in a waiting get connection
     * @return the waiting connection number
     */
    public int getWaitersNum() {
        return objectPool.getNumWaiters();
    }

    /**
     * Update the services' status when the connection is broken,
     * it is called by Session and NebulaPool
     */
    protected void updateServerStatus() {
        if (objectPool.getFactory() instanceof ConnObjectPool) {
            ((ConnObjectPool)objectPool.getFactory()).updateServerStatus();
        }
    }

    /**
     * Set the connection is invalidate, and the object pool will destroy it
     * @param connection the invalidate connection
     */
    protected void setInvalidateConnection(SyncConnection connection) {
        try {
            objectPool.invalidateObject(connection);
        } catch (Exception e) {
            log.error("Set invalidate object failed");
        }
    }

    /**
     * Return the connection to object pool
     * @param connection the return connection
     */
    protected void returnConnection(SyncConnection connection) {
        objectPool.returnObject(connection);
    }

    protected SyncConnection getConnection() throws NotValidConnectionException, IOErrorException {
        // If no idle connection, try once
        int idleConnNum = getIdleConnNum();
        int retry = idleConnNum == 0 ? 1 : idleConnNum;
        SyncConnection connection = null;
        boolean hasOkConn = false;
        try {
            while (retry-- > 0) {
                connection = objectPool.borrowObject(waitTime);
                if (connection == null) {
                    continue;
                }
                if (!connection.ping()) {
                    log.info("The connection is broken, set invalidateObject");
                    setInvalidateConnection(connection);
                    continue;
                }
                hasOkConn = true;
                break;
            }
            // All idle connections are broken, so need to create new one
            if (!hasOkConn) {
                connection = objectPool.borrowObject(waitTime);
                if (connection == null) {
                    throw new NotValidConnectionException("Get null connection from the pool");
                }
                if (!connection.ping()) {
                    log.info("The connection is broken, set invalidateObject");
                    setInvalidateConnection(connection);
                    throw new NotValidConnectionException("The connection ping failed.");
                }
                log.info("Create new connection");
            }
            log.info(String.format("Get connection to %s:%d",
                    connection.getServerAddress().getHost(),
                    connection.getServerAddress().getPort()));
            return connection;
        } catch (NotValidConnectionException | IOErrorException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw new NotValidConnectionException(e.getMessage());
        } catch (Exception e) {
            throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage());
        }
    }
}
