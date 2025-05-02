package com.example.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlinx.datetime.LocalDateTime


@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class TradeInfo(
    val price: String,
    val qty: String,
    val time: Long,
    val isBuyerMaker: Boolean
)


@Serializable
data class PairTradeInfo(
    val price: String?,
    val qty: String?,
    val time: LocalDateTime,
    val isBuyerMaker: String
)