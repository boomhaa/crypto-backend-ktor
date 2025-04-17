package com.example.services

import com.example.db.repositories.TradingPairsRepository
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
                    logger.error("Error while updating data: $e")
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
            logger.error(e.toString() + e.message)
            throw IllegalStateException(e.message)
        }
    }

    fun getPopularPairs(): List<PairInfo> {
        val now = Instant.now()
        if (Duration.between(lastUpdateTime, now) > updateInterval) {
            refreshDataFromExchange()
        }
        return tradingPairsRepository.findPopular()
    }

    fun getAllPairs(): List<PairInfo> {
        return tradingPairsRepository.findAll()
    }

}