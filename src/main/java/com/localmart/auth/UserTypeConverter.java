package com.localmart.auth;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class UserTypeConverter implements AttributeConverter<OtpVerification.UserType, String> {

    @Override
    public String convertToDatabaseColumn(OtpVerification.UserType attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public OtpVerification.UserType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return OtpVerification.UserType.valueOf(dbData.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
