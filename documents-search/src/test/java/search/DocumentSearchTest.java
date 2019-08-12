package search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DocumentSearchTest {
    
    private static final DocumentSearch DOCUMENT_SEARCH = new DocumentSearch();
    private static final String TEXTS_DIRECTORY_TEST = "./src/test/resources/sampleTexts/";
    private static final String INDEX_DIRECTORY_TEST = "./src/test/resources/indexedFiles/";
    private static Map<String, String> documentsByName;
    
    @BeforeAll
    public static void beforeAll() throws Exception {
     // modify texts directory
        Field directoryField = DocumentSearch.class.getDeclaredField("TEXTS_DIRECTORY");
        directoryField.setAccessible(true);
        
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(directoryField, directoryField.getModifiers() & ~Modifier.FINAL);
        
        directoryField.set(DOCUMENT_SEARCH, TEXTS_DIRECTORY_TEST);
        
        // modify index directory
        Field indexDirectoryField = DocumentSearch.class.getDeclaredField("INDEX_DIRECTORY");
        indexDirectoryField.setAccessible(true);
        
        modifiersField.setInt(indexDirectoryField, indexDirectoryField.getModifiers() & ~Modifier.FINAL);
        
        indexDirectoryField.set(DOCUMENT_SEARCH, INDEX_DIRECTORY_TEST);
        documentsByName = DOCUMENT_SEARCH.readFileAsString();
    }
    
    @Test
    public void testSimpleStringMatching() throws Exception {
        Map<String, Integer> result = DOCUMENT_SEARCH.searchDocument("warp", "1", documentsByName);
        assertEquals(3, result.size());
        Iterator<Entry<String, Integer>> iterator = result.entrySet().iterator();
        Entry<String, Integer> entry = iterator.next();
        assertTrue("warp_drive.txt".equals(entry.getKey()));
        assertEquals(6, entry.getValue().intValue());
        
        entry = iterator.next();
        assertTrue("hitchhikers.txt".equals(entry.getKey()));
        assertEquals(0, entry.getValue().intValue());
        
        entry = iterator.next();
        assertTrue("french_armed_forces.txt".equals(entry.getKey()));
        assertEquals(0, entry.getValue().intValue());
        
        // test a different term
        result = DOCUMENT_SEARCH.searchDocument(" and ", "2", documentsByName);
        assertEquals(3, result.size());
        iterator = result.entrySet().iterator();
        entry = iterator.next();
        assertTrue("french_armed_forces.txt".equals(entry.getKey()));
        assertEquals(27, entry.getValue().intValue());
        
        entry = iterator.next();
        assertTrue("hitchhikers.txt".equals(entry.getKey()));
        assertEquals(11, entry.getValue().intValue());
        
        entry = iterator.next();
        assertTrue("warp_drive.txt".equals(entry.getKey()));
        assertEquals(3, entry.getValue().intValue());
    }
    
    @Test
    public void testRegexStringMatching() throws Exception {
        Map<String, Integer> result = DOCUMENT_SEARCH.searchDocument("warp", "2", documentsByName);
        assertEquals(3, result.size());
        Iterator<Entry<String, Integer>> iterator = result.entrySet().iterator();
        Entry<String, Integer> entry = iterator.next();
        assertTrue("warp_drive.txt".equals(entry.getKey()));
        assertEquals(6, entry.getValue().intValue());
        
        entry = iterator.next();
        assertTrue("hitchhikers.txt".equals(entry.getKey()));
        assertEquals(0, entry.getValue().intValue());
        
        entry = iterator.next();
        assertTrue("french_armed_forces.txt".equals(entry.getKey()));
        assertEquals(0, entry.getValue().intValue());
        
        // test a different term
        result = DOCUMENT_SEARCH.searchDocument(" and ", "2", documentsByName);
        assertEquals(3, result.size());
        iterator = result.entrySet().iterator();
        entry = iterator.next();
        assertTrue("french_armed_forces.txt".equals(entry.getKey()));
        assertEquals(27, entry.getValue().intValue());
        
        entry = iterator.next();
        assertTrue("hitchhikers.txt".equals(entry.getKey()));
        assertEquals(11, entry.getValue().intValue());
        
        entry = iterator.next();
        assertTrue("warp_drive.txt".equals(entry.getKey()));
        assertEquals(3, entry.getValue().intValue());
    }
    
    @Test
    public void testIndexStringMatching() throws Exception {
        Map<String, Integer> result = DOCUMENT_SEARCH.searchDocument("warp", "3", documentsByName);
        assertEquals(3, result.size());
        Iterator<Entry<String, Integer>> iterator = result.entrySet().iterator();
        Entry<String, Integer> entry = iterator.next();
        assertTrue("warp_drive.txt".equals(entry.getKey()));
        
        entry = iterator.next();
        assertTrue("hitchhikers.txt".equals(entry.getKey()));
        
        entry = iterator.next();
        assertTrue("french_armed_forces.txt".equals(entry.getKey()));
        
        // test a different term
        result = DOCUMENT_SEARCH.searchDocument(" and ", "3", documentsByName);
        assertEquals(3, result.size());
        iterator = result.entrySet().iterator();
        entry = iterator.next();
        assertTrue("hitchhikers.txt".equals(entry.getKey()));
        
        entry = iterator.next();
        assertTrue("french_armed_forces.txt".equals(entry.getKey()));
        
        entry = iterator.next();
        assertTrue("warp_drive.txt".equals(entry.getKey()));
    }
    
    @Test
    public void testLongSearchTerm_simple() throws Exception {
        Map<String, Integer> result = DOCUMENT_SEARCH.searchDocument("paved the way for European integration", "1", documentsByName);
        assertEquals(3, result.size());
        Iterator<Entry<String, Integer>> iterator = result.entrySet().iterator();
        Entry<String, Integer> entry = iterator.next();
        assertTrue("french_armed_forces.txt".equals(entry.getKey()));
        assertEquals(1, entry.getValue().intValue());
        
        entry = iterator.next();
        assertTrue("hitchhikers.txt".equals(entry.getKey()));
        assertEquals(0, entry.getValue().intValue());
        
        entry = iterator.next();
        assertTrue("warp_drive.txt".equals(entry.getKey()));
        assertEquals(0, entry.getValue().intValue());
    }
    
    @Test
    public void testLongSearchTerm_regex() throws Exception {
        Map<String, Integer> result = DOCUMENT_SEARCH.searchDocument("paved the way for European integration", "2", documentsByName);
        assertEquals(3, result.size());
        Iterator<Entry<String, Integer>> iterator = result.entrySet().iterator();
        Entry<String, Integer> entry = iterator.next();
        assertTrue("french_armed_forces.txt".equals(entry.getKey()));
        assertEquals(1, entry.getValue().intValue());
        
        entry = iterator.next();
        assertTrue("hitchhikers.txt".equals(entry.getKey()));
        assertEquals(0, entry.getValue().intValue());
        
        entry = iterator.next();
        assertTrue("warp_drive.txt".equals(entry.getKey()));
        assertEquals(0, entry.getValue().intValue());
    }
    
    @Test
    public void testLongSearchTerm_index() throws Exception {
        Map<String, Integer> result = DOCUMENT_SEARCH.searchDocument("paved the way for European integration", "3", documentsByName);
        assertEquals(3, result.size());
        Iterator<Entry<String, Integer>> iterator = result.entrySet().iterator();
        Entry<String, Integer> entry = iterator.next();
        assertTrue("french_armed_forces.txt".equals(entry.getKey()));
        
        entry = iterator.next();
        assertTrue("hitchhikers.txt".equals(entry.getKey()));
        
        entry = iterator.next();
        assertTrue("warp_drive.txt".equals(entry.getKey()));
    }
    
    @Test
    public void testInvalidSearchMethod() throws Exception {
        assertNull(DOCUMENT_SEARCH.searchDocument("warp", "4", documentsByName));
    }
}
