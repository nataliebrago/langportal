-- Вставляем тестового админа
-- Пароль: "password" (зашифрованный через BCrypt $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG)

INSERT INTO users (email, password, name, surname, registered, role, created_at)
VALUES (
           'admin@language.com',
           '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
           'Admin',
           'Main',
           TRUE,
           'ADMIN',
           NOW()
       )
    ON CONFLICT (email) DO NOTHING;