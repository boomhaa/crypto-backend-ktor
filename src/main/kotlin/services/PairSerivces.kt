package com.example.services

import com.example.configs.ExchangeConstants
import com.example.dto.PairInfo
import com.example.exchanges.mexc.MexcClient
import org.slf4j.LoggerFactory

class PairSerivces(private val mexcClient: MexcClient) {

    private val popularPairs = ExchangeConstants.POPULAR_PAIRS
    private val logger = LoggerFactory.getLogger(this::class.java.canonicalName)

    fun getPopularPairs(): List<PairInfo> {
        return try {
            mexcClient.getTraidingPairs()
                .filter {
                    it.pair in popularPairs
                }
        } catch (e: Exception) {
            logger.error(e.message)
            throw IllegalStateException(e.message)
        }
    }

    fun getAllPairs(): List<PairInfo> {
        return mexcClient.getTraidingPairs()
    }

}