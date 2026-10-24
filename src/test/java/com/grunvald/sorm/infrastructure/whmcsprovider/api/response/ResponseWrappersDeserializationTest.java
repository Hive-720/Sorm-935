package com.grunvald.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.config.Config;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseWrappersDeserializationTest {

    private static final ObjectMapper OBJECT_MAPPER = new Config().objectMapper();

    private static <T> T fromJson(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to parse fixture json", e);
        }
    }

    @Test
    void clientsWrapper_mapsClientsAndReturnsEmptyOnNull() {
        var response = fromJson("""
                {"clients": {"client": [{"id": 1, "firstname": "Ivan", "email": "i@m.com"}]}}
                """, GetClientsResponse.class);

        var clients = response.getClients();
        assertEquals(1, clients.size());
        assertEquals(1L, clients.get(0).getId());
        assertEquals("Ivan", clients.get(0).getFirstName());
        assertEquals("i@m.com", clients.get(0).getEmail());

        assertTrue(fromJson("{}", GetClientsResponse.class).getClients().isEmpty());
    }

    @Test
    void clientGroupsWrapper_mapsGroupsAndReturnsEmptyOnNull() {
        var response = fromJson("""
                {"groups": {"group": [{"id": 1, "groupName": "VIP"}]}}
                """, GetClientGroupsResponse.class);

        assertEquals(List.of(1L), response.getClientCroups().stream().map(g -> g.getId()).toList());
        assertEquals("VIP", response.getClientCroups().get(0).getGroupName());

        assertTrue(fromJson("{}", GetClientGroupsResponse.class).getClientCroups().isEmpty());
    }

    @Test
    void clientProductsWrapper_mapsProductsAndReturnsEmptyOnNull() {
        var response = fromJson("""
                {"products": {"product": [{"id": 1, "domain": "d.ru", "regdate": "2026-01-01 10:00:00"}]}}
                """, GetClientProductsResponse.class);

        var products = response.getClientProducts();
        assertEquals(1, products.size());
        assertEquals(1L, products.get(0).getId());
        assertEquals("d.ru", products.get(0).getDomain());
        assertEquals("2026-01-01T10:00Z", products.get(0).getRegDate().toString());

        assertTrue(fromJson("{}", GetClientProductsResponse.class).getClientProducts().isEmpty());
    }

    @Test
    void contactsWrapper_mapsContactsAndReturnsEmptyOnNull() {
        var response = fromJson("""
                {"contacts": {"contact": [{"id": 1, "firstname": "A", "userid": 7}]}}
                """, GetContactsResponse.class);

        var contacts = response.getContacts();
        assertEquals(1, contacts.size());
        assertEquals(1L, contacts.get(0).getId());
        assertEquals("A", contacts.get(0).getFirstName());
        assertEquals(7L, contacts.get(0).getClientId());

        assertTrue(fromJson("{}", GetContactsResponse.class).getContacts().isEmpty());
    }

    @Test
    void creditsWrapper_mapsCreditsAndReturnsEmptyOnNull() {
        var response = fromJson("""
                {"clientid": 3, "credits": {"credit": [{"id": 1, "amount": "10.5", "date": "2026-01-01"}]}}
                """, GetCreditsResponse.class);

        var credits = response.getCredits();
        assertEquals(1, credits.size());
        assertEquals(3L, response.getClientId());
        assertEquals(1L, credits.get(0).getId());
        assertEquals("2026-01-01T00:00Z", credits.get(0).getDate().toString());

        assertTrue(fromJson("{}", GetCreditsResponse.class).getCredits().isEmpty());
    }

    @Test
    void emailsWrapper_mapsEmailsAndReturnsEmptyOnNull() {
        var response = fromJson("""
                {"emails": {"email": [{"id": 1, "subject": "s", "date": "2026-01-01"}]}}
                """, GetEmailsResponse.class);

        var emails = response.getEmails();
        assertEquals(1, emails.size());
        assertEquals(1L, emails.get(0).getId());
        assertEquals("s", emails.get(0).getSubject());
        assertEquals("2026-01-01T00:00Z", emails.get(0).getDate().toString());

        assertTrue(fromJson("{}", GetEmailsResponse.class).getEmails().isEmpty());
    }

    @Test
    void invoicesWrapper_mapsInvoicesAndReturnsEmptyOnNull() {
        var response = fromJson("""
                {"invoices": {"invoice": [{"id": 1, "date": "2026-01-01"}]}}
                """, GetInvoicesResponse.class);

        var invoices = response.getInvoices();
        assertEquals(1, invoices.size());
        assertEquals(1L, invoices.get(0).getId());
        assertEquals("2026-01-01T00:00Z", invoices.get(0).getDate().toString());

        assertTrue(fromJson("{}", GetInvoicesResponse.class).getInvoices().isEmpty());
    }

    @Test
    void ordersWrapper_mapsOrdersAndReturnsEmptyOnNull() {
        var response = fromJson("""
                {"orders": {"order": [{"id": 1, "ordernum": 42}]}}
                """, GetOrdersResponse.class);

        var orders = response.getOrders();
        assertEquals(1, orders.size());
        assertEquals(1L, orders.get(0).getId());
        assertEquals(42L, orders.get(0).getOrderNum());

        assertTrue(fromJson("{}", GetOrdersResponse.class).getOrders().isEmpty());
    }

    @Test
    void affiliatesWrapper_mapsAffiliatesAndReturnsEmptyOnNull() {
        var response = fromJson("""
                {"affiliates": {"affiliate": [{"id": 1, "clientid": 9}]}}
                """, GetAffiliatesResponse.class);

        var affiliates = response.getAffiliates();
        assertEquals(1, affiliates.size());
        assertEquals(1L, affiliates.get(0).getId());
        assertEquals(9L, affiliates.get(0).getClientId());

        assertTrue(fromJson("{}", GetAffiliatesResponse.class).getAffiliates().isEmpty());
    }

    @Test
    void productsWrapper_mapsProductsAndReturnsEmptyOnNull() {
        var response = fromJson("""
                {"products": {"product": [{"pid": 1, "name": "Hosting"}]}}
                """, GetProductsResponse.class);

        var products = response.getProducts();
        assertEquals(1, products.size());
        assertEquals(1L, products.get(0).getId());
        assertEquals("Hosting", products.get(0).getName());

        assertTrue(fromJson("{}", GetProductsResponse.class).getProducts().isEmpty());
    }

    @Test
    void usersWrapper_mapsUsersAndReturnsEmptyOnNull() {
        var response = fromJson("""
                {"users": [{"id": 1, "firstname": "Ivan"}]}
                """, GetUsersResponse.class);

        var users = response.getUsers();
        assertEquals(1, users.size());
        assertEquals(1L, users.get(0).getId());
        assertEquals("Ivan", users.get(0).getFirstName());

        assertTrue(fromJson("{}", GetUsersResponse.class).getUsers().isEmpty());
    }

    @Test
    void invoiceWrapper_mapsFieldsAndReturnsEmptyListsOnNull() {
        var response = fromJson("""
                {"invoiceid": 5, "invoicenum": "INV-1", "status": "Unpaid"}
                """, GetInvoiceResponse.class);

        assertEquals(5L, response.getId());
        assertEquals("INV-1", response.getNum());
        assertEquals("Unpaid", response.getStatus());
        assertTrue(response.getItems().isEmpty());
        assertTrue(response.getTransactions().isEmpty());
    }

    @Test
    void invoiceWrapper_mapsItemsAndTransactions() {
        var response = fromJson("""
                {
                  "invoiceid": 5,
                  "items": {"item": [{"id": 1, "type": "hosting", "relid": 7, "amount": "10.00"}]},
                  "transactions": {"transaction": [{"id": 9, "currency": "RUB", "invoiceid": 5}]}
                }
                """, GetInvoiceResponse.class);

        var items = response.getItems();
        assertEquals(1, items.size());
        assertEquals(1L, items.get(0).getId());
        assertEquals("hosting", items.get(0).getType());
        assertEquals(7, items.get(0).getRelId());

        var transactions = response.getTransactions();
        assertEquals(1, transactions.size());
        assertEquals(9L, transactions.get(0).getId());
        assertEquals("RUB", transactions.get(0).getCurrency());
        assertEquals(5L, transactions.get(0).getInvoiceId());
    }

    @Test
    void activityLogWrapper_mapsActivitiesAndReturnsEmptyOnNull() {
        var response = fromJson("""
                {"activity": {"entry": [{"id": 1, "description": "d"}]}}
                """, GetActivityLogResponse.class);

        var activities = response.getActivities();
        assertEquals(1, activities.size());
        assertEquals(1L, activities.get(0).getId());
        assertEquals("d", activities.get(0).getDescription());

        assertTrue(fromJson("{}", GetActivityLogResponse.class).getActivities().isEmpty());
    }
}
