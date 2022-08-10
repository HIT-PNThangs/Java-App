package com.example.pnt.android.tvshows.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.pnt.android.tvshows.repositories.MostPopularTVShowsRepository;
import com.example.pnt.android.tvshows.responses.TVShowsResponse;

public class MostPopularTVShowsViewModel extends ViewModel {
    private final MostPopularTVShowsRepository mostPopularTVShowsRepository;

    public MostPopularTVShowsViewModel() {
        mostPopularTVShowsRepository = new MostPopularTVShowsRepository();
    }

    public LiveData<TVShowsResponse> getMostPopularTVShows(int page) {
        return mostPopularTVShowsRepository.getMostPopularTVShows(page);
    }
}
