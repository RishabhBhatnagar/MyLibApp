package com.example.vinay.mylibapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vinay on 27-06-2018.
 */

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder> {

    private List<Book> bookList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_title;

        public MyViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.title);
        }
    }

    public BooksAdapter(List<Book> books){
        bookList = books;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_single_book, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Book currentBook = bookList.get(position);
        holder.tv_title.setText(currentBook.getTitle());
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}
