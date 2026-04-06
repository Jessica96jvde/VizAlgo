package com.example.vizalgo.learn

/**
 * StackUIL - Learning Content Helper for Stack Data Structure
 * Contains explanation strings and educational content ONLY
 * Visual interactions happen in Visualize section, not here
 */
class StackUIL {
    companion object {
        fun getPushExplanation(): String = """
            🔹 PUSH OPERATION (Insertion)
            
            👉 Algorithm: PUSH(stack, element)
            1. Start
            2. Check if TOP == MAX - 1
                  → If yes, print "Stack Overflow" and STOP
            3. Else
                  → TOP = TOP + 1
                  → STACK[TOP] = element
            4. End
            
            📌 Explanation:
            We first check if stack is full
            If not, we increase TOP
            Insert the new element at TOP
            
            📌 Example:

            Before Push:

            STACK = [10, 20, 30]
            TOP = 2

            Push 40:

            STACK = [10, 20, 30, 40]
            TOP = 3
            
            Time Complexity: O(1)
            
            Key Points:
            • Always adds to the top
            • No shifting of elements needed
            • Constant time operation
        """.trimIndent()
        
        fun getPopExplanation(): String = """
            🔹 POP OPERATION (Deletion)
            
            👉 Algorithm: POP(stack)
            1. Start
            2. Check if TOP == -1
                  → If yes, print "Stack Underflow" and STOP
            3. Else
                  → element = STACK[TOP]
                  → TOP = TOP - 1
                  → print element
            4. End
            
            📌 Explanation:
            We first check if stack is empty
            If not, remove the top element
            Decrease TOP
            
            📌 Example:

            Before Pop:

            STACK = [10, 20, 30, 40]
            TOP = 3

            After Pop:

            Removed = 40
            STACK = [10, 20, 30]
            TOP = 2
            
            Time Complexity: O(1)
            
            Key Points:
            • Always removes from the top
            • Returns removed element
            • No reorganization needed
        """.trimIndent()
        
        fun getPeekExplanation(): String = """
            🔹 PEEK OPERATION (Access)
            
            Algorithm:
            1. Check if stack is empty
            2. If empty → return null/error
            3. Else → return element at TOP
            4. Do NOT decrement TOP
            
            Time Complexity: O(1)
            
            Example:
            Stack: [10, 20, 30], TOP = 2
            Peek(): Returns 30
            
            Stack remains: [10, 20, 30]
            TOP unchanged at 2
            
            Key Points:
            • Only views the element
            • Does not modify the stack
            • No elements removed
        """.trimIndent()
        
        fun getImplementationCode(): String = """
            class Stack<T> {
                private val elements = mutableListOf<T>()
                private var top = -1
                private val maxSize = 100
                
                fun push(item: T) {
                    if (top == maxSize - 1) {
                        println("Overflow!")
                    } else {
                        elements.add(item)
                        top++
                    }
                }
                
                fun pop(): T? {
                    return if (top == -1) {
                        println("Underflow!")
                        null
                    } else {
                        val removed = elements.removeAt(top)
                        top--
                        removed
                    }
                }
                
                fun peek(): T? {
                    return if (top == -1) null 
                           else elements[top]
                }
                
                fun isEmpty(): Boolean = top == -1
                
                fun isFull(): Boolean = top == maxSize - 1
            }
        """.trimIndent()
    }
}
