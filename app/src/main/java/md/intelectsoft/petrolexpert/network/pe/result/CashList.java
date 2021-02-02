package md.intelectsoft.petrolexpert.network.pe.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CashList {
    @SerializedName("CashID")
    @Expose
    private String cashID;
    @SerializedName("CashName")
    @Expose
    private String cashName;
    @SerializedName("StationName")
    @Expose
    private String stationName;

    public String getCashID() {
        return cashID;
    }

    public void setCashID(String cashID) {
        this.cashID = cashID;
    }

    public String getCashName() {
        return cashName;
    }

    public void setCashName(String cashName) {
        this.cashName = cashName;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
}
