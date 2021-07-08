package com.example.mirai.libraries.core.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Specifies that the annotated field has to be saved in database in uppercase
 *
 * @author ptummala
 * @since 1.0.0
 */
@Converter
public class UpperCaseConverter implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String str) {
        if (Objects.nonNull(str)) {
            return str.toUpperCase();
        }
        return str;
    }
    @Override
    public String convertToEntityAttribute(String str) {
        return str.toUpperCase();
    }
}
