package com.example.android.pnt.whatsapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.android.pnt.whatsapp.Adapter.FragmentAdapter;
import com.example.android.pnt.whatsapp.R;
import com.example.android.pnt.whatsapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFabIcon(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;

            case R.id.groupChat:
                startActivity(new Intent(MainActivity.this, GroupChatActivity.class));
                break;

            case R.id.logOut:
                auth.signOut();
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void changeFabIcon(int index) {
        binding.fabAction.hide();

        new Handler().postDelayed(() -> {
            switch (index) {
                case 0:
                    binding.fabAction.setImageResource(R.drawable.ic_chat);
                    break;

                case 1:
                    binding.fabAction.setImageResource(R.drawable.ic_camera);
                    break;

                case 2:
                    binding.fabAction.setImageResource(R.drawable.ic_call);
                    break;
            }

            binding.fabAction.show();
        }, 200);
    }
}