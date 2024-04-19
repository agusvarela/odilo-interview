-- Create Users table
CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                date_of_birth DATE NOT NULL
);

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
                 id INT PRIMARY KEY,
                 name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
                 user_id INT,
                 role_id INT,
                 FOREIGN KEY (user_id) REFERENCES users(id),
                 FOREIGN KEY (role_id) REFERENCES roles(id),
                 PRIMARY KEY (user_id, role_id)
);

-- Insert Roles
INSERT INTO roles (id, name) VALUES
                                 (1, 'ROLE_ADMIN'),
                                 (2, 'ROLE_USER');

-- Insert Admin with pass = 'adminpass'
INSERT INTO users (username, email, password, date_of_birth)
VALUES ('admin', 'admin@example.com', '$2a$10$VAByYbXqZfFNJSYHyjaQnuhJ1VhCG/7gFAAhZILcpM1Deh1EiV38y', '1990-01-01');

INSERT INTO user_roles (user_id, role_id)
VALUES (1,1);