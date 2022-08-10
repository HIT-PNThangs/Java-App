package com.example.pnt.android.tvshows.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pnt.android.tvshows.R;
import com.example.pnt.android.tvshows.databinding.ItemComtainerTvShowBinding;
import com.example.pnt.android.tvshows.listeners.TVShowsListener;
import com.example.pnt.android.tvshows.models.TVShow;

import java.util.List;

public class TVShowsAdapter extends RecyclerView.Adapter<TVShowsAdapter.TVShowViewHolder> {
    private List<TVShow> tvShows;
    private LayoutInflater layoutInflater;
    private TVShowsListener listener;

    public TVShowsAdapter(List<TVShow> tvShows, TVShowsListener tvShowsListener) {
        this.tvShows = tvShows;
        this.listener = tvShowsListener;
    }

    @NonNull
    @Override
    public TVShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemComtainerTvShowBinding tvShowBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.item_comtainer_tv_show, parent, false
        );

        return new TVShowViewHolder(tvShowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TVShowViewHolder holder, int position) {
        holder.bindTVShow(tvShows.get(position));
    }

    @Override
    public int getItemCount() {
        return tvShows == null ? 0 : tvShows.size();
    }

    class TVShowViewHolder extends RecyclerView.ViewHolder {
        private ItemComtainerTvShowBinding binding;

        public TVShowViewHolder(ItemComtainerTvShowBinding tvShowBinding) {
            super(tvShowBinding.getRoot());
            this.binding = tvShowBinding;
        }

        public void bindTVShow(TVShow tvShow) {
            binding.setTvShow(tvShow);
            binding.executePendingBindings();
            binding.getRoot().setOnClickListener(v -> {
                listener.onTVShowClicked(tvShow);
            });
        }
    }
}
