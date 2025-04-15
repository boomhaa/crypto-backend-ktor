package com.example

import com.example.dto.MexcExchangeInfoResponse
import com.example.dto.MexcPairInfo
import com.example.exchanges.mexc.MexcClient
import com.example.services.HttpService
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*

class MexcClientTest {

    private lateinit var httpService: HttpService
    private lateinit var mexcClient: MexcClient

    @BeforeEach
    fun setup() {
        httpService = mock(HttpService::class.java)
        mexcClient = MexcClient(httpService)
    }

    @Test
    fun `getTraidingPairs should return merged symbol and price info`() {
        val exchangeInfo = MexcExchangeInfoResponse(
            symbols = listOf(
                MexcPairInfo("BTCUSDT", "BTC", "USDT"),
                MexcPairInfo("ETHUSDT", "ETH", "USDT")
            )
        )

        val tickerJson = """
            [
              {"symbol": "BTCUSDT", "price": "50000"},
              {"symbol": "ETHUSDT", "price": "3500"}
            ]
        """.trimIndent()

        `when`(
            httpService.get(contains("exchangeInfo"), anyMap())
        ).thenReturn(Json.encodeToString(exchangeInfo))

        `when`(
            httpService.get(contains("ticker"), anyMap())
        ).thenReturn(tickerJson)

        val result = mexcClient.getTraidingPairs()

        assertEquals(2, result.size)
        assertEquals("BTC", result[0].baseAsset)
        assertEquals("50000", result[0].price)
        assertEquals("ETH", result[1].baseAsset)
        assertEquals("3500", result[1].price)
    }

    @Test
    fun `getTraidingPairs should handle empty ticker response`() {
        val exchangeInfo = MexcExchangeInfoResponse(
            symbols = listOf(MexcPairInfo("BTCUSDT", "BTC", "USDT"))
        )

        `when`(httpService.get(contains("exchangeInfo"), anyMap()))
            .thenReturn(Json.encodeToString(exchangeInfo))

        `when`(httpService.get(contains("ticker"), anyMap()))
            .thenReturn("[]")

        val result = mexcClient.getTraidingPairs()

        assertEquals(1, result.size)
        assertEquals("BTC", result[0].baseAsset)
        assertNull(result[0].price)
    }


    @Test
    fun `should throw error if env variables are missing`() {
        val mockHttpService = HttpService()

        // Создаем мок Dotenv
        val mockDotenv = mock(Dotenv::class.java)
        `when`(mockDotenv["MEXC_API_KEY"]).thenReturn(null)
        `when`(mockDotenv["MEXC_SECRET_KEY"]).thenReturn(null)

        // Передаем мок в конструктор
        assertThrows<IllegalStateException> {
            MexcClient(mockHttpService, mockDotenv)
        }    }
}
