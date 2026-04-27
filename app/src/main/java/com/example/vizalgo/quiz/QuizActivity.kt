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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
    
    // Star state for Advanced Logic
    var starLevel by remember { mutableIntStateOf(0) } // 0: Gold, 1: Yellow, 2: Red
    val isAdvancedQuiz = dsName == "Stack" || dsName == "Queue" || dsName == "Singly Linked List" || dsName == "Doubly Linked List" || dsName == "Binary Search Tree" || dsName == "AVL Tree" || dsName == "Heap" || dsName == "B-Tree" || dsName == "B+ Tree"
    
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

                // Horizontal quiz progress bar with milestone stars
                QuizProgressBar(score, questions.size)
                
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
                            } else {
                                // Decrease score and potentially revert stars
                                score = (score - 1).coerceAtLeast(0)
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
fun QuizProgressBar(score: Int, total: Int) {
    val progress = if (total > 0) score.toFloat() / total else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progressAnimation")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.2f))
        )
        
        // Progress Fill
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorResource(id = R.color.green4))
        )
        
        // Milestone Stars
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val barWidth = maxWidth
            
            // Star 1 at 10 correct
            if (total >= 10) {
                MilestoneStar(
                    milestoneScore = 10,
                    totalScore = total,
                    currentScore = score,
                    color = Color.Red,
                    barWidth = barWidth
                )
            }
            
            // Star 2 at 13 correct
            if (total >= 13) {
                MilestoneStar(
                    milestoneScore = 13,
                    totalScore = total,
                    currentScore = score,
                    color = Color.Yellow,
                    barWidth = barWidth
                )
            }
            
            // Star 3 at full score
            MilestoneStar(
                milestoneScore = total,
                totalScore = total,
                currentScore = score,
                color = Color(0xFFFFD700), // Gold
                barWidth = barWidth
            )
        }
    }
}

