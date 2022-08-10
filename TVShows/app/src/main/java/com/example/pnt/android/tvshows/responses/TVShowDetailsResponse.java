package com.example.pnt.android.tvshows.responses;

import com.example.pnt.android.tvshows.models.TVShowDetails;
import com.google.gson.annotations.SerializedName;

public class TVShowDetailsResponse {
    @SerializedName("tvShows")
    private TVShowDetails tvShowDetails;

    public TVShowDetailsResponse() {
    }

    public TVShowDetailsResponse(TVShowDetails tvShowDetails) {
        this.tvShowDetails = tvShowDetails;
    }

    public TVShowDetails getTvShowDetails() {
        return tvShowDetails;
    }
}
