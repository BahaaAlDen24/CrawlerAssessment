package CrawlerAssessment.CrawlerManagement;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * The CrawledPage is one of tow main classes in this project
 * Each CrawledPage has : 1 - Url : the link of the page .
 *                        2 - depth : its says in which depth this page exist .
 *                        3 - document : this attribute will store the page (Html ,javasecripts ,css .. ect) .
 *                        4 - urlList : Store all links that found in the page .
 *
 * This class implements Callable and override the 'call' method
 * that's because we are using Threading to improve the performance of our crawler .
 *
 * Dealing with page document done by using Jsoup .
 *
 * It contains a Map<String,Object> to store objects in the memory with a specific key
 *
 * @author  Bahaa aldeen Hussein
 * @version 1.0
 * @since   2020-11-20
 */
public class CrawledPage implements Callable<CrawledPage> {

    private static Logger LOGGER = LoggerFactory.getLogger(CrawledPage.class);

    static final int TIMEOUT = 60000;   // one minute
    private URL url;
    private int depth;
    private Document document ;
    private Set<URL> urlList = new HashSet<>();

    public CrawledPage(URL url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    /**
     * This is the main method which parse the url using Jsoup and get all linkes inside the page .
     * @param
     * @return Current object and null if any exeption happend.
     * @exception IOException On input error.
     * @see IOException
     */
    @Override
    public CrawledPage call() {
        try {
            Document document = null;
            LOGGER.info("Visiting >> " + url.toString());
            this.document = Jsoup.parse(url, TIMEOUT);
            processLinks(this.document.select("a[href]"));
            return this;
        } catch (IOException e) {
            LOGGER.error("Error : " + e.getMessage());;
            return null ;
        }
    }

    /**
     * This method get all links and add it to the Links list  .
     * @param links all links inside the main page .
     * @return boolean .
     * @exception MalformedURLException On input error.
     * @see MalformedURLException
     */
    public boolean processLinks(Elements links) {
        for (Element link : links) {
            String href = link.attr("href");
            if (StringUtils.isBlank(href) ||  href.startsWith("#")) {
                continue;
            }
            try {
                URL nextUrl = new URL(url, href);
                urlList.add(nextUrl);
            } catch (MalformedURLException e) { // ignore bad urls
                LOGGER.error("Error : " + e.getMessage());
            }
        }
        LOGGER.info("Finished Links Processing >> " + this.url + " : " +  this.getUrlList().size() + " Links Found");
        return true ;
    }

    public Set<URL> getUrlList() {
        return urlList;
    }

    public int getDepth() {
        return depth;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public URL getUrl() {
        return url;
    }
}
