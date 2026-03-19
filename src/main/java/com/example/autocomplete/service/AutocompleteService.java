package com.example.autocomplete.service;

import com.example.autocomplete.model.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class AutocompleteService {
    private final TrieNode root = new TrieNode();
    private final Map<String, Double> globalWeights = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final int K = 5;

    public void insert(String word, double weight) {
        lock.writeLock().lock();
        try {
            TrieNode current = root;
            List<TrieNode> path = new ArrayList<>();
            path.add(current);

            String normalized = word.toLowerCase().trim();
            for (char ch : normalized.toCharArray()) {
                current = current.children.computeIfAbsent(ch, k -> new TrieNode());
                path.add(current);
            }
            current.isEndOfWord = true;
            current.weight = weight;

            Suggestion updated = new Suggestion(normalized, weight);
            for (TrieNode node : path) {
                updateCache(node, updated);
            }

            // Track globally for Trends
            globalWeights.put(word.toLowerCase().trim(), weight);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void updateCache(TrieNode node, Suggestion newSug) {
        node.topCache.removeIf(s -> s.word().equals(newSug.word()));
        node.topCache.add(newSug);
        node.topCache.sort((a, b) -> Double.compare(b.weight(), a.weight()));
        if (node.topCache.size() > K)
            node.topCache.remove(K);
    }

    public List<Suggestion> search(String prefix) {
        lock.readLock().lock();
        try {
            TrieNode current = root;
            for (char ch : prefix.toLowerCase().toCharArray()) {
                current = current.children.get(ch);
                if (current == null)
                    return Collections.emptyList();
            }
            // Return the full Suggestion records (word + weight)
            return new ArrayList<>(current.topCache);
        } finally {
            lock.readLock().unlock();
        }
    }

    public TrieVisualNode getVisualization() {
        lock.readLock().lock();
        try {
            return convertToVisual(root, "ROOT");
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<TrieVisualNode> getChildrenForPrefix(String prefix) {
        lock.readLock().lock();
        try {
            TrieNode current = root;
            if (!prefix.isEmpty()) {
                for (char ch : prefix.toLowerCase().toCharArray()) {
                    current = current.children.get(ch);
                    if (current == null)
                        return Collections.emptyList();
                }
            }

            // Return only the immediate children, not the whole subtree
            return current.children.entrySet().stream()
                    .map(e -> new TrieVisualNode(
                            String.valueOf(e.getKey()),
                            e.getValue().weight,
                            e.getValue().isEndOfWord,
                            null // Children are null until requested
                    ))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Suggestion> getGlobalTrends(int limit) {
        return globalWeights.entrySet().stream()
                .map(e -> new Suggestion(e.getKey(), e.getValue()))
                .sorted((a, b) -> Double.compare(b.weight(), a.weight()))
                .limit(limit)
                .toList();
    }

    private TrieVisualNode convertToVisual(TrieNode node, String label) {
        List<TrieVisualNode> children = node.children.entrySet().stream()
                .map(e -> convertToVisual(e.getValue(), String.valueOf(e.getKey())))
                .toList();
        return new TrieVisualNode(label, node.weight, node.isEndOfWord, children);
    }
}
