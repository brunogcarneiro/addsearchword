package carneiro.bruno;

import org.junit.jupiter.api.Test;

import carneiro.bruno.exception.KeyAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

public class WordDictionaryTest {

    @Test
    public void testAddWord() throws KeyAlreadyExistsException {
        WordDictionary wordDictionary = new WordDictionary();
        wordDictionary.addWord("hello", "world");
        assertEquals("world", wordDictionary.search("hello"));
    }

    @Test
    public void testAddWordWithSameKey() throws KeyAlreadyExistsException {
        WordDictionary wordDictionary = new WordDictionary();
        wordDictionary.addWord("hello", "world");
        assertThrows(KeyAlreadyExistsException.class, () -> {
            wordDictionary.addWord("hello", "world2");
        });
    }

    @Test
    public void testConcurrentThreadsAddingWords() throws KeyAlreadyExistsException {
        // This test will create 100 thread that will try to get ownership of a list of 100 words.
        // Each thread will try the same set of 100 words.
        // Only one thread will succeed in adding the word to the dictionary.
        // The rest will throw a KeyAlreadyExistsException.
        // Each thread has your own name and will try to add the word to the dictionary with your own name.
        // The test will check if the word was added by the correct thread. 
        
        WordDictionary wordDictionary = new WordDictionary();
        int threadCount = 100;
        int wordsCount = 100;

        List<String> words = java.util.stream.IntStream.range(0, wordsCount)
                .mapToObj(i -> "word" + i)
                .toList();

        java.util.concurrent.ConcurrentMap<String, java.util.concurrent.atomic.AtomicInteger> successCounter = new java.util.concurrent.ConcurrentHashMap<>();
        words.forEach(w -> successCounter.put(w, new java.util.concurrent.atomic.AtomicInteger()));

        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(threadCount);
        java.util.concurrent.CountDownLatch startLatch = new java.util.concurrent.CountDownLatch(1);

        for (int i = 0; i < threadCount; i++) {
            final String owner = "thread-" + i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (String word : words) {
                        try {
                            wordDictionary.addWord(word, owner);
                            successCounter.get(word).incrementAndGet();
                        } catch (KeyAlreadyExistsException ignored) {
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Liberar todos os threads para começarem ao mesmo tempo
        startLatch.countDown();
        executor.shutdown();
        try {
            // Dar tempo suficiente para terminar
            executor.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Validações
        for (String word : words) {
            // Apenas um thread deve ter conseguido registrar a palavra
            assertEquals(1, successCounter.get(word).get(), "Mais de um thread conseguiu registrar a palavra " + word);
            String owner = wordDictionary.search(word);
            assertNotNull(owner, "Palavra não encontrada: " + word);
            assertTrue(owner.startsWith("thread-"), "Owner inesperado: " + owner);
        }
    }
}
