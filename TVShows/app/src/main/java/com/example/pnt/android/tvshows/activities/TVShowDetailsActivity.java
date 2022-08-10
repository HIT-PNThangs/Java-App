package com.example.pnt.android.tvshows.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pnt.android.tvshows.adapters.ImageSliderAdapter;
import com.example.pnt.android.tvshows.databinding.ActivityTvshowDetailsBinding;
import com.example.pnt.android.tvshows.viewmodels.TVShowDetailsViewModel;

public class TVShowDetailsActivity extends AppCompatActivity {
    private ActivityTvshowDetailsBinding binding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTvshowDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);

        getShowTVDetail();
    }

    private void getShowTVDetail() {
        binding.setIsLoading(true);

        String tvShowId = String.valueOf(getIntent().getIntExtra("id", -1));

        tvShowDetailsViewModel.getTVShowDetails(tvShowId).observe(this, tvShowDetailsResponse -> {
            binding.setIsLoading(false);

            if (tvShowDetailsResponse.getTvShowDetails() != null) {
                if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                    loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                    System.out.println("Length: " + tvShowDetailsResponse.getTvShowDetails().getPictures().length);
                }
            }
        });

//        String tvShowName = getIntent().getStringExtra("name");
//        String tvShowStartDate = getIntent().getStringExtra("startDate");
//        String tvShowCountry = getIntent().getStringExtra("country");
//        String tvShowNetwork = getIntent().getStringExtra("network");
//        String tvShowStatus = getIntent().getStringExtra("status");
    }

    private void loadImageSlider(String[] sliderImages) {
        binding.sliderViewPager.setOffscreenPageLimit(1);
        binding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImages));
        binding.sliderViewPager.setVisibility(View.VISIBLE);
        binding.viewFadingEdge.setVisibility(View.VISIBLE);
    }
}