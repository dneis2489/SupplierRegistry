# SupplierRegistry

Приложение на Java 17 и Spring Boot для управления HTTP-клиентами нескольких поставщиков и проверки доступности номера телефона у выбранного поставщика.

## Возможности

- Хранение клиентов поставщиков по уникальному `id`.
- Полная замена набора поставщиков из `List<Supplier>` или JSON-файла.
- Проверка номера через `GET {baseUrl}/subscribers/{phone}` с заголовком `Authorization: Bearer {apiKey}`.
- Обработка ответа: HTTP `200` с `{"status":"ok"}` — `true`, HTTP `404` — `false`. Остальные HTTP-ошибки и ошибки соединения передаются вызывающему коду.
- Атомарная замена мапы клиентов без промежуточного частично обновлённого состояния.

## Технологии

- Java 17
- Spring Boot 3.4.12, Spring `RestClient`
- Jackson
- Maven
- JUnit 5, Mockito, `MockRestServiceServer`

## Основные компоненты

| Компонент | Назначение |
| --- | --- |
| `Supplier` | Модель поставщика: `id`, `baseUrl`, `apiKey` |
| `SupplierClient` | HTTP-запросы к одному поставщику |
| `SupplierClientFactory` | Создание клиента с нужным `baseUrl` |
| `SupplierJsonLoader` | Чтение списка поставщиков из JSON |
| `SupplierClientRegistry` | Хранение, поиск и полная замена клиентов |
| `SubscriberService` | Единая операция проверки номера по ID поставщика |

## Конфигурация поставщиков

Пример файла `src/main/java/ru/registry/supplierregistry/data/suppliers.json`:

```json
[
  {
    "id": "provider-2",
    "baseUrl": "https://api2.example.com",
    "apiKey": "key-2"
  }
]
```

Файл загружается **явным вызовом** `replaceFromFile(...)`, а не автоматически при старте приложения. Путь ниже относителен к корню проекта: файл должен существовать на диске и быть доступен процессу.

## Пример использования

Получив Spring-бины `SupplierClientRegistry` и `SubscriberService`, можно заполнить реестр одним из двух способов.

**Из списка:**

```java
registry.replaceAll(List.of(
    new Supplier("provider-1", "https://api.example.com", "your-api-key")
));
```

**Из JSON-файла:**

```java
registry.replaceFromFile(Path.of(
    "src/main/java/ru/registry/supplierregistry/data/suppliers.json"
));
```

Оба метода **полностью заменяют** текущий набор клиентов, а не дополняют его. После заполнения реестра проверка выполняется так:

```java
boolean available = subscriberService.isSubscriberAvailable(
    "provider-2", "+79991234567"
);
```

В этом примере `provider-2` должен присутствовать в текущем реестре. Для использования фрагментов нужны соответствующие импорты `Supplier`, `Path` и `List`.

Если запрошенного поставщика нет, `getClient()` выбрасывает `IllegalArgumentException`. Дублирующиеся ID при замене также вызывают `IllegalArgumentException`.

## Запуск и тесты

Требуется **JDK 17**. В проект включён Maven Wrapper, поэтому отдельная установка Maven не обязательна.

**Windows:**

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Приложение не предоставляет REST-контроллеров и не запускает собственный HTTP API. Его компоненты предназначены для использования из Java-кода. При отсутствии фоновых задач приложение может завершиться сразу после успешного запуска Spring-контекста.

Тесты покрывают HTTP-ответы `200` и `404`, ошибки запросов, операции реестра, чтение JSON и замену реестра данными из файла. HTTP-запросы в тестах имитируются без обращения к реальным поставщикам.