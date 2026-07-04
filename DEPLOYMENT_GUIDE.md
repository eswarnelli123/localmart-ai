# LocalMart AI - Complete Deployment Guide

## Overview
This guide covers deploying your Spring Boot app to Render (free tier) with a free MySQL database.

---

## PART 1: Prepare Your Project

### Step 1.1: Create GitHub Repository
1. Go to https://github.com/new
2. Create a new repository named `localmart-ai`
3. Do NOT initialize with README (we have one)
4. Click "Create repository"

### Step 1.2: Push Your Code to GitHub
Open terminal in your project folder and run:

```bash
git init
git add .
git commit -m "Initial commit: LocalMart AI application"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/localmart-ai.git
git push -u origin main
```

Replace `YOUR_USERNAME` with your actual GitHub username.

### Step 1.3: Create production-ready configuration
We need to prepare the app to work with environment variables instead of hardcoded credentials.

---

## PART 2: Set Up Database (Free MySQL)

### Option A: Railway (Recommended for MySQL)

1. Go to https://railway.app
2. Sign up with GitHub
3. Click "New Project" → "MySQL"
4. Wait for deployment (2-3 minutes)
5. Once deployed, go to "MySQL" service
6. Copy these details:
   - **MYSQL_URL**: shown in "MYSQL_URL" variable
   - **MYSQL_USER**: shown in "MYSQL_USER" variable
   - **MYSQL_PASSWORD**: shown in "MYSQL_PASSWORD" variable
   - **MYSQL_HOST**: shown in connection string
   - **MYSQL_PORT**: usually 3306

**Save these values** - you'll need them in Render setup.

### Option B: PlanetScale (Alternative)

1. Go to https://planetscale.com
2. Sign up
3. Create new database
4. Click "Connect"
5. Copy the connection string and credentials

---

## PART 3: Prepare Spring Boot for Production

### Step 3.1: Create production configuration file

Create file: `src/main/resources/application-prod.properties`

```properties
# Database Configuration (uses environment variables)
spring.datasource.url=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=${MYSQL_USER}
spring.datasource.password=${MYSQL_PASSWORD}

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Mail Configuration
spring.mail.host=${MAIL_HOST:smtp.gmail.com}
spring.mail.port=${MAIL_PORT:587}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.from=${MAIL_FROM:noreply@localmart.ai}

# Server Configuration
server.port=${PORT:8080}
server.servlet.context-path=/

# Security
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}

# Logging
logging.level.root=INFO
```

### Step 3.2: Create Dockerfile

Create file: `Dockerfile`

```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ src/
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

### Step 3.3: Create .gitignore (if not present)

Create/update file: `.gitignore`

```
target/
*.jar
*.war
.DS_Store
.env
.env.local
*.log
node_modules/
.vscode/
.idea/
*.iml
```

### Step 3.4: Commit changes

```bash
git add src/main/resources/application-prod.properties
git add Dockerfile
git add .gitignore
git commit -m "Add production configuration and Docker setup"
git push origin main
```

---

## PART 4: Deploy to Render

### Step 4.1: Sign Up to Render

1. Go to https://render.com
2. Click "Sign up"
3. Sign up with GitHub
4. Authorize Render to access your repositories

### Step 4.2: Create Web Service

1. Click "+ New" button (top right)
2. Select "Web Service"
3. Select your `localmart-ai` repository
4. Fill in the details:
   - **Name**: `localmart-ai` (or your choice)
   - **Environment**: `Docker` (since we have a Dockerfile)
   - **Region**: Choose closest to your users
   - **Branch**: `main`

### Step 4.3: Configure Environment Variables

On the same page, scroll down to "Environment" section and add these variables:

```
MYSQL_HOST=<from Railway/PlanetScale>
MYSQL_PORT=3306
MYSQL_DATABASE=localmart
MYSQL_USER=<from Railway/PlanetScale>
MYSQL_PASSWORD=<from Railway/PlanetScale>

MAIL_USERNAME=projectofmca@gmail.com
MAIL_PASSWORD=noelvrmwloqtddes
MAIL_FROM=projectofmca@gmail.com

