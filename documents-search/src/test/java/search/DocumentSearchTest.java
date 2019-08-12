package search;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class DocumentSearchTest {
    
    private static final DocumentSearch documentSearch = new DocumentSearch();
    private static final String TEXTS_DIRECTORY_TEST = "./src/test/resources/sampleTexts/";
    private static final String INDEX_DIRECTORY_TEST = "./src/test/resources/indexedFiles/";
    
    @Test
    public void testSimpleStringMatching() throws Exception {
        
        // modify texts directory
        Field directoryField = DocumentSearch.class.getDeclaredField("TEXTS_DIRECTORY");
        directoryField.setAccessible(true);
        
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(directoryField, directoryField.getModifiers() & ~Modifier.FINAL);
        
        directoryField.set(documentSearch, TEXTS_DIRECTORY_TEST);
        
        // modify index directory
        Field indexDirectoryField = Field.class.getDeclaredField("INDEX_DIRECTORY_TEST");
        indexDirectoryField.setAccessible(true);
        
        modifiersField.setInt(indexDirectoryField, indexDirectoryField.getModifiers() & ~Modifier.FINAL);
        
        indexDirectoryField.set(documentSearch, INDEX_DIRECTORY_TEST);
        
        Map<String, String> documentsByName = documentSearch.readFileAsString();
        Map<String, Integer> result = documentSearch.searchDocument("warp", "1", documentsByName);
        assertEquals(3, result.size());
    }
}
