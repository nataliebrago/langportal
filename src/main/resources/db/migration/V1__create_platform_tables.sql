-- --------------------------------------------------
-- Миграция: Создание всех таблиц образовательной платформы
-- --------------------------------------------------

-- 1. Таблица пользователей (users)
-- Студенты, преподаватели, администраторы
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    surname VARCHAR(120),
    name VARCHAR(120),
    registered BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Индекс по email (уже покрыт уникальным ключом, но явно указываем)
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Комментарий к таблице
COMMENT ON TABLE users IS 'Пользователи платформы: студенты, преподаватели, администраторы';

-- -----------------------------------------------
-- 2. Таблица курсов (courses)
-- Языковые курсы с фиксированной ценой
CREATE TABLE IF NOT EXISTS courses (
                                       id BIGSERIAL PRIMARY KEY,
                                       title VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL
    CHECK (price >= 0)
    );

-- Индекс по названию курса — для поиска
CREATE INDEX IF NOT EXISTS idx_courses_title ON courses(title);

COMMENT ON TABLE courses IS 'Языковые курсы с фиксированной ценой';
COMMENT ON COLUMN courses.price IS 'Цена курса в рублях/долларах';

-- -----------------------------------------------
-- 3. Таблица покупок (purchases)
-- История покупок курсов пользователями
CREATE TABLE IF NOT EXISTS purchases (
                                         id BIGSERIAL PRIMARY KEY,
                                         user_id BIGINT NOT NULL,
                                         course_id BIGINT NOT NULL,
                                         paid_amount DECIMAL(10,2) NOT NULL
    CHECK (paid_amount >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Внешние ключи
    CONSTRAINT fk_purchase_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_purchase_course
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,

    -- Уникальность: один пользователь не может купить один курс дважды
    CONSTRAINT uk_purchase_user_course
    UNIQUE (user_id, course_id)
    );

-- Индексы для производительности
CREATE INDEX IF NOT EXISTS idx_purchases_user ON purchases(user_id);
CREATE INDEX IF NOT EXISTS idx_purchases_course ON purchases(course_id);
CREATE INDEX IF NOT EXISTS idx_purchases_created ON purchases(created_at);

COMMENT ON TABLE purchases IS 'Покупки курсов пользователями (с историей)';
COMMENT ON COLUMN purchases.paid_amount IS 'Сумма, которую заплатил пользователь (может быть со скидкой)';

-- -----------------------------------------------
-- 4. Таблица подписчиков на скидки (discount_subscribers)
CREATE TABLE IF NOT EXISTS discount_subscribers (
                                                    id BIGSERIAL PRIMARY KEY,
                                                    email VARCHAR(120) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_discount_subs_email ON discount_subscribers(email);

COMMENT ON TABLE discount_subscribers IS 'Email-адреса, подписанные на рассылку скидок';
COMMENT ON COLUMN discount_subscribers.created_at IS 'Дата и время подписки';

