# Employee REST Service

A small Spring Boot API for creating, viewing, updating, and deleting employee
records. The service uses in-memory storage and starts with four fictional
employees. Data resets when the application restarts.

## Requirements

- JDK 25
- Maven

## Run locally

```shell
mvn spring-boot:run
```

The API is available at `http://localhost:8080`.

## Endpoints

| Method | Path | Result |
|---|---|---|
| `GET` | `/employees` | Return all employees |
| `GET` | `/employees/{employeeId}` | Return one employee |
| `POST` | `/employees` | Create an employee (`201 Created`) |
| `PUT` | `/employees/{employeeId}` | Replace an employee |
| `DELETE` | `/employees/{employeeId}` | Delete an employee (`204 No Content`) |

Create and update requests use the following JSON fields:

```json
{
  "employee_id": "employee-5",
  "first_name": "Taylor",
  "last_name": "Reed",
  "email": "taylor.reed@example.com",
  "title": "Platform Engineer"
}
```

`employee_id` is optional when creating an employee; if omitted, the service
assigns the next available numeric ID. Names and title must not be blank, and
email must be valid and unique, ignoring case. Employee responses use the
`employee_id`, `first_name`, and `last_name` field names.

Not-found requests return `404`, duplicate IDs or email addresses return `409`,
and invalid request bodies return `400` with a Problem Details response.

## Tests

Run the unit and integration tests with:

```shell
mvn test
```