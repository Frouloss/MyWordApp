package com.example.myapplication

import androidx.room.Room

object DatabaseProvider {
    val db by lazy {
        Room.databaseBuilder(
            App.context,   // applicationContext
            AppDatabase::class.java,
            "words.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
}