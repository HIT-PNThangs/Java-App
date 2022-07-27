package com.example.android.pnt.whatsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.pnt.whatsapp.Model.CallModel;
import com.example.android.pnt.whatsapp.R;

import java.util.List;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder> {
    private List<CallModel> list;
    private Context context;

    public CallAdapter() {
    }

    public CallAdapter(List<CallModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_call_list, parent, false);
        return new CallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class CallViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        public CallViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
