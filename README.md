# Employee REST Service

A Spring Boot REST API for managing employee records. Data is kept in memory, so
the application starts without a database or credentials and resets to its
sample employees whenever it restarts.

## Run

Requirements: JDK 25 and Maven.

```shell
mvn spring-boot:run
```

The service listens on `http://localhost:8080`.

## API

| Method | Path | Behavior |
|---|---|---|
| `GET` | `/employees` | List employees |
| `GET` | `/employees/{employeeId}` | Get one employee |
| `POST` | `/employees` | Create an employee (`201 Created`) |
| `PUT` | `/employees/{employeeId}` | Replace an employee |
| `DELETE` | `/employees/{employeeId}` | Delete an employee (`204 No Content`) |

Create and update requests require `first_name`, `last_name`, `email`, and `title`.
Names and title cannot be blank, email must be valid, and email addresses must
be unique (case-insensitive). Not-found requests return `404`; duplicate email
requests return `409`; invalid request bodies return `400`.

Example request:

```json
{
  "employee_id": "employee-5",
  "first_name": "Taylor",
  "last_name": "Reed",
  "email": "taylor.reed@example.com",
  "title": "Platform Engineer"
}
```

Employee responses retain the existing `employee_id`, `first_name`, and
`last_name` JSON field names for compatibility.
If `employee_id` is omitted, the service assigns the next available ID.

## Tests

```shell
mvn test
```
