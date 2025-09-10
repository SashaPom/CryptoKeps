#!/usr/bin/env bash
set -euo pipefail

# системный суперюзер, которого создаёт официальный образ
SUPERUSER="${POSTGRES_USER:-postgres}"

# ваши прикладные параметры (как вы и задавали в env)
CK_DB_NAME="${CK_DB_NAME:-ck}"
CK_DB_USER="${CK_DB_USER}"
CK_DB_PASSWORD="${CK_DB_PASSWORD:?CK_DB_PASSWORD is required}"

TW_DB_NAME="${TW_DB_NAME:-catalog}"
TW_DB_USER="${TW_DB_USER}"
TW_DB_PASSWORD="${TW_DB_PASSWORD:?TW_DB_PASSWORD is required}"

echo "[initdb] Creating roles and databases:"
echo "  CK_DB_NAME=${CK_DB_NAME} CK_DB_USER=${CK_DB_USER}"
echo "  TW_DB_NAME=${TW_DB_NAME} TW_DB_USER=${TW_DB_USER}"

# подключаемся суперпользователем, которого реально создал entrypoint
psql -v ON_ERROR_STOP=1 --username "$SUPERUSER" --dbname "postgres" <<SQL
DO \$\$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = '${CK_DB_USER}') THEN
    EXECUTE 'CREATE ROLE ${CK_DB_USER} LOGIN PASSWORD ''''${CK_DB_PASSWORD}''''';
  END IF;
END
\$\$;

DO \$\$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = '${TW_DB_USER}') THEN
    EXECUTE 'CREATE ROLE ${TW_DB_USER} LOGIN PASSWORD ''''${TW_DB_PASSWORD}''''';
  END IF;
END
\$\$;
SQL

if ! psql -tA --username "$SUPERUSER" --dbname "postgres" -c \
  "SELECT 1 FROM pg_database WHERE datname='${CK_DB_NAME}'" | grep -q 1; then
  psql --username "$SUPERUSER" --dbname "postgres" -c \
    "CREATE DATABASE ${CK_DB_NAME} OWNER ${CK_DB_USER}";
fi

if ! psql -tA --username "$SUPERUSER" --dbname "postgres" -c \
  "SELECT 1 FROM pg_database WHERE datname='${TW_DB_NAME}'" | grep -q 1; then
  psql --username "$SUPERUSER" --dbname "postgres" -c \
    "CREATE DATABASE ${TW_DB_NAME} OWNER ${TW_DB_USER}";
fi

psql -v ON_ERROR_STOP=1 --username "$SUPERUSER" --dbname "$CK_DB_NAME" <<SQL
ALTER SCHEMA public OWNER TO ${CK_DB_USER};
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES    TO ${CK_DB_USER};
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES  TO ${CK_DB_USER};
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS  TO ${CK_DB_USER};
GRANT ALL PRIVILEGES ON SCHEMA public TO ${CK_DB_USER};
SQL

psql -v ON_ERROR_STOP=1 --username "$SUPERUSER" --dbname "$TW_DB_NAME" <<SQL
ALTER SCHEMA public OWNER TO ${TW_DB_USER};
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES    TO ${TW_DB_USER};
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES  TO ${TW_DB_USER};
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS  TO ${TW_DB_USER};
GRANT ALL PRIVILEGES ON SCHEMA public TO ${TW_DB_USER};
SQL

echo "[initdb] done."
