package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class PairInfo(
    val pair: String,
    val baseAsset: String,
    val quoteAsset: String,
    val price: String? = null,
    val lastUpdated: String? = null
)