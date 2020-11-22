package CrawlerAssessment.UI.Controller;

import CrawlerAssessment.CrawlerManagement.Crawler;
import CrawlerAssessment.UI.Model.Product;
import CrawlerAssessment.UI.Service.ProductService;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private Crawler crawler ;

    // return all Products in the system
    @GetMapping("/Products")
    List<Product> all() {
        try {
            return productService.all();
        }catch (Exception e ){
            return null ;

        }
    }

    @GetMapping("/Crawl")
    public Set<URL> Crawl() throws IOException, InterruptedException {
        Map<URL, Document> crawledPages = crawler.startCrawling(new URL("https://magento-test.finology.com.my/circe-hooded-ice-fleece.html"));
        return  crawledPages.keySet() ;
    }

    @GetMapping("/Extract")
    public Collection<Product> Extract() throws IOException, InterruptedException {
        Map<URL, Product> crawledProducts = crawler.startExtracting();
        return  crawledProducts.values();
    }
}
