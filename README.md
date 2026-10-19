# 🤖 Intelligent Support Platform Backend

Intelligent Support Platform Backend is a Spring Boot REST API that powers an intelligent ticketing and support system. It handles authenticated workflows, AI-powered ticket analysis, attachment management, statistical reporting, and role-based access control for support operations.

## ✨ Key Features

- JWT authentication with access and refresh tokens.
- Password reset flow backed by time-limited tokens and HTML emails.
- Role-based access control for ADMIN and USER roles.
- Comprehensive ticket management with status, source, category, and priority tracking.
- AI-powered ticket analysis including sentiment analysis, keyword extraction, and confidence scoring.
- File attachment support for tickets.
- Statistical reporting and analytics for ticket insights.
- User profile management with role-based visibility.
- Swagger/OpenAPI documentation.
- Standardized API responses through a shared ApiResponse wrapper.
- Pagination and filtering support for list endpoints.

## 🧰 Technology Stack

- Java 17
- Spring Boot 4.1.0
- Spring WebMVC
- Spring Security
- Spring Data JPA / Hibernate
- Spring Validation
- Spring Mail + Thymeleaf
- PostgreSQL
- JWT with jjwt (0.12.6)
- OpenAPI / Swagger UI (Springdoc 2.7.0)
- Maven Wrapper
- Docker
- Lombok

## 🏗️ Architecture

The project is organized into the following layers:

- **controller**: REST API entry points with Swagger documentation.
- **service**: business logic and orchestration.
- **security**: JWT authentication, password reset, and mail services.
- **repository**: JPA persistence layer.
- **model**: JPA entities and enums.
- **dto**: request and response contracts.
- **exception**: domain and security exception handling.
- **config**: OpenAPI and application configuration.
- **util**: shared helpers and utilities.

## 💼 Main Business Modules

### 🔐 Authentication and Security

- Login with email and password.
- Access and refresh token lifecycle management.
- Password reset by email with short-lived tokens.
- Role-based access control (ADMIN, USER).
- CORS configured for local front-end development.
- Password reset flow with HTML email templates.

### 🎫 Ticket Management

- Create, update, view, and delete tickets.
- Ticket attributes: title, description, status, source, category, priority.
- Ticket statuses: OPEN, IN_PROGRESS, RESOLVED, CLOSED.
- Ticket sources: EMAIL, WEB, PHONE, CHAT.
- Ticket categories: TECHNICAL, BILLING, GENERAL, OTHER.
- Ticket priorities: LOW, MEDIUM, HIGH, URGENT.
- Filter and search tickets by title, status, source, category, and priority.
- Pagination support for ticket listings.
- User can only access their own tickets; admins can access all tickets.

### 🤖 AI Analysis

- AI-powered analysis for each ticket.
- Automatic ticket categorization and priority assignment.
- Sentiment analysis for understanding user emotion.
- Keyword extraction for quick insights.
- Confidence scoring for analysis reliability.
- Actionable advice generation for support agents.
- Create, update, and delete analyses linked to tickets.

### 📎 Attachment Management

- Upload and manage file attachments for tickets.
- Attachments support various file types for ticket context.
- One-to-many relationship between tickets and attachments.

### 📊 Statistics and Analytics

- Comprehensive ticket statistics dashboard.
- Ticket counts by status, source, category, and priority.
- Time-based metrics for trends and patterns.
- Admin-only access to statistical insights.

### 👤 User Management

- User profile management with name, email, phone, and profile photo.
- Role assignment (ADMIN, USER).
- Admin can view all users with filters and pagination.
- Users can view and update their own profile.
- Admin can delete users.

## 👥 Roles and Access Rules

- **ADMIN**: Full system access, ticket management, user management, statistics access, and platform administration.
- **USER**: Create and manage their own tickets, access AI analysis, upload attachments, view and update their profile.

## 🌐 API Surface

The main endpoints are grouped as follows:

- Authentication: `/auth/*`
- Users: `/users`
- Tickets: `/tickets`
- AI Analysis: `/tickets/{ticketId}/analyses`
- Attachments: `/tickets/{ticketId}/attachments`
- Statistics: `/statistics`
- Health check: `/api/health`
- OpenAPI docs: `/swagger-ui/index.html`

### 📋 Key Endpoints

#### Authentication
- `POST /auth/login` - User login with email and password
- `POST /auth/register` - User registration (admin-only)
- `POST /auth/refresh-token` - Refresh access token
- `POST /auth/forgot-password` - Request password reset
- `POST /auth/reset-password` - Reset password with token

#### Users
- `GET /users` - Get all users (admin-only, with filters and pagination)
- `GET /users/me` - Get current user profile
- `PUT /users/me` - Update current user profile
- `DELETE /users/{userId}` - Delete user (admin-only)

