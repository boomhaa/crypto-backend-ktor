package com.example

import com.example.exchanges.mexc.MexcClient
import com.example.services.PairSerivces
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.routes.*
import com.example.services.HttpService

fun Application.configureRouting() {

    val httpService = HttpService()
    val mexcClient = MexcClient(httpService)
    val pairServices = PairSerivces(mexcClient = mexcClient)

    routing {
        route("/api") {
            pairsRoutes(pairServices)
        }
    }
}
