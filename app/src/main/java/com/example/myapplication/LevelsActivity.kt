package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LevelsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_levels)

        val recyclerLevels = findViewById<RecyclerView>(R.id.recyclerLevels)


        val levels = List(9) { i -> Level(number = i + 1, isUnlocked = i == 0) }

        recyclerLevels.layoutManager = GridLayoutManager(this, 3) // 3 колонки
        recyclerLevels.adapter = LevelsAdapter(levels) { level ->
            println("Clicked level ${level.number}")
        }
    }
}
