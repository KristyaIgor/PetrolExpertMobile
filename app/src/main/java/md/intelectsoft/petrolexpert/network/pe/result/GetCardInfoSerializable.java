package md.intelectsoft.petrolexpert.network.pe.result;

import java.io.Serializable;
import java.util.List;

public class GetCardInfoSerializable implements Serializable {
    private Double allowedBalance;
    private List<AssortmentCardSerializable> assortiment = null;
    private Double balance;
    private Double blockedAmount;
    private Boolean cardEnabled;
    private String cardName;
    private String cardNumber;
    private Boolean customerEnabled;
    private String customerId;
    private String customerName;
    private Double dailyLimit;
    private Double dailyLimitConsumed;
    private Integer limitType;
    private Double monthlyLimit;
    private Double monthlyLimitConsumed;
    private String phone;
    private Boolean refusedRefillClientAccount;
    private Double tankCapacity;
    private Double weeklyLimit;
    private Double weeklyLimitConsumed;

    public GetCardInfoSerializable(
            Double allowedBalance,
            List<AssortmentCardSerializable> assortiment,
            Double balance,
            Double blockedAmount,
            Boolean cardEnabled,
            String cardName,
            String cardNumber,
            Boolean customerEnabled,
            String customerId,
            String customerName,
            Double dailyLimit,
            Double dailyLimitConsumed,
            Integer limitType,
            Double monthlyLimit,
            Double monthlyLimitConsumed,
            String phone,
            Boolean refusedRefillClientAccount,
            Double tankCapacity,
            Double weeklyLimit,
            Double weeklyLimitConsumed) {

        this.allowedBalance = allowedBalance;
        this.assortiment = assortiment;
        this.balance = balance;
        this.blockedAmount = blockedAmount;
        this.cardEnabled = cardEnabled;
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.customerEnabled = customerEnabled;
        this.customerId = customerId;
        this.customerName = customerName;
        this.dailyLimit = dailyLimit;
        this.dailyLimitConsumed = dailyLimitConsumed;
        this.limitType = limitType;
        this.monthlyLimit = monthlyLimit;
        this.monthlyLimitConsumed = monthlyLimitConsumed;
        this.phone = phone;
        this.refusedRefillClientAccount = refusedRefillClientAccount;
        this.tankCapacity = tankCapacity;
        this.weeklyLimit = weeklyLimit;
        this.weeklyLimitConsumed = weeklyLimitConsumed;
    }

    public Double getAllowedBalance() {
        return allowedBalance;
    }

    public void setAllowedBalance(Double allowedBalance) {
        this.allowedBalance = allowedBalance;
    }

    public List<AssortmentCardSerializable> getAssortiment() {
        return assortiment;
    }

    public void setAssortiment(List<AssortmentCardSerializable> assortiment) {
        this.assortiment = assortiment;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getBlockedAmount() {
        return blockedAmount;
    }

    public void setBlockedAmount(Double blockedAmount) {
        this.blockedAmount = blockedAmount;
    }

    public Boolean getCardEnabled() {
        return cardEnabled;
    }

    public void setCardEnabled(Boolean cardEnabled) {
        this.cardEnabled = cardEnabled;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Boolean getCustomerEnabled() {
        return customerEnabled;
    }

    public void setCustomerEnabled(Boolean customerEnabled) {
        this.customerEnabled = customerEnabled;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public Integer getLimitType() {
        return limitType;
    }

    public void setLimitType(Integer limitType) {
        this.limitType = limitType;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getRefusedRefillClientAccount() {
        return refusedRefillClientAccount;
    }

    public void setRefusedRefillClientAccount(Boolean refusedRefillClientAccount) {
        this.refusedRefillClientAccount = refusedRefillClientAccount;
    }

    public Double getTankCapacity() {
        return tankCapacity;
    }

    public void setTankCapacity(Double tankCapacity) {
        this.tankCapacity = tankCapacity;
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
