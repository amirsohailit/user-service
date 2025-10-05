## User Service Microservice

This is a reactive microservice built with **Java 21 + Spring WebFlux** to manage user registration. 
It implements full CRUD operations on user registration data and follows modern microservice best practices, 
including reactive streams, containerization, event publishing with Kafka, and API documentation with OpenAPI.

## Assumptions & Design Decisions
While my first instinct was to use traditional Spring Boot since I have a lot of experience with it,
I decided to go with **Spring WebFlux** to take advantage of its non-blocking and reactive model.
Furthermore, I chose hexagonal architecture (also known as ports and adapters architecture) to ensure a clean separation 
between the core domain logic and external concerns such as databases, messaging systems, and web frameworks
Other options could have been Quarkus or Micronut, but I chose Spring due to its maturity, and strong ecosystem. 
---

##  Features

-  Create, update, delete, and retrieve users
-  Pagination and filtering (e.g., by country)
-  MongoDB as storage
-  Kafka integration to publish user changes as events
-  Integration tests using Testcontainers
-  Unit testing with JUnit 5 and Mockito
-  OpenAPI documentation via Swagger UI
-  Spring Boot Actuator for health checks
-  Dockerized with Docker Compose
-  Centralized error handling with a global REST exception handler (`@RestControllerAdvice`)


---
##  Tech Stack

-  Java 21
-  Spring Boot 3 (WebFlux)
-  MongoDB
-  Apache Kafka
-  Testcontainers
-  Docker & Docker Compose
-  OpenAPI / Swagger
---

##  Getting Started
## Prerequisites
-  Java 21 (for local env)
-  Docker + Docker Compose (recommended), docker must be installed on your machine
## How to Run

To start the application locally using Docker Compose, run:

## bash
-    ./mvnw clean package
-    docker-compose build
-    docker-compose up

This command starts the following services in a dockerized environment:

- Spring Boot WebFlux app → http://localhost:8080

- MongoDB → accessible on localhost:27017

- Kafka + Zookeeper → used for event publishing

Note: For restarting/retesting, it's advised to stop all containers first:
-    docker-compose down


### Health Check
Spring Boot Actuator health endpoint:
 http://localhost:8080/actuator/health

### Testing
For Bruno Collections and example curl commands, please refer to the [`Bruno Collection`](./Bruno Collection) directory.
- Run all tests:
-  ./mvnw verify

### Unit Tests
- Validate business logic and service methods
- Mock repositories and external dependencies


### Integration Tests
- Use Testcontainers to spin up real MongoDB in test container
- End-to-end flow tested with test data

API Documentation
Swagger UI is available at:
http://localhost:8080/swagger-ui.html

### API Endpoints:
| Method | Endpoint | Description                                              |
| ------ | -------- |----------------------------------------------------------|
| POST   | `/`      | Create a new user                                        |
| PUT    | `/{id}`  | Update an existing user by ID                            |
| DELETE | `/{id}`  | Delete a user by ID                                      |
| GET    | `/`      | List users by country or without (paginated, filterable) |
| GET    | `/{id}`  | Get user by ID                                           |


## Future Improvements
To make this service more robust, scalable, and suitable for production-grade deployments, 
the following improvements are recommended:

- Add authentication & authorization (e.g., JWT + OAuth2)
- Usage of Spring Cloud Config for centralized properties storage
- Usage of Vault or cloud provider keyvault for sensitive configurations
- Add support for gRPC for internal service-to-service communication
- Add Prometheus/Grafana monitoring or Dynatrace
- Add Performance Test (e.g Gatling)
- Add retries, fallbacks, circuit breakers (e.g., Resilience4J)
- Container orchestration (e.g., deploy to Kubernetes)
- CI/CD pipeline integration (e.g., GitHub Actions)

### Packages Hierarchy

For a detailed overview of the project’s package structure and architectural organization,  
please refer to [`packages-hierarchy.txt`](./packages-hierarchy.txt).