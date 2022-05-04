package com.thetatechnolabs.networkinterceptor.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import kotlinx.coroutines.flow.Flow

@Dao
internal interface NetworkCallListDao {
    // Factory methods to utilize database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(networkInfo: NetworkInfo)

    @Query("SELECT * FROM network_call")
    fun getNetworkCallList(): Flow<List<NetworkInfo>>
}