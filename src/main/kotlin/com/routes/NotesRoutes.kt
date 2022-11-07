package com.routes

import com.db.DatabaseConnection
import com.entities.NoteEntity
import com.models.NewNote
import com.models.Note
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*

fun Route.notesRouting() {

    val db = DatabaseConnection.db

    route("/notes") {

        //get all notes
        get {
            try {
                val notes = db.from(NoteEntity).select().map {
                    val id = it[NoteEntity.id]
                    val description = it[NoteEntity.description]
                    if (id != null && description != null) {
                        Note(id, description )
                    } else {
                        call.respondText ( "Something went wrong", status = HttpStatusCode.InternalServerError )
                    }
                }
                call.respond(notes)
            } catch (e: Exception) {
                call.respondText("Something went wrong", status = HttpStatusCode.InternalServerError)
            }
        }

        //get note by id
        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            try {
                val note = db.from(NoteEntity).select().where { NoteEntity.id eq id.toInt() }.map {
                    val id = it[NoteEntity.id]
                    val description = it[NoteEntity.description]
                    if (id != null && description != null) {
                        Note(id, description)
                    } else {
                        call.respondText ( "Something went wrong", status = HttpStatusCode.InternalServerError )
                    }
                }
                if (note.isNotEmpty()) {
                    call.respond(note)
                } else {
                    call.respondText("No notes found with id $id", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText("Something went wrong", status = HttpStatusCode.InternalServerError)
            }
        }

        //add note
        post {
            val note = call.receive<NewNote>()
            try {
                db.insert(NoteEntity) {
                    set(it.description, note.description)
                }
                call.respondText("Note stored correctly", status = HttpStatusCode.Accepted)
            } catch (e: Exception) {
                call.respondText("Something went wrong", status = HttpStatusCode.InternalServerError)
            }
        }

        //update note
        put("{id?}") {
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val note = call.receive<NewNote>()
            try {
                db.update(NoteEntity) {
                    set(it.description, note.description)
                    where { it.id eq id.toInt() }
                }
                call.respondText("Note updated correctly", status = HttpStatusCode.Accepted)
            } catch (e: Exception) {
                call.respondText("Something went wrong", status = HttpStatusCode.InternalServerError)
            }
        }

        //delete note
        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            try {
                val deletedNote = db.delete(NoteEntity) { it.id eq id.toInt() }
                if (deletedNote == 1) {
                    call.respondText("Note deleted correctly", status = HttpStatusCode.Accepted)
                } else {
                    call.respondText("Note not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText("Something went wrong", status = HttpStatusCode.InternalServerError)
            }
        }


    }
}