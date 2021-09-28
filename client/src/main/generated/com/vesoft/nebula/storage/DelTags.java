/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.storage;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.BitSet;
import java.util.Arrays;
import com.facebook.thrift.*;
import com.facebook.thrift.annotations.*;
import com.facebook.thrift.async.*;
import com.facebook.thrift.meta_data.*;
import com.facebook.thrift.server.*;
import com.facebook.thrift.transport.*;
import com.facebook.thrift.protocol.*;

@SuppressWarnings({ "unused", "serial" })
public class DelTags implements TBase, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("DelTags");
  private static final TField ID_FIELD_DESC = new TField("id", TType.STRUCT, (short)1);
  private static final TField TAGS_FIELD_DESC = new TField("tags", TType.LIST, (short)2);

  public com.vesoft.nebula.Value id;
  public List<Integer> tags;
  public static final int ID = 1;
  public static final int TAGS = 2;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(ID, new FieldMetaData("id", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.Value.class)));
    tmpMetaDataMap.put(TAGS, new FieldMetaData("tags", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new FieldValueMetaData(TType.I32))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(DelTags.class, metaDataMap);
  }

  public DelTags() {
  }

  public DelTags(
      com.vesoft.nebula.Value id,
      List<Integer> tags) {
    this();
    this.id = id;
    this.tags = tags;
  }

  public static class Builder {
    private com.vesoft.nebula.Value id;
    private List<Integer> tags;

    public Builder() {
    }

    public Builder setId(final com.vesoft.nebula.Value id) {
      this.id = id;
      return this;
    }

    public Builder setTags(final List<Integer> tags) {
      this.tags = tags;
      return this;
    }

    public DelTags build() {
      DelTags result = new DelTags();
      result.setId(this.id);
      result.setTags(this.tags);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public DelTags(DelTags other) {
    if (other.isSetId()) {
      this.id = TBaseHelper.deepCopy(other.id);
    }
    if (other.isSetTags()) {
      this.tags = TBaseHelper.deepCopy(other.tags);
    }
  }

  public DelTags deepCopy() {
    return new DelTags(this);
  }

  public com.vesoft.nebula.Value getId() {
    return this.id;
  }

  public DelTags setId(com.vesoft.nebula.Value id) {
    this.id = id;
    return this;
  }

  public void unsetId() {
    this.id = null;
  }

  // Returns true if field id is set (has been assigned a value) and false otherwise
  public boolean isSetId() {
    return this.id != null;
  }

  public void setIdIsSet(boolean __value) {
    if (!__value) {
      this.id = null;
    }
  }

  public List<Integer> getTags() {
    return this.tags;
  }

  public DelTags setTags(List<Integer> tags) {
    this.tags = tags;
    return this;
  }

  public void unsetTags() {
    this.tags = null;
  }

  // Returns true if field tags is set (has been assigned a value) and false otherwise
  public boolean isSetTags() {
    return this.tags != null;
  }

  public void setTagsIsSet(boolean __value) {
    if (!__value) {
      this.tags = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case ID:
      if (__value == null) {
        unsetId();
      } else {
        setId((com.vesoft.nebula.Value)__value);
      }
      break;

    case TAGS:
      if (__value == null) {
        unsetTags();
      } else {
        setTags((List<Integer>)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case ID:
      return getId();

    case TAGS:
      return getTags();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object _that) {
    if (_that == null)
      return false;
    if (this == _that)
      return true;
    if (!(_that instanceof DelTags))
      return false;
    DelTags that = (DelTags)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetId(), that.isSetId(), this.id, that.id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetTags(), that.isSetTags(), this.tags, that.tags)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {id, tags});
  }

  public void read(TProtocol iprot) throws TException {
    TField __field;
    iprot.readStructBegin(metaDataMap);
    while (true)
    {
      __field = iprot.readFieldBegin();
      if (__field.type == TType.STOP) { 
        break;
      }
      switch (__field.id)
      {
        case ID:
          if (__field.type == TType.STRUCT) {
            this.id = new com.vesoft.nebula.Value();
            this.id.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case TAGS:
          if (__field.type == TType.LIST) {
            {
              TList _list140 = iprot.readListBegin();
              this.tags = new ArrayList<Integer>(Math.max(0, _list140.size));
              for (int _i141 = 0; 
                   (_list140.size < 0) ? iprot.peekList() : (_i141 < _list140.size); 
                   ++_i141)
              {
                int _elem142;
                _elem142 = iprot.readI32();
                this.tags.add(_elem142);
              }
              iprot.readListEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        default:
          TProtocolUtil.skip(iprot, __field.type);
          break;
      }
      iprot.readFieldEnd();
    }
    iprot.readStructEnd();


    // check for required fields of primitive type, which can't be checked in the validate method
    validate();
  }

  public void write(TProtocol oprot) throws TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    if (this.id != null) {
      oprot.writeFieldBegin(ID_FIELD_DESC);
      this.id.write(oprot);
      oprot.writeFieldEnd();
    }
    if (this.tags != null) {
      oprot.writeFieldBegin(TAGS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.I32, this.tags.size()));
        for (int _iter143 : this.tags)        {
          oprot.writeI32(_iter143);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    return toString(1, true);
  }

  @Override
  public String toString(int indent, boolean prettyPrint) {
    String indentStr = prettyPrint ? TBaseHelper.getIndentedString(indent) : "";
    String newLine = prettyPrint ? "\n" : "";
    String space = prettyPrint ? " " : "";
    StringBuilder sb = new StringBuilder("DelTags");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("id");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getId() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getId(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("tags");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getTags() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getTags(), indent + 1, prettyPrint));
    }
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}
