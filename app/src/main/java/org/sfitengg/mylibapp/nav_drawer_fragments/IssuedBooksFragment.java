package org.sfitengg.mylibapp.nav_drawer_fragments;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sfitengg.mylibapp.MainActivity;
import org.sfitengg.mylibapp.data.Book;
import org.sfitengg.mylibapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class IssuedBooksFragment extends Fragment {

private static final String KEY_BOOKS = "books";
    private final List<Book> bookList = new ArrayList<>();
    private BooksAdapter mBooksAdapter;
    private static int numberOfBooksSelected = 0;
    private Button reIssueButton;
    private SwipeRefreshLayout swipeLayout;


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
        Objects.requireNonNull(getActivity()).setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        reIssueButton = view.findViewById(R.id.re_issue_button);
        swipeLayout =  view.findViewById(R.id.swiperefresh);
        swipeLayout.setColorSchemeColors(Color.RED,Color.BLUE,Color.YELLOW);


        MainActivity mainActivity = ((MainActivity) getActivity());
        if(mainActivity.isConnectedToInternet() && !mainActivity.LOGIN_IN_THIS_RUN){
            myUpdateOperation();
            mainActivity.LOGIN_IN_THIS_RUN = true;
        }

        Bundle args = getArguments();

        List<Parcelable> parcelBooks = Objects.requireNonNull(args).getParcelableArrayList(KEY_BOOKS);

        if(parcelBooks == null){
            // if user has no books borrowed

            TextView textView = new TextView(getActivity());
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            textView.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Medium);
            textView.setText(getString(R.string.no_books_issued));

            return textView;
        }

        // Retrieve books
        for (Parcelable p : parcelBooks) {
            Book b = (Book) p;
            bookList.add(b);
        }

        // Setup the recycler-view
        RecyclerView recyclerView = view.findViewById(R.id.issued_books_recycler_view);
        mBooksAdapter = new BooksAdapter(bookList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mBooksAdapter);

        // On click listener for reissue


        swipeLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        myUpdateOperation();
                    }
                }
        );



        reIssueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Book> booksToReissue = mBooksAdapter.getBooks();

                // This method will handle all the function calls for MainActivity
                // It's created so that code is written within MainActivity for clarity
                ((MainActivity)getActivity()).startReissueProcess(booksToReissue);

            }
        });


        return view;
    }

    private void myUpdateOperation(){

        ((MainActivity) Objects.requireNonNull(getActivity())).startGetOutDocsAndCreateBooks();
        swipeLayout.setRefreshing(false);
    }

    // private since we only need it inside this class
    private class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder> {

        private final List<Book> bookList;
        private boolean anyBookSelected = false;
        private final boolean[] selectedList;


        private List<Book> getBooks(){
            List<Book> selectedBooks = new ArrayList<>();
            for(int i = 0; i<selectedList.length; i++){
                if(selectedList[i]){
                    selectedBooks.add(bookList.get(i));
                }
            }
            return selectedBooks;
        }


        class MyViewHolder extends RecyclerView.ViewHolder{

            final TextView tv_title;
            final TextView tv_duedate;
            final TextView tv_fine;
            final TextView tv_reissue_count;
            final TextView tv_acc;
            final RelativeLayout relativeLayout;
            final CheckBox reissueCheckBox;
            private boolean selected = false;
            final TextView tv_daysLeft;




            void Onclick(){

                if(selected){
                    numberOfBooksSelected -= 1;
                    reissueCheckBox.setChecked(false);
                    if(numberOfBooksSelected==0){
                        reIssueButton.setEnabled(false);
                        reIssueButton.setBackgroundResource(R.drawable.disable);

                    }
                }
                else{
                    numberOfBooksSelected += 1;
                    reissueCheckBox.setChecked(true);
                    reIssueButton.setEnabled(true);
                    reIssueButton.setBackgroundResource(R.drawable.shape);

                }
                if(numberOfBooksSelected<1){
                    anyBookSelected = false;
                }
                selected =! selected;

            }


            MyViewHolder(final View view) {
                super(view);
                tv_daysLeft=view.findViewById(R.id.days_left);
                tv_title = view.findViewById(R.id.name);
                tv_duedate = view.findViewById(R.id.due_date);
                tv_fine = view.findViewById(R.id.fine_amt);
                tv_reissue_count=view.findViewById(R.id.re_issue_counter);
                reissueCheckBox = view.findViewById(R.id.reissue_checkbox);
                relativeLayout=view.findViewById(R.id.relative_layout);
                tv_acc = view.findViewById(R.id.acc);




                reIssueButton.setEnabled(false);
                reIssueButton.setBackgroundResource(R.drawable.disable);

                //region onClickListener reissue
                reissueCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                         Onclick();

                        // https://stackoverflow.com/questions/29983848/how-to-highlight-the-selected-item-of-recycler-view
                        selectedList[getAdapterPosition()] = selected;
                        relativeLayout.setSelected(selected);
                        if(selected){
                            anyBookSelected = true;
                        }
                    }
                });
                //endregion

                //region onclick
                relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        anyBookSelected = true;

                         Onclick();
                        // https://stackoverflow.com/questions/29983848/how-to-highlight-the-selected-item-of-recycler-view
                        selectedList[getAdapterPosition()] = false;
                        relativeLayout.setSelected(selected);
                        return true;
                    }
                });
                //endregion
                relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         if(anyBookSelected){
                            Onclick();
                            selectedList[getAdapterPosition()] = selected;
                            relativeLayout.setSelected(selected);
                        }
                    }
                });


            }

        }

        BooksAdapter(List<Book> books){
            bookList = books;
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

       int DaysLeft=0;
        @SuppressLint("SimpleDateFormat")// added supress lint to take care of "US date format" which is mm/dd/yyyy
        String daysleft(int position) {
            String daysLeft="";
            Book currentBook = bookList.get(position);
            SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date todayDate = new Date();
            String inputString1 = myFormat.format(todayDate);
            String inputString2 = currentBook.getDueDate();

            try {
                Date date1 = myFormat.parse(inputString1);
                Date date2 = myFormat.parse(inputString2);
                long diff = date2.getTime() - date1.getTime();
                          DaysLeft = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                          daysLeft= String.valueOf(DaysLeft);
            } catch (ParseException e) {
                e.printStackTrace();
            }

                if(DaysLeft<=0) {
                    daysLeft="0";
                    return daysLeft;
                }else{
                return daysLeft;
                }

        }


        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            holder.selected = false;
            Book currentBook = bookList.get(position);
            holder.tv_title.setText(currentBook.getTitle());
            holder.tv_duedate.setText(currentBook.getDueDate());
            holder.tv_fine.setText(currentBook.getFineAmount());
            holder.tv_reissue_count.setText(currentBook.getRenewCount());
            holder.tv_daysLeft.setText(daysleft(position));
            holder.tv_acc.setText(currentBook.getAcc_no());






            if(!bookList.get(position).isCanRenew()) {
                holder.reissueCheckBox.setChecked(false);
                holder.reissueCheckBox.setEnabled(false);
                holder.relativeLayout.setEnabled(false);
                holder.relativeLayout.setSelected(false);
            }

        }

        @Override
        public int getItemCount() {
            return bookList.size();
        }
    }
}