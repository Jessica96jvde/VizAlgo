package com.example.vizalgo.visualize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class BPlusTreeVisualize : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BPlusTreeScreen()
        }
    }
}
