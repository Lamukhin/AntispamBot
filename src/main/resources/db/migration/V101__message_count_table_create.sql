CREATE TABLE message_count
(
    id               SERIAL PRIMARY KEY,
    id_chat_telegram bigint,
    counter          bigint
);
