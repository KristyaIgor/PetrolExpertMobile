package md.intelectsoft.petrolmpos.network.pe.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SimpleResponse {
    @SerializedName("ErrorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("NoError")
    @Expose
    private Boolean noError;

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

}
