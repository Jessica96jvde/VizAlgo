package com.example.vizalgo.learn

class Queue {
    private val elements = mutableListOf<String>()

    fun enqueue(item: String) {
        elements.add(item)
    }

    fun dequeue(): String? {
        return if (elements.isNotEmpty()) elements.removeAt(0) else null
    }

    fun peek(): String? {
        return elements.firstOrNull()
    }

    fun isEmpty(): Boolean = elements.isEmpty()
    
    fun getElements(): List<String> = elements.toList()
}
