package com.thetatechnolabs.networkinterceptor.data.repositories

import android.content.Context
import com.thetatechnolabs.networkinterceptor.data.database.NetworkDatabase
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import kotlinx.coroutines.flow.Flow

internal class NetworkRepo(context: Context) {
    // Repo to manage data access to the views
    private val database = NetworkDatabase.getInstance(context)
    private val networkCallDao = database.networkCallDao()

    suspend fun addNetworkCall(networkInfo: NetworkInfo) {
        networkCallDao.insertItem(networkInfo)
    }

    fun getNetworkCallList(): Flow<List<NetworkInfo>> {
        return networkCallDao.getNetworkCallList()
    }

    companion object {
        private var INSTANCE: NetworkRepo? = null

        fun getInstance(context: Context): NetworkRepo = INSTANCE ?: synchronized(this) {
            return INSTANCE ?: NetworkRepo(context).also { INSTANCE = it }
        }
    }
}