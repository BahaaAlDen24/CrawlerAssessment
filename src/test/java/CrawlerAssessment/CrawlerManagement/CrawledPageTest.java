package CrawlerAssessment.CrawlerManagement;

import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CrawledPageTest {

    CrawledPage crawledPage = new CrawledPage(new URL("http://magento-test.finology.com.my/breathe-easy-tank.html"),4) ;

    public CrawledPageTest() throws MalformedURLException {
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    public void testCall(){
        CrawledPage result  = crawledPage.call() ;
        assertEquals(crawledPage,result);
    }
}
