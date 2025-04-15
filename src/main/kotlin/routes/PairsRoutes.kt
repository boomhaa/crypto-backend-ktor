package com.example.routes

import com.example.services.PairSerivces
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.pairsRoutes(pairService: PairSerivces) {
    route("/pairs") {

        get {
            val allPairs = pairService.getAllPairs()
            call.respond(HttpStatusCode.OK, allPairs)
        }

        get("/popular") {

            val popularPairs = pairService.getPopularPairs()
            try {
                call.respond(HttpStatusCode.OK, popularPairs)
            }catch (e: Exception){
                call.respond(HttpStatusCode.OK) { "error" to e.message }
            }


        }
    }

}