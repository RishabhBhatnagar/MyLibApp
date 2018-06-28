package com.example.vinay.mylibapp.nav_drawer_fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vinay.mylibapp.Book;
import com.example.vinay.mylibapp.BooksAdapter;
import com.example.vinay.mylibapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinay on 27-06-2018.
 */

public class IssuedBooksFragment extends Fragment {

    static String KEY_BOOKS = "books";
    List<Book> bookList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BooksAdapter mBooksAdapter;

    public static IssuedBooksFragment newInstance(List<Book> bookList){
        IssuedBooksFragment issuedBooksFragment = new IssuedBooksFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_BOOKS, (ArrayList<? extends Parcelable>) bookList);

        issuedBooksFragment.setArguments(args);
        return issuedBooksFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.frag_issued_books, container, false);

        Bundle args = getArguments();

        List<Parcelable> parcelBooks = args.getParcelableArrayList(KEY_BOOKS);

        if(parcelBooks == null){
            // if user has no books borrowed

            TextView textView = new TextView(getActivity());
            textView.setText("NO BOOKS ISSUED");

            return textView;
        }

        // Retrieve books
        for (Parcelable p : parcelBooks) {
            Book b = (Book) p;
            bookList.add(b);
        }

        // Setup the recyclerview
        recyclerView = view.findViewById(R.id.issued_books_recycler_view);
        mBooksAdapter = new BooksAdapter(bookList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mBooksAdapter);



        return view;
    }
}
