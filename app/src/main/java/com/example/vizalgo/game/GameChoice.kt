package com.example.vizalgo.game

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R
import com.example.vizalgo.game.AVLTree.AVLTreeGame
import com.example.vizalgo.game.BPlusTree.BPlusTreeGame
import com.example.vizalgo.game.BTree.BTreeGame
import com.example.vizalgo.game.BinaryTree.BinaryTreeGame
import com.example.vizalgo.game.CircularLinkedList.CircularLinkedListGame
import com.example.vizalgo.game.DoublyLinkedList.DoublyLinkedListGame
import com.example.vizalgo.game.Heap.HeapGame
import com.example.vizalgo.game.Queue.QueueGame
import com.example.vizalgo.game.SinglyLinkedList.SinglyLinkedListGame
import com.example.vizalgo.game.Stack.StackGame
import com.example.vizalgo.utils.glassmorphic

class GameChoice : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dsName = intent.getStringExtra("DS_NAME") ?: "Stack"
        
        setContent {
            GameChoiceScreen(dsName) { mode ->
                val gameClass = when (dsName) {
                    "Stack" -> StackGame::class.java
                    "Queue" -> QueueGame::class.java
                    "Singly Linked List" -> SinglyLinkedListGame::class.java
                    "Doubly Linked List" -> DoublyLinkedListGame::class.java
                    "Circular Linked List" -> CircularLinkedListGame::class.java
                    "Binary Tree" -> BinaryTreeGame::class.java
                    "AVL Tree" -> AVLTreeGame::class.java
                    "Heap" -> HeapGame::class.java
                    "B-Tree" -> BTreeGame::class.java
                    "B+ Tree" -> BPlusTreeGame::class.java
                    else -> StackGame::class.java
                }
                
                val intent = Intent(this, gameClass).apply {
                    putExtra("GAME_MODE", mode)
                }
                startActivity(intent)
            }
        }
    }
}

@Composable
fun GameChoiceScreen(dsName: String, onModeSelected: (String) -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.gamebg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dsName,
                fontFamily = cantoraFont,
                fontSize = 42.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "SELECT MODE",
                fontFamily = cantoraFont,
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(60.dp))
            
            ModeButton("CHALLENGE", "Master the operations", Color(0xFF81C784), cantoraFont) {
                onModeSelected("CHALLENGE")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ModeButton("QUIZ", "Test your knowledge", Color(0xFFFFB74D), cantoraFont) {
                onModeSelected("QUIZ")
            }
        }
    }
}

@Composable
fun ModeButton(title: String, desc: String, color: Color, font: FontFamily, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() }
            .glassmorphic(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = color, fontFamily = font, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        }
    }
}
