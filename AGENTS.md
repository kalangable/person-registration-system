# AGENTS.md - Development Guide for Coding Agents

This guide provides essential information for AI coding agents working on the Person Registration System (Party Model) project.

## Project Overview

- **Language**: Java 21
- **Framework**: Spring Boot 4.0.2
- **Build Tool**: Maven 3.8+
- **Database**: PostgreSQL 16
- **Architecture**: Standard Layered Architecture (Controller → Service → Repository → Model)

## Build Commands

### Compile and Build
```bash
# From root directory
mvn clean install

# From services/foundation directory
cd services/foundation
mvn clean install

# Compile without tests
mvn clean compile -DskipTests

# Package as JAR
mvn clean package -DskipTests
```

### Run Application
```bash
cd services/foundation
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Database Migrations
```bash
cd services/foundation
# Run Liquibase migrations
mvn liquibase:update

# Rollback last changeset
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

## Testing Commands

### Run All Tests
```bash
cd services/foundation
mvn test
```

### Run Single Test Class
```bash
mvn test -Dtest=PersonServiceTest
```

### Run Single Test Method
```bash
mvn test -Dtest=PersonServiceTest#shouldCreatePersonSuccessfully
```

### Run Tests with Coverage
```bash
mvn clean test jacoco:report
# View coverage: target/site/jacoco/index.html
```

### Run Integration Tests Only
```bash
mvn verify -Dgroups=integration
```

## Code Style Guidelines

### Package Structure
```
com.akstack.foundation/
├── config/                      # Spring configurations (JpaAuditingConfig)
├── domain/
│   ├── model/                  # JPA entities (Party, Person, Organization)
│   └── repository/             # Spring Data JPA repositories
├── mapper/                      # MapStruct mappers (PersonMapper, OrganizationMapper)
├── service/
│   ├── exception/              # Business exceptions (ResourceNotFoundException, etc.)
│   ├── validation/             # Strategy pattern validators (IdentificationValidator)
│   └── [Services]              # Business logic services (PersonService, etc.)
├── web/
│   ├── controller/             # REST controllers (PersonController, etc.)
│   ├── dto/                    # Request/Response DTOs
│   └── exception/              # GlobalExceptionHandler
└── FoundationApplication.java
```

### Imports Organization
```java
// 1. Java standard library
import java.time.LocalDate;
import java.util.List;

// 2. Third-party libraries (Jakarta, Spring, etc.)
import jakarta.persistence.*;
import org.springframework.stereotype.Service;

// 3. Project imports (alphabetically by package)
import com.akstack.foundation.domain.model.Person;
import com.akstack.foundation.domain.repository.PersonRepository;
import com.akstack.foundation.mapper.PersonMapper;
import com.akstack.foundation.service.PersonService;
import com.akstack.foundation.service.exception.ResourceNotFoundException;
import com.akstack.foundation.service.validation.IdentificationValidator;
import com.akstack.foundation.web.dto.CreatePersonRequest;
import com.akstack.foundation.web.dto.PersonResponse;
```

### Naming Conventions

**Classes**:
- Entities: `Person`, `Organization`, `Party`
- Services: `PersonService`, `OrganizationService`
- Controllers: `PersonController`, `OrganizationController`
- Repositories: `PersonRepository`, `OrganizationRepository`
- DTOs: `CreatePersonRequest`, `PersonResponse`
- Exceptions: `ResourceNotFoundException`, `InvalidIdentificationException`

**Methods**:
- CRUD operations: `create()`, `findById()`, `findAll()`, `update()`, `delete()`
- Business methods: `activate()`, `deactivate()`, `softDelete()`, `restore()`
- Queries: `findByIdentification()`, `existsByIdentificationAndNotDeleted()`
- Validators: `isValid()`, `supports()`, `format()`

**Variables**: camelCase (`firstName`, `partyNumber`, `createdAt`)

**Constants**: UPPER_SNAKE_CASE (`MAX_NAME_LENGTH`, `DEFAULT_PAGE_SIZE`)

### Type Usage

