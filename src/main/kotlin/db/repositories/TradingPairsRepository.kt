package com.example.db.repositories

import com.example.configs.ExchangeConstants
import com.example.db.tables.TradingPairsTable
import com.example.dto.PairDetailInfo
import com.example.dto.PairInfo
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.Instant

class TradingPairsRepository {

    private val logger = LoggerFactory.getLogger(TradingPairsRepository::class.java)

    fun saveAll(pairs: List<PairDetailInfo>) = transaction {
        try {
            pairs.forEach { pairDetailInfo ->
                val exists =
                    TradingPairsTable.select { TradingPairsTable.pair eq pairDetailInfo.pair }
                        .limit(1)
                        .count() > 0
                if (exists){
                    TradingPairsTable.update({TradingPairsTable.pair eq pairDetailInfo.pair}){
                        it[price] = pairDetailInfo.price?.toBigDecimal()
                        it[highPrice24h] = pairDetailInfo.highPrice?.toBigDecimal()
                        it[lowPrice24h] = pairDetailInfo.lowPrice?.toBigDecimal()
                        it[volumeBaseAsset] = pairDetailInfo.volume?.toBigDecimal()
                        it[volumeQuoteAsset] = pairDetailInfo.quoteVolume?.toBigDecimal()
                        it[lastUpdated] = Instant.now()

                    }
                }else{
                    TradingPairsTable.insert {
                        it[pair] = pairDetailInfo.pair
                        it[baseAsset] = pairDetailInfo.baseAsset
                        it[quoteAsset] = pairDetailInfo.quoteAsset
                        it[price] = pairDetailInfo.price?.toBigDecimal()
                        it[highPrice24h] = pairDetailInfo.highPrice?.toBigDecimal()
                        it[lowPrice24h] = pairDetailInfo.lowPrice?.toBigDecimal()
                        it[volumeBaseAsset] = pairDetailInfo.volume?.toBigDecimal()
                        it[volumeQuoteAsset] = pairDetailInfo.quoteVolume?.toBigDecimal()
                        it[lastUpdated] = Instant.now()
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
                row[TradingPairsTable.price]?.toFormattedString()
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
                row[TradingPairsTable.price]?.toFormattedString()
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
        addLogger(StdOutSqlLogger)
        val loweredQuery = "%${query.lowercase()}%"
        TradingPairsTable
            .select { TradingPairsTable.pair.lowerCase() like loweredQuery }
            .map { row ->
                val price = try {
                    row[TradingPairsTable.price]?.toFormattedString()
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

    fun findByName(pair: String): List<PairDetailInfo> = transaction{
        val loweredPair = pair.lowercase()
        TradingPairsTable.select { TradingPairsTable.pair.lowerCase() eq loweredPair }
            .map {
                    row ->
                val price = try {
                    row[TradingPairsTable.price]?.toFormattedString()
                } catch (e: Exception) {
                    logger.error("findByName: Can't convert price to string: $e: ${e.message}")
                    null
                }
                val highPrice24h = try {
                    row[TradingPairsTable.highPrice24h]?.toFormattedString()
                } catch (e: Exception) {
                    logger.error("findByName: Can't convert highPrice24h to string: $e: ${e.message}")
                    null
                }
                val lowPrice24h = try {
                    row[TradingPairsTable.lowPrice24h]?.toFormattedString()
                } catch (e: Exception) {
                    logger.error("findByName: Can't convert lowPrice24h to string: $e: ${e.message}")
                    null
                }
                val volumeBaseAsset24h = try {
                    row[TradingPairsTable.volumeBaseAsset]?.toFormattedString()
                } catch (e: Exception) {
                    logger.error("findByName: Can't convert volumeBaseAsset24h to string: $e: ${e.message}")
                    null
                }
                val volumeQuoteAsset24h = try {
                    row[TradingPairsTable.volumeQuoteAsset]?.toFormattedString()
                } catch (e: Exception) {
                    logger.error("findByName: Can't convert volumeQuoteAsset24h to string: $e: ${e.message}")
                    null
                }
                PairDetailInfo(
                    pair = row[TradingPairsTable.pair],
                    baseAsset = row[TradingPairsTable.baseAsset],
                    quoteAsset = row[TradingPairsTable.quoteAsset],
                    price = price,
                    highPrice = highPrice24h,
                    lowPrice = lowPrice24h,
                    volume = volumeBaseAsset24h,
                    quoteVolume = volumeQuoteAsset24h,
                    lastUpdated = row[TradingPairsTable.lastUpdated].toString()
                )
            }
    }

    private fun BigDecimal?.toFormattedString(): String? {
        if (this == null) return null

        val plainString = this.toPlainString()
        return if (plainString.contains('.')) {
            plainString.replace(Regex("0*$"), "").replace(Regex("\\.$"), "")
        } else {
            plainString
        }
    }
}