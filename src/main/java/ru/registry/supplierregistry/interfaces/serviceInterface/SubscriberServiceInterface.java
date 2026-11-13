package ru.registry.supplierregistry.interfaces.serviceInterface;

/**
 Интерфейс описания методов проверки доступности номера у поставщика
 */
public interface SubscriberServiceInterface
{
    boolean isSubscriberAvailable(String supplierId, String phone);
}
