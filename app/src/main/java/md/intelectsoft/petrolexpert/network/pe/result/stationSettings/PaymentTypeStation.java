package md.intelectsoft.petrolexpert.network.pe.result.stationSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentTypeStation {
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("PaymentCode")
    @Expose
    private String paymentCode;
    @SerializedName("Type")
    @Expose
    private Integer type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
