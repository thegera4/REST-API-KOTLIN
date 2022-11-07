package com.models

import kotlinx.serialization.Serializable

@Serializable
data class NewUser(val name: String, val age: Int, val email: String)