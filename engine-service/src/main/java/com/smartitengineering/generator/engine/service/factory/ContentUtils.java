/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.service.factory;

import com.smartitengineering.cms.api.content.ContentId;
import com.smartitengineering.cms.api.content.MutableField;
import com.smartitengineering.cms.api.content.MutableFieldValue;
import com.smartitengineering.cms.api.factory.SmartContentAPI;
import com.smartitengineering.cms.api.factory.content.ContentLoader;
import com.smartitengineering.cms.api.factory.content.WriteableContent;
import com.smartitengineering.cms.api.type.ContentType;
import com.smartitengineering.cms.api.type.ContentTypeId;
import com.smartitengineering.cms.api.type.FieldDef;
import com.smartitengineering.cms.api.type.FieldValueType;
import com.smartitengineering.cms.api.workspace.WorkspaceId;
import java.util.Collection;
import java.util.Date;
import org.apache.commons.codec.binary.StringUtils;

/**
 *
 * @author imyousuf
 */
public final class ContentUtils {

  private ContentUtils() {
  }

  public static ContentTypeId getContentTypeId(WorkspaceId id, String typeNamespace, String typeName) {
    return SmartContentAPI.getInstance().getContentTypeLoader().createContentTypeId(id, typeNamespace, typeName);
  }

  public static ContentId getContentId(WorkspaceId workspaceId, String contentLocalId) {
    return SmartContentAPI.getInstance().getContentLoader().createContentId(workspaceId, StringUtils.getBytesUtf8(
        contentLocalId));
  }

  public static WriteableContent createContent(ContentTypeId typeId) {
    return createContent(typeId.getContentType());
  }

  public static WriteableContent createContent(WorkspaceId workspaceId, ContentTypeId typeId, boolean withId) {
    return createContent(workspaceId, typeId.getContentType(), withId);
  }

  public static WriteableContent createContent(ContentType type) {
    return createContent(null, type, false);
  }

  public static WriteableContent createContent(WorkspaceId workspaceId, ContentType type, boolean withId) {
    final ContentLoader contentLoader = SmartContentAPI.getInstance().getContentLoader();
    final WriteableContent newContent = contentLoader.createContent(type);
    if (withId && workspaceId != null) {
      newContent.setContentId(contentLoader.generateContentId(workspaceId));
    }
    return newContent;
  }

  public static MutableField getField(String fieldName, ContentTypeId typeId, Object val) {
    if (typeId == null) {
      return null;
    }
    final ContentType contentType = typeId.getContentType();
    if (contentType == null) {
      return null;
    }
    return getField(fieldName, contentType, val);
  }

  public static MutableField getField(String fieldName, ContentType ctype, Object val) {
    if (org.apache.commons.lang.StringUtils.isBlank(fieldName) || ctype == null || val == null || ctype.getFieldDefs().
        get(fieldName) == null) {
      return null;
    }
    FieldDef def = ctype.getFieldDefs().get(fieldName);
    if (def == null) {
      return null;
    }
    return getField(def, val);
  }

  public static MutableField getField(FieldDef def, Object val) {
    final MutableFieldValue fieldValue = getFieldValue(def.getValueDef().getType(), val);
    fieldValue.setValue(val);
    MutableField field = SmartContentAPI.getInstance().getContentLoader().createMutableField(null, def);
    field.setValue(fieldValue);
    return field;
  }

  public static MutableFieldValue getFieldValue(FieldValueType type, Object val) {
    final MutableFieldValue fieldValue;
    ContentLoader loader = SmartContentAPI.getInstance().getContentLoader();
    switch (type) {
      case BOOLEAN:
        checkExpectedValueType(Boolean.class, val, type);
        fieldValue = loader.createBooleanFieldValue();
        break;
      case COLLECTION:
        checkExpectedValueType(Collection.class, val, type);
        fieldValue = loader.createCollectionFieldValue();
        break;
      case COMPOSITE:
        checkExpectedValueType(Collection.class, val, type);
        fieldValue = loader.createCompositeFieldValue();
        break;
      case CONTENT:
        checkExpectedValueType(ContentId.class, val, type);
        fieldValue = loader.createContentFieldValue();
        break;
      case DATE_TIME:
        checkExpectedValueType(Date.class, val, type);
        fieldValue = loader.createDateTimeFieldValue();
        break;
      case DOUBLE:
        checkExpectedValueType(Double.class, val, type);
        fieldValue = loader.createDoubleFieldValue();
        break;
      case ENUM:
        checkExpectedValueType(String.class, val, type);
        fieldValue = loader.createStringFieldValue();
        break;
      case INTEGER:
        checkExpectedValueType(Integer.class, val, type);
        fieldValue = loader.createIntegerFieldValue();
        break;
      case LONG:
        checkExpectedValueType(Long.class, val, type);
        fieldValue = loader.createLongFieldValue();
        break;
      case OTHER:
        fieldValue = loader.createOtherFieldValue();
        break;
      default:
      case STRING:
        checkExpectedValueType(String.class, val, type);
        fieldValue = loader.createStringFieldValue();
        break;
    }
    return fieldValue;
  }

  protected static void checkExpectedValueType(final Class clazz, Object val, final FieldValueType type) throws
      IllegalArgumentException {
    if (!(clazz.isAssignableFrom(val.getClass()))) {
      throw new IllegalArgumentException(new StringBuilder("Value for ").append(type).append(
          " field must be ").append(clazz.getName()).toString());
    }
  }
}
