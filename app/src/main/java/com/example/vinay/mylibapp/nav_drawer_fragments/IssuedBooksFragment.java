package com.example.vinay.mylibapp.nav_drawer_fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vinay on 27-06-2018.
 */

public class IssuedBooksFragment extends Fragment {

    public static IssuedBooksFragment newInstance(Bundle bundle){
        IssuedBooksFragment issuedBooksFragment = new IssuedBooksFragment();


        // setArguments ie the book list

        return issuedBooksFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
