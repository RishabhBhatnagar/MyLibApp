package org.sfitengg.library.mylibapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.sfitengg.library.mylibapp.data.Book;
import org.sfitengg.library.mylibapp.data.DataHolder;
import org.sfitengg.library.mylibapp.nav_drawer_fragments.IssuedBooksFragment;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LibraryWidget extends AppWidgetProvider {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Toast.makeText(context, "update", Toast.LENGTH_SHORT).show();
            sharedPreferences = context.getSharedPreferences(MainActivity.titleSharedPrefs, MODE_PRIVATE);
            editor = sharedPreferences.edit();


            String bookString = sharedPreferences.getString(MainActivity.BOOKS_STRING_TAG, "no books borrowed");
            List<Book> books = new ArrayList<>();

            books = MainActivity.stringToBooks(bookString);
            if(books == null || books.size() < 1){
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
    }



    private void getData(){

        //TODO : @Vinay-Deshmukh : please, get the data from website here.
  /*      // Obtain the data holder object
        final DataHolder dataHolder = DataHolder.getDataHolder();
        final Handler handler = new Handler();

        GoGoGadget goGoGadget = new GoGoGadget(this,
                dataHolder.getBundleURLs(),
                GoGoGadget.LOGIN_AND_GET_COOKIES,
                handler);
        new Thread(goGoGadget).start();
 */
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
/*
class WidgetCallback extends MyCallback{

    @Override
    public void sendBooksToCaller(List<Book> books) {
        // This method will be executed after onCreate finishes

        bookList = books;

        if(books != null) {
            // If user has some borrowed books

            StringBuilder bookStringBuilder = new StringBuilder();
            for (Book book : books) {
                bookStringBuilder.append(bookToString(book));
            }

            editor.putString(BOOKS_STRING_TAG, bookStringBuilder.toString());

        } else {
            // If user has not borrwed any books

            editor.putString(BOOKS_STRING_TAG, NO_BOOKS_BORROWED);
        }

        editor.apply();

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Set books details
        Fragment fragment = IssuedBooksFragment.newInstance(books);

        fragmentManager.beginTransaction().replace( R.id.fragment_frame, fragment).commit();

    }

}
*/