package md.intelectsoft.petrolmpos.network.pe.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterDevice {

    @SerializedName("ErrorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("NoError")
    @Expose
    private Boolean noError;
    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @SerializedName("Longitude")
    @Expose
    private Double longitude;
    @SerializedName("RadiusAroundPoint")
    @Expose
    private Integer radiusAroundPoint;
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

    public Boolean getNoError() {
        return noError;
    }

    public void setNoError(Boolean noError) {
        this.noError = noError;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRadiusAroundPoint() {
        return radiusAroundPoint;
    }

    public void setRadiusAroundPoint(Integer radiusAroundPoint) {
        this.radiusAroundPoint = radiusAroundPoint;
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
