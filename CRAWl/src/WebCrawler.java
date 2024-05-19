import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class WebCrawler {

    private static final int MAX_DEPTH = 3;
    private Set<String> visitedUrls = new HashSet<>();
    private Map<String, Map<String, Integer>> invertedIndex = new HashMap<>();

    public static void main(String[] args) {
        WebCrawler crawler = new WebCrawler();
        crawler.crawl("https://en.wikipedia.org/wiki/List_of_pharaohs", 0);
        crawler.buildInvertedIndex();

        // Example query
        String query = "ancient Egyptian pharaohs";
        List<String> topDocuments = crawler.rankDocuments(query, 10);
        System.out.println("Top 10 Documents:");
        for (String doc : topDocuments) {
            System.out.println(doc);
        }
    }

    public void crawl(String url, int depth) {
        if (depth > MAX_DEPTH) {
            return;
        }

        if (!visitedUrls.contains(url)) {
            try {
                System.out.println("Crawling: " + url);
                Document doc = Jsoup.connect(url).get();
                visitedUrls.add(url);

                // Process the page here
                // For simplicity, let's just extract text and build inverted index
                String text = doc.text().toLowerCase();
                String[] words = text.split("\\W+");
                Map<String, Integer> wordFreqMap = new HashMap<>();
                for (String word : words) {
                    wordFreqMap.put(word, wordFreqMap.getOrDefault(word, 0) + 1);
                }
                invertedIndex.put(url, wordFreqMap);

                // Get all links on the page
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String nextUrl = link.absUrl("href");
                    if (nextUrl.startsWith("https://en.wikipedia.org/") && !visitedUrls.contains(nextUrl)) {
                        crawl(nextUrl, depth + 1);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error while fetching URL: " + url);
            }
        }
    }

    public void buildInvertedIndex() {
        for (String url : visitedUrls) {
            Map<String, Integer> wordFreqMap = invertedIndex.get(url);
            for (String word : wordFreqMap.keySet()) {
                Map<String, Integer> docFreqMap = invertedIndex.computeIfAbsent(word, k -> new HashMap<>());
                docFreqMap.put(url, wordFreqMap.get(word));
            }
        }
    }

    public List<String> rankDocuments(String query, int k) {
        String[] terms = query.toLowerCase().split("\\W+");
        Map<String, Double> documentScores = new HashMap<>();
        for (String term : terms) {
            Map<String, Integer> termFreqMap = invertedIndex.getOrDefault(term, new HashMap<>());
            double idf = Math.log10((double) visitedUrls.size() / termFreqMap.size());
            for (String url : termFreqMap.keySet()) {
                double tfidf = (1 + Math.log10((double) termFreqMap.get(url))) * idf;
                documentScores.put(url, documentScores.getOrDefault(url, 0.0) + tfidf);
            }
        }

        // Normalize scores by document length
        for (String url : documentScores.keySet()) {
            double docLength = Math.sqrt(invertedIndex.get(url).values().stream().mapToInt(Integer::intValue).sum());
            documentScores.put(url, documentScores.get(url) / docLength);
        }

        // Sort documents by score
        List<String> topDocuments = new ArrayList<>(documentScores.keySet());
        topDocuments.sort((a, b) -> Double.compare(documentScores.get(b), documentScores.get(a)));

        // Return top k documents
        return topDocuments.subList(0, Math.min(k, topDocuments.size()));
    }
}
