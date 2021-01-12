package md.intelectsoft.petrolmpos.network.pe.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetAssortmentSerializable implements Serializable {
    private List<AssortmentSerializable> assortmentList = null;
    private Double balanta;
    private Double clientAmount;
    private String clientName;
    private Double credit;
    private Double limitDay;
    private Double limitMount;
    private String limitType;
    private Integer pIN;
    private Double weeklyLimit;

    public GetAssortmentSerializable(List<AssortmentSerializable> assortmentList, Double balanta, Double clientAmount, String clientName, Double credit, Double limitDay, Double limitMount, String limitType, Double weeklyLimit) {
        this.assortmentList = assortmentList;
        this.balanta = balanta;
        this.clientAmount = clientAmount;
        this.clientName = clientName;
        this.credit = credit;
        this.limitDay = limitDay;
        this.limitMount = limitMount;
        this.limitType = limitType;
        this.weeklyLimit = weeklyLimit;
    }

    public List<AssortmentSerializable> getAssortmentList() {
        return assortmentList;
    }

    public void setAssortmentList(List<AssortmentSerializable> assortmentList) {
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