JWT_SECRET=E1c9mR7sV2xY8nP4qZ6fB3hL0pW5tU2a
JWT_EXPIRATION=86400000

SPRING_PROFILES_ACTIVE=prod
```

### Step 4.4: Set Build and Start Commands

- **Build Command**: `mvn clean package -DskipTests`
- **Start Command**: `java -jar target/*.jar`

### Step 4.5: Deploy

Click "Create Web Service" button. Render will:
1. Clone your repository
2. Build Docker image
3. Deploy the container
4. Assign a public URL (like `https://localmart-ai.onrender.com`)

**Wait 5-10 minutes** for the build and deployment to complete.

---

## PART 5: Set Up Database Schema

Your app uses `spring.jpa.hibernate.ddl-auto=validate`, which means the database tables must already exist.

### Option A: Manual SQL Setup (Recommended)

1. Go to Railway dashboard
2. Click on MySQL service
3. Click "Connect"
4. Open SQL client (or use their web client)
5. Run the database initialization scripts from your project

If you have migration files in `src/main/resources/db/migration/`, they should run automatically if you use Flyway.

### Option B: Use Application Migration

Temporarily change in production config:

```properties
spring.jpa.hibernate.ddl-auto=create
```

Deploy once (this creates tables), then change back to `validate` and redeploy.

---

## PART 6: Test Your Deployment

### Step 6.1: Get Your Public URL

1. Go to Render dashboard
2. Click on your web service
3. Copy the URL (e.g., `https://localmart-ai.onrender.com`)

### Step 6.2: Test endpoints

Open in browser or Postman:

```
GET https://localmart-ai.onrender.com/api/admin/categories
POST https://localmart-ai.onrender.com/api/auth/register
```

### Step 6.3: Troubleshoot

If deployment fails:
1. Go to "Logs" in Render dashboard
2. Check error messages
3. Verify all environment variables are set correctly
4. Check database connectivity

---

## PART 7: Ongoing Maintenance

### Update Your App

After making code changes:

```bash
git add .
git commit -m "Your changes"
git push origin main
```

Render will automatically redeploy (you can disable auto-deploy in settings).

### Monitor Logs

In Render dashboard → Logs section, check application logs for errors.

### Scale Resources

If app is slow:
1. Render dashboard → Settings
2. Upgrade instance type (from free tier if needed)

---

## Important Notes

### Free Tier Limitations
- **Render**: 750 hours/month free (shared CPU)
- **Railway**: $5 credit/month, can run out quickly
- **Spin down**: Services stop after 15 mins of inactivity on free tier

### Database Backups
- Manual backups: Export data regularly from Railway/PlanetScale
- Critical data: Implement backup strategy

### Security Considerations
- Store all secrets in environment variables (never in code)
- Rotate JWT secret periodically
- Monitor logs for suspicious activity

### Email Limitations
- Gmail SMTP works but has rate limits
- Consider using SendGrid, Mailgun for production
- (Both have free tiers)

---

## Quick Reference: Final URLs

After deployment:
- **App URL**: `https://localmart-ai.onrender.com`
- **Database**: Connected via environment variables
- **Admin Panel**: `https://localmart-ai.onrender.com/admin`
- **Retailer Dashboard**: `https://localmart-ai.onrender.com/retailer`

---

## Troubleshooting Common Issues

### 1. "Build failed"
- Check Maven build locally: `mvn clean package`
- Verify Java version compatibility
- Check for missing dependencies

### 2. "Connection refused"
- Verify database credentials in Render env vars
- Check MySQL service is running on Railway
- Ensure firewall allows outbound MySQL connections

### 3. "Application won't start"
- Check logs: Render → Logs
- Verify all required env variables are set
- Check for typos in environment variable names

### 4. "Database schema missing"
- Run SQL scripts manually in database
- Or temporarily set `ddl-auto=create` and redeploy

---

## Summary: What You Just Did

✅ Pushed code to GitHub  
✅ Set up free MySQL database  
✅ Created production configuration  
✅ Built Docker image  
✅ Deployed to Render  
✅ Configured environment variables  
✅ Made app public with live URL  

Your LocalMart AI app is now live! 🎉
