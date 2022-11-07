package com.db

import io.github.cdimascio.dotenv.dotenv
import org.ktorm.database.Database

object DatabaseConnection {
    private val dotenv = dotenv()

    val db = Database.connect(
        url = "jdbc:mysql://localhost:3333/users-ktor-test", //change this for deployment
        driver = "com.mysql.cj.jdbc.Driver", //change this for different databases (PostgreSQL, Oracle, etc.)
        user = dotenv["MYSQL_USER"],
        password = dotenv["MYSQL_PASSWORD"]
    )
}
