package ru.registry.supplierregistry.interfaces.clientInterface;

import org.springframework.web.client.RestClient;
import ru.registry.supplierregistry.entity.Supplier;

/**
 * Интерфейс для методов SupplierClient
 *      boolean isAvailable - пробрасывает GET запрос для проверки доступности номера
 */
public interface SupplierClientInterface
{
    boolean isAvailable(String phone);
}
