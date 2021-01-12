package md.intelectsoft.petrolmpos.network.pe.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AssortmentSerializable implements Serializable {
    private Double count;
    private String name;
    private Double price;
    private String priceLineID;

    public AssortmentSerializable(Double count, String name, Double price, String priceLineID) {
        this.count = count;
        this.name = name;
        this.price = price;
        this.priceLineID = priceLineID;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
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
