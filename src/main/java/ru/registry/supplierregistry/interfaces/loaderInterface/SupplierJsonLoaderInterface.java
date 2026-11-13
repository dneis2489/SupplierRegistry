package ru.registry.supplierregistry.interfaces.loaderInterface;

import ru.registry.supplierregistry.entity.Supplier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface SupplierJsonLoaderInterface
{
    List<Supplier> load(Path filePath) throws IOException;
}
