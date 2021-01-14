package md.intelectsoft.petrolmpos.network.pe.result;

import java.io.Serializable;

public class AssortmentSerializable implements Serializable {
    private String assortimentID;
    private String assortmentCode;
    private Double discount;
    private String name;
    private Double price;
    private String priceLineID;

    public AssortmentSerializable(String assortimentID, String assortmentCode, Double discount, String name, Double price, String priceLineID) {
        this.assortimentID = assortimentID;
        this.assortmentCode = assortmentCode;
        this.discount = discount;
        this.name = name;
        this.price = price;
        this.priceLineID = priceLineID;
    }

    public String getAssortimentID() {
        return assortimentID;
    }

    public void setAssortimentID(String assortimentID) {
        this.assortimentID = assortimentID;
    }

    public String getAssortmentCode() {
        return assortmentCode;
    }

    public void setAssortmentCode(String assortmentCode) {
        this.assortmentCode = assortmentCode;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPriceLineID() {
        return priceLineID;
    }

    public void setPriceLineID(String priceLineID) {
        this.priceLineID = priceLineID;
    }
}
