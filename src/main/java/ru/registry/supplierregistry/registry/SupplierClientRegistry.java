package ru.registry.supplierregistry.registry;

import org.springframework.stereotype.Component;
import ru.registry.supplierregistry.client.SupplierClient;
import ru.registry.supplierregistry.client.SupplierClientFactory;
import ru.registry.supplierregistry.entity.Supplier;
import ru.registry.supplierregistry.loader.SupplierJsonLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


/**
 Реализация реестра поставщиков. Предоставляет методы по замене всех поставщиков и получение конкретного поставщика по id
 */
@Component
public class SupplierClientRegistry
{
    private final SupplierClientFactory clientFactory;
    private final SupplierJsonLoader jsonLoader;

    private final AtomicReference<Map<String, SupplierClient>> clients =
            new AtomicReference<>(Map.of());

    public SupplierClientRegistry(SupplierClientFactory clientFactory,
                                  SupplierJsonLoader jsonLoader
    )
    {
        this.clientFactory = clientFactory;
        this.jsonLoader = jsonLoader;
    }

    //Замена всех поставщиков
    public void replaceAll(List<Supplier> suppliers)
    {
        Map<String, SupplierClient> newClients = new HashMap<>();

        for (Supplier supplier : suppliers) {
            SupplierClient client = clientFactory.create(supplier);

            SupplierClient previous = newClients.put(supplier.id(), client);

            if (previous != null) {
                throw new IllegalArgumentException(
                        "Duplicate supplier id: " + supplier.id()
                );
            }
        }

        clients.set(Map.copyOf(newClients));
    }

    //Загрузка данных для реестра из файла
    public void replaceFromFile(Path path) throws IOException {
        List<Supplier> suppliers = jsonLoader.load(path);
        replaceAll(suppliers);
    }

    //Получение конкретного поставщика по id
    public SupplierClient getClient(String supplierId)
    {
        SupplierClient client = clients.get().get(supplierId);

        if (client == null) {
            throw new IllegalArgumentException(
                    "Unknown supplier: " + supplierId
            );
        }

        return client;
    }
}
