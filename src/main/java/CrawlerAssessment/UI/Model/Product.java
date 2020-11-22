package CrawlerAssessment.UI.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
public class Product {

    @Id
    @GeneratedValue
    private Integer id ;
    private String url ;
    private String name ;
    private String price ;
    @Column(length=2000)
    private String description ;
    @Column(length=2000)
    private String extraInformation ;
    private Date lastUpdate ;

    public Product() {
    }

    public Product(String name) {
        this.name = name;
    }

    public Product(Integer id, String url, String name, String price, String description, String extraInformation, Date lastUpdate) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.price = price;
        this.description = description;
        this.extraInformation = extraInformation;
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) &&
                Objects.equals(url, product.url) &&
                Objects.equals(name, product.name) &&
                Objects.equals(price, product.price) &&
                Objects.equals(description, product.description) &&
                Objects.equals(extraInformation, product.extraInformation) &&
                Objects.equals(lastUpdate, product.lastUpdate);
    }

    @Override
    public String toString() {
        return "Product{" +
                "  name: '" + name + '\'' +
                ", price: '" + price + '\'' +
                ", description: '" + description + '\'' +
                ", extraInformation: '" + extraInformation + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, name, price, description, extraInformation, lastUpdate);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtraInformation() {
        return extraInformation;
    }

    public void setExtraInformation(String extraInformation) {
        this.extraInformation = extraInformation;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
