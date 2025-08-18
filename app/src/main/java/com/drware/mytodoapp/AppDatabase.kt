/*
 * File: AppDatabase.kt
 * Path: E:/Documents/Android_Studio_Projects/MyTodoApp/app/src/main/java/com/drware/mytodoapp/AppDatabase.kt
 * Date: Mon Aug 18 12:35:29 2025
 * Purpose: This file defines the main Room database for the application. It lists all
 * entities (tables) and provides a singleton instance of the database.
 */

package com.drware.mytodoapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ✅ This annotation is crucial. It lists all tables and sets the DB version.
@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object {
        // Volatile ensures that the value of INSTANCE is always up-to-date.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Return existing instance or build a new one inside a synchronized block
            // to ensure only one instance is created.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/* EOF */