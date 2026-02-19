# Security & Compliance

---

## Authentication

- JWT Access Token
- Refresh Token
- Expiration: 15 min

---

## Authorization

Roles:

- ROLE_ADMIN
- ROLE_FINANCE
- ROLE_MANAGER
- ROLE_EMPLOYEE

---

## Security Measures

- HTTPS required
- Password hashing (BCrypt)
- Input validation
- SQL injection protection (JPA)
- CSRF protection disabled for API
- Rate limiting (optional Redis)

---

## Compliance Readiness

- Audit log tracking
- Payroll locking mechanism
- Data isolation per tenant
- Export tracking log
