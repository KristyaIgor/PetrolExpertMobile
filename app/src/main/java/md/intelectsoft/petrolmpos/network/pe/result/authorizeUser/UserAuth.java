package md.intelectsoft.petrolmpos.network.pe.result.authorizeUser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserAuth {
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Surname")
    @Expose
    private String surname;
    @SerializedName("UserID")
    @Expose
    private String userID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
