-- создаёт БД для Django и отдаёт права текущему суперпользователю контейнера
-- (у нас это POSTGRES_USER=ck_owner из .env)

CREATE DATABASE catalog OWNER ck_owner;
GRANT ALL PRIVILEGES ON DATABASE catalog TO ck_owner;