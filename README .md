# Campus Event Management & Ticketing System

This project is a Java Servlet/JSP MVC web app for campus event creation, discovery, ticket reservations, attendance tracking, and admin control.

## Tech Stack
- Java Servlets + JSP
- JDBC
- MySQL
- MVC architecture
- Strategy Pattern for event search/filter
- Factory Method Pattern for event type creation

## Database Setup
1. Create a MySQL database by running `src/main/resources/schema.sql`.
2. Optional environment variables (defaults shown):
   - `CAMPUS_DB_URL` (default: `jdbc:mysql://localhost:3306/campus_event_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`)
   - `CAMPUS_DB_USER` (default: `root`)
   - `CAMPUS_DB_PASS` (default: `123456`)

## Default Admin
- Email: `admin@campus.edu`
- Password: `admin123`

## Feature Mapping
- User Management: registration, login, session, profile update
- Roles:
  - Student: browse events, reserve/cancel tickets, view attendance result
  - Organizer: create/close/complete/delete events, mark attendance
  - Admin: manage users, manage events, manage departments/categories
- Event Management:
  - Event fields and status handling
  - Factory Method via `EventFactoryProvider`
  - Automatic event expiration (`markExpiredEvents`)
- Reservation Workflow:
  - Reserve/cancel with transaction locking (`SELECT ... FOR UPDATE`)
  - Seat count updates atomically
- Search & Filter:
  - Strategy implementations for title/department/date/category/type/availability

## Main URLs
- `/index.jsp`
- `/secure/dashboard`
- `/secure/events`
- `/secure/reservations`
- `/secure/organizer/events`
- `/secure/admin/users`

## Author
**Safaa Quraan**, Computer Science graduate, Jordan University of Science and Technology (JUST).
