CREATE TABLE metadata
(
    id               UUID PRIMARY KEY,
    bot_name         VARCHAR(100),
    messages_deleted int,
    users_banned     int,
    date_start       date
);