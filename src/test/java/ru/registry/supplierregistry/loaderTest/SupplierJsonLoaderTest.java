package ru.registry.supplierregistry.loaderTest;

import ru.registry.supplierregistry.entity.Supplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.registry.supplierregistry.loader.SupplierJsonLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SupplierJsonLoaderTest {

    @TempDir
    Path tempDir;

    private SupplierJsonLoader loader;

    @BeforeEach
    void setUp() {
        loader = new SupplierJsonLoader(new ObjectMapper());
    }

    @Test
    void shouldLoadSuppliersFromJsonFile() throws IOException {
        Path file = tempDir.resolve("suppliers.json");

        Files.writeString(file, """
                [
                  {
                    "id": "provider-1",
                    "baseUrl": "https://api1.example.com",
                    "apiKey": "key-1"
                  },
                  {
                    "id": "provider-2",
                    "baseUrl": "https://api2.example.com",
                    "apiKey": "key-2"
                  }
                ]
                """);

        List<Supplier> suppliers = loader.load(file);

        assertEquals(2, suppliers.size());

        assertEquals("provider-1", suppliers.get(0).id());
        assertEquals(
                "https://api1.example.com",
                suppliers.get(0).baseUrl()
        );
        assertEquals("key-1", suppliers.get(0).apiKey());

        assertEquals("provider-2", suppliers.get(1).id());
    }

    @Test
    void shouldThrowExceptionWhenFileDoesNotExist() {
        Path file = tempDir.resolve("missing.json");

        assertThrows(
                IOException.class,
                () -> loader.load(file)
        );
    }

    @Test
    void shouldThrowExceptionForInvalidJson() throws IOException {
        Path file = tempDir.resolve("invalid.json");

        Files.writeString(file, """
                [
                  {
                    "id": "provider-1",
                    "baseUrl":
                  }
                ]
                """);

        assertThrows(
                IOException.class,
                () -> loader.load(file)
        );
    }
}
