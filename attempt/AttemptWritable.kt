package com.ltl.mpmp_lab3.attempt

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

data class AttemptWritable(val userEmail: String, var score: Long, var difficulty: String) {

    constructor(userEmail: String, score: Long, _createdAt: FieldValue, difficulty: String) : this(userEmail, score, difficulty) {
        createdAt = _createdAt
    }

    lateinit var createdAt: FieldValue

    override fun toString(): String {
        return "$createdAt $score"
    }
}