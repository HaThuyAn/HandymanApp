package com.example.handyman.utils

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction

fun incrementMetric(path: String) {
    val ref = FirebaseDatabase.getInstance().getReference(path)
    ref.runTransaction(object : Transaction.Handler {
        override fun doTransaction(currentData: MutableData): Transaction.Result {
            val current = currentData.getValue(Int::class.java) ?: 0
            currentData.value = current + 1
            return Transaction.success(currentData)
        }

        override fun onComplete(
            error: com.google.firebase.database.DatabaseError?,
            committed: Boolean,
            snapshot: com.google.firebase.database.DataSnapshot?
        ) {
            
        }
    })
}

fun updateSessionMetrics(durationMin: Double, isBounce: Boolean, year: String, month: String) {
    val metricsRef = FirebaseDatabase.getInstance()
        .getReference("userEngagement/$year/$month")

    // Update total session time
    metricsRef.child("totalSessionTime").runTransaction(object : Transaction.Handler {
        override fun doTransaction(data: MutableData): Transaction.Result {
            val current = data.getValue(Double::class.java) ?: 0.0
            data.value = current + durationMin
            return Transaction.success(data)
        }

        override fun onComplete(
            error: com.google.firebase.database.DatabaseError?,
            committed: Boolean,
            snapshot: com.google.firebase.database.DataSnapshot?
        ) {
            if (error != null) {
                // placeholder
            }
        }
    })

    // Update session count
    metricsRef.child("sessionCount").runTransaction(object : Transaction.Handler {
        override fun doTransaction(data: MutableData): Transaction.Result {
            val current = data.getValue(Int::class.java) ?: 0
            data.value = current + 1
            return Transaction.success(data)
        }

        override fun onComplete(
            error: com.google.firebase.database.DatabaseError?,
            committed: Boolean,
            snapshot: com.google.firebase.database.DataSnapshot?
        ) {
            if (error != null) {
                // placeholder
            }
        }
    })

    // Update bounce count if applicable
    if (isBounce) {
        metricsRef.child("bounceCount").runTransaction(object : Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val current = data.getValue(Int::class.java) ?: 0
                data.value = current + 1
                return Transaction.success(data)
            }

            override fun onComplete(
                error: com.google.firebase.database.DatabaseError?,
                committed: Boolean,
                snapshot: com.google.firebase.database.DataSnapshot?
            ) {
                if (error != null) {
                    // placeholder
                }
            }
        })
    }
}

fun getCurrentYearMonth(): Pair<String, String> {
    val now = java.util.Calendar.getInstance()
    val year = now.get(java.util.Calendar.YEAR).toString()
    val month = now.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.ENGLISH) ?: "Unknown"
    return Pair(year, month)
}
