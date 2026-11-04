package com.example.util

import android.util.Log

enum class OperationType {
    CREATE, READ, UPDATE, DELETE, LIST
}

object FirestoreErrorHandler {
    fun handleFirestoreError(e: Exception, operation: OperationType, path: String) {
        Log.e("FirestoreError", "Firestore error during $operation at $path: ${e.message}", e)
    }
}
