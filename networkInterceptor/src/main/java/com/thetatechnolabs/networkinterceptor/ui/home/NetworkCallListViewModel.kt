package com.thetatechnolabs.networkinterceptor.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import com.thetatechnolabs.networkinterceptor.data.repositories.NetworkRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Used [AndroidViewModel] to access context in view model
internal class NetworkCallListViewModel(application: Application) : AndroidViewModel(application) {
    var networkCallList: Flow<List<NetworkInfo>> = flow {
        NetworkRepo.getInstance(application.applicationContext).getNetworkCallList()
            .collect { list ->
                emit(list)
            }
    }
}