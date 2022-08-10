package com.example.pnt.android.tvshows.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShowDetails {
    @SerializedName("url")
    private String url;

    @SerializedName("description")
    private String description;

    @SerializedName("runtime")
    private String runtime;

    @SerializedName("image_path")
    private String imagePath;

    @SerializedName("rating")
    private String rating;

    @SerializedName("genres")
    private String[] genres;

    @SerializedName("pictures")
    private String[] pictures;

    @SerializedName("episodes")
    private List<Episode> episodes;

    public TVShowDetails() {
    }

    public TVShowDetails(String url, String description, String runtime,
                         String imagePath, String rating, String[] genres,
                         String[] pictures, List<Episode> episodes) {
        this.url = url;
        this.description = description;
        this.runtime = runtime;
        this.imagePath = imagePath;
        this.rating = rating;
        this.genres = genres;
        this.pictures = pictures;
        this.episodes = episodes;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getRating() {
        return rating;
    }

    public String[] getGenres() {
        return genres;
    }

    public String[] getPictures() {
        return pictures;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }
}
