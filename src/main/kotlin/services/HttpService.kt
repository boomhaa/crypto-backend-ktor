package com.example.services

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HttpService {

    private val client = HttpClient.newBuilder().build()


    fun get(url: String, headers: Map<String, String>): String {
        val requestBuilder = HttpRequest.newBuilder().uri(URI.create(url))
        headers.forEach {
            requestBuilder.header(it.key, it.value)
        }
        val request = requestBuilder.build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println("Got response: $response")
        return response.body()
    }

}