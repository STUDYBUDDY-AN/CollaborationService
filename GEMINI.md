# Collaboration Service

This project is a Spring Boot-based microservice for managing study groups.

## Project Overview

*   **Purpose:** To provide functionality for creating, joining, and managing study groups.
*   **Technologies:**
    *   Java 25
    *   Spring Boot 3.5.8
    *   Gradle
    *   MySQL
    *   Flyway for database migrations
    *   MinIO for object storage
    *   Lombok
*   **Architecture:** The project follows a standard Spring Boot application structure. It exposes a REST API for managing study groups, with data persisted in a MySQL database and files stored in MinIO.

## Building and Running

### Prerequisites

*   JDK 25
*   Docker (for running the MySQL database and MinIO)

### Running the application

1.  **Start the database and MinIO:**
    ```bash
    docker-compose up -d
    ```

2.  **Run the application:**
    ```bash
    ./gradlew bootRun
    ```

The application will be available at `http://localhost:8082`.

### Building the application

To build the application into a JAR file, run:

```bash
./gradlew build
```

## Development Conventions

*   **Database Migrations:** Database schema changes are managed using Flyway. Migration scripts are located in `src/main/resources/db/migration`.
*   **Testing:** The project uses JUnit 5 for testing. Tests are located in the `src/test` directory. Run tests with:
    ```bash
    ./gradlew test
    ```
*   **API:** The API is documented via the controller endpoints in `src/main/java/com/studybuddy/collaboration_service/groups/controller/GroupController.java`.

## Key Files

*   `build.gradle`: Defines project dependencies and build configuration.
*   `docker-compose.yml`: Defines the MySQL and MinIO services.
*   `src/main/java/com/studybuddy/collaboration_service/CollaborationServiceApplication.java`: The main Spring Boot application class.
*   `src/main/resources/application.yml`: Application configuration (e.g., database connection details).
*   `src/main/resources/db/migration/V1__create_collaboration_tables.sql`: The initial database schema.
*   `src/main/java/com/studybuddy/collaboration_service/groups/controller/GroupController.java`: The REST controller for managing groups.
*   `src/main/java/com/studybuddy/collaboration_service/groups/service/GroupService.java`: The service layer containing the business logic for group management.
