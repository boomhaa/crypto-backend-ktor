package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class PairDetailInfo(
    val pair: String,
    val baseAsset: String,
    val quoteAsset: String,
    val price: String? = null,
    val highPrice: String? = null,
    val lowPrice: String? = null,
    val volume: String? = null,
    val quoteVolume: String? = null,
    val lastUpdated: String? = null
)