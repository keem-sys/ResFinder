-- Drop all objects if they exist to ensure a clean slate
DROP ALL OBJECTS;

-- Create the users table for testing
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       full_name VARCHAR(255) NOT NULL,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       registration_date TIMESTAMP NOT NULL
);