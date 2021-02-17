package md.intelectsoft.petrolexpert.network.pe.result.stationSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class EmployeesCard extends RealmObject {
    @SerializedName("CardBarcode")
    @Expose
    private String cardBarcode;
    @SerializedName("CardNumber")
    @Expose
    private String cardNumber;
    @SerializedName("UserName")
    @Expose
    private String userName;

    private boolean toDelete;

    public String getCardBarcode() {
        return cardBarcode;
    }

    public void setCardBarcode(String cardBarcode) {
        this.cardBarcode = cardBarcode;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isToDelete() {
        return toDelete;
    }

    public void setToDelete(boolean toDelete) {
        this.toDelete = toDelete;
    }
}
