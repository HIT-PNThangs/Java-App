package com.example.pnt.android.tvshows.models;

import com.google.gson.annotations.SerializedName;

public class Episode {
    @SerializedName("season")
    private String season;

    @SerializedName("episode")
    private String episode;

    @SerializedName("name")
    private String name;

    @SerializedName("air_date")
    private String airDate;

    public Episode() {
    }

    public Episode(String season, String episode, String name, String airDate) {
        this.season = season;
        this.episode = episode;
        this.name = name;
        this.airDate = airDate;
    }

    public String getSeason() {
        return season;
    }

    public String getEpisode() {
        return episode;
    }

    public String getName() {
        return name;
    }

    public String getAirDate() {
        return airDate;
    }
}
