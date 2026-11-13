package ru.registry.supplierregistry.interfaces.clientInterface;

import ru.registry.supplierregistry.client.SupplierClient;
import ru.registry.supplierregistry.entity.Supplier;

public interface SupplierClientFactoryInterface
{
    public SupplierClient create(Supplier supplier);
}
