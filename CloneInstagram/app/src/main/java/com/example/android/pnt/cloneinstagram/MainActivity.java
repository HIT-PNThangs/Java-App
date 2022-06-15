package com.example.android.pnt.cloneinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.pnt.cloneinstagram.databinding.ActivityMainBinding;
import com.example.android.pnt.cloneinstagram.fragment.HomeFragment;
import com.example.android.pnt.cloneinstagram.fragment.NotificationFragment;
import com.example.android.pnt.cloneinstagram.fragment.ProfileFragment;
import com.example.android.pnt.cloneinstagram.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private Fragment selectorFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        binding.bottomNavigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        break;

                    case R.id.nav_search:
                        selectorFragment = new SearchFragment();
                        break;

                    case R.id.nav_add:
                        selectorFragment = null;
                        startActivity(new Intent(MainActivity.this, PostActivity.class).
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;

                    case R.id.nav_heart:
                        selectorFragment = new NotificationFragment();
                        break;

                    case R.id.nav_profile:
                        selectorFragment = new ProfileFragment();
                        break;
                }

                if(selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }
            }
        });
    }
}