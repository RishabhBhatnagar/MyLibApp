package com.example.vinay.mylibapp.nav_drawer_fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.vinay.mylibapp.MainActivity;
import com.example.vinay.mylibapp.data.Book;
import com.example.vinay.mylibapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by vinay on 27-06-2018.
 */

public class IssuedBooksFragment extends Fragment {

    private static String KEY_BOOKS = "books";
    List<Book> bookList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BooksAdapter mBooksAdapter;
    private Button re_issue;

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
        View view = inflater.inflate(R.layout.frag_issued_books, container, false);



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
        //f(posi);

        // Setup the recyclerview
        recyclerView = view.findViewById(R.id.issued_books_recycler_view);
        mBooksAdapter = new BooksAdapter(bookList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mBooksAdapter);
        return view;
    }

    /**
     * Created by vinay on 27-06-2018.
     */

    // private since we only need it inside this class
    private class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder> {

        private List<Book> bookList;
        public Button reIssueButtton;
        MainActivity mainActivity;




        public class MyViewHolder extends RecyclerView.ViewHolder{

            public TextView tv_title,tv_duedate,tv_fine,tv_reissue_count,tv_daysLeft;
            public RelativeLayout relativeLayout;




            public MyViewHolder(final View view) {
                super(view);
                tv_daysLeft=view.findViewById(R.id.days_left);
                tv_title = (TextView) view.findViewById(R.id.name);
                tv_duedate = view.findViewById(R.id.due_date);
                tv_fine = view.findViewById(R.id.fine_amt);
                tv_reissue_count=view.findViewById(R.id.re_issue_counter);


                reIssueButtton=view.findViewById(R.id.re_issue_button);

                relativeLayout=view.findViewById(R.id.relative_layout);


                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int p=getLayoutPosition();

                        Toast.makeText(view.getContext(), "Clicked on :"+p, Toast.LENGTH_SHORT).show();

                        // https://stackoverflow.com/questions/29983848/how-to-highlight-the-selected-item-of-recycler-view
                        relativeLayout.setSelected(true);

                        return true;
                    }
                });
            }
        }
       // FragToActivity fragToActivity;
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
        //FragToActivity fragToActivity;

        String daysLeft="";
        @SuppressLint("SimpleDateFormat")
        public String DaysLeft(int position)
        {
            String DaysLeft="";
            Book currentBook = bookList.get(position);
            String inputDateString = currentBook.getDueDate();
            //Toast.makeText(, position, Toast.LENGTH_SHORT).show();
//            Bundle bundle = new Bundle();
//            bundle.putString("myData", inputDateString);
//            Intent i = new Intent(getContext(),MainActivity.class);
//            i.putExtras(bundle);
//            startActivity(i);



            Calendar calCurr = Calendar.getInstance();
            Calendar day = Calendar.getInstance();
            try {
                day.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(inputDateString));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(day.after(calCurr)){
                  DaysLeft= String.valueOf((day.get(Calendar.DAY_OF_MONTH) -(calCurr.get(Calendar.DAY_OF_MONTH))));

            }

              daysLeft=DaysLeft;

              return DaysLeft;

        }


       // int Position=0;
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            Book currentBook = bookList.get(position);
            holder.tv_title.setText(currentBook.getTitle());
            holder.tv_duedate.setText(currentBook.getDueDate());
            holder.tv_fine.setText(currentBook.getFineAmount());
            holder.tv_reissue_count.setText(currentBook.getRenewCount());
            holder.tv_daysLeft.setText(DaysLeft(position));

        }



        @Override
        public int getItemCount() {
            return bookList.size();
        }
    }
}
