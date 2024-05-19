import java.util.List;

public class Main {

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
}
