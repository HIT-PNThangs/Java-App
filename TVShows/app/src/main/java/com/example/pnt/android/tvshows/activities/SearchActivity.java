package com.example.pnt.android.tvshows.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pnt.android.tvshows.adapters.TVShowsAdapter;
import com.example.pnt.android.tvshows.databinding.ActivitySearchBinding;
import com.example.pnt.android.tvshows.listeners.TVShowsListener;
import com.example.pnt.android.tvshows.models.TVShow;
import com.example.pnt.android.tvshows.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private SearchViewModel viewModel;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter adapter;

    private int currentPage = 1;
    private int totalAvailablePage = 1;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());

        binding.searchRecycleView.setHasFixedSize(true);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        adapter = new TVShowsAdapter(tvShows, new TVShowsListener() {
            @Override
            public void onTVShowClicked(TVShow tvShow) {
                Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);

                intent.putExtra("tvShow", tvShow);

                startActivity(intent);
            }
        });

        binding.searchRecycleView.setAdapter(adapter);

        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().isEmpty()) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                currentPage = 1;
                                totalAvailablePage = 1;
                                searchTVShow(editable.toString());
                            });
                        }
                    }, 800);
                } else {
                    tvShows.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        binding.searchRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!binding.searchRecycleView.canScrollVertically(1)) {
                    if (!binding.inputSearch.getText().toString().isEmpty()) {
                        if (currentPage < totalAvailablePage) {
                            currentPage += 1;
                            searchTVShow(binding.inputSearch.getText().toString());
                        }
                    }
                }
            }
        });

        binding.inputSearch.requestFocus();
    }

    private void searchTVShow(String query) {
        toggleLoading();

        viewModel.searchTVShow(query, currentPage).observe(this, tvShowsResponse -> {
            toggleLoading();

            if (tvShowsResponse != null) {
                totalAvailablePage = tvShowsResponse.getTotalPages();

                if (tvShowsResponse.getTvShows() != null) {
                    int oldCount = tvShows.size();

                    tvShows.addAll(tvShowsResponse.getTvShows());
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