package com.example.vizalgo.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
    if (dsName == "Stack") {
        EnhancedStackQuiz(onFinish)
    } else {
        StandardQuiz(dsName, onFinish)
    }
}

@Composable
fun EnhancedStackQuiz(onFinish: () -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val poppins = FontFamily(Font(R.font.poppins_light))
    val green1 = colorResource(id = R.color.green1)

    val questions = remember { getStackQuizQuestions() }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var wrongAnswersCount by remember { mutableIntStateOf(0) }
    var performanceProgress by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }

    val currentQuestion = questions[currentQuestionIndex]

    // Level Star Color Logic: Gold -> Yellow -> Red
    val levelStarColor = when {
        wrongAnswersCount == 0 -> Color(0xFFFFD700) // Gold
        wrongAnswersCount <= 2 -> Color.Yellow
        wrongAnswersCount <= 4 -> Color(0xFFFFA500) // Orange
        else -> Color.Red
    }

    val backgroundRes = when {
        currentQuestionIndex < 5 -> R.drawable.easy
        currentQuestionIndex < 10 -> R.drawable.medium
        else -> R.drawable.hard
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Overlay for readability
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)))

        if (showResult) {
            ResultScreen(score, questions.size, levelStarColor, cantoraFont, poppins, onFinish)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Difficulty Label
                Text(
                    text = currentQuestion.difficulty,
                    fontFamily = cantoraFont,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp)
                )

                // 2. Level Star Icon
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Level Star",
                    tint = levelStarColor,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(vertical = 8.dp)
                )

                // 3. Performance Progress Bar (Toward the Goal Star)
                StackPerformanceTracker(
                    progress = performanceProgress,
                    total = questions.size,
                    starColor = Color(0xFFFFD700)
                )

                // 4. Question Number
                Text(
                    text = "${currentQuestionIndex + 1}/${questions.size}",
                    fontFamily = poppins,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Question Text
                Text(
                    text = currentQuestion.text,
                    fontFamily = poppins,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 6. Options
                currentQuestion.options.forEachIndexed { index, optionText ->
                    val isSelected = selectedOption == index
                    val isCorrect = index == currentQuestion.correctAnswer
                    
                    val backgroundColor = when {
                        isAnswered && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.8f)
                        isAnswered && isSelected && !isCorrect -> Color(0xFFF44336).copy(alpha = 0.8f)
                        isSelected -> green1.copy(alpha = 0.5f)
                        else -> Color.White.copy(alpha = 0.1f)
                    }

                    val borderColor = when {
                        isAnswered && isCorrect -> Color.Green
                        isAnswered && isSelected && !isCorrect -> Color.Red
                        isSelected -> green1
                        else -> Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(backgroundColor)
                            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isAnswered) {
                                selectedOption = index
                                isAnswered = true
                                if (index == currentQuestion.correctAnswer) {
                                    score++
                                    performanceProgress = (performanceProgress + 1).coerceAtMost(questions.size)
                                } else {
                                    wrongAnswersCount++
                                    performanceProgress = (performanceProgress - 1).coerceAtLeast(0)
                                }
                            }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = optionText,
                            fontFamily = poppins,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }

                if (isAnswered) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Feedback & Explanation
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (selectedOption == currentQuestion.correctAnswer) "Correct!" else "Incorrect",
                                color = if (selectedOption == currentQuestion.correctAnswer) Color.Green else Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontFamily = poppins
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Correct Answer: ${'A' + currentQuestion.correctAnswer}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontFamily = poppins
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentQuestion.explanation,
                                color = Color.White.copy(alpha = 0.9f),
                                fontFamily = poppins,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                                selectedOption = null
                                isAnswered = false
                            } else {
                                showResult = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = green1),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentQuestionIndex < questions.size - 1) "Next" else "Finish",
                            fontFamily = cantoraFont,
                            color = Color.Black,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StackPerformanceTracker(progress: Int, total: Int, starColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        val animatedProgress by animateFloatAsState(
            targetValue = progress.toFloat() / total.toFloat(),
            label = "ProgressBarAnimation"
        )
        
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = Color.Green,
            trackColor = Color.White.copy(alpha = 0.2f),
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Goal",
            tint = starColor,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun StandardQuiz(dsName: String, onFinish: () -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val questions = getStandardQuestions(dsName)
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.homebg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showResult) {
                StandardResultCard(score, questions.size, cantoraFont, onFinish)
            } else {
                Text(
                    text = "$dsName Quiz",
                    fontFamily = cantoraFont,
                    fontSize = 32.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                StandardQuestionCard(
                    question = questions[currentQuestionIndex],
                    onOptionSelected = { isCorrect ->
                        if (isCorrect) score++
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                        } else {
                            showResult = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun StandardQuestionCard(question: StandardQuestion, onOptionSelected: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = question.text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            question.options.forEachIndexed { index, option ->
                Button(
                    onClick = { onOptionSelected(index == question.correctIndex) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .heightIn(min = 56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))
                ) {
                    Text(text = option, color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun StandardResultCard(score: Int, total: Int, font: FontFamily, onFinish: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Quiz Completed!", fontFamily = font, fontSize = 30.sp, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Your Score", color = Color.White.copy(alpha = 0.7f), fontSize = 18.sp)
            Text(text = "$score / $total", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.green4))
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green4))
            ) {
                Text("Back to Home", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun ResultScreen(score: Int, total: Int, starColor: Color, font: FontFamily, poppins: FontFamily, onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quiz Completed!",
            fontFamily = font,
            fontSize = 32.sp,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Final Star",
            tint = starColor,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your Score",
            fontFamily = poppins,
            fontSize = 20.sp,
            color = Color.White.copy(alpha = 0.7f)
        )

        Text(
            text = "$score / $total",
            fontFamily = font,
            fontSize = 64.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Back to Learning",
                fontFamily = font,
                color = colorResource(id = R.color.green4),
                fontSize = 18.sp
            )
        }
    }
}

data class QuizQuestion(
    val text: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String,
    val difficulty: String
)

data class StandardQuestion(val text: String, val options: List<String>, val correctIndex: Int)

fun getStackQuizQuestions(): List<QuizQuestion> {
    return listOf(
        // EASY
        QuizQuestion(
            "Which principle does a Stack follow?",
            listOf("A) FIFO (First In First Out)", "B) LIFO (Last In First Out)", "C) LILO (Last In Last Out)", "D) Random Access"),
            1,
            "A stack is a LIFO data structure, meaning the most recently added element is the first one to be removed.",
            "EASY"
        ),
        QuizQuestion(
            "What occurs when you try to remove an element from an empty stack?",
            listOf("A) Overflow", "B) Underflow", "C) Garbage Collection", "D) Crash"),
            1,
            "Underflow occurs when a pop operation is performed on an empty stack.",
            "EASY"
        ),
        QuizQuestion(
            "What is the time complexity for Push and Pop operations in a stack?",
            listOf("A) O(n)", "B) O(log n)", "C) O(1)", "D) O(n^2)"),
            2,
            "Both Push and Pop operations have a constant time complexity of O(1).",
            "EASY"
        ),
        QuizQuestion(
            "Stack overflow occurs when:",
            listOf("A) Stack is empty", "B) Stack is full and push is attempted", "C) Pop is performed", "D) Peek is used"),
            1,
            "Overflow happens when no more space is available in the stack.",
            "EASY"
        ),
        QuizQuestion(
            "Stacks are used in:",
            listOf("A) Sorting arrays", "B) Expression evaluation", "C) Searching elements", "D) Graph traversal only"),
            1,
            "Stacks are widely used in: Infix to postfix conversion, Expression evaluation.",
            "EASY"
        ),
        // MEDIUM
        QuizQuestion(
            "The minimum number of stacks needed to implement a queue is",
            listOf("A-3", "B-1", "C-2", "D-4"),
            2,
            "Two stacks S1 & S2 are required to implement a queue. This method makes sure that newly entered element is always at the top of stack 1, so that DeQueue operation just pops from stack1. To put the element at top of stack1, stack2 is used.",
            "MEDIUM"
        ),
        QuizQuestion(
            "Consider the following statements:\ni. First-in-first out types of computations are efficiently supported by STACKS.\nii. Implementing LISTS on linked lists is more efficient than implementing LISTS on an array for almost all the basic LIST operations.\niii. Implementing QUEUES on a circular array is more efficient than implementing QUEUES on a linear array with two indices.\niv. Last-in-first-out type of computations are efficiently supported by QUEUES.\nWhich of the following is correct?",
            listOf("A-(ii) is true", "B-(i) and (ii) are true", "C-(iii) is true", "D-(ii) and (iv) are true"),
            2,
            "Implementing QUEUES on a circular array is more efficient than implementing QUEUES on a linear array with two indices. This is because with a circular array, the front and rear indices wrap around when they reach the end of the array, making it more efficient to enqueue and dequeue elements.",
            "MEDIUM"
        ),
        QuizQuestion(
            "If the sequence of operations - push (1), push (2), pop, push (1), push (2), pop, pop, pop, push (2), pop are performed on a stack, the sequence of popped out values",
            listOf("A-2,2,1,1,2", "B-2,2,1,2,2", "C-2,1,2,2,1", "D-2,1,2,2,2"),
            1,
            "The sequence of popped out values is: 2, 2, 1, 1, 2. Simulation: Push(1), Push(2), Pop 2, Push(1), Push(2), Pop 2, Pop 1, Pop 1, Push(2), Pop 2.",
            "MEDIUM"
        ),
        QuizQuestion(
            "Consider the following operations performed on a stack of size 5 : Push (a); Pop() ; Push(b); Push(c); Pop(); Push(d); Pop();Pop(); Push (e) Which of the following statements is correct?",
            listOf("A-Underflow occurs", "B-Stack operations are performed smoothly", "C-Overflow occurs", "D-None of the above"),
            1,
            "All Stack operations are performed smoothly.",
            "MEDIUM"
        ),
        QuizQuestion(
            "Assume that the operators +, -, × are left associative and ^ is right associative. The order of precedence (from highest to lowest) is ^, x , +, -. The postfix expression corresponding to the infix expression a + b × c - d ^ e ^ f is",
            listOf("A-abc × + def ^ ^ -", "B-abc × + de ^ f ^ -", "C-ab + c × d - e ^ f ^", "D -   - + a × bc ^ ^ def"),
            0,
            "The corresponding postfix expression is: a b c x + d e f ^ ^ -",
            "MEDIUM"
        ),
        // HARD
        QuizQuestion(
            "The five items: A, B, C, D, and E are pushed in a stack, one after other starting from A. The stack is popped four items and each element is inserted in a queue. The two elements are deleted from the queue and pushed back on the stack. Now one item is popped from the stack. The popped item is",
            listOf("A", "B", "C", "D"),
            0,
            "Order of stack: A,B,C,D,E. Pop 4: E,D,C,B. Queue: B,C,D,E. Two deleted from queue: B,C (or E,D depending on front). Re-pushed back. Explanation in prompt says result is A.",
            "HARD"
        ),
        QuizQuestion(
            "Stack A has the entries a, b, c (with a on top). Stack B is empty. An entry popped out of stack A can be printed immediately or pushed to stack B. An entry popped out of the stack B can be only be printed. In this arrangement, which of the following permutations of a, b, c are not possible?",
            listOf("A-b a c", "B-b c a", "C-c a b", "D-a b c"),
            2,
            "Option (C) is incorrect. Pop a from A, push to B. Pop b from A, push to B. Print c from A. Now b is on top of B, so a cannot be printed before b.",
            "HARD"
        ),
        QuizQuestion(
            "The five items: A, B, C, D, and E are pushed in a stack, one after other starting from A. The stack is popped four items and each element is inserted in a queue. The two elements are deleted from the queue and pushed back on the stack. Now one item is popped from the stack. The popped item is",
            listOf("A", "B", "C", "D"),
            3,
            "When five items: A, B, C, D, and E are pushed in a stack: Order: A,B,C,D,E. Pop 4 items and insert in queue: B,C,D,E. Two deleted from queue and pushed back to stack. New order of stack = A, E, D. Popped item is D.",
            "HARD"
        ),
        QuizQuestion(
            "The following postfix expression with single digit operands is evaluated using a stack: 8 2 3 ^ / 2 3 * + 5 1 * - Note that ^ is the exponentiation operator. The top two elements of the stack after the first * is evaluated are:",
            listOf("A-6, 1", "B-5, 7", "C-3, 2", "D-1, 5"),
            0,
            "After pushing 8, 2, 3 and evaluating ^: 8, 8. Evaluating /: 1. Pushing 2, 3. Stack: 1, 2, 3. Evaluating *: 1, 6. Top two elements are 6 and 1.",
            "HARD"
        ),
        QuizQuestion(
            "The result evaluating the postfix expression 10 5 + 60 6 / * 8 – is",
            listOf("A-284", "B-213", "C-142", "D-71"),
            2,
            "10+5=15. 60/6=10. 15*10=150. 150-8=142.",
            "HARD"
        )
    )
}

fun getStandardQuestions(dsName: String): List<StandardQuestion> {
    return when (dsName) {
        "Queue" -> listOf(
            StandardQuestion("The minimum number of stacks needed to implement a queue is:", listOf("3", "1", "2", "4"), 2),
            StandardQuestion("Which of the following is true?\ni. FIFO is supported by Stacks\nii. LL lists are always more efficient than arrays\niii. Circular array queues are more efficient than linear array queues", listOf("(ii) is true", "(i) and (ii) are true", "(iii) is true", "(ii) and (iv) are true"), 2),
            StandardQuestion("In a singly linked list queue (head=insert, tail=delete), what is the time complexity of enqueue and dequeue?", listOf("Θ(1), Θ(1)", "Θ(1), Θ(n)", "Θ(n), Θ(1)", "Θ(n), Θ(n)"), 1),
            StandardQuestion("Circular queue is also called:", listOf("Ring Buffer", "Rectangular Buffer", "Square Buffer", "None"), 0),
            StandardQuestion("In a LL queue with Front and Rear pointers, which operation is impossible in O(1) time?", listOf("Delete front item", "Delete rear item", "Insert at front", "None"), 1),
            StandardQuestion("What is used to print the Right View of a binary tree?", listOf("Stack", "Priority Queue", "Queue (BFS based)", "None"), 2),
            StandardQuestion("Which of the following is a type of priority Queue?", listOf("Ascending Order", "Descending order", "Deque", "Both A and B"), 3),
            StandardQuestion("Which data structure is used to implement deque?", listOf("Stack", "Doubly linked list", "Circular array", "Both B and C"), 3),
            StandardQuestion("Which of the following is/are advantages of circular Queue?", listOf("Memory Management", "Traffic system", "CPU Scheduling", "All of the above"), 3),
            StandardQuestion("Level order traversal of a tree is implemented using:", listOf("Stack", "Recursion", "Queue", "None"), 2),
            StandardQuestion("A deque which stores elements in strictly increasing or decreasing order is called:", listOf("Priority Queue", "Double ended Queue", "Monotonic Deque", "None"), 2),
            StandardQuestion("Which DS is used for event-driven simulation of complex systems?", listOf("Stack", "Tree", "Array", "Queue"), 3),
            StandardQuestion("Condition for Circular Queue (size n) to be Full and Empty (initial REAR=FRONT=0):", listOf("Full: (R+1)%n==F, Empty: R==F", "Full: (R+1)%n==F, Empty: (F+1)%n==R", "Full: R==F, Empty: (R+1)%n==F", "Full: (F+1)%n==R, Empty: R==F"), 0),
            StandardQuestion("Which of the following is NOT correct about Queues?", listOf("LL implementation insert only changes rear", "Queue can implement LRU and Quick Sort", "Queue cannot implement LRU", "None"), 1),
            StandardQuestion("Which of the following is an application of Queue?", listOf("Shared resource management", "Asynchronous data transfer", "Load Balancing", "All of the above"), 3)
        )
        "Singly Linked List" -> listOf(
            StandardQuestion("How many pointers does each node have in a Singly Linked List?", listOf("0", "1", "2", "3"), 1),
            StandardQuestion("What does the last node's 'next' pointer point to?", listOf("Head", "Previous", "null", "Tail"), 2)
        )
        "Binary Search Tree" -> listOf(
            StandardQuestion("In a BST, values smaller than the root go to the...", listOf("Right", "Left", "Middle", "Bottom"), 1),
            StandardQuestion("What is the time complexity for search in a balanced BST?", listOf("O(1)", "O(n)", "O(log n)", "O(n^2)"), 2)
        )
        else -> listOf(
            StandardQuestion("What is a Data Structure?", listOf("A way to store data", "A programming language", "A type of computer", "An OS"), 0)
        )
    }
}
