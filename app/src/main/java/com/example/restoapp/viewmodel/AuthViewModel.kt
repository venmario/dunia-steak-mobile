package com.example.restoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.restoapp.model.User

class AuthViewModel(application: Application):AndroidViewModel(application) {
    var userLD = MutableLiveData<User>()
}