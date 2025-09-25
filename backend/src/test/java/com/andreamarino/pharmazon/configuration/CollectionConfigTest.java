package com.andreamarino.pharmazon.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CollectionConfigTest {
    
    @InjectMocks
    private CollectionConfig collectionConfig;

    @Test
    public void testProductsBean() {
        assertNotNull(collectionConfig.products());
    }

    @Test
    public void testPharmacistsBean() {
        assertNotNull(collectionConfig.pharmacists());
    }

    @Test
    public void testCartsBean() {
        assertNotNull(collectionConfig.carts());
    }

    @Test
    public void testReviewsBean() {
        assertNotNull(collectionConfig.reviews());
    }

    @Test
    public void testUsersBean() {
        assertNotNull(collectionConfig.users());
    }

    @Test
    public void testPrescriptionsBean() {
        assertNotNull(collectionConfig.prescriptions());
    }

    @Test
    public void testBookingsBean() {
        assertNotNull(collectionConfig.bookings());
    }

    @Test
    public void testOrdersBean() {
        assertNotNull(collectionConfig.orders());
    }

    @Test
    public void testCreditCardsBean() {
        assertNotNull(collectionConfig.creditCards());
    }

    @Test
    public void testServicesBean() {
        assertNotNull(collectionConfig.services());
    }
}
