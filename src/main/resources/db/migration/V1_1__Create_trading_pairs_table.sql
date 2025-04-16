CREATE TABLE IF NOT EXISTS trading_pairs (
    id SERIAL PRIMARY KEY,
    pair VARCHAR(100) NOT NULL UNIQUE,
    base_asset VARCHAR(100) NOT NULL,
    quote_asset VARCHAR(100) NOT NULL,
    price VARCHAR(100),
    last_updated TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_trading_pairs_pair ON trading_pairs(pair);