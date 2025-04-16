package com.example

import com.example.db.repositories.TradingPairsRepository
import com.example.dto.PairInfo
import com.example.exchanges.mexc.MexcClient
import com.example.services.PairServices
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class PairServicesTest {

    private lateinit var mexcClient: MexcClient
    private lateinit var pairServices: PairServices
    private lateinit var tradingPairsRepository: TradingPairsRepository

    @BeforeEach
    fun setup() {
        mexcClient = mock()
        tradingPairsRepository = mock()
        pairServices = PairServices(mexcClient, tradingPairsRepository)
    }

    @Test
    fun `getAllPairs should return all trading pairs from repository`() {
        val mockPairs = listOf(
            PairInfo("BTCUSDT", "BTC", "USDT"),
            PairInfo("DOGEUSDT", "DOGE", "USDT")
        )
        whenever(tradingPairsRepository.findAll()).thenReturn(mockPairs)

        val result = pairServices.getAllPairs()

        assertEquals(mockPairs, result)
    }

    @Test
    fun `getPopularPairs should return only popular trading pairs`() {
        val mockPairs = listOf(
            PairInfo("BTCUSDT", "BTC", "USDT"),
            PairInfo("DOGEUSDT", "DOGE", "USDT"),
            PairInfo("ETHUSDT", "ETH", "USDT")
        )
        whenever(tradingPairsRepository.findPopular()).thenReturn(mockPairs)

        val result = pairServices.getPopularPairs()

        assertEquals(mockPairs, result)
    }

    @Test
    fun `getPopularPairs should return empty list when no match`() {
        whenever(tradingPairsRepository.findPopular()).thenReturn(emptyList())

        val result = pairServices.getPopularPairs()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllPairs should return empty list when repository returns empty`() {
        whenever(tradingPairsRepository.findAll()).thenReturn(emptyList())

        val result = pairServices.getAllPairs()

        assertTrue(result.isEmpty())
    }
}
