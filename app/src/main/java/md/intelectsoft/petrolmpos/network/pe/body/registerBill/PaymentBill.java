package md.intelectsoft.petrolmpos.network.pe.body.registerBill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class PaymentBill extends RealmObject {
    @SerializedName("PaymentCode")
    @Expose
    private Integer paymentCode;
    @SerializedName("RRN")
    @Expose
    private Integer rRN;
    @SerializedName("Sum")
    @Expose
    private Double sum;

    public Integer getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(Integer paymentCode) {
        this.paymentCode = paymentCode;
    }

    public Integer getRRN() {
        return rRN;
    }

    public void setRRN(Integer rRN) {
        this.rRN = rRN;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }
}
