package com.example.pnt.android.tvshows.responses;

import com.example.pnt.android.tvshows.models.TVShow;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShowsResponse {
    @SerializedName("page")
    private int page;

    @SerializedName("pages")
    private int totalPages;

    @SerializedName("tv_shows")
    private List<TVShow> tvShows;

    public TVShowsResponse() {
    }

    public TVShowsResponse(int page, int totalPages, List<TVShow> tvShows) {
        this.page = page;
        this.totalPages = totalPages;
        this.tvShows = tvShows;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<TVShow> getTvShows() {
        return tvShows;
    }
}
