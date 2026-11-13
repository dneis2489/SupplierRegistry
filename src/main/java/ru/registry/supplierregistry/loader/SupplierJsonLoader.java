package ru.registry.supplierregistry.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.registry.supplierregistry.entity.Supplier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Component
public class SupplierJsonLoader {
    private final ObjectMapper objectMapper;

    public SupplierJsonLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Supplier> load(Path filePath) throws IOException {
        return objectMapper.readValue(
                filePath.toFile(),
                new TypeReference<List<Supplier>>() {
                }
        );
    }
}
