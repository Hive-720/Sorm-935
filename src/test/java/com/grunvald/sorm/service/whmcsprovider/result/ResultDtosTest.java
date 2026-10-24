package com.grunvald.sorm.service.whmcsprovider.result;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultDtosTest {

    @Test
    void activityLogsResult_builderStoresValues() {
        var result = ActivityLogsResult.builder()
                .elements(List.of())
                .hashNextPage(true)
                .nextOffset(5L)
                .build();

        assertEquals(List.of(), result.getElements());
        assertTrue(result.isHashNextPage());
        assertEquals(5L, result.getNextOffset());
    }

    @Test
    void affiliatesResult_builderStoresValues() {
        var result = AffiliatesResult.builder()
                .elements(List.of())
                .hasNextPage(false)
                .nextOffset(7L)
                .build();

        assertEquals(List.of(), result.getElements());
        assertFalse(result.isHasNextPage());
        assertEquals(7L, result.getNextOffset());
    }

    @Test
    void clientDetailsResult_builderStoresValues() {
        var result = ClientDetailsResult.builder()
                .element(null)
                .deleted(true)
                .build();

        assertTrue(result.isDeleted());
    }

    @Test
    void clientGroupsResult_builderStoresValues() {
        var result = ClientGroupsResult.builder()
                .elements(List.of())
                .hashNextPage(true)
                .build();

        assertTrue(result.isHashNextPage());
    }

    @Test
    void clientProductsResult_builderStoresValues() {
        var result = ClientProductsResult.builder()
                .elements(List.of())
                .hasNextPage(true)
                .nextOffset(9L)
                .build();

        assertTrue(result.isHasNextPage());
        assertEquals(9L, result.getNextOffset());
    }

    @Test
    void clientsResult_builderStoresValues() {
        var result = ClientsResult.builder()
                .elements(List.of())
                .hasNextPage(true)
                .nextOffset(3L)
                .build();

        assertTrue(result.isHasNextPage());
        assertEquals(3L, result.getNextOffset());
    }

    @Test
    void contactsResult_builderStoresValues() {
        var result = ContactsResult.builder()
                .elements(List.of())
                .hasNextPage(false)
                .nextOffset(4L)
                .build();

        assertFalse(result.isHasNextPage());
        assertEquals(4L, result.getNextOffset());
    }

    @Test
    void creditsResult_constructorStoresValues() {
        var result = new CreditsResult(List.of());

        assertEquals(List.of(), result.getElements());
    }

    @Test
    void emailsResult_builderStoresValues() {
        var result = EmailsResult.builder()
                .elements(List.of())
                .hasNextPage(true)
                .nextOffset(6L)
                .build();

        assertTrue(result.isHasNextPage());
        assertEquals(6L, result.getNextOffset());
    }

    @Test
    void invoicesResult_builderStoresValues() {
        var result = InvoicesResult.builder()
                .elements(List.of())
                .hasNextPage(true)
                .nextOffset(8L)
                .build();

        assertTrue(result.isHasNextPage());
        assertEquals(8L, result.getNextOffset());
    }

    @Test
    void ordersResult_builderStoresValues() {
        var result = OrdersResult.builder()
                .elements(List.of())
                .hasNextPage(true)
                .nextOffset(2L)
                .build();

        assertTrue(result.isHasNextPage());
        assertEquals(2L, result.getNextOffset());
    }

    @Test
    void paymentMethodsResult_builderStoresValues() {
        var result = PaymentMethodsResult.builder()
                .elements(List.of())
                .hashNextPage(false)
                .nextOffset(1L)
                .build();

        assertFalse(result.isHashNextPage());
        assertEquals(1L, result.getNextOffset());
    }

    @Test
    void productsResult_builderStoresValues() {
        var result = ProductsResult.builder()
                .elements(List.of())
                .hashNextPage(true)
                .build();

        assertTrue(result.isHashNextPage());
    }

    @Test
    void usersResult_builderStoresValues() {
        var result = UsersResult.builder()
                .elements(List.of())
                .hashNextPage(false)
                .nextOffset(11L)
                .build();

        assertFalse(result.isHashNextPage());
        assertEquals(11L, result.getNextOffset());
    }
}
