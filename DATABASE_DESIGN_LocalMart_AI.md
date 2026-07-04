# MySQL Database Design for LocalMart AI
## Project Name: LocalMart AI – Smart Local Shopping & Offer Discovery Platform

## 1. Overview
The database design for LocalMart AI is based on a relational model that supports users, retailers, offers, categories, authentication, and administrative operations. The database is designed to be secure, scalable, and easy to maintain.

The system uses MySQL as the primary database engine. The design includes tables for:
- Users and roles
- Retailers
- Offers and categories
- OTP/email verification
- Admin activity and audit records

---

## 2. Database Design Goals
The database design aims to:
- Store user and retailer information efficiently
- Maintain relationships between offers and their owners
- Support search and filtering of offers
- Ensure data integrity through primary and foreign keys
- Support future extension such as ratings, favorites, and AI recommendations

---

## 3. Entity Relationship Summary
The core relationships are:
- One role can be assigned to many users
- One retailer can publish many offers
- One category can contain many offers
- One user can request many OTP records
- One admin can manage many offers or users

---

## 4. Table Design

### 4.1 Table: roles
Purpose: Stores user roles such as Customer, Retailer, and Admin.

| Column | Type | Constraints | Description |
|---|---|---|---|
| role_id | INT | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each role |
| role_name | VARCHAR(50) | NOT NULL UNIQUE | Name of the role, e.g. CUSTOMER, RETAILER, ADMIN |
| description | VARCHAR(255) | NULL | Short description of the role |

#### Indexes
- PRIMARY KEY on role_id
- UNIQUE INDEX on role_name

#### Sample Data
| role_id | role_name | description |
|---|---|---|
| 1 | CUSTOMER | Regular platform user |
| 2 | RETAILER | Business account that posts offers |
| 3 | ADMIN | Platform administrator |

---

### 4.2 Table: users
Purpose: Stores all registered users in the system.

| Column | Type | Constraints | Description |
|---|---|---|---|
| user_id | BIGINT | PRIMARY KEY AUTO_INCREMENT | Unique user id |
| full_name | VARCHAR(100) | NOT NULL | Full name of the user |
| email | VARCHAR(150) | NOT NULL UNIQUE | Email address used for login |
| password_hash | VARCHAR(255) | NOT NULL | Securely hashed password |
| phone_number | VARCHAR(20) | NULL | Optional phone number |
| role_id | INT | NOT NULL, FOREIGN KEY REFERENCES roles(role_id) | User role |
| is_active | TINYINT(1) | DEFAULT 1 | Indicates if the account is active |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation time |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Last update time |

#### Indexes
- PRIMARY KEY on user_id
- UNIQUE INDEX on email
- INDEX on role_id
- INDEX on created_at

#### Foreign Key
- roles.role_id → users.role_id

#### Sample Data
| user_id | full_name | email | password_hash | phone_number | role_id | is_active | created_at |
|---|---|---|---|---|---|---|---|
| 1 | Arjun Sharma | arjun@example.com | hashed_pw_1 | 9876543210 | 1 | 1 | 2026-06-01 10:00:00 |
| 2 | Priya Stores | priya@example.com | hashed_pw_2 | 9988776655 | 2 | 1 | 2026-06-02 11:00:00 |
| 3 | Admin User | admin@example.com | hashed_pw_3 | 9123456780 | 3 | 1 | 2026-06-03 09:30:00 |

---

### 4.3 Table: retailers
Purpose: Stores retailer-specific business profile information.

| Column | Type | Constraints | Description |
|---|---|---|---|
| retailer_id | BIGINT | PRIMARY KEY AUTO_INCREMENT | Unique retailer id |
| user_id | BIGINT | NOT NULL, UNIQUE, FOREIGN KEY REFERENCES users(user_id) | Related user account |
| shop_name | VARCHAR(150) | NOT NULL | Name of the shop or business |
| shop_address | VARCHAR(255) | NOT NULL | Business address |
| city | VARCHAR(100) | NOT NULL | City where the shop is located |
| state | VARCHAR(100) | NULL | State or province |
| pincode | VARCHAR(20) | NULL | Postal code |
| business_type | VARCHAR(100) | NULL | Type of business |
| contact_person | VARCHAR(100) | NULL | Contact person's name |
| contact_number | VARCHAR(20) | NULL | Contact number |
| is_verified | TINYINT(1) | DEFAULT 0 | Verification status |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation time |

#### Indexes
- PRIMARY KEY on retailer_id
- UNIQUE INDEX on user_id
- INDEX on city
- INDEX on shop_name

#### Foreign Key
- users.user_id → retailers.user_id

#### Sample Data
| retailer_id | user_id | shop_name | shop_address | city | state | pincode | business_type | contact_person | contact_number | is_verified | created_at |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 1 | 2 | Priya Stores | 12 Market Street | Pune | Maharashtra | 411001 | Grocery | Priya | 9988776655 | 1 | 2026-06-02 11:00:00 |

