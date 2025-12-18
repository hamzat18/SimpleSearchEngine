import java.util.*;
import java.util.regex.*;

// map words -> documents -> frequency count
public class InvertedIndex {
    private Map<String, Map<String, Integer>> index = new HashMap<>();
    private Map<String, String> contentStore = new HashMap<>();

    // Add a document to the index and process the text
    public void addDocument(String fileName, String content) {
        contentStore.put(fileName, content);
        // clean text
        String[] words = content.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split("\\s+");
        for (String word : words) {
            if (word.length() < 2)
                continue;
            index.putIfAbsent(word, new HashMap<>());
            // increase the count for that word
            index.get(word).put(fileName, index.get(word).getOrDefault(fileName, 0) + 1);
        }
    }

    // searh words method
    public List<Map<String, Object>> search(String query) {
        String term = query.toLowerCase().trim().split("\\s+")[0];
        List<Map<String, Object>> results = new ArrayList<>();
        Map<String, Integer> candidates = index.getOrDefault(term, Collections.emptyMap());

        for (String docName : candidates.keySet()) {
            List<String> snippets = findSnippets(docName, term);
            if (!snippets.isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put("docName", docName);
                data.put("frequency", snippets.size());
                data.put("snippet", formatAsHtmlList(snippets)); // convert the snippit to html
                results.add(data);
            }
        }
        // sorts result so higher frequency words appear first
        results.sort((a, b) -> (int) b.get("frequency") - (int) a.get("frequency"));
        return results;
    }

    // preview/snippit method
    private List<String> findSnippets(String docName, String term) {
        String text = contentStore.get(docName);
        if (text == null)
            return Collections.emptyList();
        List<String> snippets = new ArrayList<>();

        String regex = Pattern.quote(term);
        Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text);
        // get text around the search word
        while (matcher.find()) {
            int start = Math.max(0, matcher.start() - 30);
            int end = Math.min(text.length(), matcher.end() + 30);
            String chunk = text.substring(start, end).replace("\n", " ").trim();
            snippets.add(chunk);
        }
        return snippets;
    }

    // convert snippit to html ordered list
    private String formatAsHtmlList(List<String> snippets) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ol style='margin: 8px 0 0 20px; color: #555; padding-left: 10px;'>");
        for (String s : snippets) {
            sb.append("<li style='margin-bottom: 6px;'>");
            sb.append("...").append(s).append("...");
            sb.append("</li>");
        }
        sb.append("</ol>");
        return sb.toString();
    }
}