package com.example.uas_android.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Movies::class], version = 1, exportSchema = false)
abstract class MoviesRoomDatabase : RoomDatabase(){
    abstract fun moviesDao(): MoviesDao

    companion object {
        private const val DATABASE_NAME = "movie"

        @Volatile
        private var instance: MoviesRoomDatabase? = null

        fun getInstance(context: Context): MoviesRoomDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): MoviesRoomDatabase {
            return Room.databaseBuilder(context, MoviesRoomDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }

    }

}