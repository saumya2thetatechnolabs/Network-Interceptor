package com.thetatechnolabs.networkinterceptor.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thetatechnolabs.networkinterceptor.data.database.dao.NetworkCallListDao
import com.thetatechnolabs.networkinterceptor.data.database.entities.Info
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import com.thetatechnolabs.networkinterceptor.data.database.entities.Request
import com.thetatechnolabs.networkinterceptor.data.database.entities.Response
import com.thetatechnolabs.networkinterceptor.data.database.typeconverters.InfoTypeConverter
import com.thetatechnolabs.networkinterceptor.data.database.typeconverters.RequestTypeConverter
import com.thetatechnolabs.networkinterceptor.data.database.typeconverters.ResponseTypeConverter

@Database(
    entities = [
        Info::class,
        Request::class,
        Response::class,
        NetworkInfo::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    InfoTypeConverter::class,
    RequestTypeConverter::class,
    ResponseTypeConverter::class
)
internal abstract class NetworkDatabase : RoomDatabase() {
    abstract fun networkCallDao(): NetworkCallListDao

    companion object {
        private var INSTANCE: NetworkDatabase? = null

        fun getInstance(context: Context): NetworkDatabase = INSTANCE ?: synchronized(this) {
            return INSTANCE ?: Room.databaseBuilder(
                context,
                NetworkDatabase::class.java,
                "network_database"
            ).fallbackToDestructiveMigration().build().also {
                INSTANCE = it
            }
        }
    }
}