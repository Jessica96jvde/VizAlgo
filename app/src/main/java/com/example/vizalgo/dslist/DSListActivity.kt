package com.example.vizalgo.dslist

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R
import com.example.vizalgo.utils.glow
import com.example.vizalgo.learn.LearnActivity
import com.example.vizalgo.quiz.QuizActivity
import com.example.vizalgo.game.Stack.StackGame
import com.example.vizalgo.game.Queue.QueueGame
import com.example.vizalgo.game.SinglyLinkedList.SinglyLinkedListGame
import com.example.vizalgo.game.DoublyLinkedList.DoublyLinkedListGame
import com.example.vizalgo.game.CircularLinkedList.CircularLinkedListGame
import com.example.vizalgo.game.BinaryTree.BinaryTreeGame
import com.example.vizalgo.game.AVLTree.AVLTreeGame
import com.example.vizalgo.game.Heap.HeapGame
import com.example.vizalgo.game.BTree.BTreeGame
import com.example.vizalgo.game.BPlusTree.BPlusTreeGame
import com.example.vizalgo.visualize.*

class DSListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mode = intent.getStringExtra("MODE") ?: "Learn"
        
        setContent {
            DSListScreen(mode) { dsName ->
                val destinationActivity: Class<*> = when (mode) {
                    "Quiz" -> QuizActivity::class.java
                    "Challenge" -> {
                        when (dsName) {
                            "Stack" -> StackGame::class.java
                            "Queue" -> QueueGame::class.java
                            "Singly Linked List" -> SinglyLinkedListGame::class.java
                            "Doubly Linked List" -> DoublyLinkedListGame::class.java
                            "Circular Linked List" -> CircularLinkedListGame::class.java
                            "Binary Search Tree" -> BinaryTreeGame::class.java
                            "AVL Tree" -> AVLTreeGame::class.java
                            "Heap" -> HeapGame::class.java
                            "B-Tree" -> BTreeGame::class.java
                            "B+ Tree" -> BPlusTreeGame::class.java
                            else -> QueueGame::class.java
                        }
                    }
                    "Visualize" -> {
                        when (dsName) {
                            "Stack" -> StackVisualize::class.java
                            "Queue" -> QueueVisualize::class.java
                            "Singly Linked List", "Doubly Linked List" -> LinkedListVisualize::class.java
                            "Circular Linked List" -> CircularLinkedListVisualize::class.java
                            "Binary Search Tree" -> BinaryTreeVisualize::class.java
                            "AVL Tree" -> AVLTreeVisualize::class.java
                            "Heap" -> HeapVisualize::class.java
                            "B-Tree" -> BTreeVisualize::class.java
                            "B+ Tree" -> BPlusTreeVisualize::class.java
                            else -> LearnActivity::class.java
                        }
                    }
                    else -> LearnActivity::class.java
                }
                
                val intent = Intent(this, destinationActivity).apply {
                    putExtra("MODE", mode)
                    putExtra("DS_NAME", dsName)
                    if (dsName == "Doubly Linked List") {
                        putExtra("IS_DOUBLY", true)
                    }
                }
                startActivity(intent)
            }
        }
    }
}

@Composable
fun DSListScreen(mode: String, onDSSelected: (String) -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val green4 = colorResource(id = R.color.green4)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.homebg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            val title = when(mode) {
                "Quiz" -> "Select for Quiz"
                "Challenge" -> "Select Challenge"
                "Visualize" -> "Select Visualizer"
                else -> "$mode Mode"
            }

            Text(
                text = title,
                fontFamily = cantoraFont,
                fontSize = 32.sp,
                color = green4,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            val dataStructures = listOf(
                "Stack", "Queue", "Singly Linked List", "Doubly Linked List",
                "Circular Linked List", "Binary Search Tree", "AVL Tree", "Heap",
                "B-Tree", "B+ Tree"
            )

            dataStructures.forEach { ds ->
                DSItemCard(ds, R.drawable.splashbackground, cantoraFont) { onDSSelected(ds) }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun DSItemCard(title: String, imageRes: Int, font: FontFamily, onClick: () -> Unit) {
    val green4 = colorResource(id = R.color.green4)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .glow(color = green4, alpha = 0.5f, borderRadius = 24.dp, glowRadius = 15.dp)
            .border(
                width = 3.dp,
                color = green4,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontFamily = font,
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
