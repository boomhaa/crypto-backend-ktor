package com.example

import com.example.configs.ExchangeConstants
import com.example.db.DbConfig
import com.example.db.repositories.PairLastTradeRepository
import com.example.db.repositories.TradingPairsRepository
import com.example.db.tables.TradingPairsTable
import com.example.exchanges.mexc.MexcClient
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.routes.*
import com.example.services.HttpService
import com.example.services.PairServices
import kotlinx.coroutines.delay
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.configureRouting() {

    DbConfig.init()

    val tradingPairsRepository = TradingPairsRepository()
    val pairLastTradeRepository = PairLastTradeRepository()

    val httpService = HttpService()
    val mexcClient = MexcClient(httpService)
    val pairServices =
        PairServices(
            mexcClient = mexcClient,
            tradingPairsRepository = tradingPairsRepository,
            pairLastTradeRepository = pairLastTradeRepository
        )

    pairServices.startDataRefreshJob()
    Thread.sleep(500)

    val specificPairs = listOf("btcusdt", "ethusdt", "dogeusdt")

    ExchangeConstants.ALL_PAIRS = transaction {
        TradingPairsTable
            .select { TradingPairsTable.pair inList specificPairs }
            .associate { row ->
                row[TradingPairsTable.pair] to row[TradingPairsTable.id]
            }
    }
    pairServices.startLastTradesRefreshJob()
    routing {
        route("/api") {
            pairsRoutes(pairServices)
        }
    }
}
