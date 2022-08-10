package com.example.pnt.android.tvshows.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pnt.android.tvshows.adapters.TVShowsAdapter;
import com.example.pnt.android.tvshows.databinding.ActivityMainBinding;
import com.example.pnt.android.tvshows.listeners.TVShowsListener;
import com.example.pnt.android.tvshows.models.TVShow;
import com.example.pnt.android.tvshows.viewmodels.MostPopularTVShowsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private MostPopularTVShowsViewModel viewModel;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter adapter;
    private int currentPage = 1;
    private int totalAvailablePages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        binding.tvShowsRecyclerView.setHasFixedSize(true);

        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        adapter = new TVShowsAdapter(tvShows, tvShow -> {
            Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);

            intent.putExtra("id", tvShow.getId());
            intent.putExtra("name", tvShow.getName());
            intent.putExtra("startDate", tvShow.getStartDate());
            intent.putExtra("country", tvShow.getCountry());
            intent.putExtra("network", tvShow.getNetwork());
            intent.putExtra("status", tvShow.getStatus());

            startActivity(intent);
        });

        binding.tvShowsRecyclerView.setAdapter(adapter);
        binding.tvShowsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!binding.tvShowsRecyclerView.canScrollHorizontally(1)) {
                    if (currentPage <= totalAvailablePages) {
                        currentPage += 1;
                        getMostPopularTVShows();
                    }
                }
            }
        });

        getMostPopularTVShows();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getMostPopularTVShows() {
        toggleLoading();

        viewModel.getMostPopularTVShows(currentPage).observe(this, mostPopularTVShowsResponse -> {
            toggleLoading();

            if (mostPopularTVShowsResponse != null) {
                totalAvailablePages = mostPopularTVShowsResponse.getTotalPages();

                if (mostPopularTVShowsResponse.getTvShows() != null) {
                    int oldCount = tvShows.size();

                    tvShows.addAll(mostPopularTVShowsResponse.getTvShows());
                    adapter.notifyItemRangeInserted(oldCount, tvShows.size());
                }
            }
        });
    }

    private void toggleLoading() {
        if (currentPage == 1) {
            binding.setIsLoading(binding.getIsLoading() == null || !binding.getIsLoading());
        } else {
            binding.setIsLoadingMore(binding.getIsLoadingMore() == null || !binding.getIsLoadingMore());
        }
    }
}