package com.example.db.repositories

import com.example.configs.ExchangeConstants
import com.example.db.tables.TradingPairsTable
import com.example.dto.PairInfo
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory

class TradingPairsRepository {

    private val logger = LoggerFactory.getLogger(TradingPairsRepository::class.java)

    fun saveAll(pairs: List<PairInfo>) = transaction {
        try {
            val existingPairs = TradingPairsTable.selectAll().associateBy { it[TradingPairsTable.pair] }

            pairs.forEach { pairInfo ->
                val existingPair = existingPairs[pairInfo.pair]
                if (existingPair != null) {
                    TradingPairsTable.update({ TradingPairsTable.pair eq pairInfo.pair }) {
                        it[price] = pairInfo.price?.toBigDecimal()
                        it[lastUpdated] = java.time.Instant.now()
                    }
                } else {
                    TradingPairsTable.insert {
                        it[pair] = pairInfo.pair
                        it[baseAsset] = pairInfo.baseAsset
                        it[quoteAsset] = pairInfo.quoteAsset
                        it[price] = pairInfo.price?.toBigDecimal()
                    }
                }
            }
            logger.info("Data was saved successfully")
        }catch (e: Exception){
            logger.error("Error while saving data: $e: ${e.message}")
        }
    }

    fun findAll(): List<PairInfo> = transaction{
        TradingPairsTable.selectAll().map { row ->
            PairInfo(
                pair = row[TradingPairsTable.pair],
                baseAsset = row[TradingPairsTable.baseAsset],
                quoteAsset = row[TradingPairsTable.quoteAsset],
                price = row[TradingPairsTable.price]?.toPlainString()
            )
        }
    }

    fun findPopular(): List<PairInfo> = transaction {
        TradingPairsTable.select {
            TradingPairsTable.pair inList ExchangeConstants.POPULAR_PAIRS
        }.map { row ->
            PairInfo(
                pair = row[TradingPairsTable.pair],
                baseAsset = row[TradingPairsTable.baseAsset],
                quoteAsset = row[TradingPairsTable.quoteAsset],
                price = row[TradingPairsTable.price]?.toPlainString()
            )

        }
    }

}