package CrawlerAssessment.CrawlerManagement;

import CrawlerAssessment.CacheManagement.CacheManager;
import CrawlerAssessment.UI.Service.ProductService;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class CrawlerTest {

    @InjectMocks
    Crawler crawler ;

    @Mock
    CacheManager cacheManager ;

    @Mock
    ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCrawling() throws MalformedURLException {
        Map<URL, Document> result = crawler.startCrawling(new URL("https://magento-test.finology.com.my/breathe-easy-tank.html")) ;
        assertNotNull(result);
    }

}
