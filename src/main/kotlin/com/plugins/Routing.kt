package com.plugins

import com.routes.notesRouting
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import com.routes.usersRouting

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("This is my first backend in Kotlin!")
        }
        usersRouting()
        notesRouting()
    }

}
