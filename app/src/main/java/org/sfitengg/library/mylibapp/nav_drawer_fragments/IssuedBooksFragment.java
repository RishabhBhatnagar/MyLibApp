package org.sfitengg.library.mylibapp.nav_drawer_fragments;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sfitengg.library.mylibapp.MainActivity;
import org.sfitengg.library.mylibapp.data.Book;
import org.sfitengg.library.mylibapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IssuedBooksFragment extends Fragment {

private static String KEY_BOOKS = "books";
    List<Book> bookList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BooksAdapter mBooksAdapter;
    private static int numberOfBooksSelected = 0;


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

        // Setup the recyclerview
        recyclerView = view.findViewById(R.id.issued_books_recycler_view);
        mBooksAdapter = new BooksAdapter(bookList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mBooksAdapter);


        return view;
    }

    // private since we only need it inside this class
    private static class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder> {

        private List<Book> bookList;
        public Button reIssueButtton;
        private boolean anyBookSelected = false;
        private boolean selectedList[];
        private Context context;

        private List<Book> getBooks(){
            List<Book> selectedBooks = new ArrayList<>();
            for(int i = 0; i<selectedList.length; i++){
                if(selectedList[i]){
                    selectedBooks.add(bookList.get(i));
                }
            }
            return selectedBooks;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            public TextView tv_title,tv_duedate,tv_fine,tv_reissue_count,tv_daysLeft;
            public RelativeLayout relativeLayout;
            public CheckBox reissueCheckBox;
            private boolean selected = false;


            public MyViewHolder(final View view) {
                super(view);
                tv_daysLeft=view.findViewById(R.id.days_left);
                tv_title = (TextView) view.findViewById(R.id.name);
                tv_duedate = view.findViewById(R.id.due_date);
                tv_fine = view.findViewById(R.id.fine_amt);
                tv_reissue_count=view.findViewById(R.id.re_issue_counter);
                reissueCheckBox = view.findViewById(R.id.reissue_checkbox);

                reIssueButtton=view.findViewById(R.id.re_issue_button);
                relativeLayout=view.findViewById(R.id.relative_layout);


                reissueCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(selected){
                            numberOfBooksSelected -= 1;
                            reissueCheckBox.setChecked(false);
                        }
                        else{
                            numberOfBooksSelected += 1;
                            reissueCheckBox.setChecked(true);
                        }
                        if(numberOfBooksSelected<1){
                            anyBookSelected = false;
                        }

                        // https://stackoverflow.com/questions/29983848/how-to-highlight-the-selected-item-of-recycler-view
                        selected =! selected;

                        selectedList[getAdapterPosition()] = selected;

                        relativeLayout.setSelected(selected);
                        if(selected){
                            anyBookSelected = true;
                        }
                    }
                });
                relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        anyBookSelected = true;

                        if(selected){
                            numberOfBooksSelected -= 1;
                            reissueCheckBox.setChecked(false);
                        }
                        else{
                            numberOfBooksSelected += 1;
                            reissueCheckBox.setChecked(true);
                        }
                        if(numberOfBooksSelected<1){
                            anyBookSelected = false;
                        }

                        // https://stackoverflow.com/questions/29983848/how-to-highlight-the-selected-item-of-recycler-view
                        selected =! selected;
                        selectedList[getAdapterPosition()] = false;
                        relativeLayout.setSelected(selected);
                        return true;
                    }
                });
                relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(anyBookSelected){

                            if(selected){
                                numberOfBooksSelected -= 1;
                                reissueCheckBox.setChecked(false);
                            }
                            else{
                                numberOfBooksSelected += 1;
                                reissueCheckBox.setChecked(true);
                            }
                            if(numberOfBooksSelected<1){
                                anyBookSelected = false;
                            }
                            selected =! selected;
                            selectedList[getAdapterPosition()] = selected;

                            relativeLayout.setSelected(selected);
                        }
                    }
                });
            }
        }

        public BooksAdapter(List<Book> books, Context context){
            bookList = books;
            this.context = context;
            selectedList = new boolean[books.size()];
            for(int i = 0; i<selectedList.length; i++){
                selectedList[i] = false;
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_single_book, parent, false);

            return new MyViewHolder(itemView);
        }

        @SuppressLint("SimpleDateFormat")
        public String f(int position) {
            String DaysLeft="";
            Book currentBook = bookList.get(position);
            String inputDateString = currentBook.getDueDate();
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
            return DaysLeft;
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            holder.selected = false;
            Book currentBook = bookList.get(position);
            holder.tv_title.setText(currentBook.getTitle());
            holder.tv_duedate.setText(currentBook.getDueDate());
            holder.tv_fine.setText(currentBook.getFineAmount());
            holder.tv_reissue_count.setText(currentBook.getRenewCount());
            holder.tv_daysLeft.setText(f(position));
        }

        @Override
        public int getItemCount() {
            return bookList.size();
        }
    }
}
