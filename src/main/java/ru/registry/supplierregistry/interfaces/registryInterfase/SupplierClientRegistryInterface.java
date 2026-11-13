package ru.registry.supplierregistry.interfaces.registryInterfase;

import ru.registry.supplierregistry.client.SupplierClient;
import ru.registry.supplierregistry.entity.Supplier;

import java.util.List;

/**
 Интерфейс описания методов реестра поставщиков
 */
public interface SupplierClientRegistryInterface
{
    void replaceAll(List<Supplier> suppliers);

    SupplierClient getClient(String supplierId);
}
