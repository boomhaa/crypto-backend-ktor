package com.example

import com.example.db.DbConfig
import com.example.db.repositories.TradingPairsRepository
import com.example.exchanges.mexc.MexcClient
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.routes.*
import com.example.services.HttpService
import com.example.services.PairServices


fun Application.configureRouting() {

    DbConfig.init()

    val tradingPairsRepository = TradingPairsRepository()

    val httpService = HttpService()
    val mexcClient = MexcClient(httpService)
    val pairServices = PairServices(mexcClient = mexcClient, tradingPairsRepository)

    pairServices.startDataRefreshJob()
    routing {
        route("/api") {
            pairsRoutes(pairServices)
        }
    }
}
