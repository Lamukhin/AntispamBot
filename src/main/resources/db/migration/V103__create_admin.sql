CREATE TABLE admin
(
    id               UUID PRIMARY KEY,
    user_telegram_id bigint,
    is_active        boolean
);