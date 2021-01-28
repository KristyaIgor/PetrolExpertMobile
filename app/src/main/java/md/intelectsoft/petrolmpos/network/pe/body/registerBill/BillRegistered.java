package md.intelectsoft.petrolmpos.network.pe.body.registerBill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BillRegistered {
    @SerializedName("ClientCardCode")
    @Expose
    private String clientCardCode;
    @SerializedName("BillNumber")
    @Expose
    private int billNumber = -1;
    @SerializedName("Lines")
    @Expose
    private List<LineBill> lines = null;
    @SerializedName("OfficeCode")
    @Expose
    private String officeCode;
    @SerializedName("ShiftId")
    @Expose
    private String shiftId;
    @SerializedName("Payments")
    @Expose
    private List<PaymentBill> paymentBills;

    public String getClientCardCode() {
        return clientCardCode;
    }

    public void setClientCardCode(String clientCardCode) {
        this.clientCardCode = clientCardCode;
    }

    public List<LineBill> getLines() {
        return lines;
    }

    public void setLines(List<LineBill> lines) {
        this.lines = lines;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public List<PaymentBill> getPaymentBills() {
        return paymentBills;
    }

    public void setPaymentBills(List<PaymentBill> paymentBills) {
        this.paymentBills = paymentBills;
    }
}
