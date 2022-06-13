package com.example.android.pnt.socaialmediaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.android.pnt.socaialmediaapp.databinding.ActivityMainBinding;
import com.example.android.pnt.socaialmediaapp.fragment.AddFragment;
import com.example.android.pnt.socaialmediaapp.fragment.HomeFragment;
import com.example.android.pnt.socaialmediaapp.fragment.NotificationFragment;
import com.example.android.pnt.socaialmediaapp.fragment.PersonFragment;
import com.example.android.pnt.socaialmediaapp.fragment.SearchFragment;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replace(new HomeFragment());

        binding.bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                switch (i) {
                    case 0:
                        replace(new HomeFragment());
                        break;

                    case 1:
                        replace(new NotificationFragment());
                        break;

                    case 2:
                        replace(new AddFragment());
                        break;

                    case 3:
                        replace(new SearchFragment());
                        break;

                    case 4:
                        replace(new PersonFragment());
                        break;
                }

                return true;
            }
        });
    }

    private void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentLayout, fragment).commit();
    }
}