---

### 4.4 Table: categories
Purpose: Stores offer categories such as groceries, electronics, fashion, and food.

| Column | Type | Constraints | Description |
|---|---|---|---|
| category_id | INT | PRIMARY KEY AUTO_INCREMENT | Unique category id |
| category_name | VARCHAR(100) | NOT NULL UNIQUE | Category name |
| description | VARCHAR(255) | NULL | Category description |

#### Indexes
- PRIMARY KEY on category_id
- UNIQUE INDEX on category_name

#### Sample Data
| category_id | category_name | description |
|---|---|---|
| 1 | Grocery | Food and household items |
| 2 | Electronics | Gadgets and devices |
| 3 | Fashion | Clothing and accessories |
| 4 | Food | Restaurants and food deals |

---

### 4.5 Table: offers
Purpose: Stores all promotional offers published by retailers.

| Column | Type | Constraints | Description |
|---|---|---|---|
| offer_id | BIGINT | PRIMARY KEY AUTO_INCREMENT | Unique offer id |
| retailer_id | BIGINT | NOT NULL, FOREIGN KEY REFERENCES retailers(retailer_id) | Owner of the offer |
| category_id | INT | NOT NULL, FOREIGN KEY REFERENCES categories(category_id) | Offer category |
| title | VARCHAR(150) | NOT NULL | Offer title |
| description | TEXT | NOT NULL | Offer details |
| discount_percent | DECIMAL(5,2) | NULL | Discount percentage |
| price | DECIMAL(10,2) | NULL | Original price |
| offer_price | DECIMAL(10,2) | NULL | Final discounted price |
| start_date | DATE | NOT NULL | Offer start date |
| end_date | DATE | NOT NULL | Offer end date |
| city | VARCHAR(100) | NOT NULL | City where offer is valid |
| is_active | TINYINT(1) | DEFAULT 1 | Whether the offer is active |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation time |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Last update time |

#### Indexes
- PRIMARY KEY on offer_id
- INDEX on retailer_id
- INDEX on category_id
- INDEX on city
- INDEX on is_active
- INDEX on start_date, end_date

#### Foreign Keys
- retailers.retailer_id → offers.retailer_id
- categories.category_id → offers.category_id

#### Sample Data
| offer_id | retailer_id | category_id | title | description | discount_percent | price | offer_price | start_date | end_date | city | is_active | created_at |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 1 | 1 | 1 | Flat 20% Off Groceries | Discount on daily essentials | 20.00 | 1000.00 | 800.00 | 2026-06-01 | 2026-06-30 | Pune | 1 | 2026-06-01 12:00:00 |
| 2 | 1 | 2 | Smartphone Deal | Discount on selected phones | 15.00 | 25000.00 | 21250.00 | 2026-06-05 | 2026-06-25 | Pune | 1 | 2026-06-05 14:20:00 |

---

### 4.6 Table: otp_verifications
Purpose: Stores OTP codes for email verification or password reset flows.

| Column | Type | Constraints | Description |
|---|---|---|---|
| otp_id | BIGINT | PRIMARY KEY AUTO_INCREMENT | Unique OTP record id |
| user_id | BIGINT | NOT NULL, FOREIGN KEY REFERENCES users(user_id) | User requesting OTP |
| otp_code | VARCHAR(10) | NOT NULL | One-time password |
| purpose | VARCHAR(50) | NOT NULL | Example: EMAIL_VERIFICATION, PASSWORD_RESET |
| expires_at | DATETIME | NOT NULL | OTP expiration time |
| is_used | TINYINT(1) | DEFAULT 0 | Whether OTP has already been used |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation time |

#### Indexes
- PRIMARY KEY on otp_id
- INDEX on user_id
- INDEX on expires_at

#### Foreign Key
- users.user_id → otp_verifications.user_id

#### Sample Data
| otp_id | user_id | otp_code | purpose | expires_at | is_used | created_at |
|---|---|---|---|---|---|---|---|
| 1 | 1 | 482931 | EMAIL_VERIFICATION | 2026-06-01 11:30:00 | 0 | 2026-06-01 11:00:00 |

---

### 4.7 Table: admin_activity_log
Purpose: Stores activity logs for administrative actions.

| Column | Type | Constraints | Description |
|---|---|---|---|
| log_id | BIGINT | PRIMARY KEY AUTO_INCREMENT | Unique log id |
| admin_user_id | BIGINT | NOT NULL, FOREIGN KEY REFERENCES users(user_id) | Admin who performed the action |
| action | VARCHAR(100) | NOT NULL | Action name, e.g. APPROVE_OFFER |
| target_type | VARCHAR(100) | NULL | Type of target such as OFFER or USER |
| target_id | BIGINT | NULL | Id of affected record |
| description | TEXT | NULL | Explanation of the activity |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Log creation time |

