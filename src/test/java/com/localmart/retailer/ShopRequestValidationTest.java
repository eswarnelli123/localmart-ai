package com.localmart.retailer;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShopRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shopRequestShouldRequireLocationAndPostalCode() {
        ShopRequest request = new ShopRequest();
        request.setName("Test Shop");
        request.setLocation("   ");
        request.setPostalCode("   ");

        Set<ConstraintViolation<ShopRequest>> violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(v -> "location".equals(v.getPropertyPath().toString())));
        assertTrue(violations.stream().anyMatch(v -> "postalCode".equals(v.getPropertyPath().toString())));
    }
}
