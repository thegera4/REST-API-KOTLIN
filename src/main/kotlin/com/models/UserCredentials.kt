package com.models

import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class UserCredentials(val email: String, val password: String){
    fun hashPassword(): String{
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun isValidCredentials(): Boolean{
        return email.length >= 3 && password.length >= 6
    }
}
