package com.localmart.shop;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShopGeocodeServiceTest {

    @Test
    void buildAddressQueryShouldIncludeAllAvailableAddressParts() {
        ShopRepository repository = Mockito.mock(ShopRepository.class);
        ShopGeocodeService service = new ShopGeocodeService(repository);

        Shop shop = new Shop();
        shop.setAddressLine1("123 Main Street");
        shop.setAddressLine2("Apt 4B");
        shop.setCity("Delhi");
        shop.setState("Delhi");
        shop.setPostalCode("110001");
        shop.setCountry("India");

        String query = service.buildAddressQuery(shop);

        assertTrue(query.contains("123 Main Street"));
        assertTrue(query.contains("Delhi"));
        assertTrue(query.contains("110001"));
        assertTrue(query.contains("India"));
        assertEquals("123 Main Street, Apt 4B, Delhi, Delhi, 110001, India", query);
    }
}
