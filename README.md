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

