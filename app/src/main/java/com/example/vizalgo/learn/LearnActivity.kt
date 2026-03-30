package com.example.vizalgo.learn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
    val green4 = colorResource(id = R.color.green4)
    val green1 = colorResource(id = R.color.green1)
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = if (dsName == "Stack") {
        listOf("Overview", "Operations", "Algorithm", "Rules", "Complexity", "Applications", "Examples")
    } else {
        listOf("Overview", "Operations", "Complexity", "Examples")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(green4)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(green4.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .border(2.dp, green1.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
        ) {
            // Enhanced Tabs at the top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .offset(y = (-4).dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(horizontal = 2.dp)
                            .background(
                                if (isSelected) green1 else Color.LightGray.copy(alpha = 0.7f),
                                RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                            )
                            .then(
                                if (isSelected) Modifier.shadow(6.dp, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                else Modifier
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) green4 else Color.Gray.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                            )
                            .clickable { selectedTab = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = if (isSelected) 15.sp else 13.sp,
                            fontFamily = poppins,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF444444),
                            letterSpacing = if (isSelected) 1.2.sp else 0.5.sp
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
                // Main heading visually distinct
                Text(
                    text = dsName,
                    fontFamily = cantoraFont,
                    fontSize = 36.sp,
                    color = green4,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
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
                    color = green1
                )

                Spacer(modifier = Modifier.height(40.dp))

                if (selectedTab == 1) { // Operations
                    OperationDetails(dsName, poppins, green4, green1)
                }
            }
        }
    }
}

@Composable
fun OperationDetails(dsName: String, font: FontFamily, accentColor: Color, green1: Color) {
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
                Text(text = op, fontFamily = font, fontSize = 16.sp, color = green1)
            }
        }
    }
}

fun getDetailedLearnContent(dsName: String, tab: Int): String {
    return when (dsName) {
        "Stack" -> when (tab) {
            0 -> "📚 WHAT IS A STACK?\n\nA Stack is a linear data structure that follows the principle:\n\n👉 LIFO (Last In, First Out)\nThe last element inserted is the first one to be removed.\n\n📌 Real-Life Examples:\n• Stack of plates 🍽️ - You add/remove from the top\n• Undo feature in apps - Last action is undone first\n• Browser history - Back button shows last visited page\n• Function calls - Recursion uses call stack\n\nA Stack is a conceptual structure consisting of homogeneous elements based on the principle of last in first out (LIFO). It's a common data structure used in many areas of computer science."
            1 -> "🔹 BASIC OPERATIONS IN STACK\n\n1. PUSH - Insert an element into the stack\n• Adds element to the TOP\n• Time Complexity: O(1)\n\n2. POP - Remove the top element\n• Removes the most recently added element\n• Time Complexity: O(1)\n\n3. PEEK/TOP - View the top element\n• Returns top element without removing it\n• Time Complexity: O(1)\n\n4. isEmpty - Check if stack is empty\n• Returns true if no elements\n\n5. isFull - Check if stack is full\n• For array implementation only\n• Returns true if capacity is reached\n\n🔹 1. PUSH Operation (Insertion)\n👉 Algorithm: PUSH(stack, element)\n1. Start\n2. Check if TOP == MAX - 1\n      → If yes, print \"Stack Overflow\" and STOP\n3. Else\n      → TOP = TOP + 1\n      → STACK[TOP] = element\n4. End\n\n📌 Explanation:\nWe first check if stack is full\nIf not, we increase TOP\nInsert the new element at TOP\n\n📌 Example:\n\nBefore Push:\n\nSTACK = [10, 20, 30]\nTOP = 2\n\nPush 40:\n\nSTACK = [10, 20, 30, 40]\nTOP = 3\n\n🔹 2. POP Operation (Deletion)\n👉 Algorithm: POP(stack)\n1. Start\n2. Check if TOP == -1\n      → If yes, print \"Stack Underflow\" and STOP\n3. Else\n      → element = STACK[TOP]\n      → TOP = TOP - 1\n      → print element\n4. End\n\n📌 Explanation:\nWe first check if stack is empty\nIf not, remove the top element\nDecrease TOP\n\n📌 Example:\n\nBefore Pop:\n\nSTACK = [10, 20, 30, 40]\nTOP = 3\n\nAfter Pop:\n\nRemoved = 40\nSTACK = [10, 20, 30]\nTOP = 2\n\n🔹 3. Key Conditions\nCondition\tMeaning\nTOP = -1\tStack is empty\nTOP = MAX - 1\tStack is full"
            2 -> "🔹 STACK ALGORITHMS\n\n📌 1. INITIALIZATION\nAlgorithm: INIT_STACK(stack, max)\n1. Start\n2. TOP = -1 (empty stack)\n3. MAX = max (maximum size)\n4. STACK = array of size MAX\n5. End\n\n📌 2. PUSH (Add Element)\nAlgorithm: PUSH(stack, element)\n1. Start\n2. If TOP == MAX - 1 then\n      → Print \"Stack Overflow\"\n      → Return False\n3. Else\n      → TOP = TOP + 1\n      → STACK[TOP] = element\n      → Return True\n4. End\n\n📌 3. POP (Remove Element)\nAlgorithm: POP(stack)\n1. Start\n2. If TOP == -1 then\n      → Print \"Stack Underflow\"\n      → Return Null\n3. Else\n      → element = STACK[TOP]\n      → TOP = TOP - 1\n      → Return element\n4. End\n\n📌 4. PEEK (View Top Element)\nAlgorithm: PEEK(stack)\n1. Start\n2. If TOP == -1 then\n      → Print \"Stack is Empty\"\n      → Return Null\n3. Else\n      → Return STACK[TOP]\n4. End\n\n📌 5. IS_EMPTY\nAlgorithm: IS_EMPTY(stack)\n1. Start\n2. If TOP == -1 then\n      → Return True\n3. Else\n      → Return False\n4. End\n\n📌 6. IS_FULL\nAlgorithm: IS_FULL(stack)\n1. Start\n2. If TOP == MAX - 1 then\n      → Return True\n3. Else\n      → Return False\n4. End\n\n📌 7. DISPLAY (Print All Elements)\nAlgorithm: DISPLAY(stack)\n1. Start\n2. If TOP == -1 then\n      → Print \"Stack is Empty\"\n      → Return\n3. For i = TOP down to 0\n      → Print STACK[i]\n4. End"
            3 -> "🔹 STACK RULES\n\n1. Only one end (TOP) is used for both insertion and deletion\n• You cannot add/remove from the middle or bottom\n\n2. Push & Pop operations happen at TOP only\n• All modifications are at the TOP\n\n3. Cannot access middle elements directly\n• Must pop elements from top to access others\n\n4. Overflow\n• Occurs when trying to push to a full stack\n• Memory limit exceeded (array implementation)\n\n5. Underflow\n• Occurs when trying to pop from an empty stack\n• No elements to remove\n\n🔹 STACK REPRESENTATIONS:\n✅ Using Array - Fixed size, faster access, may overflow\n✅ Using Linked List - Dynamic size, no overflow until memory full"
            4 -> "⏱️ TIME COMPLEXITY ANALYSIS\n\nStack Operations Performance:\n\nOperation | Time Complexity | Space\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\nPush      | O(1)           | O(n)\nPop       | O(1)           | O(n)\nPeek/Top  | O(1)           | O(1)\nSearch    | O(n)           | -\nAccess    | O(n)           | -\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\nWhy is Push O(1)?\n→ Inserting at a fixed position (TOP)\n→ No need to shift elements\n\nWhy is Pop O(1)?\n→ Removing from a fixed position (TOP)\n→ No reorganization needed\n\nWhy is Search O(n)?\n→ Must pop all elements to find a value\n→ Cannot access middle directly"
            5 -> "💡 APPLICATIONS OF STACK\n\n1. Expression Evaluation & Conversion\n• Evaluating infix expressions\n• Converting infix to postfix notation\n• Calculating mathematical expressions\n\n2. Parenthesis/Bracket Checking\n• Validating balanced parentheses: ( { [ ] } )\n• Compiler syntax checking\n\n3. Function Calls (Call Stack)\n• Managing function calls in recursion\n• Call stack trace in debuggers\n• Program execution flow\n\n4. Undo/Redo Operations\n• Undo in text editors (Word, Google Docs)\n• Undo in design software (Photoshop, Figma)\n• Browser back button\n\n5. Backtracking Algorithms\n• Maze solving (DFS)\n• Puzzle solving\n• Game AI decision making\n\n6. Memory Management\n• Memory allocation on stack vs heap\n• Variable scope management\n\n7. Browser History\n• Storing visited pages\n• Back navigation"
            else -> "🔹 STEP-BY-STEP EXAMPLES\n\n📌 PUSH Operation:\nInitial: [10, 20, 30]\nTOP = 2\n\nAfter Push(40):\nTOP → [40]\n      [30]\n      [20]\n      [10]\n\n📌 POP Operation:\nInitial:\nTOP → [40]\n      [30]\n      [20]\n      [10]\n\nAfter Pop():\nRemoved: 40\nTOP → [30]\n      [20]\n      [10]\n\n📌 PEEK Operation:\nStack: [10, 20, 30]\nPeek() → Returns 30 (without removing)\n\n💡 BONUS: Implementation Tips\n→ Use dynamic arrays/lists for flexibility\n→ Handle overflow/underflow gracefully\n→ Add animations for better visualization\n→ Create step-by-step simulation mode"
        }
        "Queue" -> when (tab) {
            0 -> "A Queue is a linear structure which follows a particular order in which the operations are performed. The order is First In First Out (FIFO).\n\nA good example of a queue is any service of consumers where the first-come is first-served."
            1 -> "Basic operations include:\n- Enqueue: Adds an item to the rear.\n- Dequeue: Removes an item from the front.\n- Front: Get the first item."
            2 -> "Queue Performance:\n\n- Access: O(n)\n- Search: O(n)\n- Insertion (Enqueue): O(1)\n- Deletion (Dequeue): O(1)"
            else -> "Real-world Example:\n- Printing queue\n- CPU Task Scheduling\n- Call Center systems"
        }
        else -> "Content coming soon..."
    }
}
