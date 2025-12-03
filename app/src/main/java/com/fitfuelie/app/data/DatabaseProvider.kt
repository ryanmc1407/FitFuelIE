package com.fitfuelie.app.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var database: FitFuelDatabase? = null

    fun getDatabase(context: Context): FitFuelDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                FitFuelDatabase::class.java,
                FitFuelDatabase.DATABASE_NAME
            ).build().also { database = it }
        }
    }
}
