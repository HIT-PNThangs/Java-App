package com.example.android.pnt.whatsapp.Model;

public class CallModel {
    private String userId, userName, date, profilePic, callType;

    public CallModel() {
    }

    public CallModel(String userId, String userName, String date, String profilePic, String callType) {
        this.userId = userId;
        this.userName = userName;
        this.date = date;
        this.profilePic = profilePic;
        this.callType = callType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }
}
