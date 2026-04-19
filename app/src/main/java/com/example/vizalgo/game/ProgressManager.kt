package com.example.vizalgo.game

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ProgressManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("VizAlgoProgress", Context.MODE_PRIVATE)
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getUnlockedLevel(dsName: String): Int {
        return prefs.getInt("${dsName}_unlocked", 1)
    }

    fun calculateTotalXP(): Int {
        val dsList = listOf("Stack", "Queue", "Singly Linked List", "Doubly Linked List", "Circular Linked List", "Binary Search Tree", "AVL Tree", "Heap", "B-Tree", "B+ Tree")
        var totalXP = 0
        dsList.forEach { ds ->
            val starsMap = getAllLevelStars(ds)
            starsMap.forEach { (level, stars) ->
                val multiplier = when {
                    level <= 5 -> 1.0f
                    level <= 10 -> 1.5f
                    else -> 2.0f
                }
                totalXP += (stars * 100 * multiplier).toInt()
            }
        }
        return totalXP
    }

    fun syncXPToFirestore() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val totalXP = calculateTotalXP()
            db.collection("users").document(uid).set(
                hashMapOf("xp" to totalXP),
                SetOptions.merge()
            )
        }
    }

    fun saveProgress(dsName: String, level: Int, stars: Int) {
        val currentStars = getLevelStars(dsName, level)
        if (stars > currentStars) {
            prefs.edit().putInt("${dsName}_level_${level}_stars", stars).apply()
            syncXPToFirestore()
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
