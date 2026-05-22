# PG CRUD with Indexing

A comprehensive CRUD application built with Spring Boot, PostgreSQL, Redis, JWT authentication, and activity logging.

## Features

- **Database**: PostgreSQL with optimized indexing
- **Caching**: Redis for performance optimization
- **Authentication**: JWT-based authentication with refresh tokens
- **Authorization**: Role-based access control (ADMIN/USER)
- **Activity Logging**: Comprehensive audit trail for all operations
- **API Documentation**: Swagger/OpenAPI 3.0 documentation
- **Validation**: Input validation and error handling
- **Security**: CORS configuration and security headers

## Architecture

### Entities
1. **Users** - User management with status tracking
2. **Orders** - Order management with status workflow
3. **Activity Log** - Audit trail for all system activities

### API Endpoints

#### Authentication (`/api/auth`)
- `POST /generate-token` - Generate JWT token
- `POST /validate-token` - Validate JWT token
- `POST /refresh-token` - Refresh JWT token
- `POST /logout` - User logout
- `POST /register` - User registration

#### Users (`/api/users`)
- `POST /` - Create user (ADMIN only)
- `GET /` - Get all users (ADMIN only)
- `GET /{id}` - Get user by ID
- `GET /username/{username}` - Get user by username
- `PUT /{id}` - Update user
- `DELETE /{id}` - Delete user (ADMIN only)
- `GET /search` - Search users (ADMIN only)
- `GET /status/{status}` - Get users by status (ADMIN only)
- `GET /count` - Get user count (ADMIN only)
- `GET /count/status/{status}` - Get user count by status (ADMIN only)

#### Orders (`/api/orders`)
- `POST /` - Create order
- `GET /` - Get all orders (ADMIN only)
- `GET /{id}` - Get order by ID
- `GET /number/{orderNumber}` - Get order by order number
- `GET /user/{userId}` - Get orders by user ID
- `PUT /{id}` - Update order
- `DELETE /{id}` - Delete order (ADMIN only)
- `GET /search` - Search orders (ADMIN only)
- `GET /status/{status}` - Get orders by status (ADMIN only)
- `GET /date-range` - Get orders by date range (ADMIN only)
- `GET /amount-range` - Get orders by amount range (ADMIN only)
- `GET /count` - Get order count (ADMIN only)
- `GET /count/status/{status}` - Get order count by status (ADMIN only)
- `GET /revenue/status/{status}` - Get total revenue by status (ADMIN only)

#### Activity Log (`/api/activity-log`)
- `GET /` - Get all activity logs (ADMIN only)
- `GET /{id}` - Get activity log by ID (ADMIN only)
- `GET /user/{userId}` - Get activity logs by user ID
- `GET /username/{username}` - Get activity logs by username
- `GET /action/{action}` - Get activity logs by action (ADMIN only)
- `GET /entity-type/{entityType}` - Get activity logs by entity type (ADMIN only)
- `GET /search` - Search activity logs (ADMIN only)
- `GET /filter` - Filter activity logs (ADMIN only)
- `GET /export` - Export activity logs (ADMIN only)
- `GET /count` - Get activity log count (ADMIN only)
- `GET /count/action/{action}` - Get activity log count by action (ADMIN only)
- `GET /count/entity-type/{entityType}` - Get activity log count by entity type (ADMIN only)

## Database Schema

### Users Table
- `id` (Primary Key, Auto-increment)
- `username` (Unique, Indexed)
- `email` (Unique, Indexed)
- `password` (Hashed)
- `first_name`
- `last_name`
- `phone_number`
- `status` (ACTIVE, INACTIVE, SUSPENDED)
- `created_at` (Indexed)
- `updated_at`

### Orders Table
- `id` (Primary Key, Auto-increment)
- `order_number` (Unique, Indexed)
- `user_id` (Foreign Key, Indexed)
- `product_name`
- `quantity`
- `unit_price`
- `total_amount` (Indexed)
- `status` (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED, REFUNDED, Indexed)
- `shipping_address`
- `billing_address`
- `notes`
- `created_at` (Indexed)
- `updated_at`
- `shipped_at`
- `delivered_at`

### Activity Log Table
- `id` (Primary Key, Auto-increment)
- `user_id` (Indexed)
- `username`
- `action` (CREATE, READ, UPDATE, DELETE, LOGIN, LOGOUT, SEARCH, EXPORT, FILTER, Indexed)
- `entity_type` (USER, ORDER, ACTIVITY_LOG, AUTH, Indexed)
- `entity_id`
- `description`
- `details`
- `ip_address` (Indexed)
- `user_agent`
- `created_at` (Indexed)

## Setup and Installation

### Prerequisites
- Java 21+
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+

### Database Setup
1. Create PostgreSQL database:
```sql
CREATE DATABASE pg_crud_db;
```

2. Create user and grant permissions:
```sql
CREATE USER postgres WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE pg_crud_db TO postgres;
```

### Redis Setup
1. Install Redis and start the server:
```bash
redis-server
```

### Application Setup
1. Clone the repository
2. Navigate to the project directory
3. Update application properties if needed
4. Run the application:
```bash
mvn spring-boot:run
```

### Docker Setup

#### Development Environment
```bash
# Start all services (PostgreSQL, Redis, App, Nginx)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Clean up volumes
docker-compose down -v
```

#### Production Environment
```bash
# Copy environment file
cp .env.example .env
# Edit .env with your production values

# Start production services
docker-compose -f docker-compose.prod.yml up -d

# Start with monitoring
docker-compose -f docker-compose.prod.yml --profile monitoring up -d
```

#### Individual Service Management
```bash
# Build only the application
docker build -t pg-crud-app .

# Run only PostgreSQL
docker run -d --name postgres -e POSTGRES_DB=pg_crud_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password -p 5432:5432 postgres:15

# Run only Redis
docker run -d --name redis -p 6379:6379 redis:7-alpine

# Run application with external services
docker run -d --name app -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/pg_crud_db -e SPRING_DATA_REDIS_HOST=host.docker.internal pg-crud-app
```

## Configuration

### Application Properties
Key configuration options in `src/main/resources/application.properties`:

- Database connection settings
- Redis connection settings
- JWT secret and expiration times
- Server port and context path
- CORS settings
- Logging levels

### Security Configuration
- JWT-based authentication
- Role-based authorization
- CORS configuration
- Password encryption with BCrypt

## API Documentation

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/api/swagger-ui.html
```

## Usage Examples

### Authentication
```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'

# Generate token
curl -X POST http://localhost:8080/api/auth/generate-token \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

### Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"userId":1,"productName":"Laptop","quantity":1,"unitPrice":999.99}'
```

### Get Activity Logs
```bash
curl -X GET http://localhost:8080/api/activity-log \
  -H "Authorization: Bearer <token>"
```

## Performance Optimization

### Database Indexing
- Optimized indexes on frequently queried columns
- Composite indexes for complex queries
- Indexes on foreign keys and search fields

### Redis Caching
- Session management
- Query result caching
- Token blacklisting

### Connection Pooling
- HikariCP for database connections
- Jedis connection pool for Redis

## Security Features

- JWT authentication with refresh tokens
- Password hashing with BCrypt
- CORS configuration
- Input validation
- SQL injection prevention
- XSS protection

## Monitoring and Logging

- Comprehensive activity logging
- Request/response logging
- Error tracking
- Performance metrics
- Health check endpoints

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

## Deployment

### Production Deployment
1. Update database credentials
2. Configure Redis cluster
3. Set environment variables
4. Deploy as JAR or WAR file
5. Configure reverse proxy (nginx/Apache)

### Docker Deployment
```bash
docker build -t pg-crud-app .
docker run -p 8080:8080 pg-crud-app
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For issues and questions, please create an issue in the repository.
# pg-basic-with-table-indexing
# pg-crud-with-indexing
