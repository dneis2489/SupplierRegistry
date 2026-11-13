package ru.registry.supplierregistry.entity;

/**
 Модель для описания поставщика
     * @param id идентификатор поставщика
     * @param baseUrl базовый URL для обращения к поставщику
     * @param apiKey ключ доступа
 */
public record Supplier(
        String id,
        String baseUrl,
        String apiKey
)
{
}
