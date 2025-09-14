package com.example.myapplication

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        preloadWords()
    }

    private fun preloadWords() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = DatabaseProvider.db.wordDao()
            val count = dao.getCount()
            if (count == 0) {
                try {
                    val inputStream = assets.open("words.json")
                    val json = inputStream.bufferedReader().use { it.readText() }

                    val words = Gson().fromJson(json, Array<Word>::class.java).toList()

                    dao.insertAll(*words.toTypedArray())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        lateinit var context: Context
    }
}
