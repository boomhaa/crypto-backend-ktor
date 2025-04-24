CREATE TABLE IF NOT EXISTS pair_last_trades (
    id INT PRIMARY KEY,
    trading_pair_id INT NOT NULL REFERENCES trading_pairs(id),
    direction TEXT CHECK (direction IN ('buy', 'sell')) NOT NULL,
    price DECIMAL(100, 8) NOT NULL,
    quantity DECIMAL(100, 8) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_pair_last_trades ON pair_last_trades (trading_pair_id, direction, timestamp DESC);