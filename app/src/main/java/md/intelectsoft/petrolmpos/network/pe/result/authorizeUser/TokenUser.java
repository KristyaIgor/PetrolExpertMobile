package md.intelectsoft.petrolmpos.network.pe.result.authorizeUser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenUser {
    @SerializedName("Uid")
    @Expose
    private String uid;
    @SerializedName("ValidTo")
    @Expose
    private String validTo;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }
}
