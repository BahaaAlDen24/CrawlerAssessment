package CrawlerAssessment.CrawlerManagement;

import CrawlerAssessment.CacheManagement.CacheManager;
import CrawlerAssessment.UI.Model.Product;
import CrawlerAssessment.UI.Service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The Crawler is the main class in this project
 * Crawler class has :    1 - CacheManager '@Autowired' : Used for store Products after finish extracting and get it again if we call extracting more than one time.
 *                            Note : there was an approach to cache the crawled links but i faced some logical problems made me prefer to crawl all links every time to get changes but without Extracting data while crawling.
 *                                   Crawling without cache , Extracting with cache .
 *
 *                        2 - crawledPages : This attribute will store all crawled pages each node of this Map will contain :
 *                                                1- key : it will be the url .
 *                                                2- value : it will be a document .
 *                        3 - crawledProducts : This attribute will store all Extracted Products each node of this Map will contain :
 *                                                1- key : it will be the url .
 *  *                                             2- value : it will be a product .
 *
 *                        4 -executorService : This is the Thread pool manager  .
 *
 *                        5 -futures : Its a queue for all pages before visit it and extract the links from it   .
 *
 *                        5 -THREAD_COUNT , PAUSE_TIME : Settings for thread manager .
 *
 *
 * This class contain tow main methods  :
 *      1 - startCrawling : this will crawl all the webSite .
 *      2 - startExtracting : this will extract data and store it in the database 'Sqlite' .
 *
 * @author  Bahaa aldeen Hussein
 * @version 1.0
 * @since   2020-11-20
 */



@Service
public class Crawler {

    @Autowired
    CacheManager cacheManager ;

    @Autowired
    private ProductService productService;

    private static Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    public static final int THREAD_COUNT = 10;
    private static final long PAUSE_TIME = 1000;

    private Map<URL, Product> crawledProducts = new HashMap<URL,Product>();
    private Map<URL,Document> crawledPages = new HashMap<URL,Document>();

    private List<Future<CrawledPage>> futures = new ArrayList<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

    private String urlBase;

    /**
     * startUrl : It can be any Url from the website .
     * Example : 1- https://magento-test.finology.com.my/breathe-easy-tank.html
     *           2- https://magento-test.finology.com.my
     *           3- https://magento-test.finology.com.my/women/tops-women/tanks-women.html
     */
    public Map<URL,Document> startCrawling(URL startUrl){
        try {
            // Get the base url of the website .
            urlBase =  "https://" + startUrl.getHost() ;

            LOGGER.info("Start Crawling");
            LOGGER.info("Base Url : " + urlBase);
            LOGGER.info("start Url :" + startUrl.toString());

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            submitNewURL(startUrl, 0);

            while (checkCrawledPages()) ;

            stopWatch.stop();

            cacheManager.putInCache("CachedCrawledPages",crawledPages);

            LOGGER.info("Crawling Finished : Found " + crawledPages.size() + " urls in " + stopWatch.getTime() / 1000 + " seconds");
            return  crawledPages ;

        } catch (Exception e) {
            LOGGER.error("Error Something went wrong  : " + e.getMessage());
            return  null ;
        }
    }

    public Map<URL, Product> startExtracting() {
        try {
            // Get the base url of the website .
            Date CrawlingDate = new Date();

            LOGGER.info("Start Extracting : ");

            crawledProducts = (Map<URL, Product>) cacheManager.getFromCache("crawledProducts");
            if (crawledProducts == null){
                crawledProducts = new HashMap<URL, Product>() ;
            }

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            if (!(crawledPages == null) && crawledPages.size() > 0) {
                crawledPages.forEach((key, document) -> {
                    Element metaTypeTag = document.select("meta[property=og:type]").first();
                    if (!productService.isExist(key.toString())) {
                        if ((metaTypeTag != null) && metaTypeTag.attr("content").equals("product") && key!=null){
                            Product product;
                            if (crawledProducts.containsKey(key)) {
                                product = crawledProducts.get(key);
                                LOGGER.info("Get Product From Cache >> : " + product.toString());
                            } else {
                                product = productService.ExtractProductData(document, key.toString(), CrawlingDate);
                                LOGGER.info("Extracting from Page : " + key.toString() + " >> : " + product.toString());
                            }
                            productService.save(product);
                            crawledProducts.put(key, product);
                        }
                    }else{
                        LOGGER.info("Product Exist in the database : " + key.toString());
                    }
                });

                cacheManager.putInCache("crawledProducts", crawledProducts);
            }
            stopWatch.stop();
            LOGGER.info("Extracting Finished : Found " + crawledProducts.size() + " Products in " + stopWatch.getTime() / 1000 + " seconds");

            return crawledProducts ;

        } catch(Exception e){
            LOGGER.error("Error : " + e.getMessage());
            return  null ;
        }
    }

    private boolean checkCrawledPages(){
        try {
            Thread.sleep(PAUSE_TIME);
            Set<CrawledPage> pageSet = new HashSet<>();
            Iterator<Future<CrawledPage>> iterator = futures.iterator();

            while (iterator.hasNext()) {
                Future<CrawledPage> future = iterator.next();
                if (future.isDone()) {
                    iterator.remove();
                    try {
                        pageSet.add(future.get());
                    } catch (InterruptedException e) {  // skip pages that load too slow
                        LOGGER.error("Error InterruptedException : " + e.getMessage());
                    } catch (ExecutionException e) {
                        LOGGER.error("Error ExecutionException : " + e.getMessage());
                    }
                }
            }

            for (CrawledPage crawledPage : pageSet) {
                crawledPages.replace(crawledPage.getUrl(), crawledPage.getDocument()) ;
                addNewURLs(crawledPage);
            }

            return (futures.size() > 0);

        }catch (Exception e){
            LOGGER.error("Error : " + e.getMessage());;
            return false ;
        }
    }

    private void addNewURLs(CrawledPage crawledPage) {
        try {
            for (URL url : crawledPage.getUrlList()) {
                if (url.toString().contains("#")) {
                    url = new URL(StringUtils.substringBefore(url.toString(), "#"));
                }
                submitNewURL(url, crawledPage.getDepth() + 1);
            }
        } catch (Exception e) {
            LOGGER.error("Error : " + e.getMessage());;
        }
    }

    private void submitNewURL(URL url, int depth) {
        try {
            if (shouldVisit(url, depth)) {
                crawledPages.put(url,null);
                CrawledPage crawledPage = new CrawledPage(url, depth);
                Future<CrawledPage> future = executorService.submit(crawledPage);
                futures.add(future);
            }
        } catch (Exception e) {
            LOGGER.error("Error : " + e.getMessage());;
        }
    }

    /**
     * Redementary visitation filter.
     */
    private boolean shouldVisit(URL url, int depth) {
        try {
            String regex    =   "^(" + urlBase + ").*(html)$";
            if ((!crawledPages.containsKey(url) && ((url.toString().matches(regex)) || url.toString().equals(urlBase)))) {
                return true ;
            }else {
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Error : " + e.getMessage());
            return  false ;
        }
    }

    public Map<URL, Document> getCrawledPages() {
        return crawledPages;
    }

    public Map<URL, Product> getCrawledProducts() {
        return crawledProducts;
    }
}
