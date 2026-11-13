package ru.registry.supplierregistry.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.registry.supplierregistry.entity.Supplier;
import ru.registry.supplierregistry.interfaces.loaderInterface.SupplierJsonLoaderInterface;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SupplierJsonLoader implements SupplierJsonLoaderInterface {
    private final ObjectMapper objectMapper;

    public SupplierJsonLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Supplier> load(Path filePath) throws IOException {
        return objectMapper.readValue(
                filePath.toFile(),
                new TypeReference<List<Supplier>>() {
                }
        );
    }
}
