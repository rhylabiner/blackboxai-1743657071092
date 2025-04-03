package com.example.elibrary.models

import com.google.firebase.firestore.DocumentId

data class Book(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val genre: String = "",
    val description: String = "",
    val qrCode: String = "",
    val available: Boolean = true,
    val imageUrl: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "title" to title,
            "author" to author,
            "genre" to genre,
            "description" to description,
            "qrCode" to qrCode,
            "available" to available,
            "imageUrl" to imageUrl
        )
    }
}