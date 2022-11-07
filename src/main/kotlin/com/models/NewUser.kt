package com.models

import kotlinx.serialization.Serializable

@Serializable
data class NewUser(val email: String, val password: String)