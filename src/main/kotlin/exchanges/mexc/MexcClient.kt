package com.example.exchanges.mexc

import com.example.dto.MexcExchangeInfoResponse
import com.example.dto.PairInfo
import io.github.cdimascio.dotenv.*
import com.example.exchanges.configs.MexcConfig
import com.example.services.HttpService
import kotlinx.serialization.json.Json

class MexcClient(private val httpService: HttpService,
                 dotenv: Dotenv = Dotenv.load()) {

    private val MEXC_API_KEY: String =
        dotenv["MEXC_API_KEY"] ?: throw IllegalStateException("MEXC_API_KEY is not set in .env file")
    //private val MEXC_SECRET_KEY: String =
    //    dotenv["MEXC_SECRET_KEY"] ?: throw IllegalStateException("MEXC_SECRET_KEY is not set in .env file")

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


    private fun getPairsPrice(): Map<String, String> {

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