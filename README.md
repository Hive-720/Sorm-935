# СОРМ - Система оперативно-розыскных мероприятий

## Общее описание

**СОРМ** — это система, разработанная в соответствии с требованиями приказа Министерства цифрового развития, связи и
массовых коммуникаций РФ от 1 ноября 2023 г.
№935 ([Полный приказ](https://www.garant.ru/products/ipo/prime/doc/408010659)).
Система предназначена для интеграции с платформой автоматизации веб-хостинга [WHMCS](https://www.whmcs.com/)
и предоставления уполномоченным государственным органам доступа к данным провайдера хостинга в режиме реального времени.

### Основное назначение

- Сбор и синхронизация данных из WHMCS
- Предоставление GraphQL API для поиска и фильтрации данных
- Реализация подписок на события в реальном времени

## Диаграмма архитектуры

```mermaid
flowchart TB
    ExternalInterface["Пульт управления"]
    Proxy["NGINX"]
    WHMCS["WHMCS"]
    POSTGRES[("PostgreSQL")]
    PROMETHEUS["Prometheus"]
    ALERT_MANAGER["AlertManager"]

    subgraph Application["СОРМ"]
        subgraph Presentation["Слой представления"]
            REST["REST Controllers<br>NotificationsController"]
            GQL_QUERY["GraphQL Query Resolvers"]
            GQL_SUB["GraphQL Subscription Resolver"]
            SECURITY["Security Filter<br>API Key Authentication"]
            GQL_CONFIG["GraphQL Config<br>Schema"]
        end
        subgraph Business["Бизнес-логика"]
            DATA_SERVICES["WHMCS Data Services<br>ClientService, ProductService, etc."]
            SYNC_SERVICES["Data Sync Services<br>ClientSyncService, ProductSyncService, etc."]
            SYSTEM_SERVICES["System Services<br>NotificationService, MetadataService"]
            EVENT_PUB["Event Publisher<br>События системы"]
        end
        subgraph Infrastructure["Инфраструктура"]
            REPOS["R2DBC Repositories<br>QueryDSL, Reactive Queries"]
            WHMCS_PROVIDER["WHMCS Provider"]
            WHMCS_CLIENT["WHMCS API Client<br>WebClient, Request/Response"]
            EVENT_BUS["Event Bus<br>NotificationEventBus"]
            PERSIST_CONFIG["Persistence Config"]
        end
        subgraph Scheduling["Слой планирования"]
            SCHEDULERS["Sync Schedulers<br>15 типов синхронизации"]
            SCHEDULER_COORD["Scheduler Coordinator<br>Управление зависимостями"]
            SCHEDULER_CONFIG["Scheduler Configs<br>Конфигурация расписаний"]
            SYNC_TASK["SyncTask"]
        end
        subgraph Core["Ядро приложения"]
            APP["SormApplication<br>Spring Boot Application"]
            APP_CONFIG["Application Config"]
            RESOURCES["Resources<br>GraphQL Schema, Messages"]
        end
        subgraph Monitoring["Мониторинг"]
            ACTUATOR["Spring Actuator<br>Health, Metrics"]
            LOGGING["Logback<br>Structured Logging"]
        end
    end
    ExternalInterface --> Proxy
    Proxy --> REST & GQL_QUERY & GQL_SUB
    WHMCS_CLIENT -- API Calls --> WHMCS
    REPOS -- R2DBC --> POSTGRES
    ACTUATOR -- Metrics --> PROMETHEUS
    PROMETHEUS --> ALERT_MANAGER
    ALERT_MANAGER --> REST
    REST --> SYSTEM_SERVICES
    GQL_QUERY --> DATA_SERVICES
    GQL_SUB --> SYSTEM_SERVICES
    SECURITY --> REST & GQL_QUERY & GQL_SUB
    DATA_SERVICES --> REPOS
    SYNC_SERVICES --> REPOS & WHMCS_PROVIDER
    SYSTEM_SERVICES --> REPOS & EVENT_PUB
    EVENT_PUB --> EVENT_BUS
    EVENT_BUS --> REPOS
    WHMCS_PROVIDER --> WHMCS_CLIENT
    SCHEDULERS --> SYNC_SERVICES & SCHEDULER_COORD & SYNC_TASK
    SCHEDULER_COORD --> SCHEDULERS
    SCHEDULER_CONFIG --> SCHEDULERS
    SYNC_TASK --> REPOS
    APP --> ACTUATOR & LOGGING
    RESOURCES --> APP
    WHMCS:::external
    POSTGRES:::external
    PROMETHEUS:::external
    ExternalInterface:::external
    Proxy:::external
    ALERT_MANAGER:::external
    REST:::presentation
    GQL_QUERY:::presentation
    GQL_SUB:::presentation
    SECURITY:::presentation
    GQL_CONFIG:::presentation
    DATA_SERVICES:::business
    SYNC_SERVICES:::business
    SYSTEM_SERVICES:::business
    EVENT_PUB:::business
    REPOS:::infrastructure
    WHMCS_PROVIDER:::infrastructure
    WHMCS_CLIENT:::infrastructure
    EVENT_BUS:::infrastructure
    PERSIST_CONFIG:::infrastructure
    SCHEDULERS:::scheduling
    SCHEDULER_COORD:::scheduling
    SCHEDULER_CONFIG:::scheduling
    SYNC_TASK:::scheduling
    APP:::core
    APP_CONFIG:::core
    RESOURCES:::core
    ACTUATOR:::monitoring
    LOGGING:::monitoring
    classDef external fill: #2196F3, stroke: #0d47a1, stroke-width: 3px, color: #fff
    classDef presentation fill: #9C27B0, stroke: #4a148c, stroke-width: 3px, color: #fff
    classDef business fill: #4CAF50, stroke: #1b5e20, stroke-width: 3px, color: #fff
    classDef infrastructure fill: #FF9800, stroke: #e65100, stroke-width: 3px, color: #fff
    classDef scheduling fill: #E91E63, stroke: #880e4f, stroke-width: 3px, color: #fff
    classDef core fill: #00BCD4, stroke: #004d40, stroke-width: 3px, color: #fff
    classDef monitoring fill: #8BC34A, stroke: #33691e, stroke-width: 3px, color: #fff
```

## Технологический стек

### **Основной стек**

- **Backend**: Java 21, Spring Boot 3.5.5, WebFlux, Project Reactor
- **Базы данных**:
    - Основная: PostgreSQL
    - Миграции: Liquibase
    - Драйвер: R2DBC (реактивный доступ к PostgreSQL)
    - Динамическая генерация SQL: QueryDSL
- **Сборка**: Gradle
- **Контейнеризация**: Docker, Docker Compose

### **API**

- **GraphQL**: Spring GraphQL (запросы + подписки), schema-first (
  явная [graphql схема](src/main/resources/graphql/schema.graphqls))
- **REST API**: Дополнительные контроллеры для уведомлений
- **Транспорты**: HTTP, WebSocket (graphql-ws протокол)

### **Мониторинг**

- **Метрики**: Micrometer, Prometheus (порт `9990`, эндпоинт `/actuator/prometheus`)
- **Логирование**: Structured logging (JSON) с Logback, доставка логов в Loki через Promtail
- **Health checks**: Spring Boot Actuator (`/actuator/health`)
- **Визуализация**: Grafana (дашборд `sorm-935-overview` из `grafana/provisioning/dashboards`)

### **Тестирование**

- **Unit tests**: JUnit 5, Mockito
- **Интеграционные**: Testcontainers, R2DBC тесты
- **GraphQL тесты**: Spring GraphQL Test
- **Реактивные тесты**: StepVerifier (Project Reactor)

## Сборка и запуск

### Требования

- JDK 21
- Docker и Docker Compose (для локального стека: PostgreSQL, Prometheus, Grafana, Loki, Promtail)

### Сборка и тесты

```bash
./gradlew clean build        # сборка + unit-тесты
./gradlew integrationTest    # интеграционные тесты (Testcontainers, PostgreSQL)
./gradlew testCoverageReport # сводный отчёт JaCoCo (unit + integration)
```

### Запуск в Docker

```bash
docker compose -f docker-compose.dev.yml up --build
```

| Сервис        | Адрес                                              |
|---------------|----------------------------------------------------|
| SORM Server   | порт `9931` (GraphQL `/query`, WebSocket `/subscription`) |
| Management    | порт `9990` (`/actuator/health`, `/actuator/prometheus`) |
| GraphiQL      | `http://localhost:9931/graphiql`                   |
| Prometheus    | `http://localhost:9090`                            |
| Grafana       | `http://localhost:3000` (admin/admin, дашборд `sorm-935-overview`) |
| Loki          | `http://localhost:3100`                            |
| pgAdmin       | `http://localhost:5081`                            |

### Профили

- `dev` (по умолчанию) - локальная разработка, GraphiQL включён
- `docker` - конфигурация для docker-compose (параметры подключения из переменных окружения `.env`)
- `integrationTest` - профиль для интеграционных тестов

### CI/CD

Bitbucket Pipelines (`bitbucket-pipelines.yml`): сборка и unit-тесты (`./gradlew clean build test`)
с публикацией артефактов (jar, отчёты), сканирование секретов (git-secrets-scan).

## Структура проекта

| Каталог | Назначение |
|---------|------------|
| `src/main/java/com/grunvald/sorm/presentation` | REST-контроллеры, GraphQL-резолверы, безопасность, конфигурация |
| `src/main/java/com/grunvald/sorm/service` | Бизнес-логика: сервисы данных WHMCS, синхронизация, системные уведомления |
| `src/main/java/com/grunvald/sorm/infrastructure` | R2DBC-репозитории, WHMCS API-клиент, event bus |
| `src/main/java/com/grunvald/sorm/syncdatascheduler` | Планировщики синхронизации и их конфигурации |
| `src/main/resources/graphql/schema.graphqls` | GraphQL-схема (schema-first) |
| `src/main/resources/db/changelog` | Миграции Liquibase |
| `src/main/resources/messages` | Локализация сообщений (`en`, `ru`, `fr`) |
| `src/integrationTest` | Интеграционные тесты (Testcontainers) |
| `grafana/provisioning` | Дашборд и источники данных Grafana |
| `prometheus`, `promtail` | Конфигурации сбора метрик и логов |

# API Документация

## Локализация

- Заголовок запроса: `lang`
- Значение: Alpha2 код страны
- Поддерживаемые локали: `en` (по умолчанию), `ru`, `fr`
- Некорректное значение заголовка игнорируется - используется локаль по умолчанию `en`

## Безопасность

Аутентификация включается/отключается настройкой `sorm.security.enabled` (по умолчанию `false`).
При включении применяется ко всем запросам - и REST, и GraphQL:

- **Заголовок**: `X-API-Key` (обязательный) - при отсутствии или несовпадении ключа возвращается `401`
- **Ключ**: `sorm.security.api-key`, не короче 16 символов
- **Исключения**: эндпоинты из списка `sorm.security.exclude-endpoints` не проверяются
  (например, `/actuator/health` для health check'ов)
- **HTTPS**: защищённое соединение требуется приказом № 935; терминирование TLS/mTLS
  выполняется на уровне прокси (NGINX) - само приложение проверяет только `X-API-Key`

## REST API

REST API используется для приёма уведомлений: пульт управления (ПУ) и AlertManager отправляют события,
которые сохраняются в БД и в реальном времени транслируются подписчикам GraphQL-подписки `trap`.

### `PUT /notifications/push`

Приём системного уведомления от ПУ.

| Поле       | Тип      | Описание                                                                                                                            |
|------------|----------|-------------------------------------------------------------------------------------------------------------------------------------|
| `type`     | string   | Тип события: `RESTARTDB`, `UNAUTHORIZEDACCESS`, `CRITICALERROR`, `MAJORERROR`, `MINORERROR`, `SCHEMCHANGED`, `METRICALERTS` (обязательное) |
| `message`  | string   | Текст уведомления (обязательное, до 1000 символов)                                                                                   |
| `startAt`  | datetime | Время возникновения события (обязательное)                                                                                           |

### `PUT /notifications/metric/alerts/push`

Приём алертов от AlertManager (формат webhook AlertManager): `groupKey`, `status` (`firing`/`resolved`),
`receiver`, `groupLabels`, `commonLabels`, `commonAnnotations`, `externalURL` и массив `alerts`.
Событие пересылается подписчикам как `Trap` типа `METRICALERTS`.

### Ограничение нагрузки

Количество одновременных запросов ограничено семафорами:
`sorm.rest.api.configs.push.max_requests` и `sorm.rest.api.configs.alert.max_requests`
(по умолчанию 50, в примере конфигурации - 100). При превышении возвращается `429`.

### Коды ошибок REST

| Код                  | HTTP | Описание                                        |
|----------------------|------|-------------------------------------------------|
| `INVALID_REQUEST`    | 400  | Некорректный запрос или тело запроса            |
| `INVALID_PARAMETER`  | 400  | Ошибка валидации поля                           |
| `MESSAGE_SIZE_EXCEED`| 400  | Превышен допустимый размер тела запроса         |
| `METHOD_NOT_ALLOWED` | 405  | Метод не поддерживается                         |
| `RESOURCE_NOT_FOUND` | 404  | Ресурс не найден                                |
| `TOO_MANY_REQUESTS`  | 429  | Превышен лимит одновременных запросов           |
| `INTERNAL_ERROR`     | 500  | Внутренняя ошибка                               |

Тексты сообщений об ошибках локализуются по заголовку `lang`.

## GraphQL API

GraphQL API предоставляет три ключевых метода:

### Конечные точки

| Протокол              | Путь            | Назначение                              |
|-----------------------|-----------------|-----------------------------------------|
| HTTP POST/GET         | `/query`        | Выполнение запросов (`request`, `getSchema`) |
| WebSocket (graphql-ws)| `/subscription` | Подписка `trap`                         |
| HTTP                  | `/graphiql`     | Интерактивная консоль GraphiQL (включена в профилях `dev` и `docker`) |

Пути задаются свойствами `spring.graphql.path` и `spring.graphql.websocket.path`.

### Основные методы

| Метод       | Тип          | Назначение                            |
|-------------|--------------|---------------------------------------|
| `request`   | Query        | Запрос данных из WHMCS системы        |
| `getSchema` | Query        | Получение GraphQL схемы в DSL формате |
| `trap`      | Subscription | Подписка на системные события         |

### Метод `request`

Предоставляет доступ к 15 сущностям WHMCS системы через отдельные методы:

1. `clients` - Клиенты
2. `clientLogins` - Аутентификации клиентов
3. `clientGroups` - Группы клиентов
4. `clientProducts` - Продукты клиентов
5. `users` - Пользователи
6. `contacts` - Контакты
7. `affiliates` - Партнеры
8. `orders` - Заказы
9. `invoices` - Счета
10. `transactions` - Транзакции
11. `credits` - Кредиты
12. `products` - Продукты
13. `activityLog` - Логи активности
14. `paymentMethods` - Методы оплаты
15. `emails` - Электронные письма

#### Пагинация

Каждый метод поддерживает два типа пагинации:

- **Offset**: параметры `offset` (≥ 0) и `limit`
- **Cursor**: параметры `cursor` и `limit` - курсор возвращается в результате
  и передаётся в следующий запрос

Ограничения: `limit` по умолчанию равен 25, максимальное значение - 100
(директива `@Range(min: 1, max: 100)`). Значение `cursor` - строка с положительным целым числом
(валидируется регулярным выражением `^[1-9]\d*$`). Актуальный максимум размера страницы
сообщается клиенту в поле `maxPageSize` типа `QueryMessage` (настройка `sorm.graphql.api.max_page_size`).

#### Фильтрация

Фильтры организованы в иерархическую структуру:

##### Базовые операторы фильтрации

- `allofterms` - строка содержит все указанные термы (через запятую)
- `anyofterms` - строка содержит хотя бы один из указанных термов
- `eq` - точное соответствие
- `in` - соответствие одному из значений массива
- `regexp` - поиск по регулярному выражению
- `alloftext` - текстовый поиск, должны совпасть все фрагменты
- `anyoftext` - текстовый поиск, должен совпасть хотя бы один фрагмент
- `le` (≤) - меньше или равно
- `lt` (<) - меньше
- `ge` (≥) - больше или равно
- `gt` (>) - больше
- `between` - диапазон значений

##### Логические операторы

- `AND` - логическое И
- `OR` - логическое ИЛИ
- `NOT` - логическое НЕ

Логические операторы задаются вложенными полями `and` / `or` / `not` фильтра
(глубина вложенности ограничена анализом сложности запроса).

Числовые операторы (`le`, `lt`, `ge`, `gt`, `between`) применимы к `IntFilter`
и `DateTimeStringFilter`; текстовые - к строковым фильтрам соответствующего типа.

### Пример структуры фильтра

```graphql
# Offset-пагинация с комбинированными условиями
query {
  request {
    requestID
    offline
    maxPageSize
    clients(
      filter: {
        and: [
          { name: { fullName: { allofterms: "иванов,петров" } } },
          { or: [
            { email: { email: { regexp: "@example\\.com$" } } },
            { inn: { inn: { eq: "7701234567" } } }
          ] }
        ]
      }
      offset: 0
      limit: 25
    ) {
      cursor
      hasNextPage
      result {
        id
        name { fullName }
        email { email }
        inn { inn }
        status
      }
    }
  }
}
```

```graphql
# Cursor-пагинация: курсор из результата передаётся в следующий запрос
query {
  request {
    clients(
      filter: { name: { fullName: { anyofterms: "иванов" } } }
      cursor: "42"
      limit: 10
    ) {
      cursor
      hasNextPage
      result {
        id
        name { fullName }
      }
    }
  }
}
```

```graphql
# Получение полной схемы в SDL-формате
query {
  getSchema
}
```

### Директивы валидации

```text
@Size
@Pattern
@PositiveOrZero
@ContainerSize
@Range
```

### Основные типы данных

#### `QueryMessage`

```graphql
type QueryMessage {
    requestID: String!          # Уникальный идентификатор запроса
    offline: Boolean!           # Флаг офлайн-режима
    maxPageSize: Int            # Максимальный размер страницы
    # ... методы запросов сущностей
}
```

#### Результаты запросов

Все методы возвращают структуры вида:

```graphql
type EntityResult {
    cursor: String              # Курсор для пагинации
    hasNextPage: Boolean        # Наличие следующей страницы
    result: [Entity!]           # Массив сущностей
}
```

### Типы событий (Subscription)

Подписка `trap` - канал событий в реальном времени: уведомления, отправленные ПУ через REST API
(`PUT /notifications/push`), алерты AlertManager (`PUT /notifications/metric/alerts/push`),
а также внутренние события системы (ошибки, перезапуск и т. д.) доставляются всем подписчикам.

```graphql
subscription {
  trap {
    type
    message
    startsAt { utc }
    metricalerts {
      status
      receiver
      externalURL
      alerts {
        status
        fingerprint
        startAt { utc }
      }
    }
  }
}
```

```graphql
enum TrapType {
    RESTARTDB             # Перезапуск базы данных
    UNAUTHORIZEDACCESS    # Неавторизованный доступ
    CRITICALERROR         # Критическая ошибка
    MAJORERROR           # Серьезная ошибка
    MINORERROR           # Незначительная ошибка
    SCHEMCHANGED         # Изменение схемы
    METRICALERTS         # Метрические алерты
}
```

### Базовые типы фильтров

```graphql
# Строковые фильтры
input StringTermExactFilter     # Точный поиск по термам
input StringRegExpExactFilter   # Точный поиск по регулярному выражению
input StringTermFullTextFilter  # Полнотекстовый поиск по термам
input StringExactFilter         # Простое точное соответствие
input StringRegExpFullTextFilter # Полнотекстовый поиск с regexp

# Числовые фильтры
input IntFilter
input IntRange   # Диапазон

# Фильтр даты/времени
input DateTimeStringFilter
input DateTimeRange    # Диапазон дат
```

### Обработка ошибок

- Все GraphQL ошибки возвращаются в стандартном формате GraphQL - в массиве `errors`
- Ошибки валидации и выполнения GraphQL возвращаются с HTTP-статусом 200 в теле ответа;
  HTTP `4xx`/`5xx` используются REST API и ошибками HTTP-уровня, возникшими до разбора GraphQL-запроса
- Валидация выполняется с помощью директив GraphQL
- Запросы ограничены по глубине и сложности (`sorm.graphql.api.query.analysis.max_depth`
  и `max_complexity`); при превышении возвращается код `REQUEST_TOO_COMPLEX`

#### Коды ошибок

API GraphQL может возвращать следующие коды ошибок:

| Error Code                     | Description                                                                                                                                                                                                                                                                        |
|--------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `GRAPHQL_PARSE_FAILED`         | Запрос на языке GraphQL содержит синтаксическую ошибку                                                                                                                                                                                                                             |
| `GRAPHQL_VALIDATION_FAILED`    | Запрос на языке GraphQL недействителен для текущей схемы данных                                                                                                                                                                                                                    |
| `BAD_USER_INPUT`               | Запрос на языке GraphQL содержит некорректное значение для аргумента поля                                                                                                                                                                                                          |
| `OPERATION_RESOLUTION_FAILURE` | Невозможно определить операцию для выполнения из запроса на языке GraphQL                                                                                                                                                                                                          |
| `BAD_REQUEST`                  | Ошибка возникла До начала анализа запроса на языке GraphQL                                                                                                                                                                                                                         |
| `REQUEST_NOT_FOUND`            | Отложенный запрос с указанным идентификатором не найден                                                                                                                                                                                                                            |
| `NO_REQUEST_RESULT`            | Результаты выполнения отложенного запроса с указанным идентификатором не найдены                                                                                                                                                                                                   |
| `REQUEST_TOO_COMPLEX`          | Запрос на языке GraphQL содержит критерии поиска ( (аргументы поля) ) не только на верхнем уровне запроса согласно пункту 21 настоящего приложения, , может применяться только в ТС OPM, осуществляющих поиск информации в ИС со среднесуточным объемом новых данных свыше 1 Гбайт |
| `INTERNAL_SERVER_ERROR`        | Внутренняя ошибка. Возвращается в случае, если ни один из вышеуказанных кодов не подходит                                                                                                                                                                                          |

## Интеграции и синхронизация данных

### Используемые API WHMCS

- [GetActivityLog](https://developers.whmcs.com/api-reference/getactivitylog)
- [GetAffiliates](https://developers.whmcs.com/api-reference/getaffiliates)
- [GetClientGroups](https://developers.whmcs.com/api-reference/getclientgroups)
- [GetClients](https://developers.whmcs.com/api-reference/getclients)
- [GetClientDetails](https://developers.whmcs.com/api-reference/getclientsdetails)
- [GetClientProducts](https://developers.whmcs.com/api-reference/getclientsproducts)
- [GetContacts](https://developers.whmcs.com/api-reference/getcontacts)
- [GetCredits](https://developers.whmcs.com/api-reference/getcredits)
- [GetEmails](https://developers.whmcs.com/api-reference/getemails)
- [GetInvoice](https://developers.whmcs.com/api-reference/getinvoice)
- [GetInvoices](https://developers.whmcs.com/api-reference/getinvoices)
- [GetOrders](https://developers.whmcs.com/api-reference/getorders)
- [GetPaymentMethods](https://developers.whmcs.com/api-reference/getpaymentmethods)
- [GetProducts](https://developers.whmcs.com/api-reference/getproducts)
- [GetUsers](https://developers.whmcs.com/api-reference/getusers)

### Планировщики синхронизации данных

| Планировщик                                                                                                                         | Описание                        | Тип операций                                        |
|-------------------------------------------------------------------------------------------------------------------------------------|---------------------------------|-----------------------------------------------------|
| [`ActivityLogSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/ActivityLogSyncScheduler.java)     | Логи активностей                | 📥 Загрузка новых записей                           |
| [`AffiliateSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/AffiliateSyncScheduler.java)         | Партнерки клиентов              | 📥 Загрузка и 🔄 обновление                         |
| [`ClientDetailSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/ClientDetailSyncScheduler.java)   | Детали клиентов                 | 🔄 Обновление + 🗑️ отметка удаленных               |
| [`ClientGroupSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/ClientGroupSyncScheduler.java)     | Группы клиентов                 | 📥 Загрузка + 🔄 обновление                         |
| [`ClientProductSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/ClientProductSyncScheduler.java) | Продукты клиентов               | 📥 Загрузка + 🔄 обновление                         |
| [`ClientSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/ClientSyncScheduler.java)               | Клиенты                         | 📥 Загрузка новых записей                           |
| [`ContactDeleteSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/ContactDeleteSyncScheduler.java) | Определение удаленных контактов | 🗑️ Отметка удаленных записей                       |
| [`ContactSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/ContactSyncScheduler.java)             | Контакты                        | 📥 Загрузка + 🔄 обновление                         |
| [`CreditSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/CreditSyncScheduler.java)               | Кредиты                         | 📥 Загрузка новых записей                           |
| [`EmailSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/EmailSyncScheduler.java)                 | Электронная почта               | 📥 Загрузка новых записей                           |
| [`InvoiceSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/InvoiceSyncScheduler.java)             | Счета и транзакции              | 📥 Загрузка + 🔄 обновление                         |
| [`OrderSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/OrderSyncScheduler.java)                 | Заказы                          | 📥 Загрузка + 🔄 обновление                         |
| [`PaymentMethodSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/PaymentMethodSyncScheduler.java) | Методы оплаты                   | 📥 Загрузка + 🔄 Обновление + 🗑️ отметка удаленных |
| [`ProductSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/ProductSyncScheduler.java)             | Продукты и услуги               | 📥 Загрузка + 🔄 Обновление + 🗑️ отметка удаленных |
| [`UserSyncScheduler.java`](src/main/java/com/grunvald/sorm/syncdatascheduler/scheduler/impl/UserSyncScheduler.java)                   | Пользователи                    | 📥 Загрузка + 🔄 обновление                         |

### Режимы работы планировщиков

Каждый планировщик поддерживает три режима (`mode`):

- `NORMAL` - после успешной начальной выгрузки работа продолжается периодически по `cron_expression`
- `FORCE_FIRST` - при каждом запуске приложения начальная выгрузка выполняется заново,
  затем работа продолжается по `cron_expression`
- `ONLY_FIRST` - выполняется только начальная выгрузка, периодические задачи не запускаются

Начальная выгрузка (`first_start`) повторяется при ошибках согласно `delays`
(список задержек в миллисекундах - количество элементов равно числу попыток);
флаг `continue_after_error` определяет, запускать ли периодические задачи,
если начальная выгрузка так и не завершилась успешно.

Зависимости между планировщиками (например, детали клиентов выгружаются после клиентов)
управляются SchedulerCoordinator.

### Настройки

```yaml
sorm:
  security:
    enabled: false #false по умолчанию
    api-key: testApiKey123456 #Ключ для авторизации (не короче 16 символов)
    exclude-endpoints: #Эндпоинты которые будут исключены из проверки
      - '/actuator/health'
  system:
    notifications: #Настройки отправления уведомлений на ПУ
      errors_graphql_api: true #Ошибки в graphql запросах. По умолчанию true
      errors_api: true #Ошибки в обычных rest запросах. По умолчанию true
      errors_scheduler: true #Ошибки в планировщике. По умолчанию true
      graphql_schema_version_mismatch: true #Graphql схема была изменена. По умолчанию true
      reload: true #Сервер был перезагружен. По умолчанию true
  graphql:
    schema:
      version_check:
        enabled: true #Проверка на изменение в graphql схеме. По умолчанию true
    api:
      max_page_size: 100 #Максимальное количество элементов которые может запросить ПУ для одного метода. По умолчанию 100
      query:
        analysis:
          max_depth: 20 #Максимальная глубина запроса
          max_complexity: 200 #Максимальная сложность запроса
  rest:
    api:
      configs:
        alert:
          max_requests: 100
        push:
          max_requests: 100
  whmcs:
    credentials: #Данные для авторизации в системе WHMCS
      identifier: 'identifier123' #Идентификатор
      secret: 'testSecret' #Секретный ключ
    client:
      url: 'https://localhost:8080/includes/api.php' #Хост + whmcs эндпоинт
      connect_timeout: 60000 #60000 по умолчанию
      read_timeout: 60000 #60000 по умолчанию
      write_timeout: 60000 #60000 по умолчанию
      response_timeout: 60000 #60000 по умолчанию
    data_sync:
      enabled: false #Глобально вкл/выкл синхронизацию данных. По умолчанию false.
      schedulers:
        activity_log:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #Мод для работы планировщика. NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика (Начальная  выгрузка)
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true
          limit: 100 #Ограничение на выгрузку элементов из whmcs за запрос. По умолчанию 100.
          skip_batch_error: false #Пропускать пакет с ошибкой и продолжать выгрузку. По умолчанию false.
          forbidden_description_patterns: #Пропускать элементы, поле description которых совпадает с одним паттерном из списка
            - ''
            - ''
            - ''
          max_age_years: 3 #Обрабатывать элементы не старше указанного количества лет. П умолчанию 3.

        affiliate:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true
          limit: 25   #Ограничение на выгрузку элементов из whmcs за запрос. По умолчанию 25.
          skip_batch_error: false #Пропускать пакет с ошибкой и продолжать выгрузку. По умолчанию false.

        client_details:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true
          client_limit: 100 #Количество выгружаемых клиентов из бд за один оборот задачи. По умолчанию 25.
          skip_batch_error: false #Пропускать пакет с ошибкой и продолжать выгрузку. По умолчанию false.
          concurrency: 20 #Параллельное количество запросов в whmcs. По умолчанию не задано - настраивается явно (в примере 20).

        client_group:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true

        client_product:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true
          limit: 25 #Ограничение на выгрузку элементов из whmcs за запрос. По умолчанию 25.
          skip_batch_error: false #Пропускать пакет с ошибкой и продолжать выгрузку. По умолчанию false.

        client:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true
          limit: 25 #Ограничение на выгрузку элементов из whmcs за запрос. По умолчанию 25.
          skip_batch_error: false #Пропускать пакет с ошибкой и продолжать выгрузку. По умолчанию false.

        contact:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true
          limit: 25 #Ограничение на выгрузку элементов из whmcs за запрос. По умолчанию 25.
          skip_batch_error: false #Пропускать пакет с ошибкой и продолжать выгрузку. По умолчанию false.

        contact_delete:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          limit: 25 #Ограничение на выгрузку элементов из whmcs за запрос. По умолчанию 25.

        credit:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true
          client_limit: 25 #Количество выгружаемых клиентов из бд за один оборот задачи. По умолчанию 25.
          skip_batch_error: false #Пропускать пакет с ошибкой и продолжать выгрузку. По умолчанию false.
          max_age_years: 3 #Обрабатывать элементы не старше указанного количества лет. По умолчанию 3
          max_client_credit_per_task: 1000 #Максимальное количество элементов которое можно подгрузить из whmcs за оборот задачи. По умолчанию 1000.

        email:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true
          client_limit: 15 #Количество выгружаемых клиентов из бд за один оборот задачи. По умолчанию 15
          limit: 25 #Ограничение на выгрузку элементов из whmcs за запрос. По умолчанию 25.
          skip_batch_error: false #Пропускать пакет с ошибкой и продолжать выгрузку. По умолчанию false.
          max_age_years: 3 #Обрабатывать элементы не старше указанного количества лет. По умолчанию 3
          max_client_emails_per_task: 1000 #Максимальное количество элементов которое можно подгрузить из whmcs за оборот задачи. По умолчанию 1000.
          mask_body_message_patterns: #Список регулярных выражений, которые маскируют совпавшие значения в теле письма.
            - ''
            - ''

        invoice:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true
          limit: 25 #Ограничение на выгрузку элементов из whmcs за запрос. По умолчанию 25.
          skip_batch_error: true #Пропускать пакет с ошибкой и продолжать выгрузку. По умолчанию false.
          max_age_years: 3 #Обрабатывать элементы не старше указанного количества лет. По умолчанию 3

        order:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true
          limit: 25 #Ограничение на выгрузку элементов из whmcs за запрос. По умолчанию 25.
          skip_batch_error: false #Пропускать пакет с ошибкой и продолжать выгрузку. По умолчанию false.
          max_age_years: 3 #Обрабатывать элементы не старше указанного количества лет. По умолчанию 3

        payment_method:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true

        product:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true

        user:
          enabled: true #Вкл/выкл планировщик. По умолчанию false.
          mode: 'NORMAL'  #NORMAL, FORCE_FIRST, ONLY_FIRST (NORMAL - по умолчанию)
          cron_expression: '' #Крон выражение для периодических задач планировщика (после начальной выгрузки)
          first_start: #Самый первый запуск планировщика
            delays: [ 100, 200, 500, 1000, 10000, 50000, 100000 ] #Повторы при ошибке. Количество элементов - количество попыток, значение элемента - миллисекунды ожидания. По умолчанию [1000, 5000, 10000, 20000, 60000, 120000]
            continue_after_error: true #Запускать периодические задачи если начальная выгрузка оказалась неуспешной. По умолчанию true
          limit: 25 #Ограничение на выгрузку элементов из whmcs за запрос. По умолчанию 25.
          skip_batch_error: false #Пропускать пакет с ошибкой и продолжать выгрузку. По умолчанию false.
```