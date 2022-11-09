package com.ltl.mpmp_lab3.user

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserRepository {

    private val TAG: String = "userRepository"
    private val db = Firebase.firestore

    fun addUserIfNotExist(user: UserModel){
        val docRef = db.collection("users").document(user.email)
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
        val docRef = db.collection("users").document(user.email)
        docRef.set(user)
    }

    fun findByEmail(email: String): UserModel?{
/*        val docRef = db.collection("users").document(email)
        var user: UserModel? = null
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()){
                    user = UserModel(
                        document["displayname"].toString(),
                        document["email"].toString(),
                        document["record"] as Long
                    )
                    Log.d(TAG, "Document: ${document.id}")
                } else {
                    Log.d(TAG, "Document not exists: ${document.id}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
            .addOnCompleteListener {
            Log.d(TAG, "user: " + user.toString())
            }
        return user*/

        val docRef = db.collection("users").document(email)
        var user: UserModel? = null
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()){
                    user = UserModel(
                        document["displayname"].toString(),
                        document["email"].toString(),
                        document["record"] as Long
                    )
                    Log.d(TAG, "Document: ${document.id}")
                    Log.d(TAG, "user: " + user.toString())

                } else { Log.d(TAG, "Document not exists: ${document.id}") }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        return user

/*        var user: UserModel? = null
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "Document: ${document.id}")
                    user = UserModel(
                        document["displayname"].toString(),
                        document["email"].toString(),
                        document["record"] as Long
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
            .addOnCompleteListener {
                Log.d(TAG, "user: " + user.toString())
            }
        return user*/
    }

    fun findByEmail1(email: String, model: UserViewModel){
        val docRef = db.collection("users").document(email)
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

                    model.setCurrentUser(user!!)
                    model.setNewUser(user!!)
                    Log.d(TAG, "v2 user from UserViewModel: " + model.currentUser.value.toString())
                    Log.d(TAG, "v3 user from UserViewModel: " + model.currentUser.value.toString())

                } else { Log.d(TAG, "Document not exists: ${document.id}") }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}