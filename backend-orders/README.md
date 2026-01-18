# Lite Thinking Test - Juan Pablo Rivadeneira

Este proyecto es un microservicio para la gestión de pedidos (Orders), desarrollado como parte de una prueba técnica. Implementa una arquitectura robusta utilizando las últimas versiones de Java y Spring Boot.

## Tecnologías Utilizadas

*   **Java 25** (OpenJDK)
*   **Spring Boot 4.0.1**
*   **Spring Data JPA** (Hibernate 7.2.0)
*   **Spring Security** (con soporte para JWT)
*   **Flyway** (Gestión de migraciones de base de datos)
*   **Microsoft SQL Server** (Base de datos principal)
*   **Docker & Docker Compose** (Orquestación de contenedores)
*   **Lombok** (Reducción de código boilerplate)
*   **GraalVM Native Image** (Soporte para compilación nativa)

## Arquitectura

El proyecto sigue un enfoque de **Arquitectura Hexagonal/Limpia**, separando las responsabilidades en las siguientes capas:

*   **`domain`**: Contiene las entidades de negocio, interfaces de repositorio y lógica core (independiente de frameworks).
*   **`application`**: Casos de uso y servicios que coordinan la lógica de negocio.
*   **`infrastructure`**: Implementaciones técnicas (REST Controllers, JPA Repositories, Configuración de Seguridad, Adaptadores externos).
*   **`config`**: Configuraciones generales de Spring.

## Requisitos Previos

*   Docker y Docker Compose instalados.
*   Java 25 (si deseas ejecutarlo localmente sin Docker).
*   Gradle (opcional, incluido vía `gradlew`).

## Configuración y Ejecución

### 1. Clonar el repositorio
```bash
git clone <url-del-repositorio>
cd backend-orders
```

### 2. Variables de Entorno

Copia el archivo de ejemplo y ajusta las credenciales si es necesario:

```bash
cp .env.example .env
```

### 3. Ejecutar con Docker Compose

Este comando levantará la base de datos SQL Server y la aplicación automáticamente:

```bash
docker-compose up --build
```
Si deseas usar Spring Boot Docker Compose Support (ejecutando solo la BD en Docker y el app local):

- Levanta solo la base de datos:
```bash
docker-compose up sqlserver
```
- Ejecuta el app:
```bash
./gradlew bootRun
```

### 4. Compilación nativa con GraalVM

Para compilar el proyecto en una imagen nativa utilizando GraalVM, asegúrate de tener GraalVM instalado y configurado correctamente. Luego, ejecuta:

```bash
./gradlew nativeImage
```

Esto generará una imagen nativa en el directorio `build/native-image`.

## Seguridad y Autenticación

El sistema utiliza JSON Web Tokens (JWT) para la seguridad.

- Los endpoints bajo `/api/v1/auth` son públicos.
- El resto de los endpoints requieren el header: `Authorization: Bearer <token>`.

## Estructura de Endpoints Principales

- Auth: POST /api/v1/auth/login, POST /api/v1/auth/register
- Orders: GET/POST /api/v1/orders
- Clients: GET/POST /api/v1/clients
- Products: GET/POST /api/v1/products
- Companies: GET/POST /api/v1/companies
- Categories: GET/POST /api/v1/categories

## Pruebas

Para ejecutar la suite de pruebas (JUnit 5 + Testcontainers):

```bash
./gradlew test
```

El proyecto utiliza Testcontainers para levantar una instancia efímera de SQL Server durante los tests integrales.

## Dockerización

El proyecto está configurado para ser fácilmente desplegado con Docker. Puedes construir y ejecutar la imagen Docker con los siguientes comandos:

```bash
docker build -t backend-orders .
docker run -p 8080:8080 backend-orders
```

Esto levantará la aplicación en el puerto 8080.

## Base de datos

@startuml Database_Schema

