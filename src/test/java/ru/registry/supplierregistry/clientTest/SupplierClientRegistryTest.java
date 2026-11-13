package ru.registry.supplierregistry.clientTest;

import ru.registry.supplierregistry.client.SupplierClient;
import ru.registry.supplierregistry.client.SupplierClientFactory;
import ru.registry.supplierregistry.entity.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.registry.supplierregistry.loader.SupplierJsonLoader;
import ru.registry.supplierregistry.registry.SupplierClientRegistry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierClientRegistryTest {

    private SupplierClientFactory clientFactory;
    private SupplierClientRegistry registry;
    private SupplierJsonLoader jsonLoader;


    @BeforeEach
    void setUp() {
        clientFactory = mock(SupplierClientFactory.class);
        jsonLoader = mock(SupplierJsonLoader.class);
        registry = new SupplierClientRegistry(clientFactory, jsonLoader);
    }

    @Test
    void shouldRegisterAndFindClient() {
        Supplier supplier = new Supplier(
                "provider-1",
                "https://api1.example.com",
                "key-1"
        );

        SupplierClient client = mock(SupplierClient.class);

        when(clientFactory.create(supplier)).thenReturn(client);

        registry.replaceAll(List.of(supplier));

        assertSame(client, registry.getClient("provider-1"));
    }

    @Test
    void shouldReplaceAllClients() {
        Supplier first = new Supplier(
                "provider-1",
                "https://api1.example.com",
                "key-1"
        );

        Supplier second = new Supplier(
                "provider-2",
                "https://api2.example.com",
                "key-2"
        );

        SupplierClient firstClient = mock(SupplierClient.class);
        SupplierClient secondClient = mock(SupplierClient.class);

        when(clientFactory.create(first)).thenReturn(firstClient);
        when(clientFactory.create(second)).thenReturn(secondClient);

        registry.replaceAll(List.of(first));

        registry.replaceAll(List.of(second));

        assertSame(secondClient, registry.getClient("provider-2"));

        assertThrows(
                IllegalArgumentException.class,
                () -> registry.getClient("provider-1")
        );
    }

    @Test
    void shouldThrowExceptionForUnknownSupplier() {
        assertThrows(
                IllegalArgumentException.class,
                () -> registry.getClient("unknown")
        );
    }

    @Test
    void shouldKeepOldClientsWhenReplacementFails() {
        Supplier original = new Supplier(
                "provider-1",
                "https://api1.example.com",
                "key-1"
        );

        Supplier duplicate = new Supplier(
                "provider-2",
                "https://api2.example.com",
                "key-2"
        );

        SupplierClient originalClient = mock(SupplierClient.class);

        when(clientFactory.create(original)).thenReturn(originalClient);
        when(clientFactory.create(duplicate))
                .thenReturn(mock(SupplierClient.class));

        registry.replaceAll(List.of(original));

        assertThrows(
                IllegalArgumentException.class,
                () -> registry.replaceAll(
                        List.of(duplicate, duplicate)
                )
        );

        assertSame(
                originalClient,
                registry.getClient("provider-1")
        );
    }

    @Test
    void shouldReplaceClientsFromJsonFile() throws IOException {
        Supplier first = new Supplier(
                "provider-1",
                "https://api1.example.com",
                "key-1"
        );

        Supplier second = new Supplier(
                "provider-2",
                "https://api2.example.com",
                "key-2"
        );

        SupplierClient firstClient = mock(SupplierClient.class);
        SupplierClient secondClient = mock(SupplierClient.class);

        when(clientFactory.create(first)).thenReturn(firstClient);
        when(clientFactory.create(second)).thenReturn(secondClient);

        // Сначала заполняем реестр готовым списком
        registry.replaceAll(List.of(first));

        assertSame(firstClient, registry.getClient("provider-1"));

        // Подготавливаем результат чтения JSON-файла
        Path path = Path.of("data", "suppliers.json");

        when(jsonLoader.load(path)).thenReturn(List.of(second));

        // Теперь обновляем тот же реестр через файл
        registry.replaceFromFile(path);

        // Новый поставщик появился
        assertSame(secondClient, registry.getClient("provider-2"));

        // Старый поставщик исчез
        assertThrows(
                IllegalArgumentException.class,
                () -> registry.getClient("provider-1")
        );

        // Проверяем, что загрузчик действительно вызывался
        verify(jsonLoader).load(path);
    }
}
