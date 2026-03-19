package com.example.autocomplete.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AutocompleteService service;
    private final ResourceLoader resourceLoader;
    private final Random random = new Random();

    public DataInitializer(AutocompleteService service, ResourceLoader resourceLoader) {
        this.service = service;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(String... args) throws Exception {
        // Load the file from src/main/resources/words.txt
        Resource resource = resourceLoader.getResource("classpath:words.txt");

        if (!resource.exists()) {
            System.err.println("File words.txt not found in resources. Skipping initialization.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                if (!word.isEmpty()) {
                    // Generate a random weight between 1.0 and 100.0
                    double weight = 1.0 + (99.0 * random.nextDouble());
                    service.insert(word, weight);
                    count++;
                }
            }
            System.out.println("Trie successfully initialized with " + count + " words from words.txt.");
        }
    }
}
