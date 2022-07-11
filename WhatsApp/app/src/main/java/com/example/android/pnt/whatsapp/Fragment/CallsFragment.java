package com.example.android.pnt.whatsapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.android.pnt.whatsapp.databinding.FragmentCallsBinding;

public class CallsFragment extends Fragment {
    public CallsFragment() {
    }

    FragmentCallsBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCallsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}