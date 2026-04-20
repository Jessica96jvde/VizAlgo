package com.example.vizalgo.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R

class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dsName = intent.getStringExtra("DS_NAME") ?: "Stack"
        setContent {
            QuizScreen(dsName) {
                finish()
            }
        }
    }
}

@Composable
fun QuizScreen(dsName: String, onFinish: () -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val poppinsFont = FontFamily(Font(R.font.poppins_light))
    
    val questions = remember { getQuestionsForDS(dsName) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    
    // Progress state
    var progress by remember { mutableFloatStateOf(0f) }
    
    // Star state for both Stack and Queue (Advanced Logic)
    var starLevel by remember { mutableIntStateOf(0) } // 0: Gold, 1: Yellow, 2: Red
    val isAdvancedQuiz = dsName == "Stack" || dsName == "Queue"
    
    val currentQuestion = questions.getOrNull(currentQuestionIndex) ?: questions.first()
    
    // Difficulty logic for Advanced Quizzes
    val difficulty = when {
        !isAdvancedQuiz -> ""
        currentQuestionIndex < 5 -> "EASY"
        currentQuestionIndex < 10 -> "MEDIUM"
        else -> "HARD"
    }
    
    val quizBackground = if (isAdvancedQuiz) {
        when (difficulty) {
            "EASY" -> R.drawable.easyquiz
            "MEDIUM" -> R.drawable.mediumquiz
            "HARD" -> R.drawable.hardquiz
            else -> R.drawable.gamebg
        }
    } else {
        R.drawable.gamebg
    }

    Box(modifier = Modifier.fillMaxSize()) {
        QuizBackground(quizBackground)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showResult) {
                ResultCard(score, questions.size, cantoraFont, starLevel, isAdvancedQuiz, onFinish)
            } else {
                if (isAdvancedQuiz) {
                    Text(
                        text = difficulty,
                        fontFamily = cantoraFont,
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    val starColor = when(starLevel) {
                        0 -> Color(0xFFFFD700) // Gold
                        1 -> Color(0xFFFFFF00) // Yellow
                        else -> Color(0xFFFF0000) // Red
                    }
                    
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star Level",
                        tint = starColor,
                        modifier = Modifier.size(48.dp).padding(vertical = 8.dp)
                    )
                }

                QuizProgressBar(progress)
                
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${currentQuestionIndex + 1}/${questions.size}",
                    fontFamily = poppinsFont,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                var selectedOptionIndex by remember(currentQuestionIndex) { mutableIntStateOf(-1) }
                var answered by remember(currentQuestionIndex) { mutableStateOf(false) }

                QuestionCard(
                    question = currentQuestion,
                    isAdvancedQuiz = isAdvancedQuiz,
                    answered = answered,
                    selectedOptionIndex = selectedOptionIndex,
                    onOptionSelected = { index ->
                        if (!answered) {
                            selectedOptionIndex = index
                            answered = true
                            val isCorrect = index == currentQuestion.correctIndex
                            
                            if (isCorrect) {
                                score++
                                progress = (progress + (1f / questions.size)).coerceIn(0f, 1f)
                            } else {
                                progress = (progress - (1f / questions.size)).coerceIn(0f, 1f)
                                if (isAdvancedQuiz && starLevel < 2) {
                                    starLevel++
                                }
                            }
                        }
                    }
                )

                if (answered) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                            } else {
                                showResult = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green4))
                    ) {
                        Text("Next", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun QuizBackground(resId: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))
    }
}

