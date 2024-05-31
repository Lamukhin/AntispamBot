CREATE TABLE message_count
(
    id               UUID PRIMARY KEY,
    id_chat_telegram bigint,
    counter          bigint
);