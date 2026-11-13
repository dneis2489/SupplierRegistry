package ru.registry.supplierregistry.registry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.registry.supplierregistry.client.SupplierClient;
import ru.registry.supplierregistry.client.SupplierClientFactory;
import ru.registry.supplierregistry.loader.SupplierJsonLoader;
import ru.registry.supplierregistry.entity.Supplier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierRegistryIntegrationTest {

    @Test
    void shouldReplaceRegistryFromRealJsonFile() throws IOException {

        // Создаём настоящий JSON-загрузчик
        SupplierJsonLoader loader =
                new SupplierJsonLoader(new ObjectMapper());

        // Подменяем только фабрику HTTP-клиентов
        SupplierClientFactory factory =
                mock(SupplierClientFactory.class);

        SupplierClientRegistry registry =
                new SupplierClientRegistry(factory, loader);

        // Первый поставщик — из готового списка
        Supplier first = new Supplier(
                "provider-1",
                "https://api1.example.com",
                "key-1"
        );

        SupplierClient firstClient = mock(SupplierClient.class);
        SupplierClient secondClient = mock(SupplierClient.class);

        when(factory.create(first)).thenReturn(firstClient);

        Supplier second = new Supplier(
                "provider-2",
                "https://api2.example.com",
                "key-2"
        );

        when(factory.create(second)).thenReturn(secondClient);

        // Заполняем реестр готовым списком
        registry.replaceAll(List.of(first));

        assertSame(firstClient, registry.getClient("provider-1"));

        // Загружаем настоящий JSON-файл
        Path path = Path.of(
                "src/main/java/ru/registry/supplierregistry/data/suppliers.json"
        );

        registry.replaceFromFile(path);

        // Проверяем, что новый поставщик появился
        assertSame(secondClient, registry.getClient("provider-2"));

        // Проверяем, что старый поставщик удалён
        assertThrows(
                IllegalArgumentException.class,
                () -> registry.getClient("provider-1")
        );
    }
}
