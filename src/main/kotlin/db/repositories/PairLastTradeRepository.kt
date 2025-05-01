package com.example.db.repositories

import com.example.configs.ExchangeConstants.toFormattedString
import com.example.db.tables.PairLastTrades
import com.example.db.tables.TradingPairsTable
import com.example.dto.PairTradeInfo
import com.example.dto.TradeInfo
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.ZoneId
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInSubQuery

class PairLastTradeRepository {
    private val logger = LoggerFactory.getLogger(TradingPairsRepository::class.java)

    fun saveTrades(lastTrades: List<TradeInfo>, id: Int) = transaction {

        val groupedTrades = lastTrades.groupBy { trade ->
            if (trade.isBuyerMaker) "sell" else "buy"
        }

        groupedTrades.forEach { (direction, trades) ->
            trades.forEach { trade ->
                PairLastTrades.insert {
                    it[tradingPairId] = id
                    it[this.direction] = direction
                    it[price] = trade.price.toBigDecimal()
                    it[quantity] = trade.qty.toBigDecimal()
                    it[timestamp] = Instant.ofEpochMilli(trade.time)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDateTime()
                }
            }
            deletePrevious(id, direction)
        }
    }

    private fun deletePrevious(id: Int, direction: String) = transaction {
        PairLastTrades.deleteWhere {
            (tradingPairId eq id) and
                    (PairLastTrades.direction eq direction) and
                    (PairLastTrades.id notInSubQuery PairLastTrades.slice(PairLastTrades.id)
                        .select {
                            (tradingPairId eq id) and
                                    (PairLastTrades.direction eq direction)
                        }
                        .orderBy(timestamp, SortOrder.DESC)
                        .limit(50))
        }

    }

    fun getLastTrades(id: Int): List<PairTradeInfo> = transaction {
        PairLastTrades.select {
            (PairLastTrades.tradingPairId eq id)
        }
            .orderBy(PairLastTrades.timestamp, SortOrder.DESC)
            .limit(100)
            .map {
                PairTradeInfo(
                    price = it[PairLastTrades.price].toFormattedString(),
                    qty = it[PairLastTrades.quantity].toFormattedString(),
                    time = it[PairLastTrades.timestamp].toKotlinLocalDateTime(),
                    isBuyerMaker = it[PairLastTrades.direction]
                )
            }
    }
}