package com.example.frefalllib.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * @author Tomislav Curis
 */

@Database(entities = [FallObject::class], version = 1, exportSchema = true)
abstract class FreeFallDatabase : RoomDatabase() {
    abstract fun getFreeFallDao(): FreeFallDao

    companion object {

        @Volatile
        private var INSTANCE: FreeFallDatabase? = null

        fun getInstance(context: Context): FreeFallDatabase {

            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        FreeFallDatabase::class.java,
                        "freefall_history_database"
                    )

                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}