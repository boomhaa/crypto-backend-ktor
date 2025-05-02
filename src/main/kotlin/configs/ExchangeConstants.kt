package com.example.configs

import java.math.BigDecimal


object ExchangeConstants {
    val POPULAR_PAIRS = listOf(
        "btcusdt",
        "ethusdt",
        "bnbusdt",
        "xrpusdt",
        "adausdt",
        "solusdt",
        "dogeusdt"
    )

    lateinit var ALL_PAIRS: Map<String, Int>

    fun BigDecimal?.toFormattedString(): String? {
        if (this == null) return null

        val plainString = this.toPlainString()
        return if (plainString.contains('.')) {
            plainString.replace(Regex("0*$"), "").replace(Regex("\\.$"), "")
        } else {
            plainString
        }
    }
}