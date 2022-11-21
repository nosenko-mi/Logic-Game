package com.ltl.mpmp_lab3.user

import android.util.Log
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserRepository {

    private val TAG: String = "userRepository"
    private val COLLECTION = "users"
    private val db = Firebase.firestore

    fun addUserIfNotExist(user: UserModel){
        val docRef = db.collection(COLLECTION).document(user.email)
        docRef.get()
            .addOnSuccessListener { document ->
                if (!document.exists()){
                    docRef.set(user)
                    Log.d(TAG, "Document created: ${document.id}")
                } else {
                    Log.d(TAG, "Document already exists: ${document.id}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun updateRecord(user: UserModel){
        val docRef = db.collection(COLLECTION).document(user.email)
        docRef.set(user, SetOptions.merge())
            .addOnSuccessListener {Log.d(TAG, "Document is updated")}
            .addOnFailureListener{Log.d(TAG, "Document was not updated")}
    }

    fun findByEmail(email: String, model: UserViewModel){
        val docRef = db.collection(COLLECTION).document(email)
        var user: UserModel?

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()){

                    user = UserModel(
                        document["displayname"].toString(),
                        document["email"].toString(),
                        document["record"] as Long
                    )
                    Log.d(TAG, "Document: ${document.id}")

//                    model.setUser(user)
//                    Log.d(TAG, "user from UserViewModel: " + model.getCurrentUser().toString())

//                    model.setCurrentUser(user!!)
                    model.setNewUser(user!!)
//                    Log.d(TAG, "v2 user from UserViewModel: " + model.currentUser.value.toString())
                    Log.d(TAG, "v3 user from UserViewModel: " + model.getEmail().value.toString())

                } else { Log.d(TAG, "Document not exists: ${document.id}") }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}