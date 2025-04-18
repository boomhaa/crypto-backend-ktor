package com.example.db.repositories

import com.example.configs.ExchangeConstants
import com.example.db.tables.TradingPairsTable
import com.example.dto.PairInfo
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import java.time.Instant

class TradingPairsRepository {

    private val logger = LoggerFactory.getLogger(TradingPairsRepository::class.java)

    fun saveAll(pairs: List<PairInfo>) = transaction {
        try {
            pairs.forEach { pairInfo ->
                val exists =
                    TradingPairsTable.select { TradingPairsTable.pair eq pairInfo.pair }
                        .limit(1)
                        .count() > 0
                if (exists){
                    TradingPairsTable.update({TradingPairsTable.pair eq pairInfo.pair}){
                        it[price] = pairInfo.price?.toBigDecimal()
                        it[lastUpdated] = Instant.now()
                    }
                }else{
                    TradingPairsTable.insert {
                        it[pair] = pairInfo.pair
                        it[baseAsset] = pairInfo.baseAsset
                        it[quoteAsset] = pairInfo.quoteAsset
                        it[price] = pairInfo.price?.toBigDecimal()
                    }
                }
            }

            logger.info("Data was saved successfully")
        } catch (e: Exception) {
            logger.error("Error while saving data: $e: ${e.message}")
        }
    }

    fun findAll(): List<PairInfo> = transaction {
        TradingPairsTable.selectAll().map { row ->
            val price = try {
                row[TradingPairsTable.price]?.toPlainString()
            } catch (e: Exception) {
                logger.error("findAll: Can't convert price to string: $e: ${e.message}")
                null
            }

            PairInfo(
                pair = row[TradingPairsTable.pair],
                baseAsset = row[TradingPairsTable.baseAsset],
                quoteAsset = row[TradingPairsTable.quoteAsset],
                price = price,
                lastUpdated = row[TradingPairsTable.lastUpdated].toString()
            )
        }
    }

    fun findPopular(): List<PairInfo> = transaction {
        TradingPairsTable.select {
            TradingPairsTable.pair inList ExchangeConstants.POPULAR_PAIRS
        }.map { row ->
            val price = try {
                row[TradingPairsTable.price]?.toPlainString()
            } catch (e: Exception) {
                logger.error("findPopular: Can't convert price to string: $e: ${e.message}")
                null
            }
            PairInfo(
                pair = row[TradingPairsTable.pair],
                baseAsset = row[TradingPairsTable.baseAsset],
                quoteAsset = row[TradingPairsTable.quoteAsset],
                price = price,
                lastUpdated = row[TradingPairsTable.lastUpdated].toString()
            )
        }
    }

    fun findByQuery(query: String): List<PairInfo> = transaction {
        val loweredQuery = "%${query.lowercase()}%"
        TradingPairsTable
            .select { TradingPairsTable.pair.lowerCase() like loweredQuery }
            .map { row ->
                val price = try {
                    row[TradingPairsTable.price]?.toPlainString()
                } catch (e: Exception) {
                    logger.error("findByQuery: Can't convert price to string: $e: ${e.message}")
                    null
                }
                PairInfo(
                    pair = row[TradingPairsTable.pair],
                    baseAsset = row[TradingPairsTable.baseAsset],
                    quoteAsset = row[TradingPairsTable.quoteAsset],
                    price = price,
                    lastUpdated = row[TradingPairsTable.lastUpdated].toString()
                )
            }
    }
}