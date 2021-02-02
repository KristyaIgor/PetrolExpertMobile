package md.intelectsoft.petrolexpert.network.pe.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetCurrentShift {
    @SerializedName("ErrorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("ErrorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("LastBillDate")
    @Expose
    private String lastBillDate;
    @SerializedName("LastBillNumber")
    @Expose
    private Integer lastBillNumber;
    @SerializedName("LastBillShiftNumber")
    @Expose
    private Integer lastBillShiftNumber;
    @SerializedName("ShiftDate")
    @Expose
    private String shiftDate;
    @SerializedName("ShiftId")
    @Expose
    private String shiftId;
    @SerializedName("ShiftState")
    @Expose
    private Integer shiftState;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getLastBillDate() {
        return lastBillDate;
    }

    public void setLastBillDate(String lastBillDate) {
        this.lastBillDate = lastBillDate;
    }

    public Integer getLastBillNumber() {
        return lastBillNumber;
    }

    public void setLastBillNumber(Integer lastBillNumber) {
        this.lastBillNumber = lastBillNumber;
    }

    public Integer getLastBillShiftNumber() {
        return lastBillShiftNumber;
    }

    public void setLastBillShiftNumber(Integer lastBillShiftNumber) {
        this.lastBillShiftNumber = lastBillShiftNumber;
    }

    public String getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(String shiftDate) {
        this.shiftDate = shiftDate;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public Integer getShiftState() {
        return shiftState;
    }

    public void setShiftState(Integer shiftState) {
        this.shiftState = shiftState;
    }
}