@Composable
fun QuizProgressBar(progress: Float) {
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progressAnimation")
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .weight(1f)
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = colorResource(id = R.color.green4),
            trackColor = Color.White.copy(alpha = 0.2f)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = colorResource(id = R.color.green4),
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun QuestionCard(
    question: QuizQuestion,
    isAdvancedQuiz: Boolean,
    answered: Boolean,
    selectedOptionIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = question.text,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            question.options.forEachIndexed { index, option ->
                val isSelected = selectedOptionIndex == index
                val isCorrect = index == question.correctIndex
                
                val buttonColor by animateColorAsState(
                    targetValue = when {
                        !answered -> Color.White.copy(alpha = 0.2f)
                        isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.8f) // Green for correct
                        isSelected -> Color(0xFFF44336).copy(alpha = 0.8f) // Red for wrong selection
                        else -> Color.White.copy(alpha = 0.1f)
                    },
                    label = "buttonColorAnimation"
                )

                Button(
                    onClick = { onOptionSelected(index) },
                    enabled = !answered,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .heightIn(min = 60.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        disabledContainerColor = buttonColor
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = option,
                        color = Color.White,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            if (isAdvancedQuiz && answered && question.explanation.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "💡 Explanation:\n${question.explanation}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ResultCard(score: Int, total: Int, font: FontFamily, starLevel: Int, isAdvancedQuiz: Boolean, onFinish: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Quiz Completed!", fontFamily = font, fontSize = 32.sp, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            
            if (isAdvancedQuiz) {
                val starColor = when(starLevel) {
                    0 -> Color(0xFFFFD700)
                    1 -> Color(0xFFFFFF00)
                    else -> Color(0xFFFF0000)
                }
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = starColor,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            Text(text = "Your Score", color = Color.White.copy(alpha = 0.7f), fontSize = 20.sp)
            Text(text = "$score / $total", fontSize = 64.sp, fontWeight = FontWeight.Black, color = colorResource(id = R.color.green4))
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green4))
            ) {
                Text("Back to Home", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

data class QuizQuestion(
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String = ""
)

fun getQuestionsForDS(dsName: String): List<QuizQuestion> {
    return when (dsName) {
        "Stack" -> listOf(
            // EASY (1-5)
            QuizQuestion("Which principle does a Stack follow?", listOf("A) FIFO (First In First Out)", "B) LIFO (Last In First Out)", "C) LILO (Last In Last Out)", "D) Random Access"), 1, "A stack is a LIFO data structure, meaning the most recently added element is the first one to be removed."),
            QuizQuestion("What occurs when you try to remove an element from an empty stack?", listOf("A) Overflow", "B) Underflow", "C) Garbage Collection", "D) Crash"), 1, "Underflow occurs when a pop operation is performed on an empty stack."),
            QuizQuestion("What is the time complexity for Push and Pop operations in a stack?", listOf("A) O(n)", "B) O(log n)", "C) O(1)", "D) O(n^2)"), 2, "Both Push and Pop operations have a constant time complexity of O(1)."),
            QuizQuestion("Stack overflow occurs when:", listOf("A) Stack is empty", "B) Stack is full and push is attempted", "C) Pop is performed", "D) Peek is used"), 1, "Overflow happens when no more space is available in the stack."),
            QuizQuestion("Stacks are used in:", listOf("A) Sorting arrays", "B) Expression evaluation", "C) Searching elements", "D) Graph traversal only"), 1, "Stacks are widely used in: Infix to postfix conversion, Expression evaluation"),
            
            // MEDIUM (6-10)
            QuizQuestion("The minimum number of stacks needed to implement a queue is", listOf("A-3", "B-1", "C-2", "D-4"), 2, "Two stacks S1 & S2 are required to implement a queue. This method makes sure that newly entered element is always at the top of stack 1, so that DeQueue operation just pops from stack1. To put the element at top of stack1, stack2 is used. Hence Option (C) is the correct answer."),
            QuizQuestion("Consider the following statements:\ni. First-in-first out types of computations are efficiently supported by STACKS.\nii. Implementing LISTS on linked lists is more efficient than implementing LISTS on an array for almost all the basic LIST operations.\niii. Implementing QUEUES on a circular array is more efficient than implementing QUEUES on a linear array with two indices.\niv. Last-in-first-out type of computations are efficiently supported by QUEUES.\nWhich of the following is correct?", listOf("• A-(ii) is true", "• B-(i) and (ii) are true", "• C-(iii) is true", "• D-(ii) and (iv) are true"), 2, "iii. Implementing QUEUES on a circular array is more efficient than implementing QUEUES on a linear array with two indices. Explanation: i. STACKS are used to implement Last-in-First-Out (LIFO) operations... ii. Implementing LISTS... iii. Implementing QUEUES... iv. QUEUES... Hence Option (C) is the correct answer."),
            QuizQuestion("If the sequence of operations - push (1), push (2), pop, push (1), push (2), pop, pop, pop, push (2), pop are performed on a stack, the sequence of popped out values", listOf("• A-2,2,1,1,2", "• B-2,2,1,2,2", "• C-2,1,2,2,1", "• D-2,1,2,2,2"), 0, "Let's simulate the sequence of operations on a stack: Push(1): Stack = [1], Push(2): Stack = [1, 2], Pop: Pop 2, Stack = [1], Push(1): Stack = [1, 1], Push(2): Stack = [1, 1, 2], Pop: Pop 2, Stack = [1, 1], Pop: Pop 1, Stack = [1], Pop: Pop 1, Stack = [], Push(2): Stack = [2], Pop: Pop 2, Stack = []. The sequence of popped out values is: 2, 2, 1, 1, 2."),
            QuizQuestion("Consider the following operations performed on a stack of size 5 : Push (a); Pop() ; Push(b); Push(c); Pop(); Push(d); Pop();Pop(); Push (e) Which of the following statements is correct?", listOf("• A-Underflow occurs", "• B-Stack operations are performed smoothly", "• C-Overflow occurs", "• D-None of the above"), 1, "All Stack operations are performed smoothly. So, option (B) is correct."),
            QuizQuestion("Assume that the operators +, -, × are left associative and ^ is right associative. The order of precedence (from highest to lowest) is ^, x , +, -. The postfix expression corresponding to the infix expression a + b × c - d ^ e ^ f is", listOf("• A-abc × + def ^ ^ -", "• B-abc × + de ^ f ^ -", "• C-ab + c × d - e ^ f ^", "• D - - + a × bc ^ ^ def"), 0, "Start scanning from left to right... Given the infix expression a + b × c - d ^ e ^ f, the corresponding postfix expression is: a b c x + d e f ^ ^ -"),

            // HARD (11-15)
            QuizQuestion("Items A, B, C, D, E pushed in stack. Pop 4, insert in queue. Delete 2 from queue, push back on stack. Pop one. Popped item is", listOf("• A-A", "• B-B", "• C-C", "• D-D"), 0, "Order of stack: A, B, C, D, E. Popped 4 (E,D,C,B) into queue. Queue: E,D,C,B (E front). Delete 2 (E,D) push back: Stack has A, E, D. Popped item is D."),
            QuizQuestion("Stack A entries a, b, c (a on top). Pop A printed or pushed to B. Pop B printed. Which permutation of a, b, c not possible?", listOf("A-b a c", "B-b c a", "C-c a b", "D-a b c"), 2, "In option (C), printing 'a' will not be possible after printing 'c'."),
            QuizQuestion("The five items: A, B, C, D, and E are pushed in a stack, one after other starting from A. The stack is popped four items and each element is inserted in a queue. The two elements are deleted from the queue and pushed back on the stack. Now one item is popped from the stack. The popped item is", listOf("• A-A", "• B-B", "• C-C", "• D-D"), 3, "When five items: A, B, C, D, and E are pushed in a stack: Order of stack becomes: A, B, C, D, and E. stack is popped four items (E,D,C,B) into a queue. Order of queue: E, D, C, B (E at front). Two elements deleted from queue (E, D) and pushed back on stack: New order = A, E, D. As D is on top, D will be popped out. So, correct option is (D)."),
            QuizQuestion("The following postfix expression with single digit operands is evaluated using a stack: 8 2 3 ^ / 2 3 * + 5 1 * - Note that ^ is the exponentiation operator. The top two elements of the stack after the first * is evaluated are:", listOf("• A-6, 1", "• B-5, 7", "• C-3, 2", "• D-1, 5"), 0, "Push 8, 2, 3. ^ gives 8, 8. / gives 1. Push 2, 3. * gives 1, 6. Top two elements are 6 and 1."),
            QuizQuestion("The result evaluating the postfix expression 10 5 + 60 6 / * 8 – is", listOf("• A-284", "• B-213", "• C-142", "• D-71"), 2, "Expression: 10 5 + 60 6 / * 8 -. Evaluate: (10+5) * (60/6) - 8 = 15 * 10 - 8 = 150 - 8 = 142. Step 3: The final result in the stack is: 142")
        )
        "Queue" -> listOf(
            // EASY (1-5)
            QuizQuestion("Which principle does a Queue follow?", listOf("A) LIFO", "B) FIFO", "C) LILO", "D) FILO"), 1, "Queue follows First In First Out, meaning the first inserted element is removed first."),
            QuizQuestion("What condition occurs when queue is empty and dequeue is attempted?", listOf("A) Overflow", "B) Underflow", "C) Full condition", "D) Null state"), 1, "Removing from an empty queue leads to underflow."),
            QuizQuestion("In a queue, insertion takes place at:", listOf("A) Front", "B) Middle", "C) Rear", "D) Anywhere"), 2, "Elements are always inserted at the rear end."),
            QuizQuestion("In a queue, deletion takes place at:", listOf("A) Rear", "B) Front", "C) Middle", "D) Random"), 1, "Elements are removed from the front end."),
            QuizQuestion("Which of the following is the type of priority Queue?", listOf("A-Ascending Order Priority Queue", "B-Descending order Priority Queue", "C-Deque", "D-Both A and B."), 3, "Types of Priority Queue: 1) Ascending Order Priority Queue: the element with a lower priority value is given a higher priority. 2) Descending order Priority Queue: remove the element with the highest priority first."),
            
            // MEDIUM (6-10)
            QuizQuestion("The minimum number of stacks needed to implement a queue is", listOf("A-3", "B-1", "C-2", "D-4"), 2, "Two stacks S1 & S2 are required to implement a queue. This method makes sure that newly entered element is always at the top of stack 1, so that DeQueue operation just pops from stack1. To put the element at top of stack1, stack2 is used. Hence Option (C) is the correct answer."),
            QuizQuestion("Which of the following is/are advantages of circular Queue?", listOf("A-Memory Management", "B-Traffic system", "C-CPU Scheduling", "D-All of the above"), 3, "Applications of Circular Queue: Memory Management: unused locations utilized. Traffic system: used to switch on traffic lights one by one. CPU Scheduling: OS maintains a queue of processes ready to execute. Hence option(D) is correct."),
            QuizQuestion("Which data structure is commonly used to implement the event-driven simulation of complex systems, such as in computer network simulations or traffic simulations?", listOf("A-Stack", "B-Tree", "C-Array", "D-Queue"), 3, "The data structure commonly used to implement the event-driven simulation of complex systems is a queue. In event-driven simulations, events occur at specific times, and these events need to be processed in the order of their occurrence. A queue follows FIFO principle."),
            QuizQuestion("Which one of the following is an application of Queue Data Structure?", listOf("A-When a resource is shared among multiple consumers.", "B-When data is transferred asynchronously between two processes", "C-Load Balancing", "D-All of the above"), 3, "(A) Resource sharing ensures fair access. (B) Asynchronous transfer allows decoupling Production from Consumption. (C) Load Balancing manages incoming requests evenly across resources. Hence (D) is correct."),
            QuizQuestion("Which of the following is true about linked list implementation of queue?", listOf("A-In push operation, if new nodes are inserted at the beginning of linked list, then in pop operation, nodes must be removed from end.", "B-In push operation, if new nodes are inserted at the end, then in pop operation, nodes must be removed from the beginning.", "C-Both of the above", "D-None of the above"), 2, "To keep the First In First Out order, a queue can be implemented using a linked list in any of the given two ways. Hence option (C) is the correct answer."),

            // HARD (11-15)
            QuizQuestion("Consider the following statements:\ni. First-in-first out types of computations are efficiently supported by STACKS.\nii. Implementing LISTS on linked lists is more efficient than implementing LISTS on an array for almost all the basic LIST operations.\niii. Implementing QUEUES on a circular array is more efficient than implementing QUEUES on a linear array with two indices.\niv. Last-in-first-out type of computations are efficiently supported by QUEUES.", listOf("A-(ii) is true", "B-(i) and (ii) are true", "C-(iii) is true", "D-(ii) and (iv) are true"), 2, "iii. Implementing QUEUES on a circular array is more efficient than implementing QUEUES on a linear array with two indices because with a circular array, the front and rear indices wrap around when they reach the end of the array. Hence Option (C) is the correct answer."),
            QuizQuestion("Which of the following option is not correct?", listOf("A-If the queue is implemented with a linked list, keeping track of a front pointer, Only rear pointer s will change during an insertion into an non-empty queue.", "B-Queue data structure can be used to implement least recently used (LRU) page fault algorithm and Quick short algorithm.", "C-Queue data structure can be used to implement Quick short algorithm but not least recently used (LRU) page fault algorithm.", "D-Both (A) and (C)"), 2, "If the queue is implemented with a linked list, keeping track of a front pointer, Only rear pointer s will change during an insertion into an non-empty queue. Queue data structure can be used to implement least recently used (LRU) page fault algorithm and Quick short algorithm. Only option (C) is not correct."),
            QuizQuestion("Consider a standard Circular Queue 'q' implementation whose size is 11 and elements q[0] to q[10]. front and rear initialized to point at q[2]. In which position will the ninth element be added?", listOf("A-q[0]", "B-q[1]", "C-q[9]", "D-q[10]"), 0, "Circular queue whose total size is 11, front and rear pointers are initialized to point at q[2]. Therefore, 9th element will be added at pointer q[0]. So, option (A) is correct."),
            QuizQuestion("Consider the pseudo-code for function fun. What does the function fun do?", listOf("A-Prints numbers from 0 to n-1", "B-Prints numbers from n-1 to 0", "C-Prints first n Fibonacci numbers", "D-Prints first n Fibonacci numbers in reverse order."), 2, "The function prints first n Fibonacci Numbers. Note that 0 and 1 are initially there in q. In every iteration of the loop sum of the two queue items is enqueued and the front item is dequeued. Hence option (C) is the correct answer."),
            QuizQuestion("Which of the following operations on a queue data structure has a time complexity of O(1)?\nA) Enqueue B) Dequeue C) Peek D) Clear", listOf("A-A and B", "B-B only", "C-C only", "D-A and D"), 1, "In a queue data structure, dequeueing (removing an element from the front of the queue) typically has a time complexity of O(1) because it involves removing the first element and adjusting the front pointer. Hence, Option B is correct.")
        )
        "Singly Linked List" -> listOf(
            QuizQuestion("How many pointers does each node have in a Singly Linked List?", listOf("0", "1", "2", "3"), 1),
            QuizQuestion("What does the last node's 'next' pointer point to?", listOf("Head", "Previous", "null", "Tail"), 2),
            QuizQuestion("Time complexity of inserting at the beginning?", listOf("O(1)", "O(n)", "O(log n)", "O(n^2)"), 0)
        )
        "Doubly Linked List" -> listOf(
            QuizQuestion("What pointers does a Doubly Linked List node have?", listOf("Next only", "Prev only", "Next and Prev", "None"), 2),
            QuizQuestion("Advantage of DLL over SLL?", listOf("Uses less memory", "Bidirectional traversal", "Simpler implementation", "Faster access"), 1),
            QuizQuestion("Time complexity to delete a node if pointer is given?", listOf("O(n)", "O(log n)", "O(1)", "O(n^2)"), 2)
        )
        "Circular Linked List" -> listOf(
            QuizQuestion("What does the last node in a Circular LL point to?", listOf("null", "Tail", "Head", "Middle"), 2),
            QuizQuestion("A primary use case for Circular LL is:", listOf("Undo/Redo", "Round-Robin Scheduling", "Browser History", "Call Stack"), 1),
            QuizQuestion("Which condition identifies the end of a Circular LL (pointer p)?", listOf("p.next == null", "p.next == head", "p == null", "p == tail"), 1)
        )
        "Binary Search Tree" -> listOf(
            QuizQuestion("In a BST, values smaller than the root go to the...", listOf("Right", "Left", "Middle", "Bottom"), 1),
            QuizQuestion("What is the time complexity for search in a balanced BST?", listOf("O(1)", "O(n)", "O(log n)", "O(n^2)"), 2),
            QuizQuestion("In-order traversal of a BST gives values in:", listOf("Decreasing order", "Random order", "Increasing order", "Level order"), 2)
        )
        "AVL Tree" -> listOf(
            QuizQuestion("An AVL Tree is a self-balancing...", listOf("Heap", "Queue", "Binary Search Tree", "Linked List"), 2),
            QuizQuestion("What is the maximum allowed balance factor in AVL?", listOf("0", "1", "2", "log n"), 1),
            QuizQuestion("If balance factor becomes 2, what is needed?", listOf("Deletion", "Insertion", "Rotation", "Nothing"), 2)
        )
        "Heap" -> listOf(
            QuizQuestion("In a Min-Heap, the root is always the:", listOf("Largest", "Smallest", "Middle", "Median"), 1),
            QuizQuestion("Complexity of extracting min from a heap (n elements)?", listOf("O(1)", "O(log n)", "O(n)", "O(n log n)"), 1),
            QuizQuestion("Heaps are usually implemented using:", listOf("Linked Lists", "Arrays", "Hash Tables", "Stacks"), 1)
        )
        "B-Tree" -> listOf(
            QuizQuestion("B-Trees are optimized for:", listOf("In-memory storage", "Disk-based storage", "Real-time graphics", "CPU Caches"), 1),
            QuizQuestion("In a B-Tree of order m, max children per node?", listOf("m", "m-1", "2m", "m/2"), 0),
            QuizQuestion("All leaf nodes in a B-Tree must be at:", listOf("Level 0", "The same level", "Different levels", "Level 1"), 1)
        )
        "B+ Tree" -> listOf(
            QuizQuestion("In a B+ Tree, data records are stored in:", listOf("Root node", "Internal nodes", "Leaf nodes", "All nodes"), 2),
            QuizQuestion("What links leaf nodes in a B+ Tree?", listOf("Next pointers", "Parent pointers", "Child pointers", "Nothing"), 0),
            QuizQuestion("Benefit of B+ Tree leaf linking?", listOf("Faster search", "Range queries", "Less memory", "Faster insertion"), 1)
        )
        else -> listOf(
            QuizQuestion("What is a Data Structure?", listOf("A way to store data", "A programming language", "A type of computer", "An OS"), 0)
        )
    }
}
