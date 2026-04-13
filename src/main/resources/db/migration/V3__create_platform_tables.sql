-- Добавляем колонку role в таблицу users
-- По умолчанию все существующие пользователи — USER
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- Опционально: можно добавить проверку значения
-- (ограничение будет работать для новых записей)
ALTER TABLE users
    ADD CONSTRAINT check_role CHECK (role IN ('USER', 'ADMIN'));