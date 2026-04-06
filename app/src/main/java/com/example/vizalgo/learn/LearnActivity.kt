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
        listOf("Overview", "Operations", "Algorithm", "Complexity", "Applications", "Examples")
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
                    color = Color.White,
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
            0 -> "📚 WHAT IS A QUEUE?\n\nA Queue is a linear data structure that follows the principle:\n\n👉 FIFO (First In, First Out)\nThe first element inserted is the first one to be removed.\n\n📌 Real-Life Examples:\n• Ticket queue at cinema 🎟️ - First person to join is served first\n• Supermarket checkout line - First customer is checked out first\n• Print queue - Documents printed in order received\n• Call center - Calls answered in order of arrival\n• CPU Task Scheduling - Tasks executed in order received\n\nA Queue is a conceptual structure consisting of homogeneous elements based on the principle of first in first out (FIFO). It's a common data structure used for managing tasks, scheduling, and buffering."
            1 -> "🔹 BASIC OPERATIONS IN QUEUE\n\n1. ENQUEUE - Insert an element into the queue\n• Adds element to the REAR\n• Time Complexity: O(1)\n\n2. DEQUEUE - Remove the front element\n• Removes the first element added\n• Time Complexity: O(1)\n\n3. PEEK/FRONT - View the front element\n• Returns front element without removing it\n• Time Complexity: O(1)\n\n4. isEmpty - Check if queue is empty\n• Returns true if no elements\n\n5. isFull - Check if queue is full\n• For array implementation only\n• Returns true if capacity is reached\n\n🔹 QUEUE POINTERS:\n• FRONT - Points to first element\n• REAR - Points to last element"
            2 -> "🔹 QUEUE ALGORITHMS\n\n📌 1. INITIALIZATION\nAlgorithm: INIT_QUEUE(queue, max)\n1. Start\n2. FRONT = 0 (front pointer)\n3. REAR = -1 (rear pointer)\n4. MAX = max (maximum size)\n5. QUEUE = array of size MAX\n6. End\n\n📌 2. ENQUEUE (Add Element)\nAlgorithm: ENQUEUE(queue, element)\n1. Start\n2. If REAR == MAX - 1 then\n      → Print \"Queue Overflow\"\n      → Return False\n3. Else\n      → REAR = REAR + 1\n      → QUEUE[REAR] = element\n      → Return True\n4. End\n\n📌 3. DEQUEUE (Remove Element)\nAlgorithm: DEQUEUE(queue)\n1. Start\n2. If FRONT > REAR then\n      → Print \"Queue Underflow\"\n      → Return Null\n3. Else\n      → element = QUEUE[FRONT]\n      → FRONT = FRONT + 1\n      → Return element\n4. End\n\n📌 4. PEEK (View Front Element)\nAlgorithm: PEEK(queue)\n1. Start\n2. If FRONT > REAR then\n      → Print \"Queue is Empty\"\n      → Return Null\n3. Else\n      → Return QUEUE[FRONT]\n4. End\n\n📌 5. IS_EMPTY\nAlgorithm: IS_EMPTY(queue)\n1. Start\n2. If FRONT > REAR then\n      → Return True\n3. Else\n      → Return False\n4. End\n\n📌 6. IS_FULL\nAlgorithm: IS_FULL(queue)\n1. Start\n2. If REAR == MAX - 1 then\n      → Return True\n3. Else\n      → Return False\n4. End"
            3 -> "⏱️ TIME COMPLEXITY OF QUEUE OPERATIONS\n\n🔹 1. ENQUEUE Operation (Insertion)\n\n👉 Steps involved:\n• Check if queue is full → constant time\n• Increment REAR → constant time\n• Insert element → constant time\n\n👉 Equation:\nT(n) = c1 + c2 + c3\n\n👉 Where:\nc1 = overflow check\nc2 = increment rear\nc3 = insertion\n\n👉 Simplified:\nT(n) = O(1)\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n🔹 2. DEQUEUE Operation (Deletion)\n\n👉 Steps involved:\n• Check if queue is empty → constant time\n• Access FRONT element → constant time\n• Increment FRONT → constant time\n\n👉 Equation:\nT(n) = c1 + c2 + c3\n\n👉 Where:\nc1 = underflow check\nc2 = access element\nc3 = increment front\n\n👉 Simplified:\nT(n) = O(1)\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n🔹 3. PEEK (FRONT) Operation\n\n👉 Steps:\n• Directly access FRONT element\n\n👉 Equation:\nT(n) = c\n\n👉 Simplified:\nO(1)\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n🔹 OVERALL COMPLEXITY SUMMARY\n\nOperation | Time Complexity\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\nEnqueue   | O(1)\nDequeue   | O(1)\nPeek      | O(1)\nSearch    | O(n)\nAccess    | O(n)\n\n🔹 KEY INSIGHT\n\n👉 Queue operations do not depend on number of elements (n)\n\nT(n) = constant\n\n👉 Why?\nBecause insertion and deletion happen only at fixed positions (FRONT and REAR), regardless of queue size."
            4 -> "💡 APPLICATIONS OF QUEUE\n\n1. CPU Scheduling\n\n• Processes are executed in FIFO order\n• First process gets CPU first\n• Used in time-sharing systems\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n2. Printer Queue\n\n• Documents are printed in order\n• First sent → first printed\n• Manages multiple print requests\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n3. Call Center Systems\n\n• Incoming calls handled sequentially\n• First caller → first response\n• Others wait in queue\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n4. Traffic Management\n\n• Vehicles move in sequence at signals\n• First vehicle → moves first\n• Maintains smooth traffic flow\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n5. Data Buffers (I/O Systems)\n\n• Temporary storage of data\n• Data processed in order\n• Used in streaming & networking\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n6. Breadth-First Search (BFS)\n\n• Traverses graphs level by level\n• Queue maintains visiting order\n• Widely used in algorithms\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n7. Keyboard Input Buffer\n\n• Stores user keystrokes\n• Processes inputs sequentially\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n8. Message Queues\n\n• Communication between systems\n• Messages processed one by one\n• Used in chat apps & background tasks\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n9. Task Scheduling\n\n• Tasks executed in arrival order\n• Used in job scheduling systems\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n10. Web Server Handling\n\n• User requests stored in queue\n• Processed one after another"
            else -> "🔹 STEP-BY-STEP EXAMPLES\n\n📌 ENQUEUE Operation:\nInitial: [10, 20, 30]\nFRONT = 0, REAR = 2\n\nAfter Enqueue(40):\nFRONT → [10]\n        [20]\n        [30]\n        [40] ← REAR\n\nQueue = [10, 20, 30, 40]\nFRONT = 0, REAR = 3\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n📌 DEQUEUE Operation:\nInitial:\nFRONT → [10]\n        [20]\n        [30]\n        [40] ← REAR\n\nAfter Dequeue():\nRemoved: 10\n\n        [20] ← FRONT\n        [30]\n        [40] ← REAR\n\nQueue = [X, 20, 30, 40]\nFRONT = 1, REAR = 3\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n📌 PEEK Operation:\nQueue: [10, 20, 30, 40]\nPeek() → Returns 10 (without removing)\n\nQueue remains: [10, 20, 30, 40]\nFRONT unchanged\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n💡 BONUS: Queue Types\n\n✅ Linear Queue\n→ Simple implementation\n→ May cause space waste (FRONT keeps increasing)\n\n✅ Circular Queue\n→ Efficient use of space\n→ Wraps around when reaching end\n→ Better for fixed-size applications"
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
