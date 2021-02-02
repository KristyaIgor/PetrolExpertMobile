package md.intelectsoft.petrolexpert.network.pe.result.authorizeUser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAuthorizeUser {
    @SerializedName("ErrorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("ErrorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("Token")
    @Expose
    private TokenUser token;
    @SerializedName("User")
    @Expose
    private UserAuth user;

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

    public TokenUser getToken() {
        return token;
    }

    public void setToken(TokenUser token) {
        this.token = token;
    }

    public UserAuth getUser() {
        return user;
    }

    public void setUser(UserAuth user) {
        this.user = user;
    }
}
