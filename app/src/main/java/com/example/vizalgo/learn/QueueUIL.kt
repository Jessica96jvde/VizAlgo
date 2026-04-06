package com.example.vizalgo.learn

/**
 * QueueUIL - Learning Content Helper for Queue Data Structure
 * Contains explanation strings and educational content ONLY
 * Visual interactions happen in Visualize section, not here
 */
class QueueUIL {
    companion object {
        fun getEnqueueExplanation(): String = """
            🔹 ENQUEUE OPERATION (Insertion)
            
            👉 Algorithm: ENQUEUE(queue, element)
            1. Start
            2. Check if REAR == MAX - 1
                  → If yes, print "Queue Overflow" and STOP
            3. Else
                  → REAR = REAR + 1
                  → QUEUE[REAR] = element
            4. End
            
            📌 Time Complexity Analysis:
            
            👉 Steps involved:
            • Check if queue is full → constant time
            • Increment REAR → constant time
            • Insert element → constant time
            
            👉 Equation:
            T(n) = c1 + c2 + c3
            
            👉 Where:
            c1 = overflow check
            c2 = increment rear
            c3 = insertion
            
            👉 Simplified:
            T(n) = O(1)
            
            📌 Example:

            Before Enqueue:

            QUEUE = [10, 20, 30]
            FRONT = 0, REAR = 2

            Enqueue 40:

            QUEUE = [10, 20, 30, 40]
            FRONT = 0, REAR = 3
            
            Key Points:
            • Always adds to the REAR
            • No shifting of elements needed
            • Constant time operation
        """.trimIndent()
        
        fun getDequeueExplanation(): String = """
            🔹 DEQUEUE OPERATION (Deletion)
            
            👉 Algorithm: DEQUEUE(queue)
            1. Start
            2. Check if FRONT > REAR
                  → If yes, print "Queue Underflow" and STOP
            3. Else
                  → element = QUEUE[FRONT]
                  → FRONT = FRONT + 1
                  → return element
            4. End
            
            📌 Time Complexity Analysis:
            
            👉 Steps involved:
            • Check if queue is empty → constant time
            • Access FRONT element → constant time
            • Increment FRONT → constant time
            
            👉 Equation:
            T(n) = c1 + c2 + c3
            
            👉 Where:
            c1 = underflow check
            c2 = access element
            c3 = increment front
            
            👉 Simplified:
            T(n) = O(1)
            
            📌 Example:

            Before Dequeue:

            QUEUE = [10, 20, 30, 40]
            FRONT = 0, REAR = 3

            After Dequeue:

            Removed = 10
            QUEUE = [X, 20, 30, 40]
            FRONT = 1, REAR = 3
            
            Time Complexity: O(1)
            
            Key Points:
            • Always removes from the FRONT
            • Returns removed element
            • No reorganization needed
        """.trimIndent()
        
        fun getPeekExplanation(): String = """
            🔹 PEEK (FRONT) OPERATION (Access)
            
            👉 Algorithm: PEEK(queue)
            1. Start
            2. Check if FRONT > REAR
                  → If yes, return null/error
            3. Else
                  → return QUEUE[FRONT]
            4. End
            
            📌 Time Complexity Analysis:
            
            👉 Steps:
            • Directly access FRONT element
            
            👉 Equation:
            T(n) = c
            
            👉 Simplified:
            O(1)
            
            Example:
            Queue: [10, 20, 30]
            FRONT = 0
            Peek(): Returns 10
            
            Queue remains: [10, 20, 30]
            FRONT unchanged at 0
            
            Key Points:
            • Only views the element
            • Does not modify the queue
            • No elements removed
        """.trimIndent()
        
        fun getImplementationCode(): String = """
            class Queue<T> {
                private val elements = mutableListOf<T>()
                private var front = 0
                private var rear = -1
                private val maxSize = 100
                
                fun enqueue(item: T) {
                    if (rear == maxSize - 1) {
                        println("Queue Overflow!")
                    } else {
                        elements.add(item)
                        rear++
                    }
                }
                
                fun dequeue(): T? {
                    return if (front > rear) {
                        println("Queue Underflow!")
                        null
                    } else {
                        val removed = elements[front]
                        front++
                        removed
                    }
                }
                
                fun peek(): T? {
                    return if (front > rear) null 
                           else elements[front]
                }
                
                fun isEmpty(): Boolean = front > rear
                
                fun isFull(): Boolean = rear == maxSize - 1
            }
        """.trimIndent()
    }
}

