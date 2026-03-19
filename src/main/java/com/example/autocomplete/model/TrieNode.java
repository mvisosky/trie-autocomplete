package com.example.autocomplete.model;

import java.util.*;

public class TrieNode {
    public Map<Character, TrieNode> children = new HashMap<>();
    public List<Suggestion> topCache = new ArrayList<>();
    public double weight = 0.0;
    public boolean isEndOfWord = false;
}