- Use `Long` for entity IDs (not `long`)
- Use `Boolean` for boolean fields in entities (nullable support)
- Use `LocalDate` for dates, `LocalDateTime` for timestamps
- Use `String` for VARCHAR columns
- Use enums for fixed value sets (`PartyType`, `Gender`)

### Entity Patterns

```java
@Entity
@Table(name = "person")
@PrimaryKeyJoinColumn(name = "id")
public class Person extends Party {
    
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    // Constructor (no-arg for JPA)
    public Person() {
        super(PartyType.PERSON);
    }
    
    // Getters and Setters (explicit, not Lombok in entities)
    // Business methods
}
```

### Service Layer Patterns

```java
@Service
@Transactional
public class PersonService {
    
    private final PersonRepository personRepository;
    
    // Constructor injection (no @Autowired needed)
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    
    @Transactional(readOnly = true)
    public Person findById(Long id) {
        return personRepository.findById(id)
            .filter(person -> !person.getIsDeleted())
            .orElseThrow(() -> new ResourceNotFoundException("Person", id.toString()));
    }
}
```

### Controller Layer Patterns

```java
@RestController
@RequestMapping("/api/v1/persons")
public class PersonController {
    
    private final PersonService personService;
    
    @PostMapping
    public ResponseEntity<PersonResponse> create(@Valid @RequestBody CreatePersonRequest request) {
        // Delegate to service
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

### Error Handling

- Use custom exceptions: `ResourceNotFoundException`, `InvalidIdentificationException`, `DuplicateIdentificationException`
- Global exception handling via `@RestControllerAdvice` in `GlobalExceptionHandler`
- Return `ErrorResponse` with status, message, and path
- Validation errors include field-level details

### Validation

- Use Jakarta Bean Validation in DTOs (`@NotBlank`, `@Size`, etc.)
- Use Strategy Pattern for business validation (e.g., `IdentificationValidator`)
- Validators implement `IdentificationValidator` interface with `supports()` and `isValid()` methods

### Database Conventions

- Use `snake_case` for table and column names
- Entity IDs: BIGINT auto-increment
- Business identifiers: VARCHAR unique (e.g., `party_number`)
- Soft delete: `is_deleted` (BOOLEAN) + `deleted_at` (TIMESTAMP)
- Audit fields: `created_at`, `updated_at` (managed by JPA Auditing)
- Use Liquibase XML changesets for migrations

### Testing Guidelines

- Target: 80% code coverage (70% branch coverage)
- Distribution: 70% unit tests, 20% integration tests, 10% API tests
- Use JUnit 5 + Mockito for unit tests
- Use TestContainers for integration tests (real PostgreSQL)
- Use AssertJ for fluent assertions
- Test naming: `shouldDoSomethingWhenCondition()`

## Important Notes

- **DO NOT** use Lombok for entities (explicit getters/setters for clarity)
- **DO** use constructor injection (no field injection)
- **DO** use soft delete by default (set `is_deleted = true`)
- **DO** validate identification documents using Strategy Pattern
- **DO** generate `party_number` via sequence (starting at 1000000)
- **DO** use `@Transactional(readOnly = true)` for read operations
- **DO** filter out deleted records in repository queries (`WHERE is_deleted = false`)

## Common Tasks

### Adding a New Entity
1. Create entity in `domain/model/` package extending `Party` if applicable
2. Create repository interface in `domain/repository/` package
3. Create service class in `service/` package with `@Service` and `@Transactional`
4. Create DTOs (Request/Response) in `web/dto/` package
5. Create mapper class in `mapper/` package (annotated with `@Component`)
6. Create controller in `web/controller/` package with `@RestController`
7. Add Liquibase migration in `src/main/resources/db/changelog/changes/`

### Adding a New Validator
1. Implement `IdentificationValidator` interface in `service/validation/` package
2. Annotate with `@Component`
3. Implement `supports(String identificationType)` method
4. Implement `isValid(String identificationNumber)` method
5. Register in `IdentificationValidationService` (auto-detected via Spring)

---

**Last Updated**: 2026-02-22  
**Architecture**: Layered Architecture with Domain-Driven Design influences  
**Phase**: FASE 1 - MVP Monolítico
