# Digital Voucher Management Backend

A robust backend system for digitalizing client interventions and voucher tracking in a high-tech service company. This application ensures secure technician authentication, intervention tracking, and admin analytics to optimize client management, employee performance, and profitability.

---

## **Features**

- **Technician Authentication & Security**
  - Full authentication using Spring Security and BCrypt password hashing.
  - Automatic email notification to technicians upon profile creation.
  - Forced password reset on first login to ensure secure accounts.

- **Intervention Management**
  - Technicians submit voucher details via a form, including photo upload.
  - Admins receive interventions in a card-based UI (front-end) and can:
    - Accept validated interventions (bonus applied based on submission time)
    - Reject incorrect interventions (notification sent to technician to modify and resend)

- **Dashboards & Analytics**
  - Technician dashboard for viewing interventions, statuses, bonuses, and efficiency.
  - Admin dashboard for viewing all technicians, clients, interventions, efficiency, bonuses, and profitability.

- **Data Persistence & Database**
  - PostgreSQL used for production database.
  - H2 used for initial testing.
  - Flyway for database migrations.
  - Repository and service layers with Hibernate ORM.

- **API**
  - RESTful endpoints for all operations.
  - Tested via Postman.

- **Containerization**
  - Docker Compose setup for PostgreSQL and pgAdmin with a single command.

---

## **Technologies Used**

- Java 17, Spring Boot
- Spring Data JPA, Hibernate
- Spring Security
- Flyway
- Lombok
- PostgreSQL, H2 Database
- Docker & Docker Compose
- Resend email service

---

## **Getting Started**

### **Prerequisites**
- Java 17+
- Docker & Docker Compose
- PostgreSQL
- Maven

### **Run Locally**
1. Clone the repository:
   ```bash
   git clone https://github.com/Saadiaboussiar/dweb-App-Backend.git
