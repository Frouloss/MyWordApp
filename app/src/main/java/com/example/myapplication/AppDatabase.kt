package com.example.myapplication

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Word::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}
