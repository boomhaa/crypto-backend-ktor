package com.example.services

import com.example.db.repositories.TradingPairsRepository
import com.example.dto.PairDetailInfo
import com.example.dto.PairInfo
import com.example.exchanges.mexc.MexcClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant

class PairServices(
    private val mexcClient: MexcClient,
    private val tradingPairsRepository: TradingPairsRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java.canonicalName)

    private var lastUpdateTime: Instant = Instant.MIN
    private val updateInterval = Duration.ofMinutes(2)



    fun startDataRefreshJob() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    logger.info("Function for saving or updating data from exchange is running")
                    refreshDataFromExchange()
                    delay(updateInterval.toMillis())
                } catch (e: Exception) {
                    logger.error("Error while updating data: $e: line 34")
                }
            }
        }
    }

    private fun refreshDataFromExchange() {
        try {
            val pairs = mexcClient.getTraidingPairs()
            logger.info("Data was sent for saving")
            tradingPairsRepository.saveAll(pairs)
            lastUpdateTime = Instant.now()
        } catch (e: Exception) {
            logger.error(e.toString() + e.message + " line 47")
        }
    }

    fun getPopularPairs(): List<PairInfo> {
        return tradingPairsRepository.findPopular()
    }

    fun getAllPairs(): List<PairInfo> {
        return tradingPairsRepository.findAll()
    }

    fun searchPairs(query: String): List<PairInfo>{
        return tradingPairsRepository.findByQuery(query)
    }

    fun getPairDetailInfo(pair: String): List<PairDetailInfo>{
        return tradingPairsRepository.findByName(pair)
    }
}