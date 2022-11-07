package com.models

import kotlinx.serialization.Serializable

@Serializable
data class Note(val id: Int, val description: String)