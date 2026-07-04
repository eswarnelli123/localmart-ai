-- LocalMart AI MySQL database schema
-- Database: localmart_ai_db

CREATE DATABASE IF NOT EXISTS `localmart_ai_db`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `localmart_ai_db`;

SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
SET FOREIGN_KEY_CHECKS = 0;

-- Admin users
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `admin_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(80) NOT NULL,
  `email` VARCHAR(180) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `full_name` VARCHAR(120) DEFAULT NULL,
  `role` ENUM('super_admin','operations','support') NOT NULL DEFAULT 'operations',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login_at` DATETIME NULL,
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `uk_admin_username` (`username`),
  UNIQUE KEY `uk_admin_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Categories for product organization
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `category_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(120) NOT NULL,
  `slug` VARCHAR(140) NOT NULL,
  `parent_category_id` INT UNSIGNED DEFAULT NULL,
  `description` TEXT DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uk_category_name` (`name`),
  UNIQUE KEY `uk_category_slug` (`slug`),
  KEY `idx_category_parent` (`parent_category_id`),
  CONSTRAINT `fk_category_parent` FOREIGN KEY (`parent_category_id`) REFERENCES `category` (`category_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Customers who browse and purchase
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `customer_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(80) NOT NULL,
  `last_name` VARCHAR(80) NOT NULL,
  `email` VARCHAR(180) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `is_verified` TINYINT(1) NOT NULL DEFAULT 0,
  `status` ENUM('active','inactive','suspended') NOT NULL DEFAULT 'active',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `uk_customer_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Retailers who own stores
