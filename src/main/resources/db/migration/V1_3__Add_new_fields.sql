ALTER TABLE trading_pairs
    ADD COLUMN high_price_24h DECIMAL(100, 8),
    ADD COLUMN low_price_24h DECIMAL(100, 8),
    ADD COLUMN volume_base_asset DECIMAL(100, 8),
    ADD COLUMN volume_quote_asset DECIMAL(100, 8);