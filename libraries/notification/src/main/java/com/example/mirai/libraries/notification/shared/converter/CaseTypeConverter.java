package com.example.mirai.libraries.notification.shared.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CaseTypeConverter implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String type) {
        if (type != null) {
            return type.toUpperCase();
        }
        return type;
    }
    @Override
    public String convertToEntityAttribute(String type) {
        return type;
    }
}
