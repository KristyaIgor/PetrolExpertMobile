package md.intelectsoft.petrolmpos.network.pe.result;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAssortment {

    @SerializedName("ErrorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("NoError")
    @Expose
    private Boolean noError;
    @SerializedName("AssortmentList")
    @Expose
    private List<Assortment> assortmentList = null;
    @SerializedName("Balanta")
    @Expose
    private Double balanta;
    @SerializedName("ClientAmount")
    @Expose
    private Double clientAmount;
    @SerializedName("ClientName")
    @Expose
    private String clientName;
    @SerializedName("Credit")
    @Expose
    private Double credit;
    @SerializedName("LimitDay")
    @Expose
    private Double limitDay;
    @SerializedName("LimitMount")
    @Expose
    private Double limitMount;
    @SerializedName("LimitType")
    @Expose
    private String limitType;
    @SerializedName("PIN")
    @Expose
    private Integer pIN;
    @SerializedName("WeeklyLimit")
    @Expose
    private Double weeklyLimit;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getNoError() {
        return noError;
    }

    public void setNoError(Boolean noError) {
        this.noError = noError;
    }

    public List<Assortment> getAssortmentList() {
        return assortmentList;
    }

    public void setAssortmentList(List<Assortment> assortmentList) {
        this.assortmentList = assortmentList;
    }

    public Double getBalanta() {
        return balanta;
    }

    public void setBalanta(Double balanta) {
        this.balanta = balanta;
    }

    public Double getClientAmount() {
        return clientAmount;
    }

    public void setClientAmount(Double clientAmount) {
        this.clientAmount = clientAmount;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getLimitDay() {
        return limitDay;
    }

    public void setLimitDay(Double limitDay) {
        this.limitDay = limitDay;
    }

    public Double getLimitMount() {
        return limitMount;
    }

    public void setLimitMount(Double limitMount) {
        this.limitMount = limitMount;
    }

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public Integer getPIN() {
        return pIN;
    }

    public void setPIN(Integer pIN) {
        this.pIN = pIN;
    }

    public Double getWeeklyLimit() {
        return weeklyLimit;
    }

    public void setWeeklyLimit(Double weeklyLimit) {
        this.weeklyLimit = weeklyLimit;
    }
}
