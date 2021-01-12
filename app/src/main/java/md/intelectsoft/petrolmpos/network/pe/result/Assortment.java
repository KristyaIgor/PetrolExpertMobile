package md.intelectsoft.petrolmpos.network.pe.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Assortment {
    @SerializedName("Count")
    @Expose
    private Double count;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Price")
    @Expose
    private Double price;
    @SerializedName("PriceLineID")
    @Expose
    private String priceLineID;

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
