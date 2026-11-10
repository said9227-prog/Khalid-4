package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProjectEntity::class], version = 1, exportSchema = false)
abstract class KhalidStudioDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: KhalidStudioDatabase? = null

        fun getDatabase(context: Context): KhalidStudioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KhalidStudioDatabase::class.java,
                    "khalid_studio_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
