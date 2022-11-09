package com.ltl.mpmp_lab3.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {

//    v3

    private val userRepository: UserRepository = UserRepository()

    private val _userEmail = MutableLiveData("")
    private val _userName = MutableLiveData("")
    private val _userRecord = MutableLiveData<Long>(0)

    fun getEmail(): LiveData<String>{
        return _userEmail
    }

    fun getName(): LiveData<String>{
        return _userName
    }

    fun getRecord(): LiveData<Long>{
        return _userRecord
    }

    fun setNewUser(model: UserModel){
        _userEmail.value =  model.email
        _userName.value =  model.displayname
        _userRecord.value =  model.record
    }

    fun updateRecord(newRecord: Long){
        userRepository.updateRecord(UserModel(_userEmail.value.toString(), _userName.value.toString(), newRecord))
    }

//    v2

    private val _currentUser = MutableLiveData<UserModel>(UserModel())
    val currentUser: LiveData<UserModel> = _currentUser

    fun setCurrentUser(user: UserModel) {
        _currentUser.value = user
    }

    fun hasValidUser(): Boolean{
        return _currentUser.value!!.isNullOrEmpty()
    }

//    v1

    private var user: UserModel? = null


    fun setUser(user: UserModel?){
        this.user = user
    }

    fun getCurrentUser(): UserModel?{
        return user
    }
}