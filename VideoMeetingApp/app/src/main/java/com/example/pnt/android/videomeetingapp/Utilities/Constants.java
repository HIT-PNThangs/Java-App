package com.example.pnt.android.videomeetingapp.Utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "Users";
    public static final String KEY_FIRST_NAME = "First_name";
    public static final String KEY_LAST_NAME = "Last_name";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_PASSWORD = "Password";
    public static final String KEY_USER_ID = "User_id";

    public static final String KEY_FCM_TOKEN = "fcm_token";

    public static final String KEY_PREFERENCE_NAME = "Video_meeting_preference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKE = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String dREMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> getRemoteMessageHeaders() {
        HashMap<String, String> header = new HashMap<>();

        header.put(Constants.REMOTE_MSG_AUTHORIZATION,
                "key=BDM1oxbt7aXY-OnyOUkrf2qo3HpJsyl7jwu5dGNxs73izcWtPo1B6ktkFMnefBs5ORKGoKWyKPooBQhnLacV_6w");
        header.put(Constants.REMOTE_MSG_CONTENT_TYPE, "application/json");

        return header;
    }
}