#### Indexes
- PRIMARY KEY on log_id
- INDEX on admin_user_id
- INDEX on created_at

#### Foreign Key
- users.user_id → admin_activity_log.admin_user_id

#### Sample Data
| log_id | admin_user_id | action | target_type | target_id | description | created_at |
|---|---|---|---|---|---|---|
| 1 | 3 | APPROVE_OFFER | OFFER | 1 | Approved retailer offer | 2026-06-01 12:30:00 |

---

## 5. Relationships Explained

### 5.1 One-to-Many: roles to users
One role can be assigned to many users, but each user has only one role.

### 5.2 One-to-Many: retailers to offers
One retailer can publish many offers, but each offer belongs to one retailer.

### 5.3 Many-to-One: offers to categories
Many offers can belong to one category.

### 5.4 One-to-Many: users to otp_verifications
One user can have many OTP records over time.

### 5.5 One-to-Many: users to admin_activity_log
One admin user can generate many activity log entries.

---

## 6. Recommended Indexes
The following indexes are recommended for performance:
- users(email)
- users(role_id)
- retailers(city)
- retailers(shop_name)
- offers(retailer_id)
- offers(category_id)
- offers(city)
- offers(is_active)
- otp_verifications(user_id)
- otp_verifications(expires_at)
- admin_activity_log(admin_user_id)

---

## 7. SQL Create Table Example
```sql
CREATE TABLE roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    role_id INT NOT NULL,
    is_active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE retailers (
    retailer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    shop_name VARCHAR(150) NOT NULL,
    shop_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    pincode VARCHAR(20),
    business_type VARCHAR(100),
    contact_person VARCHAR(100),
    contact_number VARCHAR(20),
    is_verified TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE offers (
    offer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    retailer_id BIGINT NOT NULL,
    category_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    discount_percent DECIMAL(5,2),
    price DECIMAL(10,2),
    offer_price DECIMAL(10,2),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    city VARCHAR(100) NOT NULL,
    is_active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (retailer_id) REFERENCES retailers(retailer_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

CREATE TABLE otp_verifications (
    otp_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    purpose VARCHAR(50) NOT NULL,
    expires_at DATETIME NOT NULL,
    is_used TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE admin_activity_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_user_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    target_type VARCHAR(100),
    target_id BIGINT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_user_id) REFERENCES users(user_id)
);
```

---

## 8. Table and Column Explanation

### roles
- role_id: Unique identifier for each role.
- role_name: Name of the role such as CUSTOMER, RETAILER, or ADMIN.
- description: Explains the purpose of the role.

### users
- user_id: Unique identifier for the user account.
- full_name: Real name of the user.
- email: Unique email used as login identity.
- password_hash: Securely hashed password.
- phone_number: Optional contact number.
- role_id: Links the user to a predefined role.
- is_active: Shows whether the account is active.
- created_at: When the account was created.
- updated_at: When the account was updated.

### retailers
- retailer_id: Unique identifier for each retailer profile.
- user_id: Link to the associated user account.
- shop_name: Name of the retailer business.
- shop_address: Physical business address.
- city: City of the shop.
- state: State or province.
- pincode: Postal code.
- business_type: Nature of the retailer business.
- contact_person: Main contact name.
- contact_number: Retailer contact number.
- is_verified: Indicates whether the retailer is verified by the admin.

### categories
- category_id: Unique identifier for the category.
- category_name: Name of the category.
- description: Brief description of the category.

### offers
- offer_id: Unique identifier for each offer.
- retailer_id: Links the offer to the retailer.
- category_id: Links the offer to a category.
- title: Name of the offer.
- description: Details of the offer.
- discount_percent: Discount percentage offered.
- price: Original price.
- offer_price: Discounted final price.
- start_date: Start date of the offer.
- end_date: End date of the offer.
- city: City in which the offer is valid.
- is_active: Shows whether the offer is currently active.

### otp_verifications
- otp_id: Unique identifier for each OTP record.
- user_id: User associated with the OTP.
- otp_code: The generated verification code.
- purpose: Used for email verification or reset purposes.
- expires_at: Expiration timestamp.
- is_used: Indicates whether the OTP was already used.

### admin_activity_log
- log_id: Unique identifier for each log entry.
- admin_user_id: Admin who performed the action.
- action: Name of the action performed.
- target_type: Resource type affected.
- target_id: Id of the affected object.
- description: Explanation of the action.

---

## 9. Future Extension Tables
The following tables can be added later:
- favorites
- offer_views
- ratings_reviews
- notifications
- payment_transactions
- recommendation_logs

---

## 10. Conclusion
The MySQL database design for LocalMart AI provides a strong foundation for storing users, retailers, offers, role-based access, OTP verification, and administrative activity. It is structured to support future growth and can easily be adapted for AI-based personalization and analytics features.