!define primary_key(x) <b><color:#b8861b><&key></color> x</b>
!define foreign_key(x) <color:#aaaaaa><&key></color> x
!define column(x) <color:#efefef><&media-record></color> x
!define table(x) entity x << (T, white) >>

entity "companies" as companies {
primary_key(id): UNIQUEIDENTIFIER
--
column(name): NVARCHAR(255) NOT NULL
column(tax_id): NVARCHAR(50) NOT NULL UNIQUE
column(address): NVARCHAR(500)
column(phone): NVARCHAR(50)
column(created_at): DATETIME2
}

entity "users" as users {
primary_key(id): UNIQUEIDENTIFIER
--
column(email): NVARCHAR(255) NOT NULL UNIQUE
column(password_hash): NVARCHAR(255) NOT NULL
column(full_name): NVARCHAR(255) NOT NULL
column(phone): NVARCHAR(50)
column(address): NVARCHAR(500)
column(role): NVARCHAR(50) NOT NULL
column(created_at): DATETIME2
}

entity "categories" as categories {
primary_key(id): UNIQUEIDENTIFIER
--
foreign_key(company_id): UNIQUEIDENTIFIER NOT NULL
column(name): NVARCHAR(100) NOT NULL
column(description): NVARCHAR(500)
}

entity "clients" as clients {
primary_key(id): UNIQUEIDENTIFIER
--
foreign_key(company_id): UNIQUEIDENTIFIER NOT NULL
column(name): NVARCHAR(255) NOT NULL
column(email): NVARCHAR(255)
column(phone): NVARCHAR(50)
column(address): NVARCHAR(500)
column(created_at): DATETIME2
}

entity "products" as products {
primary_key(id): UNIQUEIDENTIFIER
--
foreign_key(company_id): UNIQUEIDENTIFIER NOT NULL
foreign_key(category_id): UNIQUEIDENTIFIER
column(name): NVARCHAR(255) NOT NULL
column(sku): NVARCHAR(100) NOT NULL
column(description): NVARCHAR(MAX)
column(created_at): DATETIME2
}

entity "inventory" as inventory {
primary_key(id): UNIQUEIDENTIFIER
--
foreign_key(product_id): UNIQUEIDENTIFIER NOT NULL
column(quantity): INT NOT NULL
column(last_updated): DATETIME2
}

entity "product_prices" as product_prices {
primary_key(id): UNIQUEIDENTIFIER
--
foreign_key(product_id): UNIQUEIDENTIFIER NOT NULL
column(currency_code): NVARCHAR(3) NOT NULL
column(price): DECIMAL(18, 2) NOT NULL
}

entity "orders" as orders {
primary_key(id): UNIQUEIDENTIFIER
--
foreign_key(company_id): UNIQUEIDENTIFIER NOT NULL
foreign_key(client_id): UNIQUEIDENTIFIER NOT NULL
column(client_name): NVARCHAR(255) NOT NULL
column(address): NVARCHAR(500) NOT NULL
column(order_date): DATETIME2 NOT NULL
column(status): NVARCHAR(50) NOT NULL
column(currency_code): NVARCHAR(3) NOT NULL
column(total_amount): DECIMAL(18, 2) NOT NULL
}

entity "order_items" as order_items {
primary_key(id): UNIQUEIDENTIFIER
--
foreign_key(order_id): UNIQUEIDENTIFIER NOT NULL
foreign_key(product_id): UNIQUEIDENTIFIER NOT NULL
column(product_name): NVARCHAR(255) NOT NULL
column(quantity): INT NOT NULL
column(unit_price): DECIMAL(18, 2) NOT NULL
}

' Relationships
companies ||--o{ categories : "has"
companies ||--o{ clients : "has"
companies ||--o{ products : "has"
companies ||--o{ orders : "has"

categories ||--o{ products : "categorizes"

products ||--|| inventory : "has"
products ||--o{ product_prices : "has"
products ||--o{ order_items : "contains"

clients ||--o{ orders : "places"

orders ||--o{ order_items : "contains"

@enduml