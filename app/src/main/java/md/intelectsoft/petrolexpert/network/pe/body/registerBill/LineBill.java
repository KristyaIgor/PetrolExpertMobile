package md.intelectsoft.petrolexpert.network.pe.body.registerBill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class LineBill extends RealmObject {
    @SerializedName("Count")
    @Expose
    private Double count;
    @SerializedName("DiscountedPrice")
    @Expose
    private Double discountedPrice;
    @SerializedName("DiscountedSum")
    @Expose
    private Double discountedSum;
    @SerializedName("NomenclatureCode")
    @Expose
    private String nomenclatureCode;
    @SerializedName("Price")
    @Expose
    private Double price;
    @SerializedName("Sum")
    @Expose
    private Double sum;

    private String name;

    private double vatPercent;

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public Double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(Double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public Double getDiscountedSum() {
        return discountedSum;
    }

    public void setDiscountedSum(Double discountedSum) {
        this.discountedSum = discountedSum;
    }

    public String getNomenclatureCode() {
        return nomenclatureCode;
    }

    public void setNomenclatureCode(String nomenclatureCode) {
        this.nomenclatureCode = nomenclatureCode;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getVatPercent() {
        return vatPercent;
    }

    public void setVatPercent(double vatPercent) {
        this.vatPercent = vatPercent;
    }
}
