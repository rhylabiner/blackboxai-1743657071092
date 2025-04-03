package com.example.elibrary.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Transaction(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val bookId: String = "",
    val bookTitle: String = "",
    @ServerTimestamp
    val borrowDate: Timestamp? = null,
    val dueDate: Timestamp? = null,
    val returnDate: Timestamp? = null,
    val status: String = "borrowed" // borrowed, returned, overdue
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "bookId" to bookId,
            "bookTitle" to bookTitle,
            "borrowDate" to borrowDate,
            "dueDate" to dueDate,
            "returnDate" to returnDate,
            "status" to status
        )
    }
}