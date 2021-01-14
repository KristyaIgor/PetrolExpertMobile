package md.intelectsoft.petrolmpos.network.pe.result;

import java.io.Serializable;

public class AssortmentCardSerializable implements Serializable {
    private String assortimentID;
    private String assortmentCode;
    private Double discount;
    private String name;
    private Double price;
    private String priceLineID;
    private Double additionalLimit;
    private Double cardBalance;
    private Double dailyLimit;
    private Double dailyLimitConsumed;
    private Double limit;
    private Double monthlyLimit;
    private Double monthlyLimitConsumed;
    private Double weeklyLimit;
    private Double weeklyLimitConsumed;

    public AssortmentCardSerializable(
            String assortimentID,
            String assortmentCode,
            Double discount,
            String name,
            Double price,
            String priceLineID,
            Double additionalLimit,
            Double cardBalance,
            Double dailyLimit,
            Double dailyLimitConsumed,
            Double limit,
            Double monthlyLimit,
            Double monthlyLimitConsumed,
            Double weeklyLimit,
            Double weeklyLimitConsumed) {

        this.assortimentID = assortimentID;
        this.assortmentCode = assortmentCode;
        this.discount = discount;
        this.name = name;
        this.price = price;
        this.priceLineID = priceLineID;
        this.additionalLimit = additionalLimit;
        this.cardBalance = cardBalance;
        this.dailyLimit = dailyLimit;
        this.dailyLimitConsumed = dailyLimitConsumed;
        this.limit = limit;
        this.monthlyLimit = monthlyLimit;
        this.monthlyLimitConsumed = monthlyLimitConsumed;
        this.weeklyLimit = weeklyLimit;
        this.weeklyLimitConsumed = weeklyLimitConsumed;
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
