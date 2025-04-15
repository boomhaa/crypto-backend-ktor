package com.example.db.repositories

import com.example.configs.ExchangeConstants
import com.example.db.tables.TradingPairsTable
import com.example.dto.PairInfo
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class TradingPairsRepository {

    fun saveAll(pairs: List<PairInfo>) = transaction {
        pairs.forEach { pairInfo ->
            val existingPair = TradingPairsTable.select {
                TradingPairsTable.pair eq pairInfo.pair
            }.singleOrNull()

            if (existingPair != null){
                TradingPairsTable.update({TradingPairsTable.pair eq pairInfo.pair}) {
                    it[price] = pairInfo.price
                }
            }else{
                TradingPairsTable.insert {
                    it[pair] = pairInfo.pair
                    it[baseAsset] = pairInfo.baseAsset
                    it[quoteAsset] = pairInfo.quoteAsset
                    it[price] = pairInfo.price
                }
            }
        }
    }

    fun findAll(): List<PairInfo> = transaction{
        TradingPairsTable.selectAll().map { row ->
            PairInfo(
                pair = row[TradingPairsTable.pair],
                baseAsset = row[TradingPairsTable.baseAsset],
                quoteAsset = row[TradingPairsTable.quoteAsset],
                price = row[TradingPairsTable.price]
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
                price = row[TradingPairsTable.price]
            )

        }
    }

}