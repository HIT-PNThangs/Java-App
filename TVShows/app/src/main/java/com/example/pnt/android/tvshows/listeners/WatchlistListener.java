package com.example.pnt.android.tvshows.listeners;

import com.example.pnt.android.tvshows.models.TVShow;

public interface WatchlistListener {
    void onTVShowClicked(TVShow tvShow);

    void removeTVShowFromWatchlist(TVShow tvShow, int position);
}
