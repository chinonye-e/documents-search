package util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Indexes the given documents
 * @see <a href= "https://howtodoinjava.com/lucene/lucene-index-and-search-text-files/#demo">https://howtodoinjava.com/lucene/lucene-index-and-search-text-files/#demo</a>
 * @author CE017795
 *
 */
public class LuceneWriteIndexFromFile {
    public void createIndex(final String textsPath, final String indexPath) {
        //Input Path Variable
        final Path docDir = Paths.get(textsPath);
 
        try (Directory dir = FSDirectory.open( Paths.get(indexPath) )) {
            //org.apache.lucene.store.Directory instance
             
            //analyzer with the default stop words
            Analyzer analyzer = new WhitespaceAnalyzer();
             
            //IndexWriter Configuration
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
             
            //IndexWriter writes new index files to the directory
            IndexWriter writer = new IndexWriter(dir, iwc);
             
            //Its recursive method to iterate all files and directories
            indexDocs(writer, docDir);
 
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
     
    void indexDocs(final IndexWriter writer, Path path) throws IOException
    {
        //Directory?
        if (Files.isDirectory(path)) {
            //Iterate directory
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        //Index this file
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    }
                    catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else {
            //Index this file
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }
    }
 
    void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
        try (InputStream stream = Files.newInputStream(file)) {
            //Create lucene Document
            Document doc = new Document();
             
            doc.add(new StringField("path", file.toString(), Field.Store.YES));
            doc.add(new LongPoint("modified", lastModified));
            doc.add(new TextField("contents", new String(Files.readAllBytes(file)), Store.YES));
             
            //Updates a document by first deleting the document(s)
            //containing <code>term</code> and then adding the new
            //document.  The delete and then add are atomic as seen
            //by a reader on the same index
            writer.updateDocument(new Term("path", file.toString()), doc);
        }
    }
}