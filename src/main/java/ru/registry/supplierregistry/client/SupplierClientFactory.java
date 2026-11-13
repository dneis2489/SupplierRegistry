package ru.registry.supplierregistry.client;

import org.springframework.web.client.RestClient;
import ru.registry.supplierregistry.entity.Supplier;
import ru.registry.supplierregistry.interfaces.clientInterface.SupplierClientFactoryInterface;

/**
    Фабрика по созданию SupplierClient и внутреннего RestClient для каждого поставщика
 */
public class SupplierClientFactory implements SupplierClientFactoryInterface
{
    @Override
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
