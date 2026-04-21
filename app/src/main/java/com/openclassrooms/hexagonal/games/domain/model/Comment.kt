package com.openclassrooms.hexagonal.games.domain.model

import java.io.Serializable

data class Comment (
    val id: String = "",
    val content: String = "",
    val author: User? = null,
    val timestamp: Long = 0L,
    val postId: String = ""
) : Serializable
