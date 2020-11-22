package CrawlerAssessment.UI.Service;

import CrawlerAssessment.UI.Repository.ProductRepository;
import CrawlerAssessment.UI.Model.Product;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    private static Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> all() {
        try {
            return repository.findAll();
        }catch (Exception e ){
            return null ;
        }
    }

    public Object save(Product newClient) {
        try {
            return repository.save(newClient);
        }catch (Exception e ){
            return e.getMessage() ;
        }
    }

    public boolean isExist(String Url) {
        try {
            return repository.existsByUrl(Url);
        }catch (Exception e){
            return false ;
        }
    }

    public  Product ExtractProductData(Document Document, String URL, Date CrawlingDate){
        Product NewProduct = new Product() ;
        try {
            Elements ProductMainInfo = Document.getElementsByClass("product-info-main");
            Elements ProductDescription = Document.getElementsByClass("product attribute description").select("div[class=value]");
            Elements ProductExtraInfo = Document.getElementById("product-attribute-specs-table").select("tr");

            NewProduct.setUrl(URL);
            NewProduct.setLastUpdate(CrawlingDate);
            NewProduct.setName(ProductMainInfo.select("span[itemprop=name]").first().text());
            NewProduct.setPrice(ProductMainInfo.select("span[class=price]").first().text());
            NewProduct.setDescription(ProductDescription.text());

            StringBuilder ExtraInformation = new StringBuilder("");

            for (Element tr : ProductExtraInfo){
                ExtraInformation.append(" ").
                        append(tr.select("th").text()).
                        append(": ").
                        append(tr.select("td").text()).
                        append("|");
            }
            if (ExtraInformation.length() > 0) {
                ExtraInformation.deleteCharAt(ExtraInformation.length() - 1); // remove last | ;
            }
            NewProduct.setExtraInformation(ExtraInformation.toString());

            return NewProduct ;
        }catch (Exception e) {
            LOGGER.error("Error while extracting product data : " + NewProduct.getUrl());
            return NewProduct ;
        }
    }
}
