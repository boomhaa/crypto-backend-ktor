package com.example.exchanges.mexc

import com.example.dto.MexcExchangeInfoResponse
import com.example.dto.PairInfo
import io.github.cdimascio.dotenv.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import com.example.exchanges.configs.MexcConfig
import com.example.services.HttpService
import kotlinx.serialization.json.Json
import java.net.http.HttpResponse

class MexcClient(private val httpService: HttpService) {

    private val dotenv: Dotenv = Dotenv.load()

    private val MEXC_API_KEY: String =
        dotenv["MEXC_API_KEY"] ?: error("MEXC_API_KEY is not set in .env file")
    private val MEXC_SECRET_KEY: String =
        dotenv["MEXC_SECRET_KEY"] ?: error("MEXC_API_KEY is not set in .env file")

    fun getTraidingPairs(): List<PairInfo> {

        val url = "${MexcConfig.BASE_URL}${MexcConfig.Endpoints.EXCHANGE_INFO}"
        val headers = mapOf("X-MEXC-APIKEY" to MEXC_API_KEY, "Content-Type" to "application/json")

        val response = httpService.get(url, headers)
        val exchangeInfo: MexcExchangeInfoResponse = Json.decodeFromString(response)
        val prices = getPairsPrice()

        return exchangeInfo.symbols.map { pair ->
            val price = prices[pair.symbol]
            PairInfo(
                pair = pair.symbol,
                baseAsset = pair.baseAsset,
                quoteAsset = pair.quoteAsset,
                price = price
            )
        }


    }


    fun getPairsPrice(): Map<String, String> {

        val url = "${MexcConfig.BASE_URL}${MexcConfig.Endpoints.PRICE}"
        val headers = mapOf("X-MEXC-APIKEY" to MEXC_API_KEY, "Content-Type" to "application/json")

        val response = httpService.get(url, headers)

        val priceInfo: List<Map<String, String>> = Json.decodeFromString(response)

        return priceInfo.associate { item ->
            val pair = item["symbol"] ?: ""
            val price = item["price"] ?: "0.0"
            pair to price
        }
    }

}