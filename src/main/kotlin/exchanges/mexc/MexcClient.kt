package com.example.exchanges.mexc

import com.example.dto.MexcExchangeInfoResponse
import com.example.dto.Pair24hPriceInfo
import com.example.dto.PairDetailInfo
import com.example.dto.PairInfo
import io.github.cdimascio.dotenv.*
import com.example.exchanges.configs.MexcConfig
import com.example.services.HttpService
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class MexcClient(private val httpService: HttpService,
                 dotenv: Dotenv = Dotenv.load()) {


    private val logger = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val MEXC_API_KEY: String =
        dotenv["MEXC_API_KEY"] ?: throw IllegalStateException("MEXC_API_KEY is not set in .env file: line 14")
    //private val MEXC_SECRET_KEY: String =
    //    dotenv["MEXC_SECRET_KEY"] ?: throw IllegalStateException("MEXC_SECRET_KEY is not set in .env file")

    fun getTraidingPairs(): List<PairDetailInfo> {

        val url = "${MexcConfig.BASE_URL}${MexcConfig.Endpoints.EXCHANGE_INFO}"
        val headers = mapOf("X-MEXC-APIKEY" to MEXC_API_KEY, "Content-Type" to "application/json")

        val response = httpService.get(url, headers)
        val exchangeInfo: MexcExchangeInfoResponse = Json.decodeFromString(response)
        val prices = getPairsPrice()
        val price24h = getPairsLast24hPrice()

        logger.info(exchangeInfo.symbols.first().toString())

        return exchangeInfo.symbols.map { pair ->
            val price = prices[pair.symbol]
            val highPrice24h = price24h[pair.symbol]?.get(0)
            val lowPrice24h = price24h[pair.symbol]?.get(1)
            val volumeBaseAsset24h = price24h[pair.symbol]?.get(2)
            val volumeQuoteAsset24h = price24h[pair.symbol]?.get(3)
            PairDetailInfo(
                pair = pair.symbol,
                baseAsset = pair.baseAsset,
                quoteAsset = pair.quoteAsset,
                price = price,
                highPrice = highPrice24h,
                lowPrice = lowPrice24h,
                volume = volumeBaseAsset24h,
                quoteVolume = volumeQuoteAsset24h
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

    private fun getPairsLast24hPrice(): Map<String, List<String?>>{

        val url = "${MexcConfig.BASE_URL}${MexcConfig.Endpoints.PRICE_LAST_24H}"
        val headers = mapOf("X-MEXC-APIKEY" to MEXC_API_KEY, "Content-Type" to "application/json")

        val response = httpService.get(url, headers)

        val priceInfo24h: List<Pair24hPriceInfo> = Json.decodeFromString(response)

        return priceInfo24h.associate { item ->
           item.symbol to listOf(item.highPrice, item.lowPrice, item.volume, item.quoteVolume)
        }
    }
}