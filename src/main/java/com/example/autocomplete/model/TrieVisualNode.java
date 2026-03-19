package com.example.autocomplete.model;

import java.util.List;

public record TrieVisualNode(String character, double weight, boolean isWord, List<TrieVisualNode> children) {
}
