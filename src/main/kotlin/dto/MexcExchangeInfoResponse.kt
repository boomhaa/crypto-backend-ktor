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
    val status: String
)