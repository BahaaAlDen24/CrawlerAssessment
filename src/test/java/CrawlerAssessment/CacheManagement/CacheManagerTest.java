package CrawlerAssessment.CacheManagement;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.* ;

public class CacheManagerTest {

    CacheManager cacheManager  = new CacheManager();

    @BeforeEach
    void setUp() {
    }

    @Test
    public void testPutInCache() {
        Boolean Result = cacheManager.putInCache("Test",new Object()) ;
        assertTrue(Result);
    }

    @Test
    public void testDeleteFromCache() {
        Boolean Result = cacheManager.deleteFromCache("Test") ;
        assertNotNull(Result);
        assertTrue(Result);
    }

    @Test
    public void testGetNonExistKeyFromCache() {
        Object Result = cacheManager.getFromCache("Test") ;
        assertNull(Result);
    }

    @Test
    public void testGetExistKeyFromCache() {
        String testString = "Test Test Test" ;
        cacheManager.putInCache("Test",testString) ;
        Object Result = cacheManager.getFromCache("Test") ;

        assertEquals(testString,Result);
    }
}
