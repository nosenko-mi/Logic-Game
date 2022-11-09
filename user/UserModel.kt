package com.ltl.mpmp_lab3.user

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(val displayname: String, val email: String, var record: Long) : Parcelable{

    constructor() : this("emptyDisplayName", "emptyEmail",  0)

    fun isNullOrEmpty(): Boolean{
        if (email == "emptyEmail"){
            return true
        }
        return false
    }

    override fun toString(): String {
        return "UserModel(displayname='$displayname', email='$email', record=$record)"
    }
}