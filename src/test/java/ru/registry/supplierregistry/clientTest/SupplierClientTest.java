package ru.registry.supplierregistry.clientTest;

import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import ru.registry.supplierregistry.client.SupplierClient;
import ru.registry.supplierregistry.entity.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class SupplierClientTest {

    private SupplierClient supplierClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        Supplier supplier = new Supplier(
                "provider-1",
                "https://api.example.com",
                "test-api-key"
        );

        RestClient.Builder builder = RestClient.builder();

        mockServer = MockRestServiceServer.bindTo(builder).build();

        RestClient restClient = builder
                .baseUrl(supplier.baseUrl())
                .build();

        supplierClient = new SupplierClient(supplier, restClient);
    }

    @Test
    void shouldReturnTrueWhenSubscriberIsAvailable() {
        mockServer.expect(requestTo(
                        "https://api.example.com/subscribers/79991234567"
                ))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(
                        "Authorization", "Bearer "
                ))
                .andRespond(withSuccess(
                        "{\"status\":\"ok\"}",
                        MediaType.APPLICATION_JSON
                ));

        boolean result =
                supplierClient.isAvailable("79991234567");

        assertTrue(result);
        mockServer.verify();
    }

    @Test
    void shouldReturnFalseWhenSubscriberIsNotFound() {
        mockServer.expect(requestTo(
                        "https://api.example.com/subscribers/79991234567"
                ))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        boolean result =
                supplierClient.isAvailable("79991234567");

        assertFalse(result);
        mockServer.verify();
    }

    @Test
    void shouldPropagateServerError() {
        mockServer.expect(requestTo(
                        "https://api.example.com/subscribers/79991234567"
                ))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThrows(
                HttpServerErrorException.class,
                () -> supplierClient.isAvailable("79991234567")
        );

        mockServer.verify();
    }

    @Test
    void shouldPropagateNetworkError() {
        mockServer.expect(requestTo(
                        "https://api.example.com/subscribers/79991234567"
                ))
                .andExpect(method(HttpMethod.GET))
                .andRespond(request -> {
                    throw new IOException("Connection failed");
                });

        assertThrows(
                ResourceAccessException.class,
                () -> supplierClient.isAvailable("79991234567")
        );

        mockServer.verify();
    }
}
