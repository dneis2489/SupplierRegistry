package ru.registry.supplierregistry.client;

import org.springframework.web.client.RestClient;
import ru.registry.supplierregistry.entity.Supplier;

/**
    SupplierClient - объект, который умеет обращаться с одним конкретным поставщиком.
 */
public class SupplierClient
{
    private final Supplier supplier;
    private final RestClient restClient;

    // Конструктор класса
    public SupplierClient(
            Supplier supplier,
            RestClient restClient
    )
    {
        this.supplier = supplier;
        this.restClient = restClient;
    }

    // Выполняет GET запрос для определения доступности номера
    public boolean isAvailable(String phone)
    {
        try
        {
            SupplierResponse response = restClient.get()
                    .uri("/subscribers/{phone}", phone)
                    .header("Authorization",
                            "Bearer " + supplier.apiKey())
                    .retrieve()
                    .body(SupplierResponse.class);

            return response != null
                    && "ok".equals(response.status());

        }
        catch (org.springframework.web.client.HttpClientErrorException.NotFound e)
        {
            return false;
        }
    }

    private record SupplierResponse(String status)
    {
    }
}
