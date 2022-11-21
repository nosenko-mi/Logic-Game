package com.ltl.mpmp_lab3.attempt

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

data class AttemptModel(val userEmail: String, var score: Long, var difficulty: String) {

    constructor(userEmail: String, score: Long, _createdAt: Timestamp, difficulty: String) : this(userEmail, score, difficulty) {
        createdAt = _createdAt
    }

    lateinit var createdAt: Timestamp

    fun toWritable(): AttemptWritable{
        return AttemptWritable(userEmail, score, difficulty)
    }

    override fun toString(): String {
        return "$createdAt $score"
    }
}