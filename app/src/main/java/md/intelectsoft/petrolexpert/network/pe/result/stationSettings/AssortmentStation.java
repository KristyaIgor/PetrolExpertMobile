package md.intelectsoft.petrolexpert.network.pe.result.stationSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssortmentStation {
    @SerializedName("AssortimentID")
    @Expose
    private String assortimentID;
    @SerializedName("AssortmentCode")
    @Expose
    private String assortmentCode;
    @SerializedName("Discount")
    @Expose
    private Double discount;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Price")
    @Expose
    private Double price;
    @SerializedName("PriceLineID")
    @Expose
    private String priceLineID;
    @SerializedName("VATPercent")
    @Expose
    private Double vATPercent;

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

    public Double getVATPercent() {
        return vATPercent;
    }

    public void setVATPercent(Double vATPercent) {
        this.vATPercent = vATPercent;
    }
}
