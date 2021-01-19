package md.intelectsoft.petrolmpos.network.pe.body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SetFiscalBody {
    @SerializedName("LicenseActivationCode")
    @Expose
    private String licenseActivationCode;
    @SerializedName("LicenseID")
    @Expose
    private String licenseID;

    public String getLicenseActivationCode() {
        return licenseActivationCode;
    }

    public void setLicenseActivationCode(String licenseActivationCode) {
        this.licenseActivationCode = licenseActivationCode;
    }

    public String getLicenseID() {
        return licenseID;
    }

    public void setLicenseID(String licenseID) {
        this.licenseID = licenseID;
    }
}
