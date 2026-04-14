package com.example.vizalgo.learn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
    
    var selectedTab by remember { mutableIntStateOf(0) }
    var menuOpen by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    
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
                    detectHorizontalDragGestures { _, dragAmount ->
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

            HorizontalDivider(
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

                HorizontalDivider(
                    color = green1.copy(alpha = 0.3f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                if (selectedTab == 0) {
                    val imageRes = when (dsName) {
                        "Stack" -> R.drawable.stack
                        "Queue" -> R.drawable.queue
                        "Singly Linked List" -> R.drawable.singlylinkedlist
                        "Doubly Linked List" -> R.drawable.doublylinkedlist
                        "Circular Linked List" -> R.drawable.circularlinkedlist
                        "Binary Search Tree" -> R.drawable.binarysearchtree
                        "AVL Tree" -> R.drawable.avltree
                        "Heap" -> R.drawable.heap
                        "B-Tree" -> R.drawable.btree
                        "B+ Tree" -> R.drawable.bplustree
                        else -> null
                    }
                    imageRes?.let {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = "$dsName Illustration",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(bottom = 20.dp)
                                .border(1.dp, green1.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

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
            4 -> "TIME & SPACE COMPLEXITY ANALYSIS\n\nSTACK OPERATIONS PERFORMANCE\n\nOperation     | Time Complexity | Space\n-------------|-----------------|----------\nPush         | O(1)           | O(n)\nPop          | O(1)           | O(n)\nPeek/Top     | O(1)           | O(1)\nSearch       | O(n)           | -\nAccess       | O(n)           | -\nIs Empty     | O(1)           | O(1)\nIs Full      | O(1)           | O(1)\n\nTIME COMPLEXITY EQUATIONS\n\n1. Push Operation:\n   T(n) = c\n   Since insertion happens at the TOP, complexity is O(1).\n\n2. Pop Operation:\n   T(n) = c\n   Since removal happens at the TOP, complexity is O(1).\n\n3. Search Operation:\n   T(n) = c * n\n   In the worst case, we must traverse all n elements.\n\nKEY INSIGHT:\n   • Stack operations (Push/Pop) are constant time.\n   • Efficiency is independent of stack size (n).\n   • No shifting or reorganization is needed."
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
            0 -> "DEFINITION:\n\nSingly Linked List is a linear data structure where elements, known as nodes, are linked using pointers. Unlike arrays, nodes are not stored in contiguous memory locations.\n\nEach node consists of two parts:\n1. Data: The actual value stored in the node.\n2. Next: A reference or pointer to the succeeding node in the sequence.\n\nTerminology:\n• Head: The starting node of the list.\n• NULL: Indicates the end of the list; the last node's next pointer points here.\n\nVisual Representation:\n[ DATA | NEXT ] -> [ DATA | NEXT ] -> NULL"
            1 -> "BASIC OPERATIONS IN SINGLY LINKED LIST\n\n1. INSERTION - Adding a new node to the list\n   • At the Beginning: Adding a node before the current head.\n   • At the End: Appending a node after the last element.\n   • At a Specific Position: Inserting a node at a given index.\n\n2. DELETION - Removing an existing node\n   • From the Beginning: Removing the head node.\n   • From the End: Removing the last node.\n   • From a Specific Position: Removing a node based on its index.\n\n3. TRAVERSAL - Accessing each node\n   • Sequential access from head to the last node.\n\n4. SEARCHING - Finding a value\n   • Locating a specific element within the list."
            2 -> "SINGLY LINKED LIST ALGORITHMS\n\n1. INSERTION AT BEGINNING\n   1. Create a new node with the given data.\n   2. Link the new node's next pointer to the current head.\n   3. Update the head to point to the new node.\n\n2. INSERTION AT END\n   1. Create a new node.\n   2. If the list is empty, set the new node as the head.\n   3. Otherwise, traverse the list until the last node is reached.\n   4. Set the last node's next pointer to the new node.\n\n3. DELETION FROM BEGINNING\n   1. Check if the list is empty; if so, exit.\n   2. Create a temporary pointer to the head.\n   3. Move the head pointer to the next node (head = head.next).\n   4. Remove the temporary node from memory.\n\n4. DELETION FROM END\n   1. Check if the list is empty or has only one node.\n   2. Traverse the list to find the second-to-last node.\n   3. Set the second-to-last node's next pointer to NULL.\n   4. Remove the last node.\n\n5. TRAVERSAL\n   1. Start with a temporary pointer at the head.\n   2. While the pointer is not NULL:\n      - Process or print the current node's data.\n      - Move the pointer to the next node.\n\n6. SEARCHING\n   1. Initialize a pointer at the head.\n   2. Iterate through the list:\n      - If the current node's data matches the search key, return success.\n      - Move to the next node.\n   3. If NULL is reached, the element is not in the list."
            3 -> "TIME AND SPACE COMPLEXITY ANALYSIS\n\nPERFORMANCE BREAKDOWN\n\nOperation              | Time Complexity | Space\n-----------------------|-----------------|----------\nInsertion (Beginning)  | O(1)            | O(1)\nInsertion (End)        | O(n)            | O(1)\nDeletion (Beginning)   | O(1)            | O(1)\nDeletion (End)         | O(n)            | O(1)\nTraversal              | O(n)            | O(1)\nSearching              | O(n)            | O(1)\n\nOverall Space Complexity: O(n)\n\nTIME COMPLEXITY EQUATIONS\n\n1. Traversal & Searching:\n   T(n) = c * n\n   Where c is the time to visit/compare each node.\n   Since we visit n nodes: O(n)\n\n2. Insertion at Beginning:\n   T(n) = c\n   Only pointer updates are required, no traversal.\n   Complexity: O(1)\n\n3. Insertion at End:\n   T(n) = (c * n) + k\n   We must traverse n nodes (c * n) before adding the new node (k).\n   Complexity: O(n)\n\n4. Deletion at Beginning:\n   T(n) = c\n   Only the head pointer needs to be updated.\n   Complexity: O(1)\n\n5. Deletion at End:\n   T(n) = c * (n - 1)\n   We must traverse to the second-to-last node (n-1 nodes).\n   Complexity: O(n)\n\nKEY INSIGHT\n• n represents the total number of nodes currently in the list.\n• The efficiency of operations like insertion at the end is dependent on the list size, whereas operations at the beginning remain constant regardless of size."
            4 -> "APPLICATIONS OF SINGLY LINKED LIST\n\n1. Dynamic Memory Allocation\n   • Used by operating systems to manage free memory blocks.\n\n2. Implementation of Other Data Structures\n   • Serves as the foundation for Stacks and Queues.\n\n3. Undo/Redo Functionality\n   • Maintaining a history of actions in software applications.\n\n4. Navigation Systems\n   • Browser history (moving back) or music playlists where songs are played in sequence.\n\n5. Symbol Tables\n   • Used in compiler design for managing identifiers.\n\n6. Image Gallery\n   • Sequential viewing of photos in an album.\n\n7. Key Characteristics\n   • Dynamic Size: Unlike arrays, it can grow or shrink during runtime.\n   • Non-Contiguous Memory: Nodes can be scattered in memory.\n   • No Random Access: To access the i-th element, you must traverse from the head.\n   • Memory Overhead: Each node requires extra space for the pointer."
            else -> "STEP-BY-STEP EXAMPLES\n\nCREATING A LIST\nInitial State: Empty List (Head = NULL)\n\n1. Insert 10 at End:\n   - Head -> [10 | NULL]\n\n2. Insert 20 at End:\n   - Head -> [10 | Next] -> [20 | NULL]\n\n3. Insert 30 at End:\n   - Head -> [10 | Next] -> [20 | Next] -> [30 | NULL]\n\nLIST AFTER OPERATIONS:\nCurrent List: 10 -> 20 -> 30 -> NULL\n\n1. Insertion at Beginning (Value 5):\n   - Result: 5 -> 10 -> 20 -> 30 -> NULL\n\n2. Deletion at Beginning:\n   - Result: 10 -> 20 -> 30 -> NULL\n\n3. Searching for 20:\n   - Found at the second position."
        }

        "Doubly Linked List" -> when (tab) {
            0 -> "DEFINITION:\n\nA Doubly Linked List (DLL) is a complex type of linked list in which each node contains a pointer to the previous node as well as the next node in the sequence. This bidirectional nature allows for traversal in both forward and backward directions.\n\nStructure of a Node:\n1. Prev: Pointer to the previous node.\n2. Data: The value stored in the node.\n3. Next: Pointer to the next node.\n\nTerminology:\n• Head: The first node of the list (Prev is NULL).\n• Tail: The last node of the list (Next is NULL).\n\nVisual Representation:\nNULL <- [ PREV | DATA | NEXT ] <-> [ PREV | DATA | NEXT ] -> NULL"
            1 -> "BASIC OPERATIONS IN DOUBLY LINKED LIST\n\n1. INSERTION - Adding a node\n   • At the Beginning: Update head and previous pointers.\n   • At the End: Update tail and next pointers.\n   • After/Before a Node: Adjust four pointers to insert in between.\n\n2. DELETION - Removing a node\n   • From the Beginning/End: Standard removal with pointer updates.\n   • From the Middle: Re-link the surrounding nodes directly to each other.\n\n3. TRAVERSAL - Bidirectional movement\n   • Forward: Using the Next pointers.\n   • Backward: Using the Prev pointers."
            2 -> "DOUBLY LINKED LIST ALGORITHMS\n\n1. INSERTION AT BEGINNING\n   1. Create a new node.\n   2. Set new.next = head.\n   3. Set new.prev = NULL.\n   4. If head is not NULL, set head.prev = new.\n   5. Update head = new.\n\n2. DELETION FROM MIDDLE\n   1. Locate node 'X' to be deleted.\n   2. Set X.prev.next = X.next.\n   3. Set X.next.prev = X.prev.\n   4. Free node X.\n\n3. FORWARD TRAVERSAL\n   1. Set temp = head.\n   2. While temp is not NULL:\n      - Process temp.data.\n      - temp = temp.next."
            3 -> "TIME COMPLEXITY OF DLL OPERATIONS\n\nPERFORMANCE ANALYSIS\n\nOperation              | Complexity\n-----------------------|-----------\nInsertion (Beginning)  | O(1)\nInsertion (End)        | O(1) (with tail pointer)\nDeletion (Beginning)   | O(1)\nDeletion (End)         | O(1) (with tail pointer)\nSearch                 | O(n)\nTraversal              | O(n)\n\nTIME COMPLEXITY EQUATIONS\n\n1. Traversal:\n   T(n) = c * n\n   Where n is the number of nodes. Complexity: O(n).\n\n2. Insertion at Ends:\n   T(n) = c\n   Since we have head/tail pointers, no traversal is needed. Complexity: O(1).\n\n3. Deletion at a Position:\n   T(n) = (c * n) + k\n   Requires traversing to the position (O(n)) and updating pointers (O(1))."
            4 -> "APPLICATIONS OF DOUBLY LINKED LIST\n\n1. Browser History\n   • Navigating forward and backward through recently visited pages.\n\n2. Music Players\n   • Moving to the previous or next track in a playlist.\n\n3. LRU Cache (Least Recently Used)\n   • Efficiently managing memory by tracking access order.\n\n4. Text Editors\n   • Implementing undo/redo or cursor movements.\n\n5. Image Viewers\n   • Sequential viewing of images in both directions."
            else -> "STEP-BY-STEP EXAMPLES\n\nINITIAL STATE: NULL\n\n1. Insert 10 at Beginning:\n   - [NULL | 10 | NULL]\n\n2. Insert 20 at End:\n   - [NULL | 10 | Next] <-> [Prev | 20 | NULL]\n\n3. Insert 15 between 10 and 20:\n   - [NULL | 10 | Next] <-> [Prev | 15 | Next] <-> [Prev | 20 | NULL]\n\nRESULT: 10 <-> 15 <-> 20"
        }

        "Circular Linked List" -> when (tab) {
            0 -> "DEFINITION:\n\nA Circular Linked List (CLL) is a variant of a linked list where the last node points back to the first node instead of pointing to NULL. This structure forms a continuous loop, allowing for repetitive traversal without resetting to the head.\n\nKey Features:\n• No node contains a NULL pointer.\n• Traversal can start from any node and eventually reach all other nodes.\n• Often implemented as Singly Circular or Doubly Circular.\n\nVisual Representation:\n[ DATA | NEXT ] -> [ DATA | NEXT ] -> [ DATA | NEXT ]\n      ^                                      |\n      ----------------------------------------"
            1 -> "BASIC OPERATIONS IN CIRCULAR LINKED LIST\n\n1. INSERTION - Adding a node to the cycle\n   • At the Beginning: Inserting a new node and updating the last node's pointer.\n   • At the End: Appending a node while maintaining the circular link.\n   • At a Position: Inserting between two existing nodes.\n\n2. DELETION - Removing a node\n   • From the Beginning: Removing the head and re-linking the tail.\n   • From the End: Removing the last node and updating the second-to-last node.\n   • From a Position: Removing a node and closing the gap.\n\n3. TRAVERSAL - Iterating through the loop\n   • Moving through nodes until the starting node is reached again.\n\n4. SEARCHING - Finding a value\n   • Scanning nodes within the loop for a specific element."
            2 -> "CIRCULAR LINKED LIST ALGORITHMS\n\n1. INSERTION AT BEGINNING\n   1. Create a new node.\n   2. If the list is empty, set new.next = new and head = new.\n   3. Else, traverse to the last node.\n   4. Set last.next = new.\n   5. Set new.next = head.\n   6. Update head = new.\n\n2. INSERTION AT END\n   1. Create a new node.\n   2. If the list is empty, set new.next = new and head = new.\n   3. Else, traverse to the last node.\n   4. Set last.next = new.\n   5. Set new.next = head.\n\n3. DELETION FROM BEGINNING\n   1. If list is empty, exit.\n   2. If only one node exists, set head = NULL.\n   3. Else, traverse to the last node.\n   4. Set last.next = head.next.\n   5. Update head = head.next.\n\n4. TRAVERSAL\n   1. Initialize a temporary pointer at head.\n   2. Do:\n      - Process node data.\n      - Move to next node.\n   3. While the pointer is not equal to head."
            3 -> "TIME COMPLEXITY OF CLL OPERATIONS\n\nPERFORMANCE ANALYSIS\n\nOperation              | Complexity (No Tail) | Complexity (With Tail)\n-----------------------|----------------------|----------------------\nInsertion (Beginning)  | O(n)                 | O(1)\nInsertion (End)        | O(n)                 | O(1)\nDeletion (Beginning)   | O(n)                 | O(1)\nDeletion (End)         | O(n)                 | O(n)\nTraversal              | O(n)                 | O(n)\nSearching              | O(n)                 | O(n)\n\nTIME COMPLEXITY EQUATIONS\n\n1. Traversal & Searching:\n   T(n) = c * n\n   Where c is the constant time per node.\n   Complexity: O(n)\n\n2. Insertion at Beginning (No Tail Pointer):\n   T(n) = (c * n) + k\n   Requires traversing to the last node to update its pointer.\n   Complexity: O(n)\n\n3. Insertion at End (With Tail Pointer):\n   T(n) = c\n   Direct access via tail pointer allows constant time updates.\n   Complexity: O(1)\n\n4. Deletion at End:\n   T(n) = c * n\n   Must traverse to the second-to-last node to update pointers.\n   Complexity: O(n)"
            4 -> "APPLICATIONS OF CIRCULAR LINKED LIST\n\n1. CPU Scheduling\n   • Round Robin scheduling uses circular lists to cycle through processes.\n\n2. Multiplayer Games\n   • Managing turn-based mechanics where play returns to the first player after the last.\n\n3. Circular Buffers\n   • Used in streaming and data buffering where old data is overwritten by new data.\n\n4. Multimedia Playlists\n   • Repeat mode in music or video players where the list restarts automatically.\n\n5. Real-Time Systems\n   • Continuous monitoring tasks that cycle through various sensors or states.\n\nKey Advantage:\n• Infinite traversal from any node without checking for NULL.\n\nKey Disadvantage:\n• More complex implementation; risk of infinite loops if not handled correctly."
            else -> "STEP-BY-STEP EXAMPLES\n\nINSERTION EXAMPLE\nInitial: 10 -> 20 -> (back to 10)\n\n1. Insert 30 at End:\n   - Step 1: Traverse to 20.\n   - Step 2: Set 20.next = 30.\n   - Step 3: Set 30.next = 10.\n   - Result: 10 -> 20 -> 30 -> (back to 10)\n\nDELETION EXAMPLE\nInitial: 10 -> 20 -> 30 -> (back to 10)\n\n1. Delete from Beginning (10):\n   - Step 1: Traverse to last node (30).\n   - Step 2: Set 30.next = 20.\n   - Step 3: Update head = 20.\n   - Result: 20 -> 30 -> (back to 20)"
        }
        "AVL Tree" -> when (tab) {
            0 -> "DEFINITION:\n\nAn AVL Tree is a self-balancing Binary Search Tree (BST) where the height difference between the left and right subtrees of any node (Balance Factor) is at most 1. If at any time they differ by more than one, rebalancing is performed to restore this property.\n\nBalance Factor Calculation:\nBalance Factor = Height(Left Subtree) - Height(Right Subtree)\n\nAllowed Values: -1, 0, +1"
            1 -> "BASIC OPERATIONS IN AVL TREE\n\n1. INSERTION - Adding a node\n   • Follows BST insertion rules, followed by rebalancing rotations if needed.\n\n2. DELETION - Removing a node\n   • Follows BST deletion rules, followed by rebalancing rotations to maintain height properties.\n\n3. SEARCHING - Finding a key\n   • Uses the standard BST search logic efficiently due to balanced height.\n\n4. ROTATIONS - Maintaining balance\n   • Left Rotation (RR Case)\n   • Right Rotation (LL Case)\n   • Left-Right Rotation (LR Case)\n   • Right-Left Rotation (RL Case)"
            2 -> "AVL TREE ALGORITHMS\n\n1. INSERTION PROCESS\n   1. Perform standard BST insertion.\n   2. Update the height of the current node.\n   3. Calculate the Balance Factor.\n   4. If unbalanced (Factor > 1 or < -1):\n      - Apply necessary rotations (LL, RR, LR, or RL) based on the insertion path.\n\n2. SEARCHING\n   1. Compare key with root.\n   2. If key < root, search left subtree.\n   3. If key > root, search right subtree.\n   4. Repeat until found or NULL is reached.\n\n3. ROTATION (Example: Right Rotation)\n   1. Let 'y' be the node to be rotated and 'x' be its left child.\n   2. Move x's right child to be y's left child.\n   3. Set y as x's right child.\n   4. Update heights of x and y."
            3 -> "TIME COMPLEXITY OF AVL TREE OPERATIONS\n\nPERFORMANCE ANALYSIS\n\nOperation | Complexity\n----------|-----------\nSearch    | O(log n)\nInsert    | O(log n)\nDelete    | O(log n)\nTraversal | O(n)\n\nTIME COMPLEXITY EQUATIONS\n\n1. Height of AVL Tree:\n   h ≈ log(n)\n   Due to strict balancing, the height remains logarithmic relative to the number of nodes.\n\n2. Searching:\n   T(n) = c * h\n   Since h is log(n), T(n) ≈ log(n).\n\n3. Insertion & Deletion:\n   T(n) = (Search Time) + (Rotation Time)\n   T(n) = O(log n) + O(1)\n   Final Complexity: O(log n)\n\n4. Space Complexity:\n   Total Space: O(n) for storing n nodes."
            4 -> "APPLICATIONS OF AVL TREE\n\n1. Database Indexing\n   • Used in databases where frequent lookups are required and data is relatively stable.\n\n2. Memory Management\n   • Efficiently managing free memory blocks where quick allocation and deallocation are needed.\n\n3. Search Engines\n   • Maintaining large dictionaries or datasets for fast keyword searching.\n\n4. Network Routing\n   • Managing IP routing tables to ensure fast packet forwarding.\n\nKey Characteristics:\n• Strictly Balanced: Provides better search performance than a regular BST.\n• Complex Updates: Insertion and deletion take longer than BST due to rotations, but search is faster."
            else -> "STEP-BY-STEP EXAMPLES\n\n1. LL ROTATION EXAMPLE\nInitial: 30 -> 20 (Left Child)\n- Insert 10 as left child of 20.\n- Balance Factor of 30 becomes 2.\n- Right Rotation at 30 makes 20 the root.\n- Result: 20 is root, 10 is left, 30 is right.\n\n2. RR ROTATION EXAMPLE\nInitial: 10 -> 20 (Right Child)\n- Insert 30 as right child of 20.\n- Balance Factor of 10 becomes -2.\n- Left Rotation at 10 makes 20 the root.\n- Result: 20 is root, 10 is left, 30 is right.\n\n3. LR ROTATION EXAMPLE\nInitial: 30 -> 10 (Left Child)\n- Insert 20 as right child of 10.\n- Left Rotation at 10, then Right Rotation at 30.\n- Result: 20 becomes the root."
        }
        "Binary Search Tree" -> when (tab) {
            0 -> "DEFINITION:\n\nA Binary Search Tree (BST) is a hierarchical data structure where each node has at most two children. It follows a specific ordering property:\n• The left subtree contains only nodes with values less than the parent node.\n• The right subtree contains only nodes with values greater than the parent node.\n• Each subtree must also be a binary search tree.\n\nStructure:\n[ LEFT_PTR | DATA | RIGHT_PTR ]"
            1 -> "BASIC OPERATIONS IN BST\n\n1. INSERTION - Adding a value\n   • Finding the correct leaf position based on value comparisons.\n\n2. SEARCHING - Locating a value\n   • Efficiently narrowing down the search path by half at each step.\n\n3. DELETION - Removing a value\n   • Handles three cases: leaf node, node with one child, and node with two children.\n\n4. TRAVERSAL - Visiting nodes\n   • Inorder (Left, Root, Right) -> Results in sorted order.\n   • Preorder (Root, Left, Right)\n   • Postorder (Left, Right, Root)"
            2 -> "BINARY SEARCH TREE ALGORITHMS\n\n1. SEARCHING\n   1. Start at the root.\n   2. If key == root.data, return success.\n   3. If key < root.data, search the left subtree.\n   4. If key > root.data, search the right subtree.\n   5. Repeat until found or leaf is reached.\n\n2. INSERTION\n   1. If tree is empty, new node becomes root.\n   2. Compare value with current node.\n   3. Move left if smaller, right if larger.\n   4. Repeat until a NULL position is found and insert.\n\n3. DELETION (Two Children Case)\n   1. Find the inorder successor (smallest value in right subtree).\n   2. Replace node's data with successor's data.\n   3. Delete the successor node."
            3 -> "TIME COMPLEXITY OF BST OPERATIONS\n\nPERFORMANCE ANALYSIS\n\nOperation | Average Case | Worst Case (Skewed)\n----------|--------------|--------------------\nSearch    | O(log n)     | O(n)\nInsert    | O(log n)     | O(n)\nDelete    | O(log n)     | O(n)\nTraversal | O(n)         | O(n)\n\nTIME COMPLEXITY EQUATIONS\n\n1. Average Case (Balanced Tree):\n   T(n) ≈ c * log(n)\n   The path from root to leaf is logarithmic.\n\n2. Worst Case (Skewed Tree):\n   T(n) = c * n\n   Occurs when nodes are inserted in sorted order, forming a line.\n\n3. Space Complexity:\n   O(n) for storing nodes."
            4 -> "APPLICATIONS OF BST\n\n1. Maintaining Sorted Data\n   • Efficiently keeping a list of elements in order while allowing fast updates.\n\n2. Symbol Tables\n   • Used in compilers to store and look up identifiers.\n\n3. File Systems\n   • Managing hierarchical directory structures for fast file access.\n\n4. Autocomplete Systems\n   • Suggesting words based on prefix matching in a tree structure.\n\nKey Rules:\n• No duplicate keys are allowed in a standard BST.\n• The efficiency depends heavily on the tree's height."
            else -> "STEP-BY-STEP EXAMPLES\n\nINSERTION EXAMPLE\nInsert: 50, 30, 70, 20\n\n1. Root = 50\n2. 30 < 50 -> 30 becomes left child of 50.\n3. 70 > 50 -> 70 becomes right child of 50.\n4. 20 < 50, 20 < 30 -> 20 becomes left child of 30.\n\nResulting Tree:\n      50\n     /  \\\n    30   70\n   /"
        }
        "Heap" -> when (tab) {
            0 -> "DEFINITION:\n\nA Heap is a specialized complete binary tree that satisfies the Heap Property. It is primarily used to implement priority queues and for efficient access to the minimum or maximum element.\n\nTypes of Heaps:\n1. Min-Heap: The value of the root is less than or equal to its children. Every subtree follows the same rule.\n2. Max-Heap: The value of the root is greater than or equal to its children."
            1 -> "BASIC OPERATIONS IN HEAP\n\n1. INSERTION - Adding an element\n   • Add at the end of the tree and 'Heapify Up' to maintain the property.\n\n2. EXTRACTION (Delete Root) - Removing min/max\n   • Replace root with the last element and 'Heapify Down' to restore order.\n\n3. PEEK - Accessing top element\n   • Returning the root value without removal (O(1)).\n\n4. HEAPIFY - Structural adjustment\n   • Reordering nodes to satisfy the heap property after a modification."
            2 -> "HEAP ALGORITHMS\n\n1. INSERTION (Heapify Up)\n   1. Insert the new element at the first available leaf position.\n   2. Compare the element with its parent.\n   3. If property is violated (e.g., child < parent in Min-Heap), swap them.\n   4. Repeat until the root or property is satisfied.\n\n2. EXTRACTION (Heapify Down)\n   1. Copy the value from the last leaf to the root.\n   2. Remove the last leaf.\n   3. Compare the new root with its children.\n   4. Swap with the smallest child (Min-Heap) or largest (Max-Heap).\n   5. Repeat down the tree."
            3 -> "TIME COMPLEXITY OF HEAP OPERATIONS\n\nPERFORMANCE ANALYSIS\n\nOperation        | Complexity\n-----------------|-----------\nInsert           | O(log n)\nExtract Min/Max  | O(log n)\nPeek             | O(1)\nBuild Heap       | O(n)\n\nTIME COMPLEXITY EQUATIONS\n\n1. Insertion & Deletion:\n   T(n) = c * h\n   Since a heap is a complete binary tree, height h ≈ log(n).\n   Complexity: O(log n)\n\n2. Build Heap:\n   T(n) ≈ n\n   Converting an unordered array into a heap takes linear time using bottom-up heapification.\n\n3. Space Complexity:\n   O(n) for storing elements in an array."
            4 -> "APPLICATIONS OF HEAP\n\n1. Priority Queues\n   • Managing tasks based on urgency rather than arrival time.\n\n2. Heap Sort\n   • An efficient comparison-based sorting algorithm using the heap structure.\n\n3. Graph Algorithms\n   • Used in Dijkstra's shortest path and Prim's minimum spanning tree algorithms.\n\n4. Operating Systems\n   • Scheduling processes where certain tasks have higher priority.\n\nKey Note:\n• Heaps are typically implemented using arrays for memory efficiency, as the tree is always complete."
            else -> "STEP-BY-STEP EXAMPLES\n\nMIN-HEAP INSERTION\nHeap: [10, 30, 20]\n\n1. Insert 5:\n   - 5 is added at the end: [10, 30, 20, 5]\n   - Compare 5 with parent 30: 5 < 30, so swap.\n   - Heap: [10, 5, 20, 30]\n   - Compare 5 with root 10: 5 < 10, so swap.\n   - Final Heap: [5, 10, 20, 30]\n\nEXTRACTION\n1. Remove root 5, replace with last element 30.\n2. Heapify down: 30 > 10, so swap.\n3. Result: [10, 30, 20]"
        }
        "B-Tree" -> when (tab) {
            0 -> "DEFINITION:\n\nA B-Tree is a self-balancing search tree designed to work well on magnetic disks or other direct-access secondary storage devices. It is a multi-way tree where each node can contain multiple keys and have multiple children.\n\nKey Rules (Order m):\n• Every node has at most m children.\n• Internal nodes (except root) have at least ⌈m/2⌉ children.\n• All leaf nodes appear at the same level."
            1 -> "BASIC OPERATIONS IN B-TREE\n\n1. SEARCHING - Locating a key\n   • Multidirectional search within nodes and down the tree.\n\n2. INSERTION - Adding a key\n   • Keys are added to leaf nodes; if a node exceeds capacity, it splits.\n\n3. DELETION - Removing a key\n   • Complex process involving merging or borrowing from siblings to maintain balance properties.\n\n4. TRAVERSAL\n   • Similar to inorder traversal, visiting keys and children in sorted sequence."
            2 -> "B-TREE ALGORITHMS\n\n1. SEARCHING\n   1. Start at the root.\n   2. Linear or binary search within the current node's keys.\n   3. If found, return. Else, move to the child pointer between the surrounding keys.\n   4. Repeat until found or a leaf is reached.\n\n2. INSERTION\n   1. Find the appropriate leaf node.\n   2. Insert key in sorted order.\n   3. If node is full (keys == m-1):\n      - Split node into two.\n      - Move the median key up to the parent node.\n      - Repeat upward if parent becomes full."
            3 -> "TIME COMPLEXITY OF B-TREE OPERATIONS\n\nPERFORMANCE ANALYSIS\n\nOperation | Complexity\n----------|-----------\nSearch    | O(log n)\nInsert    | O(log n)\nDelete    | O(log n)\nTraversal | O(n)\n\nTIME COMPLEXITY EQUATIONS\n\n1. Height of B-Tree:\n   h ≈ logₘ(n)\n   The large branching factor (m) keeps the tree very shallow.\n\n2. Search Time:\n   T(n) = c * m * logₘ(n)\n   Where we check up to m keys at each level. Since m is a constant, this simplifies to O(log n).\n\n3. Space Complexity:\n   O(n) for storing keys and pointers."
            4 -> "APPLICATIONS OF B-TREE\n\n1. Database Systems\n   • Providing efficient indexing for large datasets stored on disk.\n\n2. File Systems\n   • Used by OS (like NTFS or HFS+) to manage directory structures and file metadata.\n\n3. Search Engines\n   • Managing massive indices where fast retrieval is critical.\n\nKey Advantages:\n• Minimized Disk Reads: The low height means fewer disk accesses are required to find data.\n• Balanced Performance: Guaranteed logarithmic time for all basic operations."
            else -> "STEP-BY-STEP EXAMPLES\n\nINSERTION (Order 3)\nMax keys = 2\n\n1. Insert 10, 20: Node = [10, 20]\n2. Insert 30:\n   - Node [10, 20, 30] is full.\n   - Split: 20 moves up to new root.\n   - Children: [10] and [30].\n   - Result:\n        [20]\n       /    \\\n    [10]    [30]"
        }
        "B+ Tree" -> when (tab) {
            0 -> "DEFINITION:\n\nA B+ Tree is an optimization of the B-Tree where internal nodes only store keys (acting as indices) and all actual data is stored in the leaf nodes. Additionally, all leaf nodes are linked together in a linked list structure.\n\nKey Differences from B-Tree:\n• Internal nodes do not store data pointers.\n• Leaves are connected, enabling efficient range queries.\n• Redundant keys may exist in internal nodes as guides."
            1 -> "BASIC OPERATIONS IN B+ TREE\n\n1. SEARCHING - Point query\n   • Always proceeds to the leaf level to find the actual data.\n\n2. INSERTION - Adding data\n   • Always performed at the leaf level, with potential splits propagating upward.\n\n3. RANGE QUERY - Sequential access\n   • Find the starting key in a leaf and follow the linked list for subsequent elements.\n\n4. DELETION\n   • Removing from a leaf and adjusting internal indices if necessary."
            2 -> "B+ TREE ALGORITHMS\n\n1. SEARCHING\n   1. Start at root.\n   2. Navigate internal nodes using keys as guides.\n   3. Reach the leaf node.\n   4. Perform a search within the leaf for the specific data.\n\n2. RANGE QUERY\n   1. Search for the lower bound key to find the starting leaf node.\n   2. Traverse the 'next' pointers of the leaf nodes.\n   3. Collect all data until the upper bound key is exceeded.\n\n3. INSERTION\n   1. Find the correct leaf.\n   2. Insert key-data pair.\n   3. If leaf overflows, split and copy the middle key up to the parent."
            3 -> "TIME COMPLEXITY OF B+ TREE OPERATIONS\n\nPERFORMANCE ANALYSIS\n\nOperation   | Complexity\n------------|-----------\nSearch      | O(log n)\nInsert      | O(log n)\nDelete      | O(log n)\nRange Query | O(log n + k)\n\n(k = number of elements in range)\n\nTIME COMPLEXITY EQUATIONS\n\n1. Search Performance:\n   T(n) ≈ logₘ(n)\n   The tree is very wide and shallow, minimizing the path to data.\n\n2. Range Query:\n   T(n) = Search(start) + k\n   Since leaves are linked, we only search once and then move linearly.\n\n3. Space Complexity:\n   O(n) with slightly more overhead than B-Tree due to linked leaves."
            4 -> "APPLICATIONS OF B+ TREE\n\n1. Relational Databases\n   • Primary indexing structure for systems like MySQL (InnoDB) and PostgreSQL.\n\n2. Large Scale Storage\n   • Managing block storage in enterprise-level file systems.\n\n3. Metadata Management\n   • Efficiently handling ranges of file versions or system logs.\n\nKey Advantages:\n• Optimized for Disk: Internal nodes are smaller (no data), so more keys fit in a single disk block.\n• Superior Range Scans: The linked leaf level makes finding a range of values much faster than in a standard B-Tree."
            else -> "STEP-BY-STEP EXAMPLES\n\nSTRUCTURE EXAMPLE\nKeys: 10, 20, 30, 40\nOrder: 3\n\nInternal Node: [30]\n             /    \\\nLeaves: [10, 20] -> [30, 40]\n\nNote: The key '30' appears in both the internal node (as a guide) and the leaf node (as data)."
        }
        else -> "Content coming soon..."
    }
}

