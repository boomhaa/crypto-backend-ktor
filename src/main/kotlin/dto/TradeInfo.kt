package com.example.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys


@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class TradeInfo(
    val id: Long,
    val price: String,
    val qty: String,
    val time: Long,
    val isBuyerMaker: Boolean
)