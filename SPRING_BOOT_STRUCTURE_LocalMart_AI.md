# Spring Boot Project Structure for LocalMart AI
## Project Name: LocalMart AI – Smart Local Shopping & Offer Discovery Platform

## 1. Overview
This document describes the complete Spring Boot project structure for LocalMart AI. It includes the recommended Maven configuration, application properties, package organization, main configuration classes, and an explanation of every dependency.

The project is built using:
- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- MySQL
- Thymeleaf
- Maven

---

## 2. Recommended Project Structure
```text
localmart-ai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/localmart/
│   │   │       ├── LocalMartAiApplication.java
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── WebConfig.java
│   │   │       ├── auth/
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── LoginRequest.java
│   │   │       │   └── RegisterRequest.java
│   │   │       ├── user/
│   │   │       │   ├── User.java
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── UserService.java
│   │   │       │   └── UserController.java
│   │   │       ├── retailer/
│   │   │       │   ├── Retailer.java
│   │   │       │   ├── RetailerRepository.java
│   │   │       │   ├── RetailerService.java
│   │   │       │   └── RetailerController.java
│   │   │       ├── offer/
│   │   │       │   ├── Offer.java
│   │   │       │   ├── OfferRepository.java
│   │   │       │   ├── OfferService.java
│   │   │       │   └── OfferController.java
│   │   │       ├── admin/
│   │   │       │   ├── AdminController.java
│   │   │       │   └── AdminService.java
│   │   │       ├── ai/
│   │   │       │   ├── RecommendationService.java
│   │   │       │   └── RecommendationController.java
│   │   │       ├── security/
│   │   │       │   ├── JwtService.java
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   └── CustomUserDetailsService.java
│   │   │       └── web/
│   │   │           └── HomeController.java
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       │   ├── home.html
│   │       │   ├── login.html
│   │       │   └── register.html
│   │       └── application.properties
│   └── test/
│       └── java/com/localmart/
├── pom.xml
├── README.md
└── .gitignore
```

---

## 3. Maven pom.xml
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.localmart</groupId>
    <artifactId>localmart-ai</artifactId>
    <version>1.0.0</version>
    <name>LocalMart AI</name>
    <description>Smart Local Shopping and Offer Discovery Platform</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>17</java.version>
        <jjwt.version>0.12.6</jjwt.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>

        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>

        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 4. application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/localmart_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

spring.thymeleaf.cache=false
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

jwt.secret=localmart-secret-key-change-me
jwt.expiration=86400000
server.port=8080
```

---

## 5. Main Application Class
```java
package com.localmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocalMartAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocalMartAiApplication.class, args);
    }
}
```

---

## 6. Configuration Classes

### 6.1 SecurityConfig.java
```java
package com.localmart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/login", "/register", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 6.2 WebConfig.java
```java
package com.localmart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/register").setViewName("register");
    }
}
```

---

## 7. Package Structure Explanation

### 7.1 com.localmart
This is the root package of the application.

### 7.2 config
Contains configuration classes such as security and web configuration.

### 7.3 auth
Contains authentication-related components such as login, registration, token handling, and authentication services.

### 7.4 user
Contains user entity, repository, service, and controller classes.

### 7.5 retailer
Contains retailer-specific entities and business logic.

### 7.6 offer
Contains offer-related entities, repositories, services, and controllers.

### 7.7 admin
Contains administrator-specific features and control operations.

### 7.8 ai
Contains AI recommendation logic and smart suggestion services.

### 7.9 security
Contains JWT utilities, filters, and custom user details services.

### 7.10 web
Contains simple controllers for public pages such as home and login.

---

## 8. Dependency Explanation

### 8.1 spring-boot-starter-web
Provides everything needed to build a web application, including Spring MVC and embedded Tomcat.

### 8.2 spring-boot-starter-thymeleaf
Enables server-side rendering of HTML pages using Thymeleaf templates.

### 8.3 spring-boot-starter-security
Adds authentication and authorization support using Spring Security.

### 8.4 spring-boot-starter-data-jpa
Provides Spring Data JPA and Hibernate for database interaction.

### 8.5 spring-boot-starter-validation
Adds validation support for request data using Bean Validation annotations.

### 8.6 spring-boot-starter-mail
Supports email sending functionality such as OTP or verification emails.

### 8.7 jjwt-api
Provides the Java JWT API for creating and parsing JSON Web Tokens.

### 8.8 jjwt-impl
Implements the JWT library runtime functions.

### 8.9 jjwt-jackson
Enables JWT serialization and deserialization with Jackson JSON support.

### 8.10 mysql-connector-j
Allows the Spring Boot application to connect to a MySQL database.

### 8.11 lombok
Reduces boilerplate code by generating getters, setters, constructors, and other methods automatically.

### 8.12 spring-boot-starter-test
Provides testing support such as JUnit, Mockito, and Spring Boot test utilities.

---

## 9. Recommended Execution Flow
1. The user opens the application in a browser.
2. The frontend sends a request to the Spring Boot controller.
3. The controller calls the relevant service layer.
4. The service layer interacts with the repository and database.
5. The response is returned to the frontend.
6. The user sees the result on the page.

---

## 10. Conclusion
The Spring Boot project structure for LocalMart AI is organized to support a modular, secure, maintainable, and scalable web application. It uses Spring Boot 3, Spring Security, JWT authentication, MySQL, and Thymeleaf to create a complete platform for smart local shopping and offer discovery.
