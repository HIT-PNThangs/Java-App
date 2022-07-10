package com.example.pnt.android.videomeetingapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pnt.android.videomeetingapp.Listener.UserListener;
import com.example.pnt.android.videomeetingapp.Models.User;
import com.example.pnt.android.videomeetingapp.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> list;
    private UserListener listener;
    private List<User> selectedUsers;

    public UserAdapter() {
    }

    public UserAdapter(List<User> list) {
        this.list = list;
    }

    public UserAdapter(List<User> list, UserListener listener) {
        this.list = list;
        this.listener = listener;
        selectedUsers = new ArrayList<>();
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_user,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtFirstChar, txtUserName, txtEmail;
        ImageView imgAudio, imgVideo, imgSelected;
        ConstraintLayout userConstraint;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            txtFirstChar = itemView.findViewById(R.id.txtFirstChar);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtUserName = itemView.findViewById(R.id.txtUserName);

            imgAudio = itemView.findViewById(R.id.imageAudioMeeting);
            imgVideo = itemView.findViewById(R.id.imageVideoMeeting);
            imgSelected = itemView.findViewById(R.id.imageSelected);

            userConstraint = itemView.findViewById(R.id.userContainer);
        }

        void setUserData(User user) {
            txtFirstChar.setText(user.getFirstName().substring(0, 1));
            txtUserName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
            txtEmail.setText(user.getEmail());

            imgAudio.setOnClickListener(view -> listener.initiateAudioMeeting(user));
            imgVideo.setOnClickListener(view -> listener.initiateVideoMeeting(user));

            userConstraint.setOnLongClickListener(view -> {
                if (imgSelected.getVisibility() != View.VISIBLE) {
                    selectedUsers.add(user);

                    imgSelected.setVisibility(View.VISIBLE);
                    imgVideo.setVisibility(View.GONE);
                    imgAudio.setVisibility(View.GONE);
                    listener.onMultipleUsersAction(true);
                }

                return true;
            });

            userConstraint.setOnClickListener(view -> {
                if (imgSelected.getVisibility() == View.VISIBLE) {
                    selectedUsers.remove(user);

                    imgSelected.setVisibility(View.GONE);
                    imgVideo.setVisibility(View.VISIBLE);
                    imgAudio.setVisibility(View.VISIBLE);

                    if (selectedUsers.size() == 0) {
                        listener.onMultipleUsersAction(false);
                    }
                } else {
                    if (selectedUsers.size() > 0) {
                        selectedUsers.add(user);

                        imgSelected.setVisibility(View.VISIBLE);
                        imgVideo.setVisibility(View.GONE);
                        imgAudio.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}
