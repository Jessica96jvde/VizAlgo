package com.example.vizalgo.learn

/**
 * Stack Data Structure Implementation
 * Follows LIFO (Last In First Out) principle
 * 
 * Operations:
 * - Push: O(1)
 * - Pop: O(1)
 * - Peek: O(1)
 * 
 * Applications:
 * - Expression evaluation
 * - Parenthesis checking
 * - Function calls (recursion)
 * - Undo/Redo operations
 * - Browser history
 * - Backtracking algorithms
 */
class Stack {
    private val elements = mutableListOf<String>()
    private var top = -1
    private val maxSize = 1000
    
    /**
     * PUSH - Insert element at the top
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    fun push(item: String) {
        if (isFull()) {
            throw StackOverflowException("Stack is full! Cannot push '$item'")
        }
        elements.add(item)
        top++
    }
    
    /**
     * POP - Remove and return element from top
     * Time Complexity: O(1)
     * Returns: Removed element or null if stack is empty
     */
    fun pop(): String? {
        return if (isEmpty()) {
            throw StackUnderflowException("Stack is empty! Cannot pop")
        } else {
            val removed = elements.removeAt(top)
            top--
            removed
        }
    }
    
    /**
     * PEEK - View top element without removing
     * Time Complexity: O(1)
     * Returns: Top element or null if stack is empty
     */
    fun peek(): String? {
        return elements.lastOrNull()
    }
    
    /**
     * Check if stack is empty
     * Returns: true if no elements, false otherwise
     */
    fun isEmpty(): Boolean = elements.isEmpty() || top == -1
    
    /**
     * Check if stack is full
     * Returns: true if reached max capacity, false otherwise
     */
    fun isFull(): Boolean = top >= maxSize - 1
    
    /**
     * Get all elements in stack (from bottom to top)
     * Returns: List of all elements
     */
    fun getElements(): List<String> = elements.toList()
    
    /**
     * Get current size of stack
     * Returns: Number of elements in stack
     */
    fun size(): Int = elements.size
    
    /**
     * Get current top position
     * Returns: Index of top element (-1 if empty)
     */
    fun getTop(): Int = top
    
    /**
     * Clear all elements from stack
     */
    fun clear() {
        elements.clear()
        top = -1
    }
    
    /**
     * Get stack representation as string
     * Returns: Visual representation of stack
     */
    override fun toString(): String {
        if (isEmpty()) return "[ EMPTY ]"
        val builder = StringBuilder()
        builder.append("TOP → [ ")
        for (i in elements.size - 1 downTo 0) {
            builder.append(elements[i])
            if (i > 0) builder.append(", ")
        }
        builder.append(" ]")
        return builder.toString()
    }
    
    /**
     * Check if specific element exists in stack
     * Time Complexity: O(n)
     * Returns: true if element found, false otherwise
     */
    fun contains(item: String): Boolean = elements.contains(item)
    
    /**
     * Get element at specific position from top
     * Time Complexity: O(1)
     * @param position: 0 = top, 1 = second from top, etc.
     * Returns: Element at position or null
     */
    fun getElementFromTop(position: Int): String? {
        return if (position >= 0 && position < elements.size) {
            elements[elements.size - 1 - position]
        } else null
    }
}

/**
 * Custom Exception for Stack Overflow
 * Thrown when pushing to a full stack
 */
class StackOverflowException(message: String) : Exception(message)

/**
 * Custom Exception for Stack Underflow
 * Thrown when popping from an empty stack
 */
class StackUnderflowException(message: String) : Exception(message)
