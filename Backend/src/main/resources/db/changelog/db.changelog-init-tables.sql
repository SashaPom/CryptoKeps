--liquibase formatted sql

--changeset sashapom:1
--preconditions onFail:MARK_RAN
--precondition-type:sql-check
--precondition-sql:SELECT COUNT(*) = 0 FROM information_schema.tables WHERE table_name = 'account_info'
CREATE TABLE account_info (
                              id UUID NOT NULL,
                              api_key VARCHAR(255),
                              api_secret VARCHAR(255),
                              is_connected BOOLEAN,
                              key_name VARCHAR(255) NOT NULL,
                              CONSTRAINT pk_account_info PRIMARY KEY (id),
                              CONSTRAINT uk_account_info_keyname UNIQUE (key_name)
);

--changeset sashapom:2
--preconditions onFail:MARK_RAN
--precondition-type:sql-check
--precondition-sql:SELECT COUNT(*) = 0 FROM information_schema.tables WHERE table_name = 'trade_history'
CREATE TABLE trade_history (
                               id UUID NOT NULL,
                               account_id UUID NOT NULL,
                               symbol VARCHAR(255) NOT NULL,
                               side VARCHAR(10) NOT NULL CHECK (side IN ('BUY','SELL')),
                               quantity NUMERIC(38,2) NOT NULL,
                               entry_price NUMERIC(38,2),
                               close_price NUMERIC(38,2),
                               stop_loss NUMERIC(38,2),
                               take_profit NUMERIC(38,2),
                               pnl NUMERIC(38,2),
                               "timestamp" TIMESTAMP(6) NOT NULL,
                               CONSTRAINT pk_trade_history PRIMARY KEY (id),
                               CONSTRAINT fk_trade_history_account FOREIGN KEY (account_id) REFERENCES account_info (id)
);

--changeset sashapom:seed-context context:dev
--preconditions onFail:MARK_RAN
--precondition-type:sql-check
--precondition-sql:SELECT COUNT(*) = 0 FROM account_info WHERE key_name = 'MainPropKey'
INSERT INTO account_info (id, api_key, api_secret, is_connected, key_name)
VALUES ('3b30748d-ead3-49c6-a6c1-db7e83d7eb66', 'IChy4VJLtjzwGevPUf', 'EaY2x2ikiQSyZaJHB3yxBYQ9FdQjFKydWyqn', true, 'MainPropKey');

--changeset sashapom:seed2-context context:dev
--preconditions onFail:MARK_RAN
--precondition-type:sql-check
--precondition-sql:SELECT COUNT(*) = 0 FROM trade_history WHERE id = 'dcabe708-3e28-4c2f-a58f-4064c628f24b'
INSERT INTO trade_history (id, account_id, symbol, side, quantity, entry_price, close_price, stop_loss, take_profit, pnl, "timestamp")
VALUES ('dcabe708-3e28-4c2f-a58f-4064c628f24b','3b30748d-ead3-49c6-a6c1-db7e83d7eb66','SOLUSDT','BUY', 0.90, 181.08, 181.30, 180.53, 236.03, 0.20, '2025-08-13T04:57:00');
