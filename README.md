# AddSearchWord

A concurrent-friendly Java implementation of the classic **Add and Search Words** data structure (a Trie-based dictionary) with safe ownership semantics.

## What is it?
This project provides:

1. `WordDictionary` – a thread-safe trie that stores words **once** and records the *owner* (thread or client) that first added the word.
2. Custom exception `KeyAlreadyExistsException` – thrown when a second caller tries to claim a word already owned.
3. JUnit 5 test-suite demonstrating single-threaded behaviour and a 100×100 stress test with concurrent threads.

> Only the first thread that reaches a given word becomes its owner; further attempts receive an exception.

## Key Features

- **Lock-free inserts** using `ConcurrentHashMap` and `AtomicReference` for high throughput.
- **Per-node atomic ownership**: `compareAndSet(null, owner)` guarantees exclusive access without global locks.
- **Fully concurrent reads** (`search`) with no synchronisation overhead.
- **Comprehensive tests**: unit tests plus a heavy multithreaded scenario validating correctness under contention.
- **Java 21** build with Maven.

## Build & Test

```bash
# compile & run tests
mvn clean test
```

Running inside IntelliJ / VS Code (Cursor) shows ▶ icons once the project builds; click to run tests individually.

## How to Use

```java
WordDictionary dict = new WordDictionary();
try {
    dict.addWord("hello", "Alice"); // Alice now owns "hello"
} catch (KeyAlreadyExistsException ignore) {}

String owner = dict.search("hello"); // returns "Alice"
```

## Folder Structure

```
src/main/java/
 └── carneiro/bruno/
     ├── WordDictionary.java      # core data structure
     └── exception/
         └── KeyAlreadyExistsException.java
src/test/java/
 └── carneiro/bruno/WordDictionaryTest.java  # JUnit 5 tests
```

## License
MIT (add your preferred license here)
