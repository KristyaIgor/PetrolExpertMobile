package md.intelectsoft.petrolmpos.network.pe.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterDevice {

    @SerializedName("ErrorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("ErrorCode")
    @Expose
    private int errorCode;
    @SerializedName("Registred")
    @Expose
    private Boolean registred;
    @SerializedName("RegistredNumber")
    @Expose
    private Integer registredNumber;
    @SerializedName("Cash")
    @Expose
    private String cash;
    @SerializedName("Owner")
    @Expose
    private String owner;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getNoError() {
        return errorCode;
    }

    public void setNoError(Integer noError) {
        this.errorCode = noError;
    }

    public Boolean getRegistred() {
        return registred;
    }

    public void setRegistred(Boolean registred) {
        this.registred = registred;
    }

    public Integer getRegistredNumber() {
        return registredNumber;
    }

    public void setRegistredNumber(Integer registredNumber) {
        this.registredNumber = registredNumber;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
