package com.example.pnt.android.tvshows.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pnt.android.tvshows.adapters.WatchlistAdapter;
import com.example.pnt.android.tvshows.databinding.ActivityWatchlistBinding;
import com.example.pnt.android.tvshows.listeners.WatchlistListener;
import com.example.pnt.android.tvshows.models.TVShow;
import com.example.pnt.android.tvshows.utilities.TempDataHolder;
import com.example.pnt.android.tvshows.viewmodels.WatchlistViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchlistActivity extends AppCompatActivity {

    private ActivityWatchlistBinding binding;
    private WatchlistViewModel viewModel;
    private WatchlistAdapter watchlistAdapter;
    private List<TVShow> watchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWatchlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        viewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);

        binding.imageBack.setOnClickListener(v -> onBackPressed());

        watchlist = new ArrayList<>();

        loadWatchlist();
    }

    private void loadWatchlist() {
        binding.setIsLoading(true);

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(viewModel.loadWatchlist().subscribeOn(Schedulers.computation()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(tvShows -> {
                    binding.setIsLoading(false);
                    if (watchlist.size() > 0) {
                        watchlist.clear();
                    }

                    watchlist.addAll(tvShows);

                    watchlistAdapter = new WatchlistAdapter(watchlist, new WatchlistListener() {
                        @Override
                        public void onTVShowClicked(TVShow tvShow) {
                            Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
                            intent.putExtra("tvShow", tvShow);
                            startActivity(intent);
                        }

                        @Override
                        public void removeTVShowFromWatchlist(TVShow tvShow, int position) {
                            CompositeDisposable compositeDisposable = new CompositeDisposable();

                            compositeDisposable.add(viewModel.removeTVShowFromWatchlist(tvShow).
                                    subscribeOn(Schedulers.computation()).
                                    observeOn(AndroidSchedulers.mainThread()).
                                    subscribe(() -> {
                                        watchlist.remove(position);
                                        watchlistAdapter.notifyItemRemoved(position);
                                        watchlistAdapter.notifyItemRangeChanged(position, watchlistAdapter.getItemCount());
                                        compositeDisposable.dispose();
                                    })
                            );
                        }
                    });

                    binding.watchlistRecyclerView.setAdapter(watchlistAdapter);
                    binding.watchlistRecyclerView.setVisibility(View.VISIBLE);

                    compositeDisposable.dispose();
                })
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TempDataHolder.IS_WATCHLIST_UPDATE) {
            loadWatchlist();
            TempDataHolder.IS_WATCHLIST_UPDATE = false;
        }
    }
}