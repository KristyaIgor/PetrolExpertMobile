package md.intelectsoft.petrolmpos.network.pe.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssortmentCard {
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
    @SerializedName("AdditionalLimit")
    @Expose
    private Double additionalLimit;
    @SerializedName("CardBalance")
    @Expose
    private Double cardBalance;
    @SerializedName("DailyLimit")
    @Expose
    private Double dailyLimit;
    @SerializedName("DailyLimitConsumed")
    @Expose
    private Double dailyLimitConsumed;
    @SerializedName("Limit")
    @Expose
    private Double limit;
    @SerializedName("MonthlyLimit")
    @Expose
    private Double monthlyLimit;
    @SerializedName("MonthlyLimitConsumed")
    @Expose
    private Double monthlyLimitConsumed;
    @SerializedName("WeeklyLimit")
    @Expose
    private Double weeklyLimit;
    @SerializedName("WeeklyLimitConsumed")
    @Expose
    private Double weeklyLimitConsumed;

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

    public Double getAdditionalLimit() {
        return additionalLimit;
    }

    public void setAdditionalLimit(Double additionalLimit) {
        this.additionalLimit = additionalLimit;
    }

    public Double getCardBalance() {
        return cardBalance;
    }

    public void setCardBalance(Double cardBalance) {
        this.cardBalance = cardBalance;
    }

    public Double getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(Double dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public Double getDailyLimitConsumed() {
        return dailyLimitConsumed;
    }

    public void setDailyLimitConsumed(Double dailyLimitConsumed) {
        this.dailyLimitConsumed = dailyLimitConsumed;
    }

    public Double getLimit() {
        return limit;
    }

    public void setLimit(Double limit) {
        this.limit = limit;
    }

    public Double getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(Double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public Double getMonthlyLimitConsumed() {
        return monthlyLimitConsumed;
    }

    public void setMonthlyLimitConsumed(Double monthlyLimitConsumed) {
        this.monthlyLimitConsumed = monthlyLimitConsumed;
    }

    public Double getWeeklyLimit() {
        return weeklyLimit;
    }

    public void setWeeklyLimit(Double weeklyLimit) {
        this.weeklyLimit = weeklyLimit;
    }

    public Double getWeeklyLimitConsumed() {
        return weeklyLimitConsumed;
    }

    public void setWeeklyLimitConsumed(Double weeklyLimitConsumed) {
        this.weeklyLimitConsumed = weeklyLimitConsumed;
    }
}
