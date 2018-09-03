package org.sfitengg.library.mylibapp;

import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.sfitengg.library.mylibapp.data.Book;
import org.sfitengg.library.mylibapp.data.DataHolder;
import org.sfitengg.library.mylibapp.nav_drawer_fragments.IssuedBooksFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_INCORRECT_PID_OR_PASSWORD;
import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_NOT_LOGGED_IN;
import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_NO_INTERNET;
import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_POST_TO_REISSUE_FAILED;
import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_SERVER_UNREACHABLE;

public class LibraryWidget extends AppWidgetProvider implements MyCallback{

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;












    private String pid, pwd;
    private List<Book> bookList;
    private static final String KEY_USER_NAME = "username";
    private static final String KEY_PID = "pid";
    private static final String KEY_PWD = "pwd";
    protected static final String BOOKS_STRING_TAG = "bst";
    private static final String NO_BOOKS_BORROWED = "none";

    // Obtain the data holder object
    private static final DataHolder dataHolder = DataHolder.getDataHolder();
    private static final Handler handler = new Handler();
    private boolean updateRequired = true;
    private Context context;

    @Override
    public void postToOutDocsSuccess() {
        startGetOutDocsAndCreateBooks();
    }

    @Override
    public void passErrorsToCaller(final int errorCode) {

        switch (errorCode){
            case ERROR_NO_INTERNET:
            case ERROR_SERVER_UNREACHABLE:
            case ERROR_INCORRECT_PID_OR_PASSWORD:
            case -8:// This line is used only because the if else ladder below gives a warning for
                // ERROR_INCORRECT_PID_OR_PASSWORD always true, since it's the last value for this case
                // So, to avoid that warning, adding a impossible value here.

                //region Create AlertDialog(loginFailedDialog) for network failure
                switch (errorCode) {
                    case ERROR_NO_INTERNET:
                        break;
                    case ERROR_SERVER_UNREACHABLE:
                        break;
                    case ERROR_INCORRECT_PID_OR_PASSWORD:
                        break;
                }

                //endregion
                break;


            case ERROR_NOT_LOGGED_IN:
                throw new RuntimeException("If you have reached till MainActivity" +
                        "then these errors will not come.\n" +
                        "Check if you passed the cookies from the correct" +
                        " login, to the MainActivity intent");
            case ERROR_POST_TO_REISSUE_FAILED:
                Log.e("error", "Something went wrong when reissuing the books. Please try again!");
                break;
            default:
                Log.e("error","Error" + errorCode);
        }

    }

    @Override
    public void sendStudentNameToCaller(String name) {
        // Used only during login
        // This method will be called when login is correct

        // Add the correct pid/pwd to shared prefs
        editor.putString(KEY_PID, pid);
        editor.putString(KEY_PWD, pwd);
        // Add the username to shared prefs
        editor.putString(KEY_USER_NAME, name);
        editor.apply();

        // Get the out docs for this user now
        startGetOutDocsAndCreateBooks();

    }

    public void startGetOutDocsAndCreateBooks(){
        GoGoGadget gGetDocs = new GoGoGadget(this,
                dataHolder.getBundleURLs(),
                GoGoGadget.GET_OUT_DOCS,
                handler);
        new Thread(gGetDocs).start();
    }

    @Override
    public void sendBooksToCaller(List<Book> books) {
        // This method will be executed after onCreate finishes

        updateRequired = true;
        bookList = books;

        if(books != null) {
            // If user has some borrowed books

            StringBuilder bookStringBuilder = new StringBuilder();
            for (Book book : books) {
                bookStringBuilder.append(MainActivity.bookToString(book));
            }

            editor.putString(BOOKS_STRING_TAG, bookStringBuilder.toString());

        } else {
            // If user has not borrwed any books

            editor.putString(BOOKS_STRING_TAG, NO_BOOKS_BORROWED);
        }

        editor.apply();
    }

    @Override
    public String getPid() {
        // Used only during login
        // Will be called in this activity since we need to login again to get fresh cookies
        return pid;//sharedPreferences.getString(KEY_PID, "1"); // 1 is any non null value
    }

    @Override
    public String getPwd() {
        // Used only during login
        // Will be called in this activity since we need to login again to get fresh cookies
        return pwd;//sharedPreferences.getString(KEY_PWD, "1"); // 1 is any non null value
    }

    @Override
    public boolean isConnectedToInternet() {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }








    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.context = context;
        if(updateRequired) {


            // Todo : uncomment getData() and resolve the error.
            //getData();

            for (int appWidgetId : appWidgetIds) {
                Toast.makeText(context, "update", Toast.LENGTH_SHORT).show();
                sharedPreferences = context.getSharedPreferences(MainActivity.titleSharedPrefs, MODE_PRIVATE);
                editor = sharedPreferences.edit();



                String bookString = sharedPreferences.getString(MainActivity.BOOKS_STRING_TAG, "no books borrowed");
                List<Book> books = MainActivity.stringToBooks(bookString);
                if (books == null || books.size() < 1) {
                    books = new ArrayList<>();
                    Book book = new Book();
                    book.setTitle("no books");
                    books.add(book);
                }
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                RemoteViews appWidgetViews = new RemoteViews(context.getPackageName(), R.layout.library_widget);

                appWidgetViews.setOnClickPendingIntent(R.id.widget_parent, pendingIntent);
                appWidgetViews.setTextViewText(R.id.time, books.get(0).getTitle());
                appWidgetManager.updateAppWidget(appWidgetId, appWidgetViews);
            }
            updateRequired = false;
        }
    }



    private void getData(){

        // Obtain the data holder object
        final DataHolder dataHolder = DataHolder.getDataHolder();
        final Handler handler = new Handler();

        GoGoGadget goGoGadget = new GoGoGadget(this,
                dataHolder.getBundleURLs(),
                GoGoGadget.LOGIN_AND_GET_COOKIES,
                handler);
        new Thread(goGoGadget).start();

    }




    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "received", Toast.LENGTH_SHORT).show();
        super.onReceive(context, intent);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Toast.makeText(context, "option change", Toast.LENGTH_SHORT).show();
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
}