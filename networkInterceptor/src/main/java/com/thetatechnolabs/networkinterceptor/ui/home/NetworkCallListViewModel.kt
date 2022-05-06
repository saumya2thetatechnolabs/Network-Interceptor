package com.thetatechnolabs.networkinterceptor.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import com.thetatechnolabs.networkinterceptor.data.repositories.NetworkRepo
import kotlinx.coroutines.flow.*

// Used [AndroidViewModel] to access context in view model
internal class NetworkCallListViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = NetworkRepo.getInstance(application.applicationContext)
    val networkCallList: Flow<List<NetworkInfo>> = flow {
        repo.getNetworkCallList()
            .collect { list ->
                emit(list)
            }
    }

    fun getFilteredList(searchText: String) = channelFlow {
        repo.getNetworkCallList().map {
            it.filter { networkInfo ->
                if (searchText.isNotEmpty()) {
                    networkInfo.info.url.contains(searchText, ignoreCase = true)
                } else {
                    true
                }
            }
        }.collectLatest {
            send(it)
        }
    }
}