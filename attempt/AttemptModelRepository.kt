package com.ltl.mpmp_lab3.attempt

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Error
import java.lang.RuntimeException
import kotlin.math.log

class AttemptModelRepository {

    private val TAG: String = "attemptRepository"
    private val COLLECTION = "attempts"
    private val db = Firebase.firestore

    fun saveAttempt(model: AttemptModel){
        val writable = model.toWritable()
        writable.createdAt = FieldValue.serverTimestamp()

        db.collection(COLLECTION)
            .add(writable)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Document written: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun findAttemptsByEmailLimited(email: String, limit: Long = 10){
        db.collection(COLLECTION)
            .whereEqualTo("userEmail", email)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun findAttemptsByEmailLimited(email: String, limit: Long = 10, viewModel: AttemptsViewModel){
        db.collection(COLLECTION)
            .whereEqualTo("userEmail", email)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener { documents ->
                val list: MutableList<AttemptModel> = mutableListOf()
                for (document in documents) {
                    try {
                        if (document["createdAt"] != null) {
                            Log.d(TAG, "ok")

                            val model = AttemptModel(
                                document["userEmail"] as String,
                                document["score"] as Long,
                                document["createdAt"] as Timestamp,
                                document["difficulty"] as String
                            )

                            list.add(model)
                            Log.d(TAG, "${document.id} => ${document.data}")
                        }

                    } catch (e: RuntimeException) {
                        Log.d(TAG, e.message.toString())
                    }
                }
                if (list.size == limit.toInt()){
                    list.removeFirst()
                }
                viewModel.setAttempts(list)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

}