# Stack Visualization & Diagrams

## 1. LIFO Principle Visualization

```
PUSH OPERATIONS:
─────────────────

Step 1: Push(10)
[ 10 ]
 TOP

Step 2: Push(20)
[ 20 ]  ← TOP (most recent)
[ 10 ]

Step 3: Push(30)
[ 30 ]  ← TOP (most recent)
[ 20 ]
[ 10 ]

Step 4: Push(40)
[ 40 ]  ← TOP (most recent, will be removed first - LIFO!)
[ 30 ]
[ 20 ]
[ 10 ]


POP OPERATIONS:
───────────────

Step 1: Pop()
Removed: 40
[ 30 ]  ← New TOP
[ 20 ]
[ 10 ]

Step 2: Pop()
Removed: 30
[ 20 ]  ← New TOP
[ 10 ]

Step 3: Pop()
Removed: 20
[ 10 ]  ← New TOP

Step 4: Pop()
Removed: 10
Empty Stack!
```

---

## 2. Operation Flow Diagram

```
               ┌─────────────────────┐
               │   Stack Operations  │
               └─────────────────────┘
                         │
          ┌──────────────┼──────────────┐
          │              │              │
        PUSH            POP            PEEK
          │              │              │
          ▼              ▼              ▼
    Add to TOP   Remove from TOP  View TOP
    O(1) time    O(1) time        O(1) time
          │              │              │
          └──────────────┼──────────────┘
                         │
              ┌──────────┴──────────┐
              │                     │
          isEmpty              isFull
          O(1) time            O(1) time
```

---

## 3. Push Operation Detailed

```
PUSH(value) Algorithm:
──────────────────────

START
  │
  ├─→ Is Stack Full? 
  │    │
  │    ├─→ YES → Throw StackOverflowException
  │    │
  │    └─→ NO
  │         │
  │         ├─→ TOP = TOP + 1
  │         │
  │         ├─→ stack[TOP] = value
  │         │
  │         └─→ Return Success
  │
END

Example:
Before: [10, 20, 30], TOP = 2
        Push(40)
After:  [10, 20, 30, 40], TOP = 3
```

---

## 4. Pop Operation Detailed

```
POP() Algorithm:
─────────────────

START
  │
  ├─→ Is Stack Empty?
  │    │
  │    ├─→ YES → Throw StackUnderflowException
  │    │
  │    └─→ NO
  │         │
  │         ├─→ value = stack[TOP]
  │         │
  │         ├─→ TOP = TOP - 1
  │         │
  │         └─→ Return value
  │
END

Example:
Before: [10, 20, 30, 40], TOP = 3
        Pop()
After:  [10, 20, 30], TOP = 2
Returns: 40
```

---

## 5. Stack Memory Layout (Array-based)

```
ARRAY REPRESENTATION:
─────────────────────

Array:  [10][20][30][40][?][?]
Index:   0   1   2   3  4  5
         │   │   │   │
         │   │   │   └─→ TOP = 3
         │   │   │
         │   │   └─→ Stack Elements
         │   │
         └───└─────→ Allocated & Used

TOP Pointer = 3 (points to last element)
Size = 4 (number of elements)
Capacity = 6 (array size)
```

---

## 6. State Transitions

```
Empty Stack ──PUSH──→ [1]
              ────►  │
                     PUSH
                      │
                      ▼
                    [1,2]
                      │
                     POP
                      │
                      ▼
                     [1]
                      │
                     POP
                      ▼
                  Empty Stack
                      │
                     POP
                      │
                      ▼
                   UNDERFLOW!
                   (Error State)

Similarly:
Full Stack ──PUSH──→ OVERFLOW!
                    (Error State)
```

---

## 7. Time Complexity Visualization

```
Operation Complexity Chart:
───────────────────────────

                  ┌─────────────┐
                  │   O(1)      │
            ┌─────┼─────────────┼─────┐
            │     │             │     │
          PUSH   POP           PEEK   │
            │     │             │     │
            └─────┴─────────────┴─────┘


            ┌─────────────────────────┐
            │       O(n)              │
        ┌───┼──────────────────────┬──┤
        │   │                      │  │
     SEARCH ACCESS            Worst Case
        │   │                      │
        └───┴──────────────────────┘

(Number of operations grows with n elements)
```

---

## 8. Real-World Example: Browser History

```
Browser Navigation with Stack:
──────────────────────────────

User visits pages in order: Google → Wikipedia → GitHub → StackOverflow

Stack After Each Visit:
┌─────────┐
│    1. Google      │
└─────────┘

┌─────────┐
│ Wikipedia       │  ← TOP
│ Google          │
└─────────┘

┌─────────┐
│ GitHub          │  ← TOP
│ Wikipedia       │
│ Google          │
└─────────┘

┌─────────┐
│ StackOverflow   │  ← TOP (Current Page)
│ GitHub          │
│ Wikipedia       │
│ Google          │
└─────────┘

User clicks BACK button:
POP() → Remove StackOverflow
Next page = GitHub (TOP)

┌─────────┐
│ GitHub          │  ← TOP (New Current)
│ Wikipedia       │
│ Google          │
└─────────┘
```

---

## 9. Expression Evaluation: Infix to Postfix

