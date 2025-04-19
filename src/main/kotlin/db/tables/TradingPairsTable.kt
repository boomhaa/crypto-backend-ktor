package com.example.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object TradingPairsTable : Table("trading_pairs") {
    val id = integer("id").autoIncrement()
    val pair = varchar("pair", 100)
    val baseAsset = varchar("base_asset", 100)
    val quoteAsset = varchar("quote_asset", 100)
    val price = decimal("price", 100, 8).nullable()
    val lastUpdated = timestamp("last_updated").default(Instant.now())
    val highPrice24h = decimal("high_price_24h", 100, 8).nullable()
    val lowPrice24h = decimal("low_price_24h", 100, 8).nullable()
    val volumeBaseAsset = decimal("volume_base_asset", 100, 8).nullable()
    val volumeQuoteAsset = decimal("volume_quote_asset", 100, 8).nullable()


    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex("idx_trading_pairs_pair", pair)
    }
}