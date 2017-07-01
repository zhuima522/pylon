package com.yotouch.core.entity;

import com.yotouch.core.entity.fv.FieldValue;
import com.yotouch.core.entity.query.QueryField;

import java.util.Map;

public interface MetaField<T> extends QueryField {
    
    String getType();
    
    String getName();
    
    String getDisplayName();
    
    String getFieldType();
    
    String getDataType();
    
    boolean isRequired();
    
    T getDefaultValue();

    FieldValue<T> getDefaultFieldValue();
    
    String getUuid();
    
    MetaEntity getMetaEntity();
    
    boolean isReference();

    boolean isMultiReference();
    
    MetaEntity getTargetMetaEntity();
    
    boolean isSingleReference();

    ValueOption getValueOption(String optionValueDisplayname);

    FieldValue<T> newFieldValue(Object value);
    
}
