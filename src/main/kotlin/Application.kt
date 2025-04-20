package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.respond
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(status, mapOf("error" to "Page not found"))
        }

        exception<BadRequestException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Malformed request: ${cause.message}")
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Invalid input: ${cause.message}")
            )
        }

        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf(
                    "error" to "Internal server error: ${cause.message}"
                )
            )
        }
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }


    configureRouting()
}
