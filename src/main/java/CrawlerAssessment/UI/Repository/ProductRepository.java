package CrawlerAssessment.UI.Repository;

import CrawlerAssessment.UI.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
    Boolean existsByUrl(String Url); //Checks if there are any records by Url
}
