package com.thetatechnolabs.networkinterceptorexample.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory constructor(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(application) as T
        } else {
            throw  IllegalArgumentException("MainViewModel not found")
        }
}