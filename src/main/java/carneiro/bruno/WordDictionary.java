package carneiro.bruno;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import carneiro.bruno.exception.KeyAlreadyExistsException;

public class WordDictionary {

    private Node root;

    public WordDictionary() {
        this.root = new Node();
    }
    
    public void addWord(String key, String owner) throws KeyAlreadyExistsException {
        Node current = root;
        for (char c : key.toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new Node());
        }

        current.owner.compareAndSet(null, owner);

        if (!current.owner.get().equals(owner)) {
            throw new KeyAlreadyExistsException();
        }
    }
    
    public String search(String word) {
        Node current = root;
        for (char c : word.toCharArray()) {
            current = current.children.get(c);
        }
        return current.owner.get();
    }

    class Node {
        private ConcurrentMap<Character, Node> children;
        private AtomicReference<String> owner;
    
        public Node() {
            this.children = new ConcurrentHashMap<>();
            this.owner = new AtomicReference<>();
        }
    }
}


