package com.example.vizalgo.game

import android.content.Context
import android.content.SharedPreferences

class ProgressManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("VizAlgoProgress", Context.MODE_PRIVATE)

    fun getUnlockedLevel(dsName: String): Int {
        return prefs.getInt("${dsName}_unlocked", 1)
    }

    fun saveProgress(dsName: String, level: Int, stars: Int) {
        val currentStars = getLevelStars(dsName, level)
        if (stars > currentStars) {
            prefs.edit().putInt("${dsName}_level_${level}_stars", stars).apply()
        }
        
        val currentUnlocked = getUnlockedLevel(dsName)
        if (level == currentUnlocked && stars > 0 && level < 15) {
            prefs.edit().putInt("${dsName}_unlocked", level + 1).apply()
        }
    }

    fun getLevelStars(dsName: String, level: Int): Int {
        return prefs.getInt("${dsName}_level_${level}_stars", 0)
    }

    fun getAllLevelStars(dsName: String): Map<Int, Int> {
        val map = mutableMapOf<Int, Int>()
        for (i in 1..15) {
            map[i] = getLevelStars(dsName, i)
        }
        return map
    }
}
