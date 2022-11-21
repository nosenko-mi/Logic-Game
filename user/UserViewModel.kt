package com.ltl.mpmp_lab3.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {

//    v3

    private val repository: UserRepository = UserRepository()

    private val _userEmail = MutableLiveData("")
    private val _userName = MutableLiveData("")
    private val _userRecord = MutableLiveData<Long>(0)
    private val _userScore = MutableLiveData<Int>(0)

    fun getEmail(): LiveData<String>{
        return _userEmail
    }

    fun getName(): LiveData<String>{
        return _userName
    }

    fun getRecord(): LiveData<Long>{
        return _userRecord
    }

    fun getScore(): LiveData<Int>{
        return _userScore
    }

    fun setNewUser(model: UserModel){
        repository.addUserIfNotExist(model)
        _userEmail.value =  model.email
        _userName.value =  model.displayname
        _userRecord.value =  model.record
    }

    fun updateRecord(newRecord: Long){
        repository.updateRecord(UserModel(_userName.value.toString(), _userEmail.value.toString(), newRecord))
        _userRecord.value =  newRecord
    }

    fun finByEmail(email: String){
        repository.findByEmail(email, this)
    }

    fun hasValidUser(): Boolean{
        return !_userEmail.value.equals("")
    }

////    v2
//
//    private val _currentUser = MutableLiveData<UserModel>(UserModel())
//    val currentUser: LiveData<UserModel> = _currentUser
//
//    fun setCurrentUser(user: UserModel) {
//        _currentUser.value = user
//    }
//
//
////    v1
//
//    private var user: UserModel? = null
//
//
//    fun setUser(user: UserModel?){
//        this.user = user
//    }
//
//    fun getCurrentUser(): UserModel?{
//        return user
//    }
}