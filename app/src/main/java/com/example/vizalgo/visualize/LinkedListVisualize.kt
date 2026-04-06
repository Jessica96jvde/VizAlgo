package com.example.vizalgo.visualize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class LinkedListVisualize : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isDoubly = intent.getBooleanExtra("IS_DOUBLY", false)
        setContent {
            LinkedListScreen(isDoubly = isDoubly)
        }
    }
}
