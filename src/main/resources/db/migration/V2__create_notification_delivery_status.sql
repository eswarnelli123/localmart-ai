-- Create notification_delivery_status table with FK to notification
CREATE TABLE IF NOT EXISTS notification_delivery_status (
  delivery_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  notification_id BIGINT NOT NULL,
  recipient_email VARCHAR(255) NOT NULL,
  delivery_status VARCHAR(32) NOT NULL,
  attempt_count INT DEFAULT 0,
  error_message TEXT,
  sent_at DATETIME,
  last_attempted_at DATETIME,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_notification_id (notification_id),
  CONSTRAINT fk_notification_delivery_status_notification
    FOREIGN KEY (notification_id) REFERENCES notification(notification_id)
    ON DELETE CASCADE
) ENGINE=InnoDB;
