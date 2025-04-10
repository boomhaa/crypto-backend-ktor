package com.example.services

import com.example.dto.PairInfo
import com.example.exchanges.mexc.MexcClient

class PairSerivces(private val mexcClient: MexcClient) {

    private val popularPairs = listOf("BTCUSDT", "ETHUSDT", "BNBUSDT", "SOLUSDT", "XRPUSDT")

    fun getPopularPairs(): List<PairInfo> {
        return mexcClient.getTraidingPairs()
            .filter {
                it.pair in popularPairs
            }

    }

    fun getAllPairs(): List<PairInfo> {
        return mexcClient.getTraidingPairs()
    }

}