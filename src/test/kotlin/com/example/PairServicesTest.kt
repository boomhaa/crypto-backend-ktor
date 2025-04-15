package com.example

import com.example.dto.PairInfo
import com.example.exchanges.mexc.MexcClient
import com.example.services.PairSerivces
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class PairServicesTest {

    private lateinit var mexcClient: MexcClient
    private lateinit var pairServices: PairSerivces

    @BeforeEach
    fun setup() {
        mexcClient = mock()
        pairServices = PairSerivces(mexcClient)
    }

    @Test
    fun `getAllPairs should return all trading pairs from client`() {
        val mockPairs = listOf(
            PairInfo("BTCUSDT", "BTC", "USDT"),
            PairInfo("DOGEUSDT", "DOGE", "USDT")
        )
        whenever(mexcClient.getTraidingPairs()).thenReturn(mockPairs)

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
        whenever(mexcClient.getTraidingPairs()).thenReturn(mockPairs)

        val result = pairServices.getPopularPairs()

        assertEquals(2, result.size)
        assertTrue(result.any { it.pair == "BTCUSDT" })
        assertTrue(result.any { it.pair == "ETHUSDT" })
        assertFalse(result.any { it.pair == "DOGEUSDT" })
    }

    @Test
    fun `getPopularPairs should return empty list when no match`() {
        val mockPairs = listOf(PairInfo("DOGEUSDT", "DOGE", "USDT"))
        whenever(mexcClient.getTraidingPairs()).thenReturn(mockPairs)

        val result = pairServices.getPopularPairs()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllPairs should return empty list when client returns empty`() {
        val mockMexc = mock<MexcClient>()
        whenever(mockMexc.getTraidingPairs()).thenReturn(emptyList())

        val service = PairSerivces(mockMexc)
        val result = service.getAllPairs()

        assertTrue(result.isEmpty())
    }
}
