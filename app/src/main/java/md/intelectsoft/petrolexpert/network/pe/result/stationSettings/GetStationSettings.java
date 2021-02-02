package md.intelectsoft.petrolexpert.network.pe.result.stationSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetStationSettings {
    @SerializedName("ErrorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("ErrorMessage")
    @Expose
    private Object errorMessage;
    @SerializedName("Assortment")
    @Expose
    private List<AssortmentStation> assortment = null;
//    @SerializedName("EmployeesCards")
//    @Expose
//    private List<EmployeesCard> employeesCards = null;
//    @SerializedName("Guns")
//    @Expose
//    private List<Object> guns = null;
    @SerializedName("PaymentTypes")
    @Expose
    private List<PaymentTypeStation> paymentTypes = null;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Object getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(Object errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<AssortmentStation> getAssortment() {
        return assortment;
    }

    public void setAssortment(List<AssortmentStation> assortment) {
        this.assortment = assortment;
    }

//    public List<EmployeesCard> getEmployeesCards() {
//        return employeesCards;
//    }
//
//    public void setEmployeesCards(List<EmployeesCard> employeesCards) {
//        this.employeesCards = employeesCards;
//    }
//
//    public List<Object> getGuns() {
//        return guns;
//    }
//
//    public void setGuns(List<Object> guns) {
//        this.guns = guns;
//    }

    public List<PaymentTypeStation> getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(List<PaymentTypeStation> paymentTypes) {
        this.paymentTypes = paymentTypes;
    }
}
