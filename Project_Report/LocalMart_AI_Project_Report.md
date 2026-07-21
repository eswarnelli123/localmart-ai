# LocalMart AI - 100 Marks Project Report

## Project Title
LocalMart AI – Smart Local Shopping and Offer Discovery Platform

## Student / Team Details
- Project: LocalMart AI
- Technology: Java 17, Spring Boot 3

## Certificate
This is a complete MCA final-year project report prepared for the LocalMart AI application. It includes project overview, objectives, system design, implementation details, testing, deployment, advantages, and future scope.

## 1. Abstract (10 marks)
LocalMart AI is a web-based local marketplace platform that enables users to discover nearby offers, discounts, and store promotions. The system provides secure registration and login, role-based access for admins and retailers, offer management, search capability, and a foundation for AI-powered recommendation services.

## 2. Introduction (10 marks)
The rapid growth of online shopping has made it difficult for local stores to become visible to nearby customers. LocalMart AI solves this problem by aggregating local promotions and presenting them through a user-friendly web interface. The platform supports customers, retailers, and administrators in a single system.

## 3. Problem Statement (10 marks)
Shoppers often miss local offers because there is no single place to discover nearby discounts. Local businesses struggle to share promotions digitally. This project addresses both problems by implementing a secure offer discovery platform that connects customers with local retailers.

## 4. Objectives (10 marks)
- Develop a responsive web platform for local offer discovery.
- Implement secure user authentication.
- Provide role-based access for customers, retailers, and admins.
- Enable offer creation, update, deletion, and search.
- Use Spring Boot, JWT, and MySQL to build a scalable backend.
- Demonstrate practical software engineering and documentation skills.

## 5. Scope of the Project (10 marks)
The project scope covers:
- User registration and login with JWT authentication.
- Offer listing and search functionality.
- Admin and retailer panels for content management.
- Local deployment using H2 and production deployment support for MySQL.
- Email-based OTP and notification support.

## 6. System Analysis and Design (15 marks)
### 6.1 System Architecture
LocalMart AI uses a layered architecture:
- Presentation layer: Thymeleaf templates and web controllers
- Business layer: Spring services and controllers
- Data layer: Spring Data JPA repositories
- Security layer: JWT authentication and Spring Security

### 6.2 High-Level Architecture
The application follows a modular design with separate packages for authentication, offers, retailers, admin, recommendations, and more. This separation supports maintainability and future enhancement.

### 6.3 Database Design
The database schema includes entities such as users, offers, retailers, categories, products, and recommendations. The project can run on H2 locally and MySQL in production.

## 7. Implementation Details (20 marks)
### 7.1 Technology Stack
- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Thymeleaf
- MySQL / H2
- JWT with `jjwt`
- Maven

### 7.2 Application Modules
- `auth`: handles registration, login, OTP, and password reset.
- `offer`: manages offers, search, and listing.
- `recommendation`: supports AI recommendation endpoints.
- `admin`: supports admin controls and dashboard.
- `retailer`: supports retailer account workflows.
- `notification`: supports mail notifications.

### 7.3 Key Components
- `LocalMartAiApplication`: main entry point.
- `AuthController`: handles `/api/auth` endpoints.
- `OfferController`: handles `/api/offers`.
- `AiRecommendationController`: handles `/api/recommendations`.
- `application.properties`: core configuration.
- `application-local.properties`: local development profile.
- `application-prod.properties`: production-ready profile.

### 7.4 Project Structure
The repository structure is organized as:
- `src/main/java/com/localmart/`: application packages
- `src/main/resources/templates/`: Thymeleaf views
- `src/main/resources/`: configuration files
- `pom.xml`: Maven dependencies
- `localmart_ai_db.sql`: database schema script

## 8. Testing and Validation (10 marks)
### 8.1 Functional Testing
- User registration and login.
- Offer creation and retrieval.
- API endpoint validation for authentication and offers.

### 8.2 Configuration Testing
- Local H2 profile tests.
- MySQL production profile setup.
- SMTP mail configuration.

### 8.3 Evidence of Testing
The repository includes `target/surefire-reports` from unit tests and a detailed `run-output.txt` / `build-output.txt` from execution logs.

## 9. Deployment and Maintenance (10 marks)
### 9.1 Deployment Setup
- Local deployment: `mvn spring-boot:run -Dspring-boot.run.profiles=local`
- Production deployment: environment variables, MySQL, Docker-ready setup.

### 9.2 Maintenance
- Supports configuration via property files.
- Modular packages make enhancements and bug fixes easier.
- Future AI recommendation features can be added in the recommendation module.

## 10. Advantages and Usefulness (5 marks)
LocalMart AI offers:
- Centralized local offer discovery
- Secure user management
- Admin and retailer content control
- A real-world example of a modern Java web application
- Foundation for AI and marketplace enhancements

## 11. Limitations (5 marks)
Current limitations include:
- No payment or checkout flow.
- No live GPS-based location features.
- Recommendation engine is basic and can be improved.

## 12. Future Enhancements (5 marks)
Possible improvements:
- AI-based recommendation engine
- Location-aware search and map view
- Ratings and reviews
- Mobile-friendly SPA frontend
- Push notifications and real-time alerts
- Payment integration and order tracking

## 13. Conclusion (5 marks)
LocalMart AI is a complete project designed for a 100-mark academic submission. It demonstrates a practical application of Spring Boot, secure authentication, database design, and user-oriented web development. The platform is ready for deployment and future enhancement, making it a strong candidate for final-year project evaluation.

## 14. Marks Distribution
| Component | Marks |
|---|---:|
| Abstract | 10 |
| Introduction | 10 |
| Problem Statement | 10 |
| Objectives | 10 |
| Scope | 10 |
| System Design | 15 |
| Implementation | 20 |
| Testing | 10 |
| Deployment | 10 |
| Documentation / Presentation | 5 |
| Total | 100 |

## 15. Existing Documentation in the Repository
- `README.md`
- `SRS_LocalMart_AI.md`
- `SYSTEM_ARCHITECTURE_LocalMart_AI.md`
- `SPRING_BOOT_STRUCTURE_LocalMart_AI.md`
- `DEPLOYMENT_GUIDE.md`
- `localmart_ai_db.sql`

## 16. References
- Spring Boot documentation
- JWT and Spring Security guides
- MySQL and H2 database documentation
- Thymeleaf template engine documentation

## 17. Appendix
Files provided with the project:
- source code under `src/`
- build artifacts under `target/`
- SQL schema `localmart_ai_db.sql`
- deployment guide and project documentation files
