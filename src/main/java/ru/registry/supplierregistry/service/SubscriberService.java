package ru.registry.supplierregistry.service;

import org.springframework.stereotype.Service;
import ru.registry.supplierregistry.client.SupplierClient;
import ru.registry.supplierregistry.registry.SupplierClientRegistry;

/**
 SubscriberService - реализует проверку доступности номера у указанного поставщика
 */
@Service
public class SubscriberService
{
    private final SupplierClientRegistry registry;

    public SubscriberService(SupplierClientRegistry registry) {
        this.registry = registry;
    }

    /**
     Проверка наличия номера телефона у поставщика с указанным id
     */
    public boolean isSubscriberAvailable(
            String supplierId,
            String phone
    ) {
        SupplierClient client = registry.getClient(supplierId);

        return client.isAvailable(phone);
    }
}
