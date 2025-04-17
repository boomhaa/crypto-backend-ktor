ALTER TABLE trading_pairs
    ALTER COLUMN price TYPE DECIMAL(20, 8) USING price::numeric;