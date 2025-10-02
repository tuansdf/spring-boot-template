# Spring Boot Backend Template

This project is a comprehensive and feature-rich backend template built with Spring Boot. It provides a solid foundation for building modern, secure, and scalable web applications, allowing developers to focus on business logic rather than boilerplate setup.

## Overview

This template is designed with a modular architecture, separating concerns into distinct feature modules. It comes pre-configured with a robust security implementation, common application utilities, and integrations with popular services.

## Features

- **Authentication:**
  - **JWT (JSON Web Token):** Secure, stateless authentication.
  - **OAuth2:** Support for social logins.
  - **Role-Based Access Control (RBAC):** Manage user permissions with a flexible User/Role/Permission model.
- **Modular Design:** Code is organized by feature (e.g., `user`, `notification`, `file`, `email`), making it easy to navigate, maintain, and extend.
- **Asynchronous Processing:**
  - **Background Tasks:** Offload long-running operations.
  - **Scheduled Jobs:** Execute tasks on a recurring schedule.
- **Database:**
  - **Spring Data JPA:** For streamlined data persistence.
  - **SQL Initialization:** `import.sql` for seeding initial data.
- **API Ready:**
  - RESTful API structure with controllers for each module.
  - Includes an `httprequests` directory with sample requests for easy API testing.
- **Integrations:**
  - **Cloud Services:** Pre-configured for AWS and Firebase.
  - **Caching:** Redis integration for improved performance.
- **Utilities:** A rich set of helpers for file handling (CSV, Excel), date manipulation, validation, and more.
- **Internationalization (i18n):** Built-in support for multiple languages.
- **Containerization:** Docker-ready with an included `Dockerfile` for easy deployment.

## Technologies Used

- **Java**
- **Spring Boot**
- **Gradle**
- **Spring Security** (JWT + OAuth2)
- **Spring Data JPA**
- **PostgreSQL** (or any other JPA-compatible database)
- **Redis**
- **Docker**

## Project Structure

The project follows a logical and layered architecture:

```
src/main/java/com/example/sbt/
├── common/         # Core utilities, DTOs, and constants
├── infrastructure/ # Cross-cutting concerns: config, exceptions, filters, helpers
└── module/         # Main business logic, organized by feature
    ├── auth/
    ├── user/
    ├── role/
    ├── permission/
    ├── notification/
    └── ...
```

## Getting Started

### Prerequisites

- JDK 21 or later
- Gradle 8.0 or later
- Docker (optional, for containerized deployment)

### Configuration

1.  Copy the example environment file `src/main/resources/.env.example` to `src/main/resources/.env`.
2.  Update the `.env` file with your database credentials, JWT secret, and other environment-specific configurations.
3.  Configure your database settings in `src/main/resources/application.properties`.

### Running the Application

You can run the application using the Gradle wrapper:

```bash
./gradlew bootRun
```

The application will start on the port configured in `application.properties` (default is 8080).

### Building for Production

To build a production-ready JAR file, run:

```bash
./gradlew build
```

The executable JAR will be located in the `build/libs/` directory.

### Running with Docker

1.  **Build the Docker image:**
    ```bash
    docker build -t spring-boot-template .
    ```

2.  **Run the Docker container:**
    ```bash
    docker run -p 8080:8080 spring-boot-template
    ```

## API Endpoints

The API endpoints are defined in the `controller` package of each module. You can find example requests in the `httprequests/` directory, which can be used with tools like IntelliJ IDEA's HTTP Client or Postman.
