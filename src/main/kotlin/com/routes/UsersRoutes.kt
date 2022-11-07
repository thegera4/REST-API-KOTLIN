package com.routes

import com.db.DatabaseConnection
import com.entities.UserEntity
import com.models.NewUser
import com.models.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*

fun Route.usersRouting() {

    val db = DatabaseConnection.db

    route("/users") {

        //get all users
        get {
            try {
                val users = db.from(UserEntity).select().map {
                    val id = it[UserEntity.id]
                    val email = it[UserEntity.email]
                    val password = it[UserEntity.password]
                    if (id != null && email != null && password != null) {
                        User(id, email, password)
                    } else {
                        call.respondText ( "Something went wrong", status = HttpStatusCode.InternalServerError )
                    }
                }
                call.respond(users)
            } catch (e: Exception) {
                call.respondText("Something went wrong", status = HttpStatusCode.InternalServerError)
            }
        }

        //get user by id
        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            try {
                val user = db.from(UserEntity).select().where { UserEntity.id eq id.toInt() }.map {
                    val id = it[UserEntity.id]
                    val email = it[UserEntity.email]
                    val password = it[UserEntity.password]
                    if (id != null && email != null && password != null ) {
                        User(id, email, password)
                    } else {
                        call.respondText ( "Something went wrong", status = HttpStatusCode.InternalServerError )
                    }
                }
                if (user.isNotEmpty()) {
                    call.respond(user)
                } else {
                    call.respondText("No user found with id $id", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText("Something went wrong", status = HttpStatusCode.InternalServerError)
            }
        }

        //create a new user
        post {
            val newUser = call.receive<NewUser>()
            try {
                db.insert(UserEntity) {
                    set(it.email, newUser.email)
                    set(it.password, newUser.password)
                }
                call.respondText("User created correctly", status = HttpStatusCode.Created)
            } catch (e: Exception) {
                call.respondText("Something went wrong", status = HttpStatusCode.InternalServerError)
            }
        }

        //delete user by id
        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            try {
                val deletedUser = db.delete(UserEntity) { UserEntity.id eq id.toInt() }
                if(deletedUser == 1) {
                    call.respondText("User deleted correctly!", status = HttpStatusCode.Accepted)
                } else {
                    call.respondText(" Can not delete a non existing user", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText("Something went wrong", status = HttpStatusCode.InternalServerError)
            }
        }

        //update user by id
        put("{id?}") {
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val updatedUser = call.receive<NewUser>()
            try {
                db.update(UserEntity) {
                    set(it.email, updatedUser.email)
                    set(it.password, updatedUser.password)
                    where { it.id eq id.toInt() }
                }
                call.respondText("User updated correctly!", status = HttpStatusCode.Accepted)
            } catch (e: Exception) {
                call.respondText("Something went wrong", status = HttpStatusCode.InternalServerError)
            }
        }

    }
}