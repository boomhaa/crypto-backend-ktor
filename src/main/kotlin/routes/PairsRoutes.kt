package com.example.routes

import com.example.services.PairServices
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.URLDecoder

fun Route.pairsRoutes(pairService: PairServices) {
    route("/pairs") {

        get {
            val allPairs = pairService.getAllPairs()
            call.respond(HttpStatusCode.OK, allPairs)
        }

        get("/popular") {
            val popularPairs = pairService.getPopularPairs()
            try {
                call.respond(HttpStatusCode.OK, popularPairs)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest) { "error" to e.message }
            }
        }
        get("/search") {
            val query = call.request.queryParameters["q"].orEmpty()
            if (query.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "Query is required")
                return@get
            }
            try {
                val decoded = URLDecoder.decode(query, Charsets.UTF_8.toString())
                val result = pairService.searchPairs(decoded)
                call.respond(HttpStatusCode.OK, result)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf(
                        "error" to "Invalid characters in the request: ${e.message}"
                    )
                )
            }
        }

        route("{pair}") {
            get {
                val pair = call.parameters["pair"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Parameter is required")
                )
                val result = pairService.getPairDetailInfo(pair)
                call.respond(HttpStatusCode.OK, result)
            }
            get("/trades") {
                val pair = call.parameters["pair"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Parameter is required")
                )
                val result = pairService.getLastTrades(pair)
                call.respond(HttpStatusCode.OK, result)
            }

        }
    }
}

