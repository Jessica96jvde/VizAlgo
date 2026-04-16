package com.example.vizalgo.game

import android.content.Context
import android.content.SharedPreferences

class ProgressManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("VizAlgoProgress", Context.MODE_PRIVATE)

    fun getUnlockedLevel(dsName: String): Int {
        return prefs.getInt("${dsName}_unlocked", 1)
    }

    fun getAllLevelStars(dsName: String): Map<Int, Int> {
        val starsMap = mutableMapOf<Int, Int>()
        for (i in 1..15) {
            val stars = prefs.getInt("${dsName}_level_${i}_stars", 0)
            if (stars > 0) {
                starsMap[i] = stars
            }
        }
        return starsMap
    }

    fun saveProgress(dsName: String, level: Int, stars: Int) {
        val currentStars = prefs.getInt("${dsName}_level_${level}_stars", 0)
        if (stars > currentStars) {
            prefs.edit().putInt("${dsName}_level_${level}_stars", stars).apply()
        }

        val unlocked = prefs.getInt("${dsName}_unlocked", 1)
        if (level == unlocked && level < 15) {
            prefs.edit().putInt("${dsName}_unlocked", level + 1).apply()
        }
    }
}
