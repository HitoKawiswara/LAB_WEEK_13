package com.example.lab_week_13.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lab_week_13.model.Movie

@Database(
    entities = [Movie::class],
    version = 1
)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {

        // @Volatile prevents Race Condition.
        // Jika ada thread lain yang meng-update instance,
        // perubahan langsung terlihat oleh thread lain.
        @Volatile
        private var instance: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase {
            // synchronized memastikan cuma satu thread yang bisa masuk blok ini sekaligus.
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context.applicationContext)
                    .also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): MovieDatabase {
            return Room.databaseBuilder(
                context,
                MovieDatabase::class.java,
                "movie-db"
            ).build()
        }
    }
}