package com.example.exchanges.configs

object MexcConfig {
    const val BASE_URL = "https://api.mexc.com"

    object Endpoints {
        const val EXCHANGE_INFO = "/api/v3/exchangeInfo"
        const val KLINE = "/api/v3/klines"
        const val PRICE = "/api/v3/ticker/price"
    }

    object Intervals {
        const val MIN15 = "15m"
        const val HOUR1 = "1h"
        const val HOUR4 = "4h"
        const val DAY1 = "1d"
        const val WEEK1 = "1w"
    }

}