package com.ltl.mpmp_lab3.attempt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AttemptsViewModel: ViewModel() {

    private val repository: AttemptModelRepository = AttemptModelRepository()

    private val _attempts = MutableLiveData<MutableList<AttemptModel>>()
    private val _currentAttempt = MutableLiveData<AttemptModel>()

    fun getAttempts(): LiveData<MutableList<AttemptModel>>{
        return _attempts
    }

    fun setAttempts(models: MutableList<AttemptModel>){
        _attempts.value = models
    }

    fun saveCurrentAttempt(model: AttemptModel){
        repository.saveAttempt(model)
        _currentAttempt.value = model
    }

    fun getAttemptsLimited(email: String, limit: Long = 10){
        repository.findAttemptsByEmailLimited(email, limit, this)
    }


}