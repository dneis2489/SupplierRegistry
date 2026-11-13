package ru.registry.supplierregistry.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.registry.supplierregistry.entity.Supplier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 Реализует загрузку данных в реестр через чтение json файла
 */
@Component
public class SupplierJsonLoader {
    private final ObjectMapper objectMapper;

    public SupplierJsonLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
        Метод преобразует данные из json файла в List
     */
    public List<Supplier> load(Path filePath) throws IOException {
        return objectMapper.readValue(
                filePath.toFile(),
                new TypeReference<List<Supplier>>() {
                }
        );
    }
}
