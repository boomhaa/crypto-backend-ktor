package com.example.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object TradingPairsTable : Table("trading_pairs") {
    val id = integer("id").autoIncrement()
    val pair = varchar("pair", 20)
    val baseAsset = varchar("base_asset", 10)
    val quoteAsset = varchar("quote_asset", 10)
    val price = varchar("price", 20).nullable()
    val lastUpdated = timestamp("last_updated").default(Instant.now())

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex("idx_trading_pairs_pair", pair)
    }
}