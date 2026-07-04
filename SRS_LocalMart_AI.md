# Software Requirements Specification (SRS)
## Project Name: LocalMart AI – Smart Local Shopping & Offer Discovery Platform

## 1. Project Overview
LocalMart AI is a web-based application designed to help users discover local shopping offers, discounts, and promotions in a simple and efficient way. The platform provides a centralized digital environment where customers can explore nearby deals, compare offers, and make better shopping decisions. The system aims to connect customers with local stores and make promotional information more visible and reachable.

The platform is especially useful for users who want to save money by discovering offers from local businesses without visiting multiple websites or physical stores. It also provides administrators with tools to manage promotional content and keep the platform fresh and useful.

## 2. Objectives
The primary objectives of the project are:
- To develop a digital platform that helps users discover local offers and promotions.
- To provide a user-friendly experience for browsing shopping deals.
- To enable local businesses to publish promotions online.
- To improve awareness of nearby product offers and discounts.
- To implement secure authentication and authorized access for users.
- To demonstrate practical software engineering concepts such as analysis, design, implementation, testing, and documentation.

## 3. Scope
The scope of LocalMart AI includes:
- User registration and login.
- Secure authentication using JWT.
- Viewing and managing offers.
- Searching offers by category, keyword, or location.
- Admin panel for managing offers.
- Responsive web interface for desktop and mobile users.

The initial version focuses on offer discovery and management. It does not include full e-commerce payment, delivery tracking, or real-time inventory management.

## 4. Functional Requirements
### FR1: User Registration
The system shall allow new users to create an account by providing their name, email, and password.

### FR2: User Login
The system shall allow registered users to log in using valid credentials.

### FR3: JWT Authentication
The system shall authenticate users with JWT-based access tokens.

### FR4: Role-Based Access
The system shall support two main roles: Customer and Administrator.

### FR5: Offer Viewing
Users shall be able to view available offers published on the platform.

### FR6: Offer Search
Users shall be able to search offers by category, keyword, or location.

### FR7: Offer Management
Administrators shall be able to add, update, and delete offers.

### FR8: Store Information
Each offer shall be associated with a shop or store name.

### FR9: Category Classification
Each offer shall be categorized such as groceries, fashion, electronics, or food.

### FR10: Responsive Interface
The system shall provide a responsive and accessible interface for multiple device types.

### FR11: Email Notification
The system shall support email-based verification or notifications for user actions.

### FR12: Session Handling
The system shall maintain authenticated sessions through secure token-based validation.

## 5. Non-Functional Requirements
### NFR1: Security
The application shall ensure secure access using encrypted passwords and token-based authentication.

### NFR2: Performance
The system shall provide quick response times for normal user actions.

### NFR3: Reliability
The system shall function consistently without data corruption under normal use.

### NFR4: Usability
The interface shall be easy to understand and navigate.

### NFR5: Maintainability
The software architecture shall be modular and easy to expand.

### NFR6: Scalability
The system shall support future growth in users, offers, and data.

### NFR7: Portability
The application shall work on standard browsers and modern operating systems.

### NFR8: Availability
The system shall remain available whenever the server is running.

## 6. User Roles
### 6.1 Customer
A customer can:
- Register and log in.
- View available offers.
- Search offers by category or location.
- Explore local promotional content.

### 6.2 Administrator
An administrator can:
- Manage offers.
- Add or delete promotions.
- Maintain the platform content.
- Ensure data accuracy and relevance.

## 7. Use Cases
### Use Case 1: User Registration
A new user creates an account.

### Use Case 2: User Login
A registered user signs in securely.

### Use Case 3: View Offers
A user browses available promotions.

### Use Case 4: Search Offers
A user searches offers by location or category.

### Use Case 5: Admin Adds Offer
An administrator publishes a new local offer.

## 8. Features
- Secure user authentication
- User registration and login
- Offer browsing
- Search and filtering
- Admin management panel
- Responsive UI design
- Secure API and backend processing

## 9. Future Scope
The future scope of the project includes:
- AI-based personalized offer recommendations
- GPS-based location-based deals
- Mobile application support
- Push notifications
- Review and rating system
- Payment integration and checkout
- Advanced admin analytics

## 10. Conclusion
LocalMart AI is a practical and useful project for an MCA final-year submission. It combines web development, security, database management, and software engineering concepts into a single system. The project provides a solid base for building a smart local shopping platform with future AI-based enhancements.