@Composable
fun MilestoneStar(
    milestoneScore: Int,
    totalScore: Int,
    currentScore: Int,
    color: Color,
    barWidth: Dp
) {
    val fraction = if (totalScore > 0) milestoneScore.toFloat() / totalScore else 0f
    val isReached = currentScore >= milestoneScore
    
    Box(
        modifier = Modifier
            .offset(x = barWidth * fraction - 14.dp)
            .offset(y = (-6).dp)
    ) {
        Icon(
            imageVector = if (isReached) Icons.Default.Star else Icons.Outlined.Star,
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .size(28.dp)
                .shadow(if (isReached) 4.dp else 0.dp, shape = CircleShape)
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
            QuizQuestion("Consider the following statements:\ni. First-in-first out types of computations are efficiently supported by STACKS.\nii. Implementing LISTS on linked lists is more efficient than implementing LISTS on an array for almost all the basic LIST operations.\niii. Implementing QUEUES on a circular array is more efficient than implementing QUEUES on a linear array with two indices.\niv. Last-in-first-out type of computations are efficiently supported by QUEUES.\nWhich of the following is correct?", listOf("A-(ii) is true", "B-(i) and (ii) are true", "C-(iii) is true", "D-(ii) and (iv) are true"), 2, "iii. Implementing QUEUES on a circular array is more efficient than implementing QUEUES on a linear array with two indices because with a circular array, the front and rear indices wrap around when they reach the end of the array. Hence Option (C) is the correct answer."),
            QuizQuestion("Which of the following option is not correct?", listOf("A-If the queue is implemented with a linked list, keeping track of a front pointer, Only rear pointer s will change during an insertion into an non-empty queue.", "B-Queue data structure can be used to implement least recently used (LRU) page fault algorithm and Quick short algorithm.", "C-Queue data structure can be used to implement Quick short algorithm but not least recently used (LRU) page fault algorithm.", "D-Both (A) and (C)"), 2, "If the queue is implemented with a linked list, keeping track of a front pointer, Only rear pointer s will change during an insertion into an non-empty queue. Queue data structure can be used to implement least recently used (LRU) page fault algorithm and Quick short algorithm. Only option (C) is not correct."),
            QuizQuestion("Consider a standard Circular Queue 'q' implementation whose size is 11 and elements q[0] to q[10]. front and rear initialized to point at q[2]. In which position will the ninth element be added?", listOf("A-q[0]", "B-q[1]", "C-q[9]", "D-q[10]"), 0, "Circular queue whose total size is 11, front and rear pointers are initialized to point at q[2]. Therefore, 9th element will be added at pointer q[0]. So, option (A) is correct."),
            QuizQuestion("Consider the pseudo-code for function fun. What does the function fun do?", listOf("A-Prints numbers from 0 to n-1", "B-Prints numbers from n-1 to 0", "C-Prints first n Fibonacci numbers", "D-Prints first n Fibonacci numbers in reverse order."), 2, "The function prints first n Fibonacci Numbers. Note that 0 and 1 are initially there in q. In every iteration of the loop sum of the two queue items is enqueued and the front item is dequeued. Hence option (C) is the correct answer."),
            QuizQuestion("Which of the following operations on a queue data structure has a time complexity of O(1)?\nA) Enqueue B) Dequeue C) Peek D) Clear", listOf("A-A and B", "B-B only", "C-C only", "D-A and D"), 1, "In a queue data structure, dequeueing (removing an element from the front of the queue) typically has a time complexity of O(1) because it involves removing the first element and adjusting the front pointer. Hence, Option B is correct.")
        )
        "Singly Linked List" -> listOf(
            // EASY (1-5)
            QuizQuestion("What is a Singly Linked List?", listOf("A) Collection of nodes with two pointers", "B) Collection of nodes where each node points to the next node", "C) Array of elements", "D) Tree structure"), 1, "A singly linked list consists of nodes where each node contains data and a pointer to the next node."),
            QuizQuestion("What does each node in a singly linked list contain?", listOf("A) Data only", "B) Data and pointer to previous node", "C) Data and pointer to next node", "D) Only pointer"), 2, "Each node stores data + reference to next node."),
            QuizQuestion("What is the first node of a linked list called?", listOf("A) Tail", "B) Head", "C) Root", "D) Front"), 1, "The head node is the starting point of the list."),
            QuizQuestion("What is the last node of a singly linked list called?", listOf("A) Head", "B) Tail", "C) Root", "D) Null"), 1, "The last node is called tail, and its next pointer is NULL."),
            QuizQuestion("What is the time complexity to access an element by index?", listOf("A) O(1)", "B) O(log n)", "C) O(n)", "D) O(n²)"), 2, "Linked list requires traversal → linear time O(n)."),
            
            // MEDIUM (6-10)
            QuizQuestion("Which operation is efficient in a linked list compared to an array?", listOf("A) Random access", "B) Insertion at beginning", "C) Binary search", "D) Index access"), 1, "Insertion at beginning is O(1) in linked list."),
            QuizQuestion("What happens when you traverse beyond the last node?", listOf("A) Overflow", "B) Underflow", "C) Null pointer", "D) Exception"), 2, "The next pointer of last node is NULL."),
            QuizQuestion("What is the time complexity of inserting at the end (without tail pointer)?", listOf("A) O(1)", "B) O(log n)", "C) O(n)", "D) O(n²)"), 2, "Must traverse entire list → O(n)."),
            QuizQuestion("Which of the following is NOT an advantage of linked lists?", listOf("A) Dynamic size", "B) Efficient insertion", "C) Random access", "D) No memory wastage"), 2, "Linked lists do not support random access."),
            QuizQuestion("Which pointer is used to traverse a singly linked list?", listOf("A) Head pointer", "B) Temporary pointer", "C) Tail pointer", "D) Random pointer"), 1, "A temporary pointer is used for traversal."),

            // HARD (11-15)
            QuizQuestion("What is the result after inserting 10, 20, 30 at beginning?", listOf("A) 10 → 20 → 30", "B) 30 → 20 → 10", "C) 20 → 10 → 30", "D) 30 → 10 → 20"), 1, "Each new element becomes head → reverse order."),
            QuizQuestion("What is the time complexity of deleting a node (given pointer to node)?", listOf("A) O(n)", "B) O(log n)", "C) O(1)", "D) O(n²)"), 2, "Direct pointer access allows deletion in O(1)."),
            QuizQuestion("Consider operations: Insert 1, Insert 2, Insert 3 at beginning. Delete one node from beginning. What remains?", listOf("A) 1 → 2", "B) 2 → 3", "C) 3 → 2", "D) 2 → 1"), 3, "Insert → 3→2→1, delete head → 2→1."),
            QuizQuestion("Which of the following operations requires traversal?", listOf("A) Insert at beginning", "B) Delete first node", "C) Search an element", "D) Update head"), 2, "Searching requires visiting nodes → O(n)."),
            QuizQuestion("What will happen if a loop exists in a linked list?", listOf("A) Program ends", "B) Traversal stops at NULL", "C) Infinite traversal", "D) Error occurs"), 2, "Loop causes traversal to never reach NULL → infinite loop.")
        )
        "Doubly Linked List" -> listOf(
            // EASY (1-5)
            QuizQuestion("What is a Doubly Linked List?", listOf("A) Nodes with one pointer", "B) Nodes with two pointers (prev and next)", "C) Array structure", "D) Tree structure"), 1, "Each node contains data, previous pointer, and next pointer."),
            QuizQuestion("How many pointers does each node in a doubly linked list have?", listOf("A) 1", "B) 2", "C) 3", "D) 4"), 1, "Each node has two pointers: prev and next."),
            QuizQuestion("What does the previous pointer of the first node contain?", listOf("A) Address of next node", "B) Address of last node", "C) NULL", "D) Garbage value"), 2, "First node has no previous node → prev = NULL."),
            QuizQuestion("What does the next pointer of the last node contain?", listOf("A) Address of previous node", "B) Address of first node", "C) NULL", "D) Random value"), 2, "Last node points to NULL."),
            QuizQuestion("Which operation is easier in doubly linked list compared to singly linked list?", listOf("A) Traversal", "B) Backward traversal", "C) Searching", "D) Sorting"), 1, "Doubly linked list allows traversal in both directions."),
            
            // MEDIUM (6-10)
            QuizQuestion("What is the time complexity of insertion at the beginning?", listOf("A) O(n)", "B) O(log n)", "C) O(1)", "D) O(n²)"), 2, "Only pointer updates → constant time."),
            QuizQuestion("What is the time complexity of deletion given a node pointer?", listOf("A) O(n)", "B) O(1)", "C) O(log n)", "D) O(n²)"), 1, "Direct pointer access allows O(1) deletion."),
            QuizQuestion("What is the minimum number of fields in a doubly linked list node?", listOf("A) 1", "B) 2", "C) 3", "D) 4"), 2, "Fields = data + prev + next = 3."),
            QuizQuestion("Which of the following is an advantage of doubly linked list?", listOf("A) Less memory usage", "B) Faster random access", "C) Bidirectional traversal", "D) Simpler structure"), 2, "Can traverse forward and backward."),
            QuizQuestion("What extra cost does a doubly linked list have compared to singly linked list?", listOf("A) Time complexity", "B) Additional memory", "C) Less flexibility", "D) Slower traversal"), 1, "Extra pointer → more memory usage."),

            // HARD (11-15)
            QuizQuestion("In a doubly linked list, the number of pointers affected for an insertion operation will be", listOf("A) 5", "B) 0", "C) 1", "D) None of these"), 0, "Insertion requires updating multiple pointers: New node prev, New node next, Previous node next, Next node prev, Possibly head/tail pointer → Total ≈ 5 pointer changes."),
            QuizQuestion("The minimum number of fields with each node of doubly linked list is", listOf("A) 1", "B) 2", "C) 3", "D) 4"), 2, "A node must have: Data, Previous pointer, Next pointer → 3 fields."),
            QuizQuestion("What happens if previous pointer is not updated during insertion?", listOf("A) No effect", "B) Memory leak", "C) Broken backward traversal", "D) Program crash"), 2, "Backward links become invalid → traversal breaks."),
            QuizQuestion("Consider inserting a node between two nodes. Which pointers must change?", listOf("A) Only next pointer", "B) Only prev pointer", "C) Both prev and next pointers of adjacent nodes", "D) None"), 2, "Must update both sides to maintain links."),
            QuizQuestion("What will happen if next pointer of a node is incorrectly assigned?", listOf("A) No issue", "B) Infinite loop", "C) Data duplication", "D) Stack overflow"), 1, "Wrong pointer may create a cycle → infinite traversal.")
        )
        "Binary Search Tree" -> listOf(
            // EASY (1-5)
            QuizQuestion("What is a Binary Search Tree (BST)?", listOf("A) Tree with only one child", "B) Tree where left < root < right", "C) Tree with cycles", "D) Complete tree"), 1, "In BST, left subtree values are smaller and right subtree values are larger than root."),
            QuizQuestion("Which of the following traversal outputs the data in sorted order in a BST?", listOf("A) Preorder", "B) Inorder", "C) Postorder", "D) Level order"), 1, "Inorder traversal of a BST outputs data in sorted order."),
            QuizQuestion("What is the maximum number of children a BST node can have?", listOf("A) 1", "B) 2", "C) 3", "D) Unlimited"), 1, "Each node has at most 2 children."),
            QuizQuestion("What is the time complexity of searching in a balanced BST?", listOf("A) O(n)", "B) O(log n)", "C) O(n²)", "D) O(1)"), 1, "Balanced BST gives logarithmic search time."),
            QuizQuestion("Access time of the symbol table will be logarithmic if it is implemented by", listOf("A) Linear list", "B) Search tree", "C) Hash table", "D) Self organization list"), 1, "Search trees (balanced BSTs) provide O(log n) access."),
            
            // MEDIUM (6-10)
            QuizQuestion("How many distinct BSTs can be constructed with 3 distinct keys?", listOf("A) 4", "B) 5", "C) 6", "D) 9"), 1, "Number of BSTs = Catalan number → 5."),
            QuizQuestion("What is the height of a BST with only one node?", listOf("A) 0", "B) 1", "C) 2", "D) Depends"), 1, "Height = number of levels → 1."),
            QuizQuestion("What is the worst-case time complexity of searching in a BST?", listOf("A) O(log n)", "B) O(n)", "C) O(n log n)"), 1, "Skewed BST behaves like a linked list."),
            QuizQuestion("What is the in-order traversal of a BST always?", listOf("A) Random", "B) Sorted order", "C) Reverse order", "D) Level order"), 1, "In-order traversal gives sorted sequence."),
            QuizQuestion("Which of the following cases produces a skewed BST?", listOf("A) Random insertion", "B) Sorted insertion", "C) Balanced insertion", "D) Heap insertion"), 1, "Sorted input leads to skewed tree."),

            // HARD (11-15)
            QuizQuestion("A binary search tree is generated by inserting in order the following integers: 50, 15, 62, 5, 20, 58, 91, 3, 8, 37, 60, 24. The number of nodes in the left subtree and right subtree of the root respectively is", listOf("A) (4, 7)", "B) (7, 4)", "C) (8, 3)", "D) (3, 8)"), 1, "Left subtree nodes = {15, 5, 20, 3, 8, 37, 24} → 7. Right subtree nodes = {62, 58, 91, 60} → 4."),
            QuizQuestion("The following numbers are inserted into an empty binary search tree in the given order: 10, 1, 3, 5, 15, 12, 16. What is the height of the binary search tree ?", listOf("A) 3", "B) 4", "C) 5", "D) 6"), 1, "Height = longest path = 4 levels"),
            QuizQuestion("Suppose the numbers 7, 5, 1, 8, 3, 6, 0, 9, 4, 2 are inserted in that order into an initially empty binary search tree. What is the in-order traversal sequence?", listOf("A) 7 5 1 0 3 2 4 6 8 9", "B) 0 2 4 3 1 6 5 9 8 7", "C) 0 1 2 3 4 5 6 7 8 9", "D) 9 8 6 4 2 3 0 1 5 7"), 2, "In-order traversal gives sorted sequence."),
            QuizQuestion("What is the time complexity of inserting n sorted elements into a BST?", listOf("A) O(n log n)", "B) O(n²)", "C) O(log n)", "D) O(n)"), 1, "Sorted insertion creates skewed BST → O(n²)."),
            QuizQuestion("Which operation is most affected in an unbalanced BST?", listOf("A) Traversal", "B) Searching", "C) Printing", "D) Memory allocation"), 1, "Search degrades to O(n) in worst case.")
        )
        "AVL Tree" -> listOf(
            // EASY (1-5)
            QuizQuestion("What is an AVL Tree?", listOf("A) A complete binary tree", "B) A balanced binary search tree", "C) A heap structure", "D) A threaded tree"), 1, "AVL tree is a self-balancing Binary Search Tree."),
            QuizQuestion("What does AVL stand for?", listOf("A) Algorithm Variable List", "B) Adelson-Velsky and Landis", "C) Advanced Value Logic", "D) Array Value List"), 1, "Named after its inventors."),
            QuizQuestion("What is the balance factor of a node?", listOf("A) Number of children", "B) Height difference of left and right subtree", "C) Number of nodes", "D) Depth"), 1, "BF = height(left) − height(right)."),
            QuizQuestion("Allowed balance factor values are:", listOf("A) -2 to 2", "B) Only 0", "C) -1, 0, 1", "D) 1, 2"), 2, "AVL strictly maintains BF ∈ {-1, 0, 1}."),
            QuizQuestion("What is done to balance AVL tree?", listOf("A) Sorting", "B) Rotation", "C) Searching", "D) Traversal"), 1, "Rotations fix imbalance."),

            // MEDIUM (6-10)
            QuizQuestion("Which rotation is used in LL case?", listOf("A) Left", "B) Right", "C) Double", "D) None"), 1, "LL → Right rotation."),
            QuizQuestion("Which rotation is used in RR case?", listOf("A) Left", "B) Right", "C) Double", "D) None"), 0, "RR → Left rotation."),
            QuizQuestion("What is time complexity of search in AVL?", listOf("A) O(n)", "B) O(log n)", "C) O(n²)", "D) O(1)"), 1, "Balanced → logarithmic."),
            QuizQuestion("Which case requires double rotation?", listOf("A) LL", "B) RR", "C) LR", "D) None"), 2, "LR & RL → double rotation."),
            QuizQuestion("PROBLEM: Insert 30, 20, 10 into an empty AVL tree. Which rotation occurs?", listOf("A) Left rotation", "B) Right rotation", "C) Left-Right rotation", "D) No rotation"), 1, "Insertion creates LL imbalance → Right rotation is applied."),

            // HARD (11-15)
            QuizQuestion("Which rotation is used in LR case?", listOf("A) Right", "B) Left", "C) Left then Right", "D) Right then Left"), 2, "LR → Left rotation + Right rotation."),
            QuizQuestion("Which rotation is used in RL case?", listOf("A) Left", "B) Right", "C) Right then Left", "D) Left then Right"), 2, "RL → Right rotation + Left rotation."),
            QuizQuestion("When does rebalancing occur?", listOf("A) Always", "B) When BF = ±2", "C) When inserting root", "D) Never"), 1, "Imbalance detected at BF = ±2."),
            QuizQuestion("PROBLEM: Insert 10, 20, 30 into AVL tree. What is the new root after balancing?", listOf("A) 10", "B) 20", "C) 30", "D) No change"), 1, "RR case → Left rotation → 20 becomes root."),
            QuizQuestion("What is the maximum number of rotations in AVL insertion?", listOf("A) 0", "B) 1", "C) 2", "D) n"), 2, "At most 2 rotations (double rotation).")
        )
        "Heap" -> listOf(
            // EASY (1-5)
            QuizQuestion("What is a Heap?", listOf("A) Sorted array", "B) Complete binary tree", "C) Graph", "D) Linked list"), 1, "Heap is a complete binary tree."),
            QuizQuestion("What are the types of Heap?", listOf("A) Binary and Ternary", "B) Min Heap and Max Heap", "C) Sorted and Unsorted", "D) Static and Dynamic"), 1, "Two main types: Min Heap and Max Heap."),
            QuizQuestion("In a Max Heap, the root contains:", listOf("A) Smallest value", "B) Largest value", "C) Middle value", "D) Random value"), 1, "Root always has maximum element."),
            QuizQuestion("In a Min Heap, the root contains:", listOf("A) Largest value", "B) Smallest value", "C) Random value", "D) Middle value"), 1, "Root holds minimum element."),
            QuizQuestion("What is the height of a heap with n nodes?", listOf("A) O(n)", "B) O(log n)", "C) O(n²)", "D) O(1)"), 1, "Height of complete binary tree is log n."),

            // MEDIUM (6-10)
            QuizQuestion("What is the time complexity of inserting into a heap?", listOf("A) O(n)", "B) O(log n)", "C) O(1)", "D) O(n²)"), 1, "Heapify-up takes log n time."),
            QuizQuestion("What is the time complexity of deleting root from heap?", listOf("A) O(1)", "B) O(log n)", "C) O(n)", "D) O(n²)"), 1, "Heapify-down takes log n time."),
            QuizQuestion("What is the array index formula for left child?", listOf("A) i + 1", "B) 2i", "C) 2i + 1", "D) i / 2"), 2, "Left child = 2i + 1 (0-based index)."),
            QuizQuestion("What is the array index formula for right child?", listOf("A) 2i", "B) 2i + 1", "C) 2i + 2", "D) i - 1"), 2, "Right child = 2i + 2."),
            QuizQuestion("What is the time complexity of building a heap?", listOf("A) O(n)", "B) O(n log n)", "C) O(log n)", "D) O(n²)"), 0, "Build-heap runs in O(n) time."),

            // HARD (11-15)
            QuizQuestion("What is the necessary condition for a Tree to be a heap?", listOf("A) Only the tree must be complete.", "B) Every Root value should be greater or smaller than the children's value only.", "C) The tree must be complete and Every Root value should be greater or smaller than the children's value.", "D) None"), 2, "Heap must satisfy: Complete binary tree property and Heap order property."),
            QuizQuestion("Which traversal gives sorted order in heap?", listOf("A) Inorder", "B) Preorder", "C) Level order", "D) None"), 3, "Heap does NOT guarantee sorted traversal."),
            QuizQuestion("PROBLEM: Insert elements 10, 20, 15 into a Max Heap. What is the root?", listOf("A) 10", "B) 15", "C) 20", "D) Depends"), 2, "Max Heap → largest element becomes root."),
            QuizQuestion("PROBLEM: Delete root from Max Heap [50, 30, 40, 10, 20]. What becomes new root?", listOf("A) 30", "B) 40", "C) 20", "D) 10"), 1, "After removing 50, heapify → 40 becomes root."),
            QuizQuestion("What is the time complexity of heap sort?", listOf("A) O(n)", "B) O(log n)", "C) O(n log n)", "D) O(n²)"), 2, "Heap sort runs in O(n log n).")
        )
        "B-Tree" -> listOf(
            // EASY (1-5)
            QuizQuestion("What is a B-Tree?", listOf("A) Binary tree", "B) Balanced multi-way search tree", "C) Heap structure", "D) Graph"), 1, "B-Tree is a self-balancing multi-way search tree used in databases."),
            QuizQuestion("What is the maximum number of children of a B-Tree node of order m?", listOf("A) m - 1", "B) m", "C) m + 1", "D) 2m"), 1, "A node can have at most m children."),
            QuizQuestion("What is the maximum number of keys in a node of order m?", listOf("A) m", "B) m - 1", "C) m + 1", "D) 2m"), 1, "Maximum keys = m - 1."),
            QuizQuestion("Where are data records stored in a B-Tree?", listOf("A) Only leaf nodes", "B) Only root", "C) All nodes", "D) Only internal nodes"), 2, "Data can be stored in all nodes."),
            QuizQuestion("What is the height of a B-Tree generally?", listOf("A) Large", "B) Small", "C) Random", "D) Zero"), 1, "High branching factor → small height."),

            // MEDIUM (6-10)
            QuizQuestion("What is the minimum number of children (except root) in a B-Tree of order m?", listOf("A) m/2", "B) (m+1)/2", "C) ceil(m/2)", "D) m-1"), 2, "Minimum children = ceil(m/2)."),
            QuizQuestion("What is the minimum number of keys in a non-root node?", listOf("A) ceil(m/2) - 1", "B) m/2", "C) m - 1", "D) 1"), 0, "Minimum keys = ceil(m/2) - 1."),
            QuizQuestion("Why are B-Trees used in databases?", listOf("A) Fast memory access", "B) Reduce disk I/O operations", "C) Simple structure", "D) Less memory"), 1, "B-Trees minimize disk access operations."),
            QuizQuestion("What happens when a node overflows?", listOf("A) Node is deleted", "B) Node splits", "C) Tree becomes unbalanced", "D) Nothing"), 1, "Overflow → node split + key promotion."),
            QuizQuestion("What connects nodes in a B-Tree?", listOf("A) Linked list", "B) Pointers", "C) Arrays", "D) Stack"), 1, "Nodes are connected using pointers."),

            // HARD (11-15)
            QuizQuestion("What happens when the root node splits?", listOf("A) Tree shrinks", "B) Height increases", "C) Height decreases", "D) No change"), 1, "Root split increases tree height."),
            QuizQuestion("PROBLEM: In a B-Tree of order 5, what is the maximum number of keys in a node?", listOf("A) 3", "B) 4", "C) 5", "D) 6"), 1, "Max keys = m - 1 = 4."),
            QuizQuestion("PROBLEM: In a B-Tree of order 4, what is the minimum number of keys in a non-root node?", listOf("A) 1", "B) 2", "C) 3", "D) 0"), 0, "ceil(4/2) = 2 → keys = 2 - 1 = 1."),
            QuizQuestion("What is the time complexity of search in B-Tree?", listOf("A) O(n)", "B) O(log n)", "C) O(n²)", "D) O(1)"), 1, "Height is small → logarithmic search time."),
            QuizQuestion("What is the main difference between B-Tree and Binary Search Tree?", listOf("A) B-Tree is binary", "B) BST is multi-way", "C) B-Tree allows multiple keys per node", "D) No difference"), 2, "B-Tree nodes store multiple keys, unlike BST.")
        )
        "B+ Tree" -> listOf(
            // EASY (1-5)
            QuizQuestion("What is a B+ Tree?", listOf("A) Binary tree", "B) Balanced multi-level indexing tree", "C) Heap structure", "D) Graph"), 1, "B+ Tree is a balanced tree used in databases and file systems."),
            QuizQuestion("Where are actual records stored in a B+ Tree?", listOf("A) Internal nodes", "B) Leaf nodes", "C) Root node", "D) All nodes"), 1, "Data is stored only in leaf nodes."),
            QuizQuestion("What do internal nodes in B+ Tree store?", listOf("A) Actual data", "B) Keys and pointers", "C) Only data", "D) Only pointers"), 1, "Internal nodes store keys for navigation + pointers."),
            QuizQuestion("What is the main advantage of B+ Tree over B Tree?", listOf("A) Faster insertion", "B) Sequential access through linked leaves", "C) Less memory", "D) Simpler structure"), 1, "Leaf nodes are linked, enabling fast range queries."),
            QuizQuestion("What is the height of a B+ Tree typically?", listOf("A) Very large", "B) Very small", "C) Random", "D) Zero"), 1, "High branching factor → small height."),

            // MEDIUM (6-10)
            QuizQuestion("B+ Trees are considered BALANCED because", listOf("A) the lengths of the paths from the root to all leaf nodes are all equal.", "B) the lengths of the paths from the root to all leaf nodes differ from each other by at most 1.", "C) the number of children of any two non-leaf sibling nodes differ by at most 1.", "D) the number of records in any two leaf nodes differ by at most 1."), 0, "All leaf nodes are at same depth, ensuring balance."),
            QuizQuestion("What is the maximum number of children if a node has m keys?", listOf("A) m", "B) m + 1", "C) m - 1", "D) 2m"), 1, "Children = keys + 1."),
            QuizQuestion("What operation benefits most from B+ Tree?", listOf("A) Sorting", "B) Searching large datasets", "C) Recursion", "D) Stack operations"), 1, "Designed for efficient disk-based searching."),
            QuizQuestion("What connects leaf nodes in a B+ Tree?", listOf("A) Parent pointer", "B) Linked list", "C) Stack", "D) Array"), 1, "Leaf nodes are linked sequentially."),
            QuizQuestion("B+ trees are preferred to binary trees in databases because (GATE CS 2000)", listOf("A) Disk capacities are greater than memory capacities", "B) Disk access is much slower than memory access", "C) Disk data transfer rates are much less than memory data transfer rates", "D) Disks are more reliable than memory"), 1, "Fewer disk accesses due to high fan-out → faster search."),

            // HARD (11-15)
            QuizQuestion("Consider a B+-tree in which the maximum number of keys in a node is 5. What is the minimum number of keys in any non-root node?", listOf("A) 1", "B) 2", "C) 3", "D) 4"), 1, "Max children = 6 → Min children = 3. Min keys = children - 1 = 2."),
            QuizQuestion("What is the minimum number of children for a non-root node in B+ Tree of order m?", listOf("A) m", "B) m/2", "C) m/2 - 1", "D) m - 1"), 1, "Minimum children = ceil(m/2)."),
            QuizQuestion("PROBLEM: If a B+ Tree has order 4, what is maximum number of children per node?", listOf("A) 3", "B) 4", "C) 5", "D) 2"), 1, "Order = max children → 4 children."),
            QuizQuestion("PROBLEM: If a node splits in B+ Tree, where does the middle key go?", listOf("A) Left node", "B) Right node", "C) Parent node", "D) Deleted"), 2, "Middle key is pushed up to parent."),
            QuizQuestion("What is the time complexity of search in B+ Tree?", listOf("A) O(n)", "B) O(log n)", "C) O(n²)", "D) O(1)"), 1, "Height is small → logarithmic search.")
        )
        else -> listOf(
            QuizQuestion("What is a Data Structure?", listOf("A) A way to store data", "B) A programming language", "C) A type of computer", "D) An OS"), 0)
        )
    }
}
