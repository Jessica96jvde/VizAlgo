# 📚 STACK - QUICK REFERENCE CARD

## What is a Stack?
**LIFO (Last In, First Out)** - Last element added is first to be removed.

---

## Core Operations

### PUSH - Insert Element ⬆️
```
Time: O(1) | Space: O(n)
Adds element to TOP
Throws: StackOverflowException if full
```

### POP - Remove Element ⬇️
```
Time: O(1) | Space: O(n)
Removes and returns TOP element
Throws: StackUnderflowException if empty
```

### PEEK - View Top Element 👀
```
Time: O(1) | Space: O(1)
Returns TOP element without removing
Returns: null if empty
```

---

## Stack Rules ⚠️

| Rule | Description |
|------|-------------|
| **One End** | Only TOP end for add/remove |
| **LIFO** | Last added = First removed |
| **No Middle Access** | Can't access middle directly |
| **Overflow** | Pushing to full stack |
| **Underflow** | Popping from empty stack |

---

## Time Complexity ⏱️

| Operation | Time | Why |
|-----------|------|-----|
| Push | O(1) | Fixed TOP position |
| Pop | O(1) | Fixed TOP position |
| Peek | O(1) | Direct access |
| Search | O(n) | Must pop all |
| Access | O(n) | Must pop to reach |

---

## Real-World Examples 🌍

1. **Browser Back Button** - History stored in stack
2. **Undo Feature** - Last action undone first
3. **Function Calls** - Call stack in recursion
4. **Parenthesis Checking** - Validate brackets
5. **Expression Evaluation** - Infix to postfix
6. **Backtracking** - Maze solving, puzzles
7. **Memory Management** - Stack vs Heap
8. **Tower of Hanoi** - Puzzle solving

---

## Implementation

### Kotlin Array-Based
```kotlin
class Stack {
    private val elements = mutableListOf<String>()
    private var top = -1
    
    fun push(item: String) { 
        elements.add(item); top++ 
    }
    fun pop(): String? { 
        return if (top == -1) null else elements[top--] 
    }
    fun peek(): String? { 
        return if (top == -1) null else elements[top] 
    }
}
```

---

## Visual Representation

```
TOP → [40]  ← Last added (First out)
      [30]
      [20]
      [10]  ← First added (Last out)
```

---

## Error Conditions 🚨

### Overflow
```
Stack: [1, 2, 3, 4, 5] (maxSize = 5)
Push(6) → StackOverflowException!
```

### Underflow
```
Stack: [] (empty)
Pop() → StackUnderflowException!
```

---

## Key Properties

✅ **LIFO Principle** - Last in, first out
✅ **O(1) Operations** - Push/Pop/Peek are constant
✅ **Fixed or Dynamic** - Array or Linked List
✅ **Memory Efficient** - No shifting needed
✅ **Single Point Access** - Only TOP accessed
✅ **Error Handling** - Overflow/Underflow checks

---

## When to Use Stack?

✅ **Use When**: You need LIFO behavior
✅ **Use For**: Undo/Redo, function calls, brackets
✅ **Don't Use For**: FIFO needs (use Queue instead)
✅ **Alternative**: Queue for FIFO behavior

---

## Comparison with Queue

| Feature | Stack | Queue |
|---------|-------|-------|
| **Order** | LIFO | FIFO |
| **Add** | Push (TOP) | Enqueue (REAR) |
| **Remove** | Pop (TOP) | Dequeue (FRONT) |
| **Use** | Undo/Redo | Print queue |

---

## Learning Tabs in VizAlgo App

1. **Overview** - What is a Stack, LIFO principle
2. **Operations** - Push, Pop, Peek, isEmpty, isFull
3. **Rules** - Stack constraints and concepts
4. **Complexity** - Time complexity analysis
5. **Applications** - Real-world uses (7+ examples)
6. **Examples** - Step-by-step demonstrations

---

## Quick Formulas

```
Size after Push:    size = size + 1
Size after Pop:     size = size - 1
TOP after Push:     TOP = TOP + 1
TOP after Pop:      TOP = TOP - 1
Is Full:            TOP == maxSize - 1
Is Empty:           TOP == -1
```

---

## Practice Questions

1. What does LIFO stand for? (Last In, First Out)
2. What's the time complexity of Push? (O(1))
3. What error occurs when popping empty stack? (Underflow)
4. Can you access middle elements? (No, must pop from TOP)
5. Name 3 applications of stack? (Undo, function calls, brackets)

---

## Resources

📖 **Documentation**: STACK_LEARNING_CONTENT.md
🛠️ **Implementation**: STACK_IMPLEMENTATION_GUIDE.md
📱 **App Resources**: stack_strings.xml
💻 **Code**: StackLearn.kt, StackUIL.kt, LearnActivity.kt

---

## 🎯 Learning Path

**Beginner**: Overview → Operations → Examples
**Intermediate**: Add Rules → Complexity → Applications
**Advanced**: Implement → Optimize → Real-world problems

---

**Quick Reference Complete! 🚀**
*Ready to learn about Stacks in VizAlgo App*