#### Tickets
- `POST /tickets` - Create a new ticket
- `GET /tickets/my-tickets` - Get current user's tickets (with filters and pagination)
- `GET /tickets/all` - Get all tickets (admin-only, with filters and pagination)
- `GET /tickets/{ticketId}` - Get ticket by ID
- `PUT /tickets/{ticketId}` - Update ticket
- `DELETE /tickets/{ticketId}` - Delete ticket

#### AI Analysis
- `POST /tickets/{ticketId}/analyses` - Create AI analysis for a ticket
- `PUT /tickets/{ticketId}/analyses/{analysisId}` - Update AI analysis
- `DELETE /tickets/{ticketId}/analyses/{analysisId}` - Delete AI analysis

#### Attachments
- `POST /tickets/{ticketId}/attachments` - Upload attachment for a ticket
- `GET /tickets/{ticketId}/attachments/{attachmentId}` - Download attachment
- `DELETE /tickets/{ticketId}/attachments/{attachmentId}` - Delete attachment

#### Statistics
- `GET /statistics/tickets` - Get comprehensive ticket statistics

## 🧾 API Response Format

Most REST endpoints return a unified response structure:

```json
{
  "message": "Operation completed successfully",
  "data": {
    "...": "..."
  }
}
```

Paginated responses follow this structure:

```json
{
  "message": "Data retrieved successfully",
  "data": {
    "content": [...],
    "totalElements": 100,
    "totalPages": 10,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

## ✅ Prerequisites

- JDK 17
- Maven Wrapper, included in the repository
- PostgreSQL database
- SMTP credentials for transactional emails
- A front-end URL for password reset links

## ⚙️ Configuration

The application reads its configuration from `src/main/resources/application.properties`. Replace the sample values below with your own environment settings.

```properties
spring.application.name=intelligent-support-platform-backend
server.port=8081

spring.datasource.url=jdbc:postgresql://<host>:<port>/<database>
spring.datasource.username=<db-user>
spring.datasource.password=<db-password>
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

application.security.jwt.secret-key=<base64-secret>
application.security.jwt.expiration=86400000
application.security.jwt.refresh-token.expiration=604800000
application.security.password-reset.token-expiration-minutes=15
application.frontend.url=http://localhost:4200

application.mail.from=<sender@example.com>
application.mail.brand-name=Intelligent Support Platform
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<smtp-user>
spring.mail.password=<smtp-app-password>
```

Notes:

- Update the CORS whitelist in SecurityConfig if your front-end URL changes.
- The password reset link is generated from `application.frontend.url`.
- HikariCP connection pool is configured with optimized settings for production use.

## 🚀 Run Locally

### Clone the repository

```bash
git clone https://github.com/aminejguirim10/intelligent-support-platform-backend.git
cd intelligent-support-platform-backend
```

On Windows:

```bash
mvnw.cmd clean test
mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Then open:

- API: http://localhost:8081
- Health check: http://localhost:8081/api/health
- Swagger UI: http://localhost:8081/swagger-ui/index.html

Important: The first administrator must be seeded manually or created through a controlled bootstrap flow, as registration endpoints are protected by admin-only access.

## 🐳 Docker

The repository includes a multi-stage Dockerfile based on Maven and Java 17.

```bash
docker build -t intelligent-support-platform-backend .
docker run --rm -p 8080:8080 intelligent-support-platform-backend
```

The container listens on port 8080 by default. Adjust the PORT environment variable if you need a different runtime port.

## 📌 Operational Notes

- Hibernate is configured with `ddl-auto=update` for development convenience.
- Swagger is secured with a Bearer JWT security scheme.
- The application exposes a health endpoint for readiness checks.
- All API responses are wrapped in a standardized ApiResponse format.
- Pagination is supported across all list endpoints with configurable page size and sorting.

## 🔒 Security Considerations

- JWT tokens are signed with a secret key configured in application properties.
- Password reset tokens have a configurable expiration time (default: 15 minutes).
- Role-based access control is enforced at the controller level using `@PreAuthorize`.
- User passwords are hashed using Spring Security's default password encoder.
- CORS is configured to allow requests from specific front-end domains.

## 📊 Database Schema

The application uses the following main entities:

- **users**: User accounts with authentication and profile information
- **tickets**: Support tickets with status, source, and metadata
- **ai_analysis**: AI-powered analysis results linked to tickets
- **attachments**: File attachments associated with tickets

## 🤝 Contributing

1. Create a feature branch.
2. Implement your changes following the existing package structure.
3. Run the test suite.
4. Open a pull request with a clear description of the change.

## 👥 Collaborators

- Mohamed Amine Jguirim
- Fares Tekaya
- Firas Hizem

## 📄 License

This project is part of the Intelligent Support Platform internship project.

## 🔗 Related Projects

- [Frontend Application](https://github.com/aminejguirim10/intelligent-support-platform-frontend) - Angular web application
- [AI Service](https://github.com/aminejguirim10/intelligent-support-platform-ai) - FastAPI AI Analysis Service