```
Convert: 3 + 4 * 2 (Infix)
         to Postfix

Using Stack:
────────────

Scan: 3  → Output: 3
Scan: +  → Push: [+]
Scan: 4  → Output: 3 4
Scan: *  → Push: [+, *] (higher precedence)
Scan: 2  → Output: 3 4 2
End:     → Pop all: Output: 3 4 2 * +

Result: 3 4 2 * +

Evaluate Postfix:
─────────────────
3 4 2 * +
= 3 (4*2) +
= 3 8 +
= 11
```

---

## 10. Stack vs Queue Comparison

```
STACK (LIFO)              QUEUE (FIFO)
──────────────            ──────────────

Add: Push (TOP)           Add: Enqueue (REAR)
Remove: Pop (TOP)         Remove: Dequeue (FRONT)

[4] ← TOP                 FRONT → [1]
[3]                               [2]
[2]                               [3]
[1]                               [4] ← REAR

Last in, First out        First in, First out


Use Case:                 Use Case:
Undo/Redo                 Print Queue
Browser Back              Task Scheduling
Function Calls            BFS Traversal
```

---

## 11. Overflow vs Underflow

```
OVERFLOW Scenario:
──────────────────

Stack has maxSize = 5

State:
[50] ← TOP
[40]
[30]
[20]
[10]

Try: Push(60)
     ↓
Error: STACK OVERFLOW!
Cannot add more elements

Result: Same stack, error thrown


UNDERFLOW Scenario:
───────────────────

Stack is empty:
[]

Try: Pop()
     ↓
Error: STACK UNDERFLOW!
No elements to remove

Result: Same empty stack, error thrown
```

---

## 12. Performance Comparison

```
Stack Implementation Comparison:
─────────────────────────────────

ARRAY-BASED STACK:
┌──────────┬──────┬──────────┐
│ Operation│ Time │ Space    │
├──────────┼──────┼──────────┤
│ Push     │ O(1) │ O(1)     │
│ Pop      │ O(1) │ O(1)     │
│ Peek     │ O(1) │ O(1)     │
│ Space    │ -    │ O(n)     │
├──────────┼──────┼──────────┤
│ Pros: Fast Access            │
│ Cons: Fixed Size, Overflow   │
└──────────┴──────┴──────────┘


LINKED LIST-BASED STACK:
┌──────────┬──────┬──────────┐
│ Operation│ Time │ Space    │
├──────────┼──────┼──────────┤
│ Push     │ O(1) │ O(1)     │
│ Pop      │ O(1) │ O(1)     │
│ Peek     │ O(1) │ O(1)     │
│ Space    │ -    │ O(n)     │
├──────────┼──────┼──────────┤
│ Pros: Dynamic Size, No OF    │
│ Cons: More Memory, No Index  │
└──────────┴──────┴──────────┘
```

---

## 13. Application: Parenthesis Matching

```
Check if parentheses are balanced:
"( { [ ] } )"

Using Stack:
────────────

Char '(' → Push: ['(']
Char '{' → Push: ['(', '{']
Char '[' → Push: ['(', '{', '[']
Char ']' → Pop '[': ✓ Match
Char '}' → Pop '{': ✓ Match
Char ')' → Pop '(': ✓ Match

Result: Balanced ✓

Invalid example: "( { [ )"
Would result in mismatch when trying to pop
Result: Not Balanced ✗
```

---

## 14. Learning Progression Diagram

```
STACK LEARNING PROGRESSION:
────────────────────────────

┌──────────────────────────────┐
│ BEGINNER LEVEL               │
├──────────────────────────────┤
│ 1. What is Stack (LIFO)      │
│ 2. Basic Operations          │
│ 3. Real-Life Examples        │
│ 4. Visual Representation     │
└──────────────────────────────┘
              ↓
┌──────────────────────────────┐
│ INTERMEDIATE LEVEL           │
├──────────────────────────────┤
│ 1. Stack Rules               │
│ 2. Time Complexity           │
│ 3. Implementation Details    │
│ 4. Error Handling            │
└──────────────────────────────┘
              ↓
┌──────────────────────────────┐
│ ADVANCED LEVEL               │
├──────────────────────────────┤
│ 1. Real-World Applications   │
│ 2. Optimization              │
│ 3. Problem Solving           │
│ 4. Compare with Other DS     │
└──────────────────────────────┘
```

---

## 15. Quick Lookup Table

```
┌─────────────┬───────────────┬─────────────┬──────────────┐
│ Operation   │ Time Complex. │ What It Does│ Exception    │
├─────────────┼───────────────┼─────────────┼──────────────┤
│ PUSH(x)     │ O(1)          │ Add to TOP  │ Overflow     │
│ POP()       │ O(1)          │ Remove TOP  │ Underflow    │
│ PEEK()      │ O(1)          │ View TOP    │ None*        │
│ isEmpty()   │ O(1)          │ Check empty │ None         │
│ isFull()    │ O(1)          │ Check full  │ None         │
│ size()      │ O(1)          │ Count elems │ None         │
│ contains(x) │ O(n)          │ Search elem │ None         │
└─────────────┴───────────────┴─────────────┴──────────────┘
* PEEK returns null if empty (no exception)
```

---

**Visual Guide Complete!** 📊
Use these diagrams to better understand Stack data structure operations and concepts.

