package com.example.vizalgo.visualize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class BinaryTreeVisualize : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BinaryTreeScreen()
        }
    }
}
