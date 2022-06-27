package com.example.android.pnt.whatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.pnt.whatsapp.Model.Users;
import com.example.android.pnt.whatsapp.R;
import com.example.android.pnt.whatsapp.Activity.ChatDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    List<Users> list;
    Context context;

    public UserAdapter() { }

    public UserAdapter(List<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Users users = list.get(position);

        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.avatar3).into(holder.image);
        holder.userName.setText(users.getUserName());
        holder.lastMessage.setText(users.getLastMessage());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatDetailActivity.class);

            intent.putExtra("userId", users.getUserId());
            intent.putExtra("profilePic", users.getProfilePic());
            intent.putExtra("userName", users.getUserName());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView userName, lastMessage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_profile);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}
