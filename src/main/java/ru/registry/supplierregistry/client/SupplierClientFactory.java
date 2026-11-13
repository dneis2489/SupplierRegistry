package ru.registry.supplierregistry.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.registry.supplierregistry.entity.Supplier;

/**
    Фабрика по созданию SupplierClient и внутреннего RestClient для каждого поставщика
 */
@Component
public class SupplierClientFactory
{
    public SupplierClient create(Supplier supplier) {
        RestClient restClient = RestClient.builder()
                .baseUrl(supplier.baseUrl())
                .build();

        return new SupplierClient(
                supplier,
                restClient
        );
    }
}
