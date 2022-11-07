package com.routes

import com.db.DatabaseConnection
import com.entities.UserEntity
import com.models.NewUser
import com.models.User
import com.models.UserCredentials
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*
import java.util.*

fun Route.authenticationRouting() {

    val db = DatabaseConnection.db

    route("/register") {
        post {
            val userCredentials = call.receive<UserCredentials>()
            val username = userCredentials.email.lowercase(Locale.getDefault())
            val password = userCredentials.hashPassword()

            db.insert(UserEntity) {
                set(it.email, username)
                set(it.password, password)
            }

            call.respondText { "User created successfully!" }
        }
    }
}