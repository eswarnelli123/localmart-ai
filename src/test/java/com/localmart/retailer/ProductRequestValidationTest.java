package com.localmart.retailer;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void productRequestShouldRequireNamePriceAndStock() {
        ProductRequest request = new ProductRequest();
        request.setName("   ");
        request.setPrice(null);
        request.setStock(null);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(v -> "name".equals(v.getPropertyPath().toString())));
        assertTrue(violations.stream().anyMatch(v -> "price".equals(v.getPropertyPath().toString())));
        assertTrue(violations.stream().anyMatch(v -> "stock".equals(v.getPropertyPath().toString())));
    }
}
