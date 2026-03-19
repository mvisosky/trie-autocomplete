# Weighted Trie Autocomplete Engine

Knowing Redis has an autocomplete feature I wanted to explore how to implement a similar feature as a REST API using Spring Boot and Java 25. This project utilizes a **Trie (Prefix Tree)** data structure with **Top-K Caching** and **Lazy-Loading** to provide instantaneous search suggestions and structural insights.

Word/phrase list is loaded from text file at application startup (yup, I know this is a toy) and assigned random weights.

---

## Key Features

* **Prefix Matching (O(L)):** Search performance is proportional only to the length of the query string, making it extremely fast regardless of dictionary size.
* **Weighted Ranking:** Priorities are based on custom weights; you can "train" the Trie to boost specific terms (e.g., promoted products or trending topics).
* **Dynamic D3.js Visualization:** A "Live-Sync" tree that re-roots and traverses itself in real-time as the user types.
* **Breadcrumb Pathing:** Visualizes the specific character-by-character trajectory through the Trie.
* **Global Trends:** A leaderboard of the highest-weighted terms across the entire data structure.

---

## Build & Run Instructions

### Prerequisites
* **JDK 25**
* **Maven 3.9.14+**
* **Docker** (Optional)

### Option 1: Running with Maven
1.  Ensure your initial word list is located at `src/main/resources/words.txt`.
2.  Run the application from the root directory:
    ```bash
    mvn spring-boot:run
    ```
3.  Access the UI at: `http://localhost:8080`

### Option 2: Running with Docker
The project includes a multi-stage Dockerfile that compiles the code and runs it on a slim JRE 25 Alpine image.
1.  Build the image:
    ```bash
    docker build -t trie-autocomplete .
    ```
2.  Run the container:
    ```bash
    docker run -p 8080:8080 trie-autocomplete
    ```

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/api/search/suggest?q={prefix}` | Returns Top-5 suggestions for a prefix, including their weights. |
| **POST** | `/api/search/train` | Updates or inserts a word/phrase with a specific `double` weight. |
| **GET** | `/api/search/trends` | Returns the Top-10 highest-weighted words globally. |
| **GET** | `/api/search/visualize/lazy?prefix={p}` | Returns only immediate children of the prefix (Optimized for D3). |

---

## Interactive UI

The frontend is a single-page dashboard designed for both users and developers to explore the Trie's behavior.

### 1. Live Search & Breadcrumbs
As you type in the search box, the **Breadcrumb Bar** updates to show your current character path (e.g., `ROOT > A > P > P`). The suggestions dropdown displays results sorted by weight, giving immediate feedback on the ranking logic.

### 2. Live-Sync Visualizer
The D3.js Tree is linked to the search input. As you type, the visualization "walks" down the tree, re-rooting the view to your current prefix.
* **Green Nodes:** Represent a completed word (`isEndOfWord`).
* **White Nodes:** Represent intermediate prefix characters.
* **Interaction:** Click any node to manually expand or collapse its specific sub-branches.

### 3. Global Trends & Training
The sidebar shows the "hottest" terms in the system. Use the **Training Form** to enter a word and a high weight (e.g., `9999`) to see that word instantly jump to the top of the trends and search results.

---

## 📂 Project Structure

* `AutocompleteService.java`: The core engine using `ReentrantReadWriteLock` for thread-safe Trie operations.
* `TrieNode.java`: The recursive data structure holding children and the `topCache` list.
* `DataInitializer.java`: Automatically primes the Trie from `words.txt` on startup.
* `index.html`: The D3.js and Vanilla JS frontend.
