package md.intelectsoft.petrolexpert.network.pe.body.registerBill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class BillRegistered extends RealmObject {
    @SerializedName("ClientCardCode")
    @Expose
    private String clientCardCode;
    @SerializedName("Lines")
    @Expose
    private RealmList<LineBill> lines = null;
    @SerializedName("OfficeCode")
    @Expose
    private String officeCode;
    @SerializedName("Payments")
    @Expose
    private RealmList<PaymentBill> paymentBills;
    @SerializedName("Validate")
    @Expose
    private boolean validate;

    //save field local storage
    private long date;
    private String authorId;
    private String authorName;
    private String cashId;
    private String cashName;
    private String stationName;
    private int globalNumberNeFisc;
    private int globalNumberFisc;
    private int shiftNumber;
    private boolean isFiscal;
    private String externId;
    private String shiftId;
    private int backGlobalNumber;

    public String getClientCardCode() {
        return clientCardCode;
    }

    public void setClientCardCode(String clientCardCode) {
        this.clientCardCode = clientCardCode;
    }

    public List<LineBill> getLines() {
        return lines;
    }

    public void setLines(RealmList<LineBill> lines) {
        this.lines = lines;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public RealmList<PaymentBill> getPaymentBills() {
        return paymentBills;
    }

    public void setPaymentBills(RealmList<PaymentBill> paymentBills) {
        this.paymentBills = paymentBills;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCashId() {
        return cashId;
    }

    public void setCashId(String cashId) {
        this.cashId = cashId;
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

    public int getGlobalNumberNeFisc() {
        return globalNumberNeFisc;
    }

    public void setGlobalNumberNeFisc(int globalNumberNeFisc) {
        this.globalNumberNeFisc = globalNumberNeFisc;
    }

    public int getGlobalNumberFisc() {
        return globalNumberFisc;
    }

    public void setGlobalNumberFisc(int globalNumberFisc) {
        this.globalNumberFisc = globalNumberFisc;
    }

    public boolean isFiscal() {
        return isFiscal;
    }

    public void setFiscal(boolean fiscal) {
        isFiscal = fiscal;
    }

    public int getShiftNumber() {
        return shiftNumber;
    }

    public void setShiftNumber(int shiftNumber) {
        this.shiftNumber = shiftNumber;
    }

    public String getExternId() {
        return externId;
    }

    public void setExternId(String externId) {
        this.externId = externId;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public int getBackGlobalNumber() {
        return backGlobalNumber;
    }

    public void setBackGlobalNumber(int backGlobalNumber) {
        this.backGlobalNumber = backGlobalNumber;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }
}
