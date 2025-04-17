package com.example

import com.example.dto.PairInfo
import com.example.routes.pairsRoutes
import com.example.services.PairServices
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PairRoutesTest {

    private fun Application.testModule(pairService: PairServices) {
        install(ContentNegotiation) {
            json(Json { prettyPrint = true })
        }
        routing {
            pairsRoutes(pairService)
        }
    }

    @Test
    fun `GET pairs should return all pairs`() = testApplication {
        val pairService = mock(PairServices::class.java)
        val expectedPairs = listOf(PairInfo("BTCUSDT", "BTC", "USDT", "50000"))
        `when`(pairService.getAllPairs()).thenReturn(expectedPairs)

        application { testModule(pairService) }

        val response = client.get("/pairs")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("BTCUSDT"))
    }

    @Test
    fun `GET pairs popular should return only popular pairs`() = testApplication {
        val pairService = mock(PairServices::class.java)
        val expectedPopular = listOf(PairInfo("ETHUSDT", "ETH", "USDT", "3000"))
        `when`(pairService.getPopularPairs()).thenReturn(expectedPopular)

        application { testModule(pairService) }

        val response = client.get("/pairs/popular")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("ETHUSDT"))
    }

    @Test
    fun `GET not existing route should return 404`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                pairsRoutes(mock(PairServices::class.java))
            }
        }

        val response = client.get("/pairs/unknown")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
