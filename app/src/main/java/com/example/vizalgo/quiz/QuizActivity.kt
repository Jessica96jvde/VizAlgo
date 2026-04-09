package com.example.vizalgo.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        val dsName = intent.getStringExtra("DS_NAME") ?: "General"
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
    val green4 = colorResource(id = R.color.green4)
    
    val questions = getQuestionsForDS(dsName)
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
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
                ResultCard(score, questions.size, cantoraFont, onFinish)
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
                
                QuestionCard(
                    question = questions[currentQuestionIndex],
                    green4 = green4,
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
fun QuestionCard(question: QuizQuestion, green4: Color, onOptionSelected: (Boolean) -> Unit) {
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
fun ResultCard(score: Int, total: Int, font: FontFamily, onFinish: () -> Unit) {
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
                Text("Back to Home", fontWeight = FontWeight.Bold)
            }
        }
    }
}

data class QuizQuestion(val text: String, val options: List<String>, val correctIndex: Int)

fun getQuestionsForDS(dsName: String): List<QuizQuestion> {
    return when (dsName) {
        "Stack" -> listOf(
            QuizQuestion("Which principle does a Stack follow?", listOf("FIFO", "LIFO", "Priority", "LILO"), 1),
            QuizQuestion("What is the operation to add an element to a stack?", listOf("Pop", "Enqueue", "Push", "Top"), 2),
            QuizQuestion("Where does the insertion/deletion happen in a stack?", listOf("Both ends", "Bottom", "Middle", "Top"), 3)
        )
        "Queue" -> listOf(
            QuizQuestion("The minimum number of stacks needed to implement a queue is:", listOf("3", "1", "2", "4"), 2),
            QuizQuestion("Which of the following is true?\ni. FIFO is supported by Stacks\nii. LL lists are always more efficient than arrays\niii. Circular array queues are more efficient than linear array queues", listOf("(ii) is true", "(i) and (ii) are true", "(iii) is true", "(ii) and (iv) are true"), 2),
            QuizQuestion("In a singly linked list queue (head=insert, tail=delete), what is the time complexity of enqueue and dequeue?", listOf("Θ(1), Θ(1)", "Θ(1), Θ(n)", "Θ(n), Θ(1)", "Θ(n), Θ(n)"), 1),
            QuizQuestion("Circular queue is also called:", listOf("Ring Buffer", "Rectangular Buffer", "Square Buffer", "None"), 0),
            QuizQuestion("In a LL queue with Front and Rear pointers, which operation is impossible in O(1) time?", listOf("Delete front item", "Delete rear item", "Insert at front", "None"), 1),
            QuizQuestion("What is used to print the Right View of a binary tree?", listOf("Stack", "Priority Queue", "Queue (BFS based)", "None"), 2),
            QuizQuestion("Which of the following is a type of priority Queue?", listOf("Ascending Order", "Descending order", "Deque", "Both A and B"), 3),
            QuizQuestion("Which data structure is used to implement deque?", listOf("Stack", "Doubly linked list", "Circular array", "Both B and C"), 3),
            QuizQuestion("Which of the following is/are advantages of circular Queue?", listOf("Memory Management", "Traffic system", "CPU Scheduling", "All of the above"), 3),
            QuizQuestion("Level order traversal of a tree is implemented using:", listOf("Stack", "Recursion", "Queue", "None"), 2),
            QuizQuestion("A deque which stores elements in strictly increasing or decreasing order is called:", listOf("Priority Queue", "Double ended Queue", "Monotonic Deque", "None"), 2),
            QuizQuestion("Which DS is used for event-driven simulation of complex systems?", listOf("Stack", "Tree", "Array", "Queue"), 3),
            QuizQuestion("Condition for Circular Queue (size n) to be Full and Empty (initial REAR=FRONT=0):", listOf("Full: (R+1)%n==F, Empty: R==F", "Full: (R+1)%n==F, Empty: (F+1)%n==R", "Full: R==F, Empty: (R+1)%n==F", "Full: (F+1)%n==R, Empty: R==F"), 0),
            QuizQuestion("Which of the following is NOT correct about Queues?", listOf("LL implementation insert only changes rear", "Queue can implement LRU and Quick Sort", "Queue cannot implement LRU", "None"), 1),
            QuizQuestion("Which of the following is an application of Queue?", listOf("Shared resource management", "Asynchronous data transfer", "Load Balancing", "All of the above"), 3)
        )
        "Singly Linked List" -> listOf(
            QuizQuestion("How many pointers does each node have in a Singly Linked List?", listOf("0", "1", "2", "3"), 1),
            QuizQuestion("What does the last node's 'next' pointer point to?", listOf("Head", "Previous", "null", "Tail"), 2)
        )
        "Binary Search Tree" -> listOf(
            QuizQuestion("In a BST, values smaller than the root go to the...", listOf("Right", "Left", "Middle", "Bottom"), 1),
            QuizQuestion("What is the time complexity for search in a balanced BST?", listOf("O(1)", "O(n)", "O(log n)", "O(n^2)"), 2)
        )
        else -> listOf(
            QuizQuestion("What is a Data Structure?", listOf("A way to store data", "A programming language", "A type of computer", "An OS"), 0)
        )
    }
}
