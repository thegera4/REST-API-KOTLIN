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
import org.mindrot.jbcrypt.BCrypt
import java.util.*

fun Route.authenticationRouting() {

    val db = DatabaseConnection.db

    route("/register") {
        post {
            // Get the user from the request
            val userCredentials = call.receive<UserCredentials>()

            //check if credentials are valid
            if(!userCredentials.isValidCredentials()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid credentials. " +
                            "Make sure your email is valid and your password is at least 6 characters long."
                )
                return@post
            }

            //save body info to variables and hash the password
            val email = userCredentials.email.lowercase(Locale.getDefault())
            val password = userCredentials.hashPassword()

            //check if email already exists
            val user = db.from(UserEntity)
                .select()
                .where { UserEntity.email eq email }
                .map { it[UserEntity.email] }
                .firstOrNull()

            if (user != null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Email is already registered. Please use another email."
                )
                return@post
            }

            //if no user with that email exists, create a new user
            db.insert(UserEntity) {
                set(it.email, email)
                set(it.password, password)
            }

            //respond with a success message
            call.respond(HttpStatusCode.Created, "User created successfully.")
        }
    }

    route("/login"){
        post{
            // Get the user from the request
            val userCredentials = call.receive<UserCredentials>()

            //check if credentials are valid
            if(!userCredentials.isValidCredentials()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid credentials. " +
                            "Make sure your email is valid and your password is at least 6 characters long."
                )
                return@post
            }

            //save body info to variables and hash the password
            val email = userCredentials.email.lowercase(Locale.getDefault())
            val password = userCredentials.password

            //check if email already exists
            val user = db.from(UserEntity)
                .select()
                .where { UserEntity.email eq email }
                .map {
                    val id = it[UserEntity.id]!!
                    val email = it[UserEntity.email]!!
                    val password = it[UserEntity.password]!!
                    User(id, email, password)
                }
                .firstOrNull()

            if (user == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid email or password."
                )
                return@post
            }

            //check if password is correct
            val passwordMatch = BCrypt.checkpw(password, user.password)
            if(!passwordMatch){
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid email or password."
                )
                return@post
            }

            //respond with a success message
            call.respond(HttpStatusCode.OK, "Login successful.")
        }
    }
}