package org.sfitengg.library.mylibapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.sfitengg.library.mylibapp.data.Book;
import org.sfitengg.library.mylibapp.data.DataHolder;
import org.sfitengg.library.mylibapp.nav_drawer_fragments.AboutFragment;
import org.sfitengg.library.mylibapp.nav_drawer_fragments.IssuedBooksFragment;
import org.sfitengg.library.mylibapp.nav_drawer_fragments.LibExtrasFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_INCORRECT_PID_OR_PASSWORD;
import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_NOT_LOGGED_IN;
import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_NO_INTERNET;
import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_POST_TO_REISSUE_FAILED;
import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_SERVER_UNREACHABLE;
import static org.sfitengg.library.mylibapp.LoginActivity.KEY_COOKIES;
import static org.sfitengg.library.mylibapp.LoginActivity.titleSharedPrefs;

public class MainActivity extends AppCompatActivity implements MyCallback{

    protected static final String BOOKS_STRING_TAG = "bst";
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    // Obtain the data holder object
    DataHolder dataHolder = DataHolder.getDataHolder();
    Handler handler = new Handler();

    // Loading Dialog
    AlertDialog.Builder alertDialogBuilder;
    Dialog loadingDialog;

    Map<String, String> cookies;
    List<Book> bookList;
    GoGoGadget gForBooks;


    //constants for encoding  books into strings.
    String attributeSeperator = ", ";
    String bookSeperator = "#";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.sfitengg.library.mylibapp.R.layout.activity_main);

        //region Create a loadingDialog instance for the activity to show during network operations
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(R.layout.loading_dialog);
        loadingDialog = alertDialogBuilder.create();
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        //endregion

        //region Setup a Toolbar to replace the ActionBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        //endregion




        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.getBoolean(LoginActivity.SKIPPED, false)) {

            //region Get outstanding documents for user before he requests it, ie in the onCreate
            Intent intent = getIntent();
            // https://stackoverflow.com/a/7578313/9485900
            cookies = (HashMap<String, String>) intent.getSerializableExtra(KEY_COOKIES);
            // Now we have cookies, so get the data in the books
            gForBooks = new GoGoGadget((MyCallback) this,
                    dataHolder.getBundleURLs(),
                    GoGoGadget.GET_OUT_DOCS,
                    handler);

            // Set the cookies needed for access
            gForBooks.setCookies(cookies);

            new Thread(gForBooks).start();

            // Start a indefinite loading dialog
            setLoadingDialog(true);

            // This will be stopped in one of the callback methods

            //endregion
        }
        else{
            bookList = stringToBooks(sharedPreferences.getString(MainActivity.BOOKS_STRING_TAG, "none"));
            }


        // Find our drawer view
        mDrawerLayout = findViewById(R.id.drawer_layout);

        //region Nav Drawer Init and Listener
        nvDrawer = findViewById(R.id.nvDrawer);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            View headerView = nvDrawer.getHeaderView(0);
            TextView nameHeader = (TextView) headerView.findViewById(R.id.header_name);
            nameHeader.setText(value);

        }
        nvDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        selectDrawerItem(menuItem);

                        return true;
                    }
                });
        //endregion
    }


    private String bookToString(Book book){
        String op = "";
        op += book.getAcc_no() + attributeSeperator;
        op += book.getDueDate() + attributeSeperator;
        op += book.getFineAmount() + attributeSeperator;
        op += book.getRenewCount() + attributeSeperator;
        op += book.getReservations() + attributeSeperator;
        op += book.getTitle() + bookSeperator;
        return op;
    }

    private List<Book> stringToBooks(String booksListsString) {
        List<Book> bL = new ArrayList<>();
        if(booksListsString.equals("none")){
            //TODO : no bookList String found.
        }
        else{
            for(String bookString : booksListsString.split(bookSeperator)){

                Book book = new Book();
                List<String> attributes= Arrays.asList(bookString.split(attributeSeperator));
                book.setAcc_no(attributes.get(0));
                book.setDueDate(attributes.get(1));
                book.setFineAmount(attributes.get(2));
                book.setRenewCount(attributes.get(3));
                book.setReservations(attributes.get(4));
                book.setTitle(attributes.get(5));

                bL.add(book);
            }
        }
        return bL;
    }

    private void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment newFragmentToPutInFrame;

        switch(menuItem.getItemId()) {
            case R.id.frag_issued_books:
            default:
                // default is used, so that IssuedBooksFragment is loaded for invalid menuItme
                newFragmentToPutInFrame = IssuedBooksFragment.newInstance(bookList);
                break;
            case R.id.frag_lib_extras:
                newFragmentToPutInFrame = new LibExtrasFragment();
                // Using default constructor, since no need for custom args

                break;
            case R.id.frag_about:
                newFragmentToPutInFrame = new AboutFragment();
                break;
            case R.id.option_sign_out:

                AlertDialog signOut = new AlertDialog.Builder(this).create();
                signOut.setTitle("Do you want to sign out?");
                //region Set Positive Button for signOut
                signOut.setButton(DialogInterface.BUTTON_POSITIVE,
                        "YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences sp = getSharedPreferences(titleSharedPrefs, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.apply();

                                // TODO: Maybe only clear the two keys, instead of clearing the shared prefs
                                editor.clear();
                                editor.apply();

                                // Go back to LoginActivity
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                                // End this activity
                                finish();
                            }
                        });
                //endregion
                //region Set Negative Button for signOut
                signOut.setButton(DialogInterface.BUTTON_NEGATIVE,
                        "NO",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //region Set the IssuedBooksFragment when sign out is denied

                                // Find the menu item for Issued Books
                                MenuItem menuItemIssuedBooks =
                                        nvDrawer.getMenu().findItem(R.id.frag_issued_books);

                                // Highlight it in the drawer
                                menuItemIssuedBooks.setChecked(true);

                                // Call the logic needed to set IssuedBooksFragment on FrameLayout
                                selectDrawerItem(menuItemIssuedBooks);
                                //endregion

                                // Dismiss the dialog
                                dialogInterface.dismiss();
                            }
                        });
                //endregion

                // Actually show the alert dialog
                signOut.show();

                // No Fragment for sign out option
                return;

        }// switch


        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_frame, newFragmentToPutInFrame).commit();

        // Set action bar title
        setTitle(menuItem.getTitle());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open drawer with the button on action bar
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startReissueProcess(List<Book> booksToReissue){
        // This method will be called from the IssuedBooksFragment
        // This method handles all the logic, so that function calls are kept within this activity

        // Start loading dialog
        setLoadingDialog(true);

        GoGoGadget gSendReissue = new GoGoGadget((MyCallback)this,
                dataHolder.getBundleURLs(),
                GoGoGadget.SEND_REISSUE,
                handler,
                booksToReissue);
        gSendReissue.setCookies(cookies);

        new Thread(gSendReissue).start();
    }

    private void setLoadingDialog(boolean show){
        // Sauce for dialog creation, and this setter method:
        // https://stackoverflow.com/a/14853439/9485900

        if (show) {
            loadingDialog.show();
        } else {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void sendBooksToCaller(List<Book> books) {
        // This method will be executed after onCreate finishes

        bookList = books;
        setLoadingDialog(false);



        String booksString = "";
        for(Book book:books){
            booksString += bookToString(book);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BOOKS_STRING_TAG, booksString);
        editor.apply();


        FragmentManager fragmentManager = getSupportFragmentManager();

        // Set books details
        Fragment fragment = IssuedBooksFragment.newInstance(books);

        fragmentManager.beginTransaction().replace( R.id.fragment_frame, fragment).commit();

//
//        List<Date> dateList = new ArrayList<>();
//        for(Book b : bookList){
//            b.getTitle();
//        }

    }

    @Override
    public void sendStudentNameToCaller(String name) {
        // Used only during login
        // Will not be called in this activity
    }

    @Override
    public void postToOutDocsSuccess() {
        // Reissue post request has been success
        setLoadingDialog(false);

        AlertDialog reissueSuccessDialog = new AlertDialog.Builder(this).create();
        reissueSuccessDialog.setTitle("Books reissued successfully!");
        reissueSuccessDialog.setMessage("Do you want to reload?");
        reissueSuccessDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Recreate the activity to reload everything
                        recreate();
                    }
                });

        reissueSuccessDialog.show();

    }

    @Override
    public void passErrorsToCaller(final int errorCode) {
        setLoadingDialog(false);
        switch (errorCode){
            case ERROR_NO_INTERNET:
            case ERROR_SERVER_UNREACHABLE:
            case -8:// This line is used only because the if else ladder below gives a warning for
                // ERROR_SERVER_UNREACHABLE always true, since it's the last value for this case
                // So, to avoid that warning, adding a impossible value here.

                //region Create AlertDialog(loginFailedDialog) for network failure
                AlertDialog loginFailedDialog = new AlertDialog.Builder(this).create();
                if ( errorCode == ERROR_NO_INTERNET) {
                    loginFailedDialog.setTitle("You are not connected to the internet");
                    loginFailedDialog.setMessage("Please connect to the internet and try again.");
                } else if ( errorCode == ERROR_SERVER_UNREACHABLE) {
                    loginFailedDialog.setTitle("Connection to the server failed!");
                    loginFailedDialog.setMessage("Server is unreachable.");
                }
                //endregion

                //region loginFailedDialog OK Button
                loginFailedDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        if(errorCode == ERROR_NO_INTERNET || errorCode == ERROR_SERVER_UNREACHABLE){
                            // Quit the app
                            finish();
                            System.exit(0);
                        }

                    }
                });
                //endregion
                break;

            case ERROR_INCORRECT_PID_OR_PASSWORD:
            case ERROR_NOT_LOGGED_IN:
                throw new RuntimeException("If you have reached till MainActivity" +
                        "then these errors will not come.\n" +
                        "Check if you passed the cookies from the correct" +
                        " login, to the MainActivity intent");
            case ERROR_POST_TO_REISSUE_FAILED:
                Toast.makeText(this,
                        "Something went wrong when posting to out docs page",
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                    Toast.makeText(this,"Error" + errorCode, Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public String getPid() {
        // Used only during login
        // Will not be called in this activity
        return null;
    }

    @Override
    public String getPwd() {
        // Used only during login
        // Will not be called in this activity
        return null;
    }

    @Override
    public boolean isConnectedToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


}
