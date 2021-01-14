package md.intelectsoft.petrolmpos.network.pe.result;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetCardInfo {

    @SerializedName("ErrorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("ErrorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("AllowedBalance")
    @Expose
    private Double allowedBalance;
    @SerializedName("Assortiment")
    @Expose
    private List<AssortmentCard> assortiment = null;
    @SerializedName("Balance")
    @Expose
    private Double balance;
    @SerializedName("BlockedAmount")
    @Expose
    private Double blockedAmount;
    @SerializedName("CardEnabled")
    @Expose
    private Boolean cardEnabled;
    @SerializedName("CardName")
    @Expose
    private String cardName;
    @SerializedName("CardNumber")
    @Expose
    private String cardNumber;
    @SerializedName("CustomerEnabled")
    @Expose
    private Boolean customerEnabled;
    @SerializedName("CustomerId")
    @Expose
    private String customerId;
    @SerializedName("CustomerName")
    @Expose
    private String customerName;
    @SerializedName("DailyLimit")
    @Expose
    private Double dailyLimit;
    @SerializedName("DailyLimitConsumed")
    @Expose
    private Double dailyLimitConsumed;
    @SerializedName("LimitType")
    @Expose
    private Integer limitType;
    @SerializedName("MonthlyLimit")
    @Expose
    private Double monthlyLimit;
    @SerializedName("MonthlyLimitConsumed")
    @Expose
    private Double monthlyLimitConsumed;
    @SerializedName("Phone")
    @Expose
    private String phone;
    @SerializedName("RefusedRefillClientAccount")
    @Expose
    private Boolean refusedRefillClientAccount;
    @SerializedName("TankCapacity")
    @Expose
    private Double tankCapacity;
    @SerializedName("WeeklyLimit")
    @Expose
    private Double weeklyLimit;
    @SerializedName("WeeklyLimitConsumed")
    @Expose
    private Double weeklyLimitConsumed;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Double getAllowedBalance() {
        return allowedBalance;
    }

    public void setAllowedBalance(Double allowedBalance) {
        this.allowedBalance = allowedBalance;
    }

    public List<AssortmentCard> getAssortiment() {
        return assortiment;
    }

    public void setAssortiment(List<AssortmentCard> assortiment) {
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