DROP TABLE IF EXISTS `retailer`;
CREATE TABLE `retailer` (
  `retailer_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `company_name` VARCHAR(150) NOT NULL,
  `contact_name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(180) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `business_license` VARCHAR(100) DEFAULT NULL,
  `verified` TINYINT(1) NOT NULL DEFAULT 0,
  `status` ENUM('pending','approved','rejected','inactive') NOT NULL DEFAULT 'pending',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`retailer_id`),
  UNIQUE KEY `uk_retailer_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Stores owned by retailers
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store` (
  `store_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `retailer_id` INT UNSIGNED NOT NULL,
  `store_name` VARCHAR(150) NOT NULL,
  `slug` VARCHAR(180) NOT NULL,
  `address_line1` VARCHAR(200) NOT NULL,
  `address_line2` VARCHAR(200) DEFAULT NULL,
  `city` VARCHAR(100) NOT NULL,
  `state` VARCHAR(100) NOT NULL,
  `postal_code` VARCHAR(20) DEFAULT NULL,
  `country` VARCHAR(80) NOT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `store_description` TEXT DEFAULT NULL,
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`store_id`),
  UNIQUE KEY `uk_store_slug` (`slug`),
  KEY `idx_store_retailer` (`retailer_id`),
  CONSTRAINT `fk_store_retailer` FOREIGN KEY (`retailer_id`) REFERENCES `retailer` (`retailer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Products listed by retailers
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `product_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `category_id` INT UNSIGNED NOT NULL,
  `store_id` INT UNSIGNED NOT NULL,
  `sku` VARCHAR(80) NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `brand` VARCHAR(120) DEFAULT NULL,
  `base_price` DECIMAL(10,2) NOT NULL,
  `taxable` TINYINT(1) NOT NULL DEFAULT 1,
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `uk_product_sku` (`sku`),
  KEY `idx_product_store` (`store_id`),
  KEY `idx_product_category` (`category_id`),
  CONSTRAINT `fk_product_store` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Images attached to products
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image` (
  `image_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `product_id` INT UNSIGNED NOT NULL,
  `image_url` VARCHAR(500) NOT NULL,
  `alt_text` VARCHAR(200) DEFAULT NULL,
  `display_order` INT UNSIGNED NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`image_id`),
  KEY `idx_product_image_product` (`product_id`),
  CONSTRAINT `fk_product_image_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Inventory records per store
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
  `inventory_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `product_id` INT UNSIGNED NOT NULL,
  `store_id` INT UNSIGNED NOT NULL,
  `quantity` INT UNSIGNED NOT NULL DEFAULT 0,
  `available_quantity` INT UNSIGNED NOT NULL DEFAULT 0,
  `reorder_threshold` INT UNSIGNED NOT NULL DEFAULT 5,
  `last_restocked_at` DATETIME DEFAULT NULL,
  `price_override` DECIMAL(10,2) DEFAULT NULL,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`inventory_id`),
  UNIQUE KEY `uk_inventory_product_store` (`product_id`,`store_id`),
  KEY `idx_inventory_store` (`store_id`),
  CONSTRAINT `fk_inventory_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_inventory_store` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Offers for product/store/category/sitewide promotions
DROP TABLE IF EXISTS `offer`;
CREATE TABLE `offer` (
  `offer_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(180) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `offer_type` ENUM('product','store','category','sitewide') NOT NULL,
  `product_id` INT UNSIGNED DEFAULT NULL,
  `store_id` INT UNSIGNED DEFAULT NULL,
  `category_id` INT UNSIGNED DEFAULT NULL,
  `discount_type` ENUM('percentage','fixed_amount','buy_x_get_y') NOT NULL,
  `discount_value` DECIMAL(10,2) NOT NULL,
  `min_purchase_amount` DECIMAL(10,2) DEFAULT NULL,
  `start_date` DATETIME NOT NULL,
  `end_date` DATETIME NOT NULL,
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`offer_id`),
  KEY `idx_offer_active_dates` (`is_active`,`start_date`,`end_date`),
  KEY `idx_offer_product` (`product_id`),
  KEY `idx_offer_store` (`store_id`),
  KEY `idx_offer_category` (`category_id`),
  CONSTRAINT `fk_offer_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_offer_store` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_offer_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Ratings given by customers for products or retailers
DROP TABLE IF EXISTS `rating`;
CREATE TABLE `rating` (
  `rating_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `customer_id` INT UNSIGNED NOT NULL,
  `product_id` INT UNSIGNED DEFAULT NULL,
  `retailer_id` INT UNSIGNED DEFAULT NULL,
  `rating_value` TINYINT UNSIGNED NOT NULL,
  `source` ENUM('product','retailer','order') NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`rating_id`),
  KEY `idx_rating_customer` (`customer_id`),
  KEY `idx_rating_product` (`product_id`),
  KEY `idx_rating_retailer` (`retailer_id`),
  CONSTRAINT `chk_rating_value` CHECK (`rating_value` BETWEEN 1 AND 5),
  CONSTRAINT `fk_rating_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_rating_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_rating_retailer` FOREIGN KEY (`retailer_id`) REFERENCES `retailer` (`retailer_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reviews written by customers for products
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `review_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `customer_id` INT UNSIGNED NOT NULL,
  `product_id` INT UNSIGNED NOT NULL,
  `rating_id` INT UNSIGNED DEFAULT NULL,
  `title` VARCHAR(200) DEFAULT NULL,
  `review_text` TEXT NOT NULL,
  `review_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` ENUM('published','pending','hidden') NOT NULL DEFAULT 'pending',
  PRIMARY KEY (`review_id`),
  KEY `idx_review_customer` (`customer_id`),
  KEY `idx_review_product` (`product_id`),
  KEY `idx_review_rating` (`rating_id`),
  CONSTRAINT `fk_review_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_review_rating` FOREIGN KEY (`rating_id`) REFERENCES `rating` (`rating_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Customer wishlist items
DROP TABLE IF EXISTS `wishlist`;
CREATE TABLE `wishlist` (
  `wishlist_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `customer_id` INT UNSIGNED NOT NULL,
  `product_id` INT UNSIGNED NOT NULL,
  `added_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`wishlist_id`),
  UNIQUE KEY `uk_wishlist_customer_product` (`customer_id`,`product_id`),
  KEY `idx_wishlist_customer` (`customer_id`),
  CONSTRAINT `fk_wishlist_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_wishlist_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Search history for personalization and analytics
DROP TABLE IF EXISTS `search_history`;
CREATE TABLE `search_history` (
  `search_history_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `customer_id` INT UNSIGNED NOT NULL,
  `search_query` VARCHAR(400) NOT NULL,
  `filters` JSON DEFAULT NULL,
  `result_count` INT UNSIGNED DEFAULT NULL,
  `searched_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`search_history_id`),
  KEY `idx_search_history_customer` (`customer_id`,`searched_at`),
  CONSTRAINT `fk_search_history_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Notifications sent to users
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `notification_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `customer_id` INT UNSIGNED DEFAULT NULL,
  `retailer_id` INT UNSIGNED DEFAULT NULL,
  `admin_id` INT UNSIGNED DEFAULT NULL,
  `title` VARCHAR(180) NOT NULL,
  `message` TEXT NOT NULL,
  `notification_type` ENUM('system','offer','order','security','reminder') NOT NULL,
  `is_read` TINYINT(1) NOT NULL DEFAULT 0,
  `sent_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `read_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`notification_id`),
  KEY `idx_notification_customer` (`customer_id`,`is_read`),
  KEY `idx_notification_retailer` (`retailer_id`,`is_read`),
  KEY `idx_notification_admin` (`admin_id`,`is_read`),
  CONSTRAINT `fk_notification_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_notification_retailer` FOREIGN KEY (`retailer_id`) REFERENCES `retailer` (`retailer_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_notification_admin` FOREIGN KEY (`admin_id`) REFERENCES `admin` (`admin_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OTP verification codes
DROP TABLE IF EXISTS `email_otp`;
CREATE TABLE `email_otp` (
  `otp_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_type` ENUM('customer','retailer') NOT NULL,
  `user_id` INT UNSIGNED NOT NULL,
  `email` VARCHAR(180) NOT NULL,
  `otp_code` VARCHAR(8) NOT NULL,
  `expires_at` DATETIME NOT NULL,
  `is_used` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`otp_id`),
  KEY `idx_email_otp_user` (`user_type`,`user_id`),
  KEY `idx_email_otp_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Login history for auditing and security
DROP TABLE IF EXISTS `login_history`;
CREATE TABLE `login_history` (
  `login_history_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_type` ENUM('customer','retailer','admin') NOT NULL,
  `user_id` INT UNSIGNED NOT NULL,
  `login_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `logout_time` DATETIME DEFAULT NULL,
  `ip_address` VARCHAR(45) DEFAULT NULL,
  `user_agent` VARCHAR(255) DEFAULT NULL,
  `login_status` ENUM('success','failure') NOT NULL DEFAULT 'success',
  `failure_reason` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`login_history_id`),
  KEY `idx_login_history_user` (`user_type`,`user_id`,`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Audit trail for administrative and system events
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log` (
  `audit_log_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `actor_type` ENUM('customer','retailer','admin','system') NOT NULL,
  `actor_id` INT UNSIGNED DEFAULT NULL,
  `action` VARCHAR(180) NOT NULL,
  `entity_type` VARCHAR(120) DEFAULT NULL,
  `entity_id` INT UNSIGNED DEFAULT NULL,
  `details` JSON DEFAULT NULL,
  `ip_address` VARCHAR(45) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`audit_log_id`),
  KEY `idx_audit_log_actor` (`actor_type`,`actor_id`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- AI-generated recommendations for customers
DROP TABLE IF EXISTS `ai_recommendation`;
CREATE TABLE `ai_recommendation` (
  `recommendation_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `customer_id` INT UNSIGNED NOT NULL,
  `product_id` INT UNSIGNED NOT NULL,
  `score` DECIMAL(5,4) NOT NULL,
  `reason` VARCHAR(255) DEFAULT NULL,
  `source` ENUM('collaborative_filtering','content_based','trending','personalized') NOT NULL DEFAULT 'personalized',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`recommendation_id`),
  KEY `idx_ai_recommendation_customer` (`customer_id`,`score`),
  CONSTRAINT `fk_ai_recommendation_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ai_recommendation_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Coupons that customers can apply to orders
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
  `coupon_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(80) NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  `discount_type` ENUM('percentage','fixed_amount','free_shipping') NOT NULL,
  `discount_value` DECIMAL(10,2) NOT NULL,
  `min_order_value` DECIMAL(10,2) DEFAULT NULL,
  `max_discount_value` DECIMAL(10,2) DEFAULT NULL,
  `valid_from` DATETIME NOT NULL,
  `valid_until` DATETIME NOT NULL,
  `usage_limit` INT UNSIGNED DEFAULT NULL,
  `per_customer_limit` INT UNSIGNED DEFAULT NULL,
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`coupon_id`),
  UNIQUE KEY `uk_coupon_code` (`code`),
  KEY `idx_coupon_validity` (`valid_from`,`valid_until`,`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Coupon redemptions by customers
DROP TABLE IF EXISTS `coupon_redemption`;
CREATE TABLE `coupon_redemption` (
  `redemption_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `coupon_id` INT UNSIGNED NOT NULL,
  `customer_id` INT UNSIGNED NOT NULL,
  `order_id` INT UNSIGNED DEFAULT NULL,
  `redeemed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `discount_amount` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`redemption_id`),
  UNIQUE KEY `uk_coupon_redemption` (`coupon_id`,`customer_id`,`order_id`),
  KEY `idx_coupon_redemption_customer` (`customer_id`),
  KEY `idx_coupon_redemption_coupon` (`coupon_id`),
  CONSTRAINT `fk_coupon_redemption_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`coupon_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_coupon_redemption_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reports generated by customers, retailers or admins
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report` (
  `report_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `reporter_type` ENUM('customer','retailer','admin','anonymous') NOT NULL,
  `reporter_id` INT UNSIGNED DEFAULT NULL,
  `report_type` ENUM('product_issue','order_issue','fraud','feedback','technical','other') NOT NULL,
  `entity_type` VARCHAR(120) DEFAULT NULL,
  `entity_id` INT UNSIGNED DEFAULT NULL,
  `subject` VARCHAR(180) NOT NULL,
  `description` TEXT NOT NULL,
  `status` ENUM('open','in_review','resolved','closed') NOT NULL DEFAULT 'open',
  `assigned_to_admin_id` INT UNSIGNED DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`report_id`),
  KEY `idx_report_status` (`status`,`created_at`),
  CONSTRAINT `fk_report_admin` FOREIGN KEY (`assigned_to_admin_id`) REFERENCES `admin` (`admin_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample data
INSERT INTO `admin` (`username`, `email`, `password_hash`, `full_name`, `role`) VALUES
  ('admin_localmart', 'admin@localmart.ai', '$2y$12$examplehash', 'Priya Mehta', 'super_admin');

INSERT INTO `category` (`name`, `slug`, `description`) VALUES
  ('Groceries', 'groceries', 'Everyday grocery essentials and packaged foods'),
  ('Home Care', 'home-care', 'Cleaning supplies and home essentials');

INSERT INTO `customer` (`first_name`, `last_name`, `email`, `password_hash`, `phone`, `is_verified`) VALUES
  ('Maria', 'Lopez', 'maria@example.com', '$2y$12$customerhash', '9876543210', 1);

INSERT INTO `retailer` (`company_name`, `contact_name`, `email`, `password_hash`, `phone`, `verified`, `status`) VALUES
  ('LocalMart Grocers', 'Anil Sharma', 'anil@localmartgrocers.com', '$2y$12$retailerhash', '9123456780', 1, 'approved');

INSERT INTO `store` (`retailer_id`, `store_name`, `slug`, `address_line1`, `city`, `state`, `postal_code`, `country`, `phone`) VALUES
  (1, 'LocalMart Central', 'localmart-central', '123 Main Street', 'Mumbai', 'Maharashtra', '400001', 'India', '9123456780');

INSERT INTO `product` (`category_id`, `store_id`, `sku`, `name`, `description`, `brand`, `base_price`) VALUES
  (1, 1, 'LMG-001', 'Organic Turmeric Powder', 'High-quality organic turmeric for cooking and wellness.', 'LocalMart', 199.00),
  (1, 1, 'LMG-002', 'Fresh Whole Wheat Flour', 'Stone-ground whole wheat flour for breads and snacks.', 'LocalMart', 120.00),
  (1, 1, 'LMG-003', 'Sparkling Mineral Water 1L', 'Refreshing mineral water with natural bubbles.', 'LocalMart', 55.00),
  (2, 1, 'LMH-001', 'Eco Dishwashing Liquid', 'Plant-based dishwashing liquid for gentle, effective cleaning.', 'LocalMart Home', 89.00);

INSERT INTO `product_image` (`product_id`, `image_url`, `alt_text`, `display_order`) VALUES
  (1, 'https://cdn.localmart.ai/products/turmeric-1.jpg', 'Organic turmeric powder jar', 1),
  (2, 'https://cdn.localmart.ai/products/whole-wheat-flour-1.jpg', 'Whole wheat flour pack', 1),
  (3, 'https://cdn.localmart.ai/products/mineral-water-1.jpg', 'Sparkling mineral water bottle', 1),
  (4, 'https://cdn.localmart.ai/products/dishwashing-liquid-1.jpg', 'Eco dishwashing liquid bottle', 1);

INSERT INTO `inventory` (`product_id`, `store_id`, `quantity`, `available_quantity`, `reorder_threshold`) VALUES
  (1, 1, 120, 110, 10),
  (2, 1, 90, 88, 8),
  (3, 1, 180, 175, 20),
  (4, 1, 60, 58, 5);

INSERT INTO `offer` (`title`, `offer_type`, `store_id`, `discount_type`, `discount_value`, `start_date`, `end_date`) VALUES
  ('Weekend Grocery Deal', 'store', 1, 'percentage', 10.00, '2026-07-01 00:00:00', '2026-07-03 23:59:59');

INSERT INTO `rating` (`customer_id`, `product_id`, `rating_value`, `source`) VALUES
  (1, 1, 5, 'product');

INSERT INTO `review` (`customer_id`, `product_id`, `rating_id`, `review_text`, `status`) VALUES
  (1, 1, 1, 'Great quality and fresh aroma.', 'published');

INSERT INTO `wishlist` (`customer_id`, `product_id`) VALUES
  (1, 1);

INSERT INTO `search_history` (`customer_id`, `search_query`, `filters`, `result_count`) VALUES
  (1, 'organic turmeric powder', JSON_OBJECT('category', 'Groceries'), 12);

INSERT INTO `notification` (`customer_id`, `title`, `message`, `notification_type`) VALUES
  (1, 'New offer available', '10% off on your favorite turmeric powder.', 'offer');

INSERT INTO `email_otp` (`user_type`, `user_id`, `email`, `otp_code`, `expires_at`) VALUES
  ('customer', 1, 'maria@example.com', '428615', '2026-06-27 15:30:00');

INSERT INTO `login_history` (`user_type`, `user_id`, `ip_address`, `user_agent`, `login_status`) VALUES
  ('customer', 1, '203.0.113.45', 'Mozilla/5.0', 'success');

INSERT INTO `audit_log` (`actor_type`, `actor_id`, `action`, `entity_type`, `entity_id`, `details`, `ip_address`) VALUES
  ('admin', 1, 'approve_retailer', 'retailer', 1, JSON_OBJECT('comment', 'Retailer approved after verification'), '203.0.113.5');

INSERT INTO `ai_recommendation` (`customer_id`, `product_id`, `score`, `reason`, `source`) VALUES
  (1, 1, 0.8725, 'Frequently viewed with similar grocery items', 'personalized');

INSERT INTO `coupon` (`code`, `description`, `discount_type`, `discount_value`, `valid_from`, `valid_until`, `usage_limit`, `per_customer_limit`) VALUES
  ('SUMMER10', '10% off on grocery purchases during July', 'percentage', 10.00, '2026-07-01 00:00:00', '2026-07-31 23:59:59', 1000, 1);

INSERT INTO `coupon_redemption` (`coupon_id`, `customer_id`, `discount_amount`) VALUES
  (1, 1, 18.00);

INSERT INTO `report` (`reporter_type`, `reporter_id`, `report_type`, `subject`, `description`, `status`) VALUES
  ('customer', 1, 'product_issue', 'Damaged packaging', 'The turmeric tin was dented when delivered.', 'open');

SET FOREIGN_KEY_CHECKS = 1;
