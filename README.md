# Collaboration Service

This project is a Spring Boot-based microservice for managing study groups within the StudyBuddy platform. It provides functionality for creating, joining, and managing study groups, as well as real-time messaging and file sharing.

## Project Overview

*   **Purpose:** To facilitate collaboration among students by enabling them to create and join study groups, chat in real-time, and share resources.
*   **Key Features:**
    *   **Group Management:** Create, join, and view study groups.
    *   **Real-time Chat:** Send and receive messages instantly using WebSockets.
    *   **File Sharing:** Upload and share files (notes, attachments) within groups.
    *   **Search:** Search for group notes and messages.
    *   **Authentication:** Simple header-based authentication (`X-User-Id`).

## Technologies

*   **Java 25**
*   **Spring Boot 3.5.8**
*   **Gradle**
*   **MySQL** (Database)
*   **Flyway** (Database Migrations)
*   **MinIO** (Object Storage for files)
*   **RabbitMQ** (Message Broker)
*   **Spring Cloud Config** (Centralized Configuration)
*   **Spring WebSocket**
*   **Lombok**

## Architecture

The project follows a standard Spring Boot layered architecture:
*   **Controller Layer:** Handles HTTP requests and WebSocket messages.
*   **Service Layer:** Contains business logic.
*   **Repository Layer:** Manages data persistence using JDBC and Flyway.
*   **Entities:** Represents the data model.

## Building and Running

### Prerequisites

*   JDK 25
*   Docker (for running MySQL, MinIO, and RabbitMQ)

### Running the application

1.  **Start the infrastructure:**
    Ensure you have a `docker-compose.yml` file (or equivalent) to start MySQL, MinIO, and RabbitMQ.
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

## Configuration

The application is configured via `src/main/resources/application.yml`. Key default settings include:

*   **Server Port:** `8082`
*   **Application Name:** `collaboration-service`
*   **Database:**
    *   URL: `jdbc:mysql://localhost:3306/collaboration_service`
    *   Username: `mr.mime`
    *   Password: `StudyBuddy*123`
*   **RabbitMQ:**
    *   Host: `localhost`
    *   Port: `5672`
*   **Config Server:**
    *   URL: `http://localhost:8888` (Optional)
*   **Actuator Endpoints:** `health`, `info`, `refresh`

## Authentication

The service uses a simple header-based authentication mechanism. All protected endpoints require the following header:

*   **Header Name:** `X-User-Id`
*   **Value:** The UUID of the authenticated user.

**Example:**
```http
X-User-Id: 123e4567-e89b-12d3-a456-426614174000
```

## API Documentation

The API is documented using Postman. A Postman collection is included in the project root: `collaboration_service.postman_collection.json`.

### Key Endpoints

**Base URL:** `/api/v1`

*   **Groups:**
    *   `POST /api/v1/groups`: Create a new group. (Requires `X-User-Id`)
    *   `POST /api/v1/groups/{groupId}/join`: Join an existing group. (Requires `X-User-Id`)
    *   `GET /api/v1/groups/{groupId}`: Get group details.
    *   `GET /api/v1/groups/me`: Get groups the current user belongs to. (Requires `X-User-Id`)
    *   `GET /api/v1/groups/all`: List all groups.
    *   `GET /api/v1/groups/{groupId}/members`: Get members of a group.

*   **Messages:**
    *   `GET /api/v1/groups/{groupId}/messages`: Get messages for a group.
    *   `POST /api/v1/groups/{groupId}/messages`: Send a message to a group. (Requires `X-User-Id`)
    *   `GET /api/v1/groups/{groupId}/messages/since`: Fetch messages since a specific timestamp.

*   **Notes:**
    *   `POST /api/v1/groups/{groupId}/notes`: Upload a note to a group. (Requires `X-User-Id`)
    *   `GET /api/v1/groups/{groupId}/notes`: Search/List notes in a group.

*   **Files:**
    *   `POST /api/v1/files/upload`: Upload a file.
    *   `POST /api/v1/files/presign/file`: Get a presigned URL for user file upload. (Requires `X-User-Id`)
    *   `POST /api/v1/files/presign/attachment`: Get a presigned URL for message attachment.
    *   `POST /api/v1/files/presign/note`: Get a presigned URL for note attachment.

### WebSockets

*   **Endpoint:** `/ws` (or configured WebSocket endpoint)
*   **Topics:**
    *   `/topic/groups/{groupId}/events`: Subscribe to receive group events (messages, typing, etc.).
*   **Destinations:**
    *   `/app/chat.send.{groupId}`: Send a message. (Requires `X-User-Id` header in STOMP frame)
    *   `/app/chat.typing.{groupId}`: Send typing indicator. (Requires `X-User-Id` header in STOMP frame)
    *   `/app/chat.edit.{groupId}`: Edit a message. (Requires `X-User-Id` header in STOMP frame)
    *   `/app/chat.delete.{groupId}`: Delete a message. (Requires `X-User-Id` header in STOMP frame)

## Development Conventions

*   **Database Migrations:** Database schema changes are managed using Flyway. Migration scripts are located in `src/main/resources/db/migration`.
*   **Testing:** The project uses JUnit 5 for testing. Run tests with:
    ```bash
    ./gradlew test
    ```
