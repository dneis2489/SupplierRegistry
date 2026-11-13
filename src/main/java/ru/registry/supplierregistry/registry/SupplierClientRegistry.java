package ru.registry.supplierregistry.registry;

import ru.registry.supplierregistry.client.SupplierClient;
import ru.registry.supplierregistry.entity.Supplier;
import ru.registry.supplierregistry.interfaces.clientInterface.SupplierClientFactoryInterface;
import ru.registry.supplierregistry.interfaces.loaderInterface.SupplierJsonLoaderInterface;
import ru.registry.supplierregistry.interfaces.registryInterfase.SupplierClientRegistryInterface;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


/**
 Реализация реестра поставщиков. Предоставляет методы по замене всех поставщиков и получение конкретного поставщика по id
 */
public class SupplierClientRegistry implements SupplierClientRegistryInterface
{
    private final SupplierClientFactoryInterface clientFactory;
    private final SupplierJsonLoaderInterface jsonLoader;

    private final AtomicReference<Map<String, SupplierClient>> clients =
            new AtomicReference<>(Map.of());

    public SupplierClientRegistry(SupplierClientFactoryInterface clientFactory,
                                  SupplierJsonLoaderInterface jsonLoader
    )
    {
        this.clientFactory = clientFactory;
        this.jsonLoader = jsonLoader;
    }

    //Замена всех поставщиков
    @Override
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

    public void replaceFromFile(Path path) throws IOException {
        List<Supplier> suppliers = jsonLoader.load(path);
        replaceAll(suppliers);
    }

    //Получение конкретного поставщика по id
    @Override
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
