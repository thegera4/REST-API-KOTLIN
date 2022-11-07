package com

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.plugins.*
import io.github.cdimascio.dotenv.dotenv

fun main() {
    val dotenv = dotenv()

    embeddedServer(
        Netty,
        port = dotenv["SERVER_PORT"].toInt(),
        host = "127.0.0.1",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
