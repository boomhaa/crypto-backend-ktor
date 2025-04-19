package com.example.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class MexcExchangeInfoResponse(
    val symbols: List<MexcPairInfo>
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class MexcPairInfo(
    val symbol: String,
    val baseAsset: String,
    val quoteAsset: String,
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Pair24hPriceInfo(
    val symbol: String,
    val highPrice: String? = null,
    val lowPrice: String? = null,
    val volume: String? = null,
    val quoteVolume: String? = null
)