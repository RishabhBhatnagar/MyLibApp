package org.sfitengg.library.mylibapp.nav_drawer_fragments;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sfitengg.library.mylibapp.R;

public class AboutFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_about, container, false);
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TextView tv = view.findViewById(R.id.tv_about);
        TextView data = view.findViewById(R.id.tv_data);
        TextView head = view.findViewById(R.id.tv_head);


        head.setText("developed by :");



        data.setText("This application helps you keep track the number of days left to return the books you have issued. You can re-issue the books via the app as well as get timely notification about books to be submitted "
                );

        tv.setText(
                "1. Vinay Deshmukh\n" +
                "2. Rishabh Bhatnagar\n" +
                "3. Sunny D'Souza\n" +
                "4. Mandar Acharekar");
        return view;
    }
}
