package com.example.vizalgo.learn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R
import kotlin.math.abs

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
    var menuOpen by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(0f) }
    
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
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        dragOffset += dragAmount
                        if (abs(dragOffset) > 100) {
                            if (dragOffset > 0 && selectedTab > 0) {
                                selectedTab--
                                dragOffset = 0f
                            } else if (dragOffset < 0 && selectedTab < tabs.size - 1) {
                                selectedTab++
                                dragOffset = 0f
                            }
                        }
                    }
                }
        ) {
            // Header with Hamburger Menu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dsName,
                    fontFamily = cantoraFont,
                    fontSize = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = { menuOpen = !menuOpen },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = green1,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Hamburger Menu Dropdown
            if (menuOpen) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(green4.copy(alpha = 0.8f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Text(
                            text = title,
                            fontFamily = poppins,
                            fontSize = 16.sp,
                            color = if (selectedTab == index) green1 else Color.White,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTab = index
                                    menuOpen = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                        )
                    }
                }
            }

            Divider(
                color = green1.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            // Content Area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Current Section Heading
                Text(
                    text = tabs[selectedTab],
                    fontFamily = poppins,
                    fontSize = 22.sp,
                    color = green1,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Divider(
                    color = green1.copy(alpha = 0.3f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                val content = getDetailedLearnContent(dsName, selectedTab)

                Text(
                    text = content,
                    fontFamily = poppins,
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    color = green1
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Navigation Hints
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedTab > 0) {
                        Text(
                            text = "Swipe left or select menu",
                            fontFamily = poppins,
                            fontSize = 12.sp,
                            color = green1.copy(alpha = 0.6f)
                        )
                    }
                    if (selectedTab < tabs.size - 1) {
                        Text(
                            text = "${selectedTab + 1} of ${tabs.size}",
                            fontFamily = poppins,
                            fontSize = 14.sp,
                            color = green1,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun getDetailedLearnContent(dsName: String, tab: Int): String {
    return when (dsName) {
        "Stack" -> when (tab) {
            0 -> "DEFINITION:\n\nStack is a linear data structure that follows LIFO (Last In First Out) Principle, the last element inserted is the first to be popped out. It means both insertion and deletion operations happen at one end only.\n\nLIFO (Last In First Out) Principle\n\nThe LIFO principle means that the last element added to a stack is the first one to be removed.\n\n• New elements are always pushed on top\n• Removal (pop) also happens only from the top\n• This ensures a strict order: last in → first out\n• No random access - only top element is accessible\n• Operations are fast because of fixed location\n\n---\n\nReal-World Examples of LIFO:\n\nStack of Plates:\nThe last plate placed on top is the first one you pick up.\nWhen you add a new plate, it goes on top.\nWhen you need a plate, you take from the top.\n\nStack of Books:\nBooks are added and removed from the top.\nThe last book placed is the first one taken.\nSimilar principle to a physical stack.\n\nUndo Feature in Applications:\nLast action performed is undone first.\nLike Word, Photoshop, or text editors.\n\nBrowser History:\nBack button shows the last visited page.\nMost recent page is always on top.\n\nFunction Call Stack:\nLast function called is first to return.\nUsed in recursion and program execution.\n\nText Editor Operations:\nUndo/Redo follows stack principle.\nLast edit is first to be undone."
            1 -> "BASIC OPERATIONS IN STACK\n\n1. PUSH - Insert an element into the stack\n   • Adds element to the TOP\n   • Time Complexity: O(1)\n   • Precondition: Stack not full\n\n2. POP - Remove the top element\n   • Removes the most recently added element\n   • Time Complexity: O(1)\n   • Precondition: Stack not empty\n   • Returns removed element\n\n3. PEEK/TOP - View the top element\n   • Returns top element without removing it\n   • Time Complexity: O(1)\n   • Precondition: Stack not empty\n   • Does not modify stack\n\n4. isEmpty - Check if stack is empty\n   • Returns true if no elements\n   • Condition: TOP == -1\n\n5. isFull - Check if stack is full\n   • For array implementation only\n   • Returns true if capacity is reached\n   • Condition: TOP == MAX - 1"
            2 -> "STACK ALGORITHMS\n\n1. INITIALIZATION\nAlgorithm: INIT_STACK(stack, max)\n1. Start\n2. TOP = -1 (empty stack)\n3. MAX = max (maximum size)\n4. STACK = array of size MAX\n5. End\n\n---\n\n2. PUSH (Add Element)\nAlgorithm: PUSH(stack, element)\n1. Start\n2. If TOP == MAX - 1 then\n   Print \"Stack Overflow\"\n   Return False\n3. Else\n   TOP = TOP + 1\n   STACK[TOP] = element\n   Return True\n4. End\n\n---\n\n3. POP (Remove Element)\nAlgorithm: POP(stack)\n1. Start\n2. If TOP == -1 then\n   Print \"Stack Underflow\"\n   Return Null\n3. Else\n   element = STACK[TOP]\n   TOP = TOP - 1\n   Return element\n4. End\n\n---\n\n4. PEEK (View Top Element)\nAlgorithm: PEEK(stack)\n1. Start\n2. If TOP == -1 then\n   Print \"Stack is Empty\"\n   Return Null\n3. Else\n   Return STACK[TOP]\n4. End\n\n---\n\n5. IS_EMPTY\nAlgorithm: IS_EMPTY(stack)\n1. Start\n2. If TOP == -1 then\n   Return True\n3. Else\n   Return False\n4. End\n\n---\n\n6. IS_FULL\nAlgorithm: IS_FULL(stack)\n1. Start\n2. If TOP == MAX - 1 then\n   Return True\n3. Else\n   Return False\n4. End\n\n---\n\n7. DISPLAY (Print All Elements)\nAlgorithm: DISPLAY(stack)\n1. Start\n2. If TOP == -1 then\n   Print \"Stack is Empty\"\n   Return\n3. For i = TOP down to 0\n   Print STACK[i]\n4. End"
            3 -> "STACK RULES\n\n1. Only one end (TOP) is used for both insertion and deletion\n   • You cannot add/remove from the middle or bottom\n   • All operations are at the TOP only\n   • No side access - restricted to top only\n\n2. Push & Pop operations happen at TOP only\n   • All modifications are at the TOP\n   • Other positions are inaccessible\n   • Cannot bypass elements\n\n3. Cannot access middle elements directly\n   • Must pop elements from top to access others\n   • No random access like arrays\n   • Sequential access only\n\n4. Overflow\n   • Occurs when trying to push to a full stack\n   • Memory limit exceeded (array implementation)\n   • Error: Stack Overflow\n\n5. Underflow\n   • Occurs when trying to pop from an empty stack\n   • No elements to remove\n   • Error: Stack Underflow\n\n---\n\nBASIC TERMINOLOGIES OF STACK\n\nTop:\n   • The position of the most recently inserted element\n   • Insertions (push) and deletions (pop) always happen at the top\n   • Initially set to -1 for empty stack\n   • Incremented when element is pushed\n   • Decremented when element is popped\n\nSize:\n   • Refers to the current number of elements in the stack\n   • Calculated as: Size = Top + 1\n   • Ranges from 0 (empty) to MAX (full)\n   • Helps determine if stack is full or empty\n\n---\n\nTYPES OF STACK\n\nFixed Size Stack\n   • Has a predefined capacity\n   • Once full, no more elements can be added\n   • Causes overflow if you try to add beyond capacity\n   • Causes underflow if you try to remove from empty stack\n   • Implemented using a static array\n   • Memory is allocated at compilation time\n\n   Advantages:\n   - Simple implementation\n   - Fast memory access\n   - Good for known, limited data\n\n   Disadvantages:\n   - Memory is fixed (wasteful if unused)\n   - Overflow risk if data exceeds size\n   - Less flexible\n\nDynamic Size Stack\n   • Can grow and shrink automatically as needed\n   • If full, capacity expands to allow more elements\n   • Memory usage shrinks when elements are removed\n   • Can be implemented using:\n     - Linked List (grows/shrinks naturally)\n     - Dynamic Array (resizes automatically)\n   • Memory is allocated at runtime\n\n   Advantages:\n   - Flexible and adaptive\n   - No overflow issues (until memory runs out)\n   - Efficient memory usage\n   - Suitable for variable-sized data\n\n   Disadvantages:\n   - Slightly slower due to memory management\n   - Extra memory for pointers (linked list)\n   - More complex implementation\n\nNote: We generally use dynamic stacks in practice, as they can grow or shrink as needed without overflow issues."
            4 -> "TIME & SPACE COMPLEXITY ANALYSIS\n\nSTACK OPERATIONS PERFORMANCE\n\nOperation     | Time Complexity | Space\n-------------|-----------------|----------\nPush         | O(1)           | O(n)\nPop          | O(1)           | O(n)\nPeek/Top     | O(1)           | O(1)\nSearch       | O(n)           | -\nAccess       | O(n)           | -\nIs Empty     | O(1)           | O(1)\nIs Full      | O(1)           | O(1)\n\nWHY IS PUSH O(1)?\n   • Inserting at a fixed position (TOP)\n   • No need to shift elements\n   • Direct memory access\n\nWHY IS POP O(1)?\n   • Removing from a fixed position (TOP)\n   • No reorganization needed\n   • Direct memory access\n\nWHY IS SEARCH O(n)?\n   • Must pop all elements to find a value\n   • Cannot access middle directly\n   • Worst case: traverse entire stack\n\nKEY INSIGHT:\n   • Stack operations are constant time\n   • Efficiency independent of stack size (n)\n   • No shifting or reorganization needed"
            5 -> "APPLICATIONS OF STACK\n\n1. Expression Evaluation & Conversion\n   • Evaluating infix expressions\n   • Converting infix to postfix notation\n   • Calculating mathematical expressions\n   • Handling operator precedence\n\n---\n\n2. Parenthesis/Bracket Checking\n   • Validating balanced parentheses: ( { [ ] } )\n   • Compiler syntax checking\n   • Code validation\n\n---\n\n3. Function Calls (Call Stack)\n   • Managing function calls in recursion\n   • Call stack trace in debuggers\n   • Program execution flow\n   • Local variable storage\n\n---\n\n4. Undo/Redo Operations\n   • Undo in text editors (Word, Google Docs)\n   • Undo in design software (Photoshop, Figma)\n   • Browser back button\n   • Command history management\n\n---\n\n5. Backtracking Algorithms\n   • Maze solving (DFS - Depth First Search)\n   • Puzzle solving (N-Queens, Sudoku)\n   • Game AI decision making\n   • Path finding\n\n---\n\n6. Memory Management\n   • Memory allocation on stack vs heap\n   • Variable scope management\n   • Return address storage\n\n---\n\n7. Browser History\n   • Storing visited pages\n   • Back navigation\n   • History management"
            else -> "STEP-BY-STEP EXAMPLES\n\nPUSH OPERATION:\nInitial: [10, 20, 30]\nTOP = 2\n\nAfter Push(40):\nTOP -> [ 40 ]\n       [ 30 ]\n       [ 20 ]\n       [ 10 ]\n\nSTACK = [10, 20, 30, 40]\nTOP = 3\n\n---\n\nPOP OPERATION:\nInitial:\nTOP -> [ 40 ]\n       [ 30 ]\n       [ 20 ]\n       [ 10 ]\n\nAfter Pop():\nRemoved: 40\nTOP -> [ 30 ]\n       [ 20 ]\n       [ 10 ]\n\nSTACK = [10, 20, 30]\nTOP = 2\n\n---\n\nPEEK OPERATION:\nStack: [10, 20, 30]\nTOP = 2\nPeek() -> Returns 30 (without removing)\n\nStack remains: [10, 20, 30]\nTOP unchanged at 2\n\n---\n\nBONUS: Implementation Tips\n- Use dynamic arrays/lists for flexibility\n- Handle overflow/underflow gracefully\n- Add animations for better visualization\n- Create step-by-step simulation mode\n- Test edge cases (empty, full, single element)"
        }
        "Queue" -> when (tab) {
            0 -> "DEFINITION:\n\nQueue is a linear data structure that follows the FIFO (First In, First Out) principle. The first element inserted is the first one to be removed.\n\nFIFO PRINCIPLE:\nQueue follows FIFO (First In, First Out) - The first element inserted is the first one removed.\n\nReal-Life Examples:\n• Ticket queue at cinema - First person to join is served first\n• Supermarket checkout line - First customer is checked out first\n• Print queue - Documents printed in order received\n• Call center - Calls answered in order of arrival\n• CPU Task Scheduling - Tasks executed in order received\n• Hospital waiting room - First patient is called first"
            1 -> "BASIC OPERATIONS IN QUEUE\n\n1. ENQUEUE - Insert an element into the queue\n   • Adds element to the REAR\n   • Time Complexity: O(1)\n   • Precondition: Queue not full\n\n2. DEQUEUE - Remove the front element\n   • Removes the first element added\n   • Time Complexity: O(1)\n   • Precondition: Queue not empty\n   • Returns removed element\n\n3. PEEK/FRONT - View the front element\n   • Returns front element without removing it\n   • Time Complexity: O(1)\n   • Precondition: Queue not empty\n\n4. isEmpty - Check if queue is empty\n   • Returns true if no elements\n   • Condition: FRONT > REAR\n\n5. isFull - Check if queue is full\n   • For array implementation only\n   • Returns true if capacity is reached\n   • Condition: REAR == MAX - 1\n\nQUEUE POINTERS:\n   • FRONT - Points to first element\n   • REAR - Points to last element"
            2 -> "QUEUE ALGORITHMS\n\n1. INITIALIZATION\nAlgorithm: INIT_QUEUE(queue, max)\n1. Start\n2. FRONT = 0 (front pointer)\n3. REAR = -1 (rear pointer)\n4. MAX = max (maximum size)\n5. QUEUE = array of size MAX\n6. End\n\n---\n\n2. ENQUEUE (Add Element)\nAlgorithm: ENQUEUE(queue, element)\n1. Start\n2. If REAR == MAX - 1 then\n   Print \"Queue Overflow\"\n   Return False\n3. Else\n   REAR = REAR + 1\n   QUEUE[REAR] = element\n   Return True\n4. End\n\n---\n\n3. DEQUEUE (Remove Element)\nAlgorithm: DEQUEUE(queue)\n1. Start\n2. If FRONT > REAR then\n   Print \"Queue Underflow\"\n   Return Null\n3. Else\n   element = QUEUE[FRONT]\n   FRONT = FRONT + 1\n   Return element\n4. End\n\n---\n\n4. PEEK (View Front Element)\nAlgorithm: PEEK(queue)\n1. Start\n2. If FRONT > REAR then\n   Print \"Queue is Empty\"\n   Return Null\n3. Else\n   Return QUEUE[FRONT]\n4. End\n\n---\n\n5. IS_EMPTY\nAlgorithm: IS_EMPTY(queue)\n1. Start\n2. If FRONT > REAR then\n   Return True\n3. Else\n   Return False\n4. End\n\n---\n\n6. IS_FULL\nAlgorithm: IS_FULL(queue)\n1. Start\n2. If REAR == MAX - 1 then\n   Return True\n3. Else\n   Return False\n4. End"
            3 -> "TIME COMPLEXITY OF QUEUE OPERATIONS\n\n1. ENQUEUE Operation (Insertion)\n\nSteps involved:\n   • Check if queue is full -> constant time\n   • Increment REAR -> constant time\n   • Insert element -> constant time\n\nEquation:\nT(n) = c1 + c2 + c3\n\nWhere:\n   c1 = overflow check\n   c2 = increment rear\n   c3 = insertion\n\nSimplified:\nT(n) = O(1)\n\n---\n\n2. DEQUEUE Operation (Deletion)\n\nSteps involved:\n   • Check if queue is empty -> constant time\n   • Access FRONT element -> constant time\n   • Increment FRONT -> constant time\n\nEquation:\nT(n) = c1 + c2 + c3\n\nWhere:\n   c1 = underflow check\n   c2 = access element\n   c3 = increment front\n\nSimplified:\nT(n) = O(1)\n\n---\n\n3. PEEK (FRONT) Operation\n\nSteps:\n   • Directly access FRONT element\n\nEquation:\nT(n) = c\n\nSimplified:\nO(1)\n\n---\n\nOVERALL COMPLEXITY SUMMARY\n\nOperation     | Time Complexity\n-------------|----------------\nEnqueue      | O(1)\nDequeue      | O(1)\nPeek         | O(1)\nSearch       | O(n)\nAccess       | O(n)\n\nKEY INSIGHT:\nQueue operations do not depend on number of elements (n)\nT(n) = constant\n\nWhy?\nBecause insertion and deletion happen only at fixed positions (FRONT and REAR), regardless of queue size."
            4 -> "APPLICATIONS OF QUEUE\n\n1. CPU Scheduling\n   • Processes are executed in FIFO order\n   • First process gets CPU first\n   • Used in time-sharing systems\n\n---\n\n2. Printer Queue\n   • Documents are printed in order\n   • First sent -> first printed\n   • Manages multiple print requests\n\n---\n\n3. Call Center Systems\n   • Incoming calls handled sequentially\n   • First caller -> first response\n   • Others wait in queue\n\n---\n\n4. Traffic Management\n   • Vehicles move in sequence at signals\n   • First vehicle -> moves first\n   • Maintains smooth traffic flow\n\n---\n\n5. Data Buffers (I/O Systems)\n   • Temporary storage of data\n   • Data processed in order\n   • Used in streaming & networking\n\n---\n\n6. Breadth-First Search (BFS)\n   • Traverses graphs level by level\n   • Queue maintains visiting order\n   • Widely used in algorithms\n\n---\n\n7. Keyboard Input Buffer\n   • Stores user keystrokes\n   • Processes inputs sequentially\n\n---\n\n8. Message Queues\n   • Communication between systems\n   • Messages processed one by one\n   • Used in chat apps & background tasks\n\n---\n\n9. Task Scheduling\n   • Tasks executed in arrival order\n   • Used in job scheduling systems\n\n---\n\n10. Web Server Handling\n    • User requests stored in queue\n    • Processed one after another"
            else -> "STEP-BY-STEP EXAMPLES\n\nENQUEUE OPERATION:\nInitial: [10, 20, 30]\nFRONT = 0, REAR = 2\n\nAfter Enqueue(40):\nFRONT -> [10]\n         [20]\n         [30]\n         [40] <- REAR\n\nQueue = [10, 20, 30, 40]\nFRONT = 0, REAR = 3\n\n---\n\nDEQUEUE OPERATION:\nInitial:\nFRONT -> [10]\n         [20]\n         [30]\n         [40] <- REAR\n\nAfter Dequeue():\nRemoved: 10\n\n         [20] <- FRONT\n         [30]\n         [40] <- REAR\n\nQueue = [X, 20, 30, 40]\nFRONT = 1, REAR = 3\n\n---\n\nPEEK OPERATION:\nQueue: [10, 20, 30, 40]\nPeek() -> Returns 10 (without removing)\n\nQueue remains: [10, 20, 30, 40]\nFRONT unchanged\n\n---\n\nBONUS: Queue Types\n\nLinear Queue\n   - Simple implementation\n   - May cause space waste (FRONT keeps increasing)\n\nCircular Queue\n   - Efficient use of space\n   - Wraps around when reaching end\n   - Better for fixed-size applications"
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

