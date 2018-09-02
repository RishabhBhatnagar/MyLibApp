package org.sfitengg.library.mylibapp.nav_drawer_fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.sfitengg.library.mylibapp.R;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_about, container, false);
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TextView name1 = view.findViewById(R.id.name1);
        TextView name2 = view.findViewById(R.id.name2);
        TextView name3 = view.findViewById(R.id.name3);
        TextView name4 = view.findViewById(R.id.name4);

        ImageView Img1 =view.findViewById(R.id.img1);
        ImageView Img2 =view.findViewById(R.id.img2);
        ImageView Img3 =view.findViewById(R.id.img3);
        ImageView Img4 =view.findViewById(R.id.img4);

        TextView data = view.findViewById(R.id.tv_data);
        TextView head = view.findViewById(R.id.tv_head);


        head.setText("developed by the students of \nst francis institute of technology :");




        data.setText("This application helps you in showing you " +
                "your due date, keep track the number of days left " +
                "to return the books you have issued. " +
                "You can also re-issue the books via the app itself. "
        );

        name1.setText("1. Vinay Deshmukh");
        name2.setText("2. Rishabh Bhatnagar");
        name3.setText("3. Sunny D'Souza");
        name4.setText("4. Mandar Acharekar");


        name1.setMovementMethod(LinkMovementMethod.getInstance());
        name1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(getString(R.string.vinay_linkedin)));
                startActivity(browserIntent);
            }
        });

        Img1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(getString(R.string.vinay_linkedin)));
                startActivity(intent);
            }
        });
        name2.setMovementMethod(LinkMovementMethod.getInstance());
        name2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(getString(R.string.rishabh_linkedin)));
                startActivity(browserIntent);
            }
        });
        Img2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(getString(R.string.rishabh_linkedin)));
                startActivity(intent);
            }
        });
        name3.setMovementMethod(LinkMovementMethod.getInstance());
         name3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(getString(R.string.sunny_linkedin)));
                startActivity(browserIntent);
            }
        });
        Img3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(getString(R.string.sunny_linkedin)));
                startActivity(intent);
            }
        });
        name4.setMovementMethod(LinkMovementMethod.getInstance());
        name4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(getString(R.string.mandar_lnkedin)));
                startActivity(browserIntent);
            }
        });
        Img4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(getString(R.string.mandar_lnkedin)));
                startActivity(intent);
            }
        });
        return view;
    }
}