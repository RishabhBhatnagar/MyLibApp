package com.example.vinay.mylibapp.nav_drawer_fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vinay.mylibapp.R;

/**
 * Created by vinay on 28-06-2018.
 */

public class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_about, container, false);
        TextView tv = view.findViewById(R.id.tv_about);
        tv.setText("This app is created by the joint efforts of\n" +
                "1. Vinay Deshmukh\n" +
                "2. Rishabh Bhatnagar\n" +
                "3. Sunny D'Souza\n" +
                "4. Mandar Acharekar");
        return view;
    }
}
