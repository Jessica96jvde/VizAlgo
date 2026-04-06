package com.example.vizalgo.learn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R

class LearnActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dsName = intent.getStringExtra("DS_NAME") ?: "Stack"

        setContent {
            NotebookScreen(dsName)
        }
    }
}

@Composable
fun NotebookScreen(dsName: String) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val poppins = FontFamily(Font(R.font.poppins_light))
    val paperColor = Color(0xFFFDF5E6) // Old Lace - paper vibe
    val green4 = colorResource(id = R.color.green4)
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Operations", "Complexity", "Examples")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.green4)) // Dark background to make the "book" pop
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .shadow(8.dp, RoundedCornerShape(8.dp))
                .background(paperColor, RoundedCornerShape(8.dp))
        ) {
            // "Tags" / Tabs at the top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .offset(y = (-4).dp) // Slightly lift tags
            ) {
                tabs.forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp)
                            .padding(horizontal = 2.dp)
                            .background(
                                if (selectedTab == index) paperColor else Color.LightGray.copy(alpha = 0.5f),
                                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                            .clickable { selectedTab = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontFamily = poppins,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) green4 else Color.Gray
                        )
                    }
                }
            }

            // Notebook Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = dsName,
                    fontFamily = cantoraFont,
                    fontSize = 32.sp,
                    color = green4
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(green4.copy(alpha = 0.3f))
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                val content = getDetailedLearnContent(dsName, selectedTab)
                
                Text(
                    text = content,
                    fontFamily = poppins,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    color = Color.DarkGray
                )
                
                // Add some "handwritten" note vibes or illustrations placeholder
                Spacer(modifier = Modifier.height(40.dp))
                
                if (selectedTab == 1) { // Operations
                    OperationDetails(dsName, poppins, green4)
                }
            }
        }
    }
}

@Composable
fun OperationDetails(dsName: String, font: FontFamily, accentColor: Color) {
    val ops = if (dsName == "Stack") {
        listOf("Push(x): Add x to top", "Pop(): Remove top", "Peek(): See top")
    } else {
        listOf("Enqueue(x): Add to back", "Dequeue(): Remove front", "Front(): See front")
    }
    
    Column {
        ops.forEach { op ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Box(modifier = Modifier.size(8.dp).background(accentColor, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = op, fontFamily = font, fontSize = 16.sp, color = Color.Black)
            }
        }
    }
}

fun getDetailedLearnContent(dsName: String, tab: Int): String {
    return when (dsName) {
        "Stack" -> when (tab) {
            0 -> "A Stack is a conceptual structure consisting of a set of homogeneous elements and is based on the principle of last in first out (LIFO).\n\nIt is a common data structure used in many areas of computer science. Think of it as a physical stack of items, where you can only add or remove the top item."
            1 -> "The primary operations of a stack are Push and Pop. \n\nPush adds an element to the top of the collection, while Pop removes the most recently added element that was not yet removed."
            2 -> "Stack operations are very efficient:\n\n- Access: O(n)\n- Search: O(n)\n- Insertion (Push): O(1)\n- Deletion (Pop): O(1)"
            else -> "Real-world Example:\n- Browsing history (Back button)\n- Undo/Redo in editors\n- Function calls (Recursion stack)"
        }
        "Queue" -> when (tab) {
            0 -> "A Queue is a linear structure which follows a particular order in which the operations are performed. The order is First In First Out (FIFO).\n\nA good example of a queue is any service of consumers where the first-come is first-served."
            1 -> "Basic operations include:\n- Enqueue: Adds an item to the rear.\n- Dequeue: Removes an item from the front.\n- Front: Get the first item."
            2 -> "Queue Performance:\n\n- Access: O(n)\n- Search: O(n)\n- Insertion (Enqueue): O(1)\n- Deletion (Dequeue): O(1)"
            else -> "Real-world Example:\n- Printing queue\n- CPU Task Scheduling\n- Call Center systems"
        }
        "Singly Linked List" -> when (tab) {
            0 -> "A Singly Linked List is a collection of nodes where each node contains data and a pointer to the 'Next' node.\n\nIt only goes in one direction, like a one-way street."
            1 -> "Operations:\n- Add Head: O(1)\n- Add Tail: O(1) (with optimization)\n- Search: O(N)"
            2 -> "Singly lists use less memory than Doubly lists because they only store ONE pointer per node."
            else -> "Use case: Simple undo buffers or symbol tables in compilers."
        }
        "Doubly Linked List" -> when (tab) {
            0 -> "A Doubly Linked List is more powerful! Each node knows its 'Next' AND its 'Previous' neighbor.\n\nYou can walk forwards or backwards through the list."
            1 -> "Operations:\n- Add/Remove at either end: O(1)\n- Delete a known node: O(1)"
            2 -> "The trade-off is Memory. Each node must store TWO pointers instead of one."
            else -> "Use case: Browser 'Forward' and 'Back' buttons, or Music Player playlists."
        }
        "AVL Tree" -> when (tab) {
            0 -> "An AVL Tree is a self-balancing Binary Search Tree (BST). It ensures that the height difference between left and right subtrees (Balance Factor) is at most 1.\n\nNamed after its inventors, Adelson-Velsky and Landis."
            1 -> "When a node is inserted or deleted, the tree checks the Balance Factor. If it becomes >1 or <-1, 'Rotations' are performed to rebalance it."
            2 -> "AVL Performance:\n- Search: O(log N)\n- Insertion: O(log N)\n- Deletion: O(log N)\n\nIt is more balanced than a regular BST, making it faster for searches."
            else -> "Real-world Example:\n- Databases (indexing)\n- Memory management systems\n- Any scenario requiring fast lookups and frequent updates."
        }
        else -> "Content coming soon..."
    }
}
