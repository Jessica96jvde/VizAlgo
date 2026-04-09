package com.example.vizalgo.visualize

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class BTreeVisualize : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Try to force landscape orientation programmatically if possible, 
        // but since we want the "Rotate" UI to work, we should allow sensor.
        // However, if the user's system rotation is locked, the 'isPortrait' check in Compose 
        // will stay true.

        setContent {
            BTreeScreen()
        }
    }
}
