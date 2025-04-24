package com.example.services

import com.example.configs.ExchangeConstants
import com.example.db.repositories.PairLastTradeRepository
import com.example.db.repositories.TradingPairsRepository
import com.example.dto.PairDetailInfo
import com.example.dto.PairInfo
import com.example.dto.PairTradeInfo
import com.example.dto.TradeInfo
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
    private val tradingPairsRepository: TradingPairsRepository,
    private val pairLastTradeRepository: PairLastTradeRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val updateInterval = Duration.ofMinutes(2)
    private val saveInterval = Duration.ofSeconds(1)


    fun startDataRefreshJob() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    logger.info("Function for saving or updating data from exchange is running")
                    refreshDataFromExchange()
                    delay(updateInterval.toMillis())
                } catch (e: Exception) {
                    logger.error("Error while updating data: $e: line 45")
                }
            }
        }
    }

    private fun refreshDataFromExchange() {
        try {
            val pairs = mexcClient.getTraidingPairs()
            logger.info("Data was sent for saving")
            tradingPairsRepository.saveAll(pairs)

        } catch (e: Exception) {
            logger.error(e.toString() + e.message + " line 58")
        }
    }

    fun startLastTradesRefreshJob() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    logger.info("Function for saving last trades from exchange is running")
                    refreshLastTradesFromExchange()
                    delay(saveInterval.toMillis())
                } catch (e: Exception) {
                    logger.error("Error while insert last trades: $e: ${e.message}: line 3")
                }
            }
        }
    }

    private fun refreshLastTradesFromExchange() {
        ExchangeConstants.ALL_PAIRS.forEach { pair ->
            try {
                val lastTrades = mexcClient.getPairLastTrades(pair.key, "50")
                pairLastTradeRepository.saveTrades(lastTrades, pair.value)

            }catch (e: Exception){
                logger.error(e.toString() + e.message + " line ")
            }
        }
    }

    fun getPopularPairs(): List<PairInfo> {
        return tradingPairsRepository.findPopular()
    }

    fun getAllPairs(): List<PairInfo> {
        return tradingPairsRepository.findAll()
    }

    fun searchPairs(query: String): List<PairInfo> {
        return tradingPairsRepository.findByQuery(query)
    }

    fun getPairDetailInfo(pair: String): List<PairDetailInfo> {
        return tradingPairsRepository.findByName(pair)
    }

    fun getLastTrades(pair: String): List<PairTradeInfo>{
        val pairId = ExchangeConstants.ALL_PAIRS[pair]!!
        logger.info("$pair $pairId")
        return pairLastTradeRepository.getLastTrades(pairId)
    }
}