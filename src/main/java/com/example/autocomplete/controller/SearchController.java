package com.example.autocomplete.controller;

import com.example.autocomplete.model.*;
import com.example.autocomplete.service.AutocompleteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final AutocompleteService service;

    public SearchController(AutocompleteService service) {
        this.service = service;
    }

    @GetMapping("/suggest")
    public List<Suggestion> suggest(@RequestParam String q) {
        if (q == null || q.isBlank())
            return List.of();
        return service.search(q);
    }

    @PostMapping("/train")
    public ResponseEntity<String> train(@RequestBody TrainRequest request) {
        service.insert(request.word(), request.weight());
        return ResponseEntity.ok("Updated: " + request.word() + " w/ weight " + request.weight());
    }

    @GetMapping("/visualize")
    public TrieVisualNode visualize() {
        return service.getVisualization();
    }

    @GetMapping("/visualize/lazy")
    public List<TrieVisualNode> visualizeLazy(@RequestParam(required = false, defaultValue = "") String prefix) {
        return service.getChildrenForPrefix(prefix);
    }

    @GetMapping("/trends")
    public List<Suggestion> getTrends() {
        return service.getGlobalTrends(10);
    }
}
