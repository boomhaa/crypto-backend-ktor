package com.example.db.tables

import com.example.db.tables.TradingPairsTable.autoIncrement
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object PairLastTrades : Table("pair_last_trades") {
    val id = integer("id").autoIncrement()
    val tradingPairId = integer("trading_pair_id")
    val direction = varchar("direction", 4)
    val price = decimal("price", 100, 8)
    val quantity = decimal("quantity", 100, 8)
    val timestamp = timestamp("timestamp")

}