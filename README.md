# PayrollTax Pro API

Multi-Company SaaS Payroll Engine with progressive tax calculation, BPJS simulation, overtime calculation, and complete payroll management.

## 🚀 Features

- **Multi-Tenant Architecture** - Support multiple companies with data isolation
- **Progressive Tax Calculation** - Indonesia tax bracket system (PPh 21)
- **BPJS Simulation** - Employer & employee contribution calculations
- **Overtime Calculation** - Weekday, weekend, and holiday rates
- **Payroll Workflow** - Draft → Approve → Lock lifecycle
- **Audit Logging** - Complete audit trail for all changes
- **Payslip PDF** - Generate payslip PDF documents
- **CSV Export** - Bank transfer file generation
- **JWT Authentication** - Secure role-based access control
- **Swagger/OpenAPI** - Interactive API documentation
- **Docker Support** - Complete containerization

## 📋 Prerequisites

- Docker and Docker Compose
- Java 21 (for local development)
- Maven 3.9+

## 🛠️ Quick Start with Docker

### 1. Clone the Repository

```bash
cd "/Users/admin/Documents/seno/PayrollTax Pro API/PayrollTax Pro API"
```

### 2. Start All Services

```bash
docker-compose up -d
```

This will start:
- **PostgreSQL** on port 5432
- **Redis** on port 6379
- **Backend API** on port 8081

### 3. Check Service Health

```bash
curl http://localhost:8081/api/health
```

### 4. Access Swagger UI

Open your browser and navigate to:
```
http://localhost:8081/api/swagger-ui.html
```

### 5. Download OpenAPI JSON

```bash
curl http://localhost:8081/api/v3/api-docs -o openapi.json
```

## 👤 Default Credentials

### Admin Account
- **Username:** `admin`
- **Password:** `admin123`

### Tenant Accounts (Password: `admin123`)

| Company | Username | Role |
|---------|----------|------|
| PT Maju Jaya Sejahtera | `budi.maju` | Manager, Finance |
| PT Maju Jaya Sejahtera | `siti.maju` | Employee |
| CV Teknologi Nusantara | `andi.teknologi` | Admin, Manager |
| CV Teknologi Nusantara | `dewi.teknologi` | Finance |
| PT Solusi Digital Indonesia | `reza.digital` | Manager, Finance |
| PT Ritel Modern Indonesia | `wulan.ritel` | Manager |

## 📊 Seed Data

The application comes with real Indonesian seed data:

### Tenants (5 companies)
1. PT Maju Jaya Sejahtera
2. CV Teknologi Nusantara
3. PT Solusi Digital Indonesia
4. PT Ritel Modern Indonesia
5. PT Konsultan Prima

### Employees (30+ employees)
- Various salary levels from Staff to C-Level
- Different tax statuses (TK0, TK1, TK2, TK3, K0, K1, K2, K3)
- Multiple banks (BCA, Mandiri, BNI, BRI, Jago)
- Real Indonesian NPWP numbers

### Salary Structures
- 14 salary grades from Junior Staff to C-Level
- Competitive Indonesian market rates (2024)

### Tax Brackets
- 5 progressive tax brackets (Indonesia PPh 21)

## 🧪 Testing with Postman

### Import Collection

1. Download the Postman collection from `postman/PayrollTax_Pro_API.postman_collection.json`
2. Import the collection into Postman
3. Import the environment from `postman/PayrollTax_Pro_API_Local.postman_environment.json`

### Example Workflow

#### 1. Login
```http
POST {{baseUrl}}/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

#### 2. Process Payroll
```http
POST {{baseUrl}}/payroll/process
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "month": 1,
  "year": 2024,
  "employeeIds": [1, 2, 3, 4, 5],
  "defaultOvertimeHours": 10
}
```

#### 3. Get Payroll Summary
```http
GET {{baseUrl}}/payroll/summary?tenantId=1&month=1&year=2024
Authorization: Bearer {{token}}
```

#### 4. Approve Payroll
```http
POST {{baseUrl}}/payroll/1/approve
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "payrollId": 1,
  "action": "APPROVE",
  "notes": "Approved for payment"
}
```

#### 5. Generate Payslip PDF
```http
GET {{baseUrl}}/payroll/1/payslip
Authorization: Bearer {{token}}
```

## 📚 API Documentation

### Swagger UI
```
http://localhost:8081/api/swagger-ui.html
```

### OpenAPI JSON
```
http://localhost:8081/api/v3/api-docs
```

### OpenAPI YAML
```
http://localhost:8081/api/v3/api-docs.yaml
```

## 🔧 Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | jdbc:postgresql://localhost:5432/payrolltax | Database URL |
| `SPRING_DATASOURCE_USERNAME` | payrolluser | Database username |
| `SPRING_DATASOURCE_PASSWORD` | payrollpass | Database password |
| `SPRING_DATA_REDIS_HOST` | localhost | Redis host |
| `SPRING_DATA_REDIS_PORT` | 6379 | Redis port |
| `JWT_SECRET` | (default) | JWT signing secret |
| `JWT_EXPIRATION` | 900000 | JWT expiration (ms) |

### BPJS Configuration
- Employer percentage: 5%
- Employee percentage: 2%
- Salary cap: Rp 12.000.000

### Overtime Multipliers
- Weekday: 1.5x
- Weekend: 2.0x
- Holiday: 2.0x

## 🏗️ Project Structure

```
src/main/java/com/payrolltaxpro/
├── config/              # Configuration classes
│   ├── SecurityConfig.java
│   ├── OpenApiConfig.java
│   ├── RedisCacheConfig.java
│   └── TenantContext.java
├── controller/          # REST controllers
├── service/            # Business logic
├── repository/         # JPA repositories
├── domain/             # Entities
├── dto/                # Data transfer objects
├── security/           # JWT authentication
└── exception/          # Global exception handling
```

## 📦 Docker Services

### PostgreSQL
```yaml
Image: postgres:16-alpine
Port: 5432
Database: payrolltax
Username: payrolluser
Password: payrollpass
```

### Redis
```yaml
Image: redis:7-alpine
Port: 6379
```

### Backend API
```yaml
Image: payrolltax-api (built from Dockerfile)
Port: 8081
```

## 🔐 Security

- JWT-based authentication
- Role-based access control (RBAC)
- Roles: ADMIN, MANAGER, FINANCE, EMPLOYEE
- Password hashing with BCrypt
- CSRF protection disabled for API

## 🧩 Advanced Features

### Multi-Tenant Support
- Tenant isolation via `X-Tenant-ID` header
- Schema-per-tenant approach (optional)
- JWT claim-based tenant resolution

### Caching
- Redis-based caching for:
  - Payroll locks
  - Tax brackets
  - Salary structures

### Audit Logging
- All payroll changes tracked
- User, timestamp, and IP address logged
- Before/after values stored

## 🚀 Production Deployment

### Build Docker Image
```bash
docker build -t payrolltax-api:latest .
```

### Run with Docker Compose
```bash
docker-compose -f compose.yaml up -d
```

### Environment-Specific Configuration
```bash
SPRING_PROFILES_ACTIVE=production docker-compose up -d
```

## 📝 License

MIT License

## 🤝 Support

For issues and questions:
- Email: support@payrolltax.local
- Documentation: See `/doc` folder
- API Docs: http://localhost:8081/api/swagger-ui.html

---

**Built with ❤️ using Spring Boot 3, Java 21, and PostgreSQL**
