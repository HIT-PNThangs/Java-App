package com.example.pnt.android.videomeetingapp.Listener;

import com.example.pnt.android.videomeetingapp.Models.User;

public interface UserListener {
    void initiateVideoMeeting(User user);
    void initiateAudioMeeting(User user);
    void onMultipleUsersAction(Boolean isMultipleUsersSelected);
}
