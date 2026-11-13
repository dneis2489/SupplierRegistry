package ru.registry.supplierregistry.service;

import ru.registry.supplierregistry.client.SupplierClient;
import ru.registry.supplierregistry.interfaces.registryInterfase.SupplierClientRegistryInterface;
import ru.registry.supplierregistry.interfaces.serviceInterface.SubscriberServiceInterface;

/**
 SubscriberService - реализует проверку доступности номера у указанного поставщика
 */
public class SubscriberService implements SubscriberServiceInterface
{
    private final SupplierClientRegistryInterface registry;

    public SubscriberService(SupplierClientRegistryInterface registry) {
        this.registry = registry;
    }

    @Override
    public boolean isSubscriberAvailable(
            String supplierId,
            String phone
    ) {
        SupplierClient client = registry.getClient(supplierId);

        return client.isAvailable(phone);
    }
}
