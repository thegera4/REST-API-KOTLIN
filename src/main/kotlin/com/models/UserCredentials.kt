package com.models

import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class UserCredentials(val email: String, val password: String){
    fun hashPassword(): String{
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}
