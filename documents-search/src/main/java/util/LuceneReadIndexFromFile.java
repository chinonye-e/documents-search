package util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Reads the indexed files and performs search
 * @see <a href= "https://howtodoinjava.com/lucene/lucene-index-and-search-text-files/#demo">https://howtodoinjava.com/lucene/lucene-index-and-search-text-files/#demo</a>
 * @author CE017795
 *
 */
public class LuceneReadIndexFromFile {
    
    public Map<String, Integer> searchIndex(final String searchTerm, final String indexDirectory) throws Exception {
      //Create lucene searcher. It search over a single IndexReader.
        IndexSearcher searcher = createSearcher(indexDirectory);
         
        //Search indexed contents using search term
        TopDocs foundDocs = searchInContent(searchTerm, searcher);
        
        Map<String, Integer> result = new HashMap<>();
        //Let's print out the path of files which have searched term
        for (ScoreDoc sd : foundDocs.scoreDocs) {
            Document d = searcher.doc(sd.doc);
//            System.out.println("Path : "+ d.get("path") + ", Score : " + sd.score);
            final String documentPath = d.get("path");
            final int index = documentPath.lastIndexOf('\\');
            final String fileName = documentPath.substring(index + 1);
            result.put(fileName, Math.round(sd.score));
        }
        return result;
    }
    
    private static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception {
        //Create search query
        QueryParser qp = new QueryParser("contents", new WhitespaceAnalyzer());
        Query query = qp.parse(textToFind);
        //search the index
        return searcher.search(query, 100);
    }
 
    private static IndexSearcher createSearcher(final String indexDirectory) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexDirectory));
         
        //It is an interface for accessing a point-in-time view of a lucene index
        IndexReader reader = DirectoryReader.open(dir);
         
        //Index searcher
        return new IndexSearcher(reader);
    }
}
