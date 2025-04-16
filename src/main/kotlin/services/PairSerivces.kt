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
    private val updateInterval = Duration.ofMinutes(5)

    init {
        startDataRefreshJob()
    }

    private fun startDataRefreshJob() {
        CoroutineScope(Dispatchers.IO).launch {
            refreshDataFromExchange()
            delay(updateInterval.toMillis())
        }
    }

    private fun refreshDataFromExchange() {
        try {
            val pairs = mexcClient.getTraidingPairs()
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
        return mexcClient.getTraidingPairs()
    }

}