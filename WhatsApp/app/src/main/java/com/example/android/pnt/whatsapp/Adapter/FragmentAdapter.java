package com.example.android.pnt.whatsapp.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.android.pnt.whatsapp.Fragment.CallsFragment;
import com.example.android.pnt.whatsapp.Fragment.ChatsFragment;
import com.example.android.pnt.whatsapp.Fragment.StatusFragment;

public class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new StatusFragment();

            case 2:
                return new CallsFragment();

            default:
                return new ChatsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Chats";
                break;

            case 1:
                title = "Status";
                break;

            case 2:
                title = "Calls";
                break;
        }
        return title;
    }
}
