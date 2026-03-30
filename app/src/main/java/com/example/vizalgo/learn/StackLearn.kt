package com.example.vizalgo.learn

class Stack {
    private val elements = mutableListOf<String>()

    fun push(item: String) {
        elements.add(item)
    }

    fun pop(): String? {
        return if (elements.isNotEmpty()) elements.removeAt(elements.size - 1) else null
    }

    fun peek(): String? {
        return elements.lastOrNull()
    }

    fun isEmpty(): Boolean = elements.isEmpty()

    fun getElements(): List<String> = elements.toList()
}
