package org.sfitengg.library.mylibapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.sfitengg.library.mylibapp.data.Book;
import org.sfitengg.library.mylibapp.data.DataHolder;
import org.sfitengg.library.mylibapp.nav_drawer_fragments.AboutFragment;
import org.sfitengg.library.mylibapp.nav_drawer_fragments.FaqFragment;
import org.sfitengg.library.mylibapp.nav_drawer_fragments.IssuedBooksFragment;
import org.sfitengg.library.mylibapp.nav_drawer_fragments.LibExtrasFragment;
import org.sfitengg.library.mylibapp.nav_drawer_fragments.LoggerInFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_INCORRECT_PID_OR_PASSWORD;
import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_NOT_LOGGED_IN;
import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_NO_INTERNET;
import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_POST_TO_REISSUE_FAILED;
import static org.sfitengg.library.mylibapp.GoGoGadget.ERROR_SERVER_UNREACHABLE;

public class MainActivity extends AppCompatActivity implements MyCallback{

    public static final String KEY_USER_NAME = "username";
    public static final String titleSharedPrefs = "my_prefs";
    public static final String KEY_PID = "pid";
    public static final String KEY_PWD = "pwd";
    protected static final String BOOKS_STRING_TAG = "bst";
    public static final String NO_BOOKS_BORROWED = "none";
    private DrawerLayout mDrawerLayout;
    private NavigationView nvDrawer;


    // SharedPreferences
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    // Obtain the data holder object
    DataHolder dataHolder = DataHolder.getDataHolder();
    Handler handler = new Handler();

    // Loading Dialog
    AlertDialog.Builder alertDialogBuilder;
    Dialog loadingDialog;
    AlertDialog reissueSuccessDialog;
    AlertDialog signOut;
    AlertDialog loginFailedDialog;
    private static final String feedback_url = "https://docs.google.com/forms/d/e/1FAIpQLScuO2G5us3_psE8MxA4bWv1A5wnmtm80xj62y8aLuIsLUEGVg/viewform";

    @Override
    protected void onPause() {
        super.onPause();

        // Dismiss all alert dialogs to prevent window leak
        Dialog[] arrayDialog = new Dialog[]{
                this.loadingDialog, this.reissueSuccessDialog,
                this.signOut, this.loginFailedDialog};
        for(Dialog d: arrayDialog){
            if(d != null && d.isShowing()){
                d.dismiss();
            }
        }
    }

    List<Book> bookList;


    //constants for encoding  books into strings.
    String attributeSeperator = "======";
    String bookSeperator = "#";


    String pid;
    String pwd;
    public void startLoginFromFragment(String pid, String pwd){
        // This method will be called from LoggerInFragment
        // It will start the login process

        setLoadingDialog(true);

        // Set the logging in values
        this.pid = pid;
        this.pwd = pwd;

        GoGoGadget goGoGadget = new GoGoGadget(
                this,
                dataHolder.getBundleURLs(),
                GoGoGadget.LOGIN_AND_GET_COOKIES,
                handler
        );
        new Thread(goGoGadget).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar;


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

        //region Create sharedPreferences
        sharedPreferences = getApplicationContext().getSharedPreferences(titleSharedPrefs, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
        //endregion


        // Find our drawer view
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                hideKeyboard(MainActivity.this);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                hideKeyboard(MainActivity.this);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                hideKeyboard(MainActivity.this);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                hideKeyboard(MainActivity.this);
            }
        });

        //region Nav Drawer Init and Listener
        nvDrawer = findViewById(R.id.nvDrawer);

        //region Set the username in nav drawer header
        String user_name = sharedPreferences.getString(KEY_USER_NAME, null);
        View headerView = nvDrawer.getHeaderView(0);
        TextView nameHeader = headerView.findViewById(R.id.header_name);
        if(user_name != null)
            nameHeader.setText(user_name);
        // else nameHeader.setText("Guest");
        // Note : uncomment this when guest login is allowed.


        //endregion


        nvDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        // Hide the keyboard
                        hideKeyboard(MainActivity.this);

                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        selectDrawerItem(menuItem, nvDrawer.getMenu());

                        return true;
                    }
                });
        //endregion


        // IssuedBooks MenuItem should be checked from default
        nvDrawer.getMenu().findItem(R.id.frag_issued_books).setChecked(true);

        pid = sharedPreferences.getString(KEY_PID, null);
        pwd = sharedPreferences.getString(KEY_PWD, null);

        if(pid != null && pwd != null){
            //region Previous logged in user exists
            // Then he will have his books stored

            String booksString = sharedPreferences.getString(BOOKS_STRING_TAG, null);

            if(booksString != null){
                // User has previously downloaded books

                bookList = stringToBooks(sharedPreferences.getString(MainActivity.BOOKS_STRING_TAG, NO_BOOKS_BORROWED));

                // Find the menu item for Issued Books
                MenuItem menuItemIssuedBooks =
                        nvDrawer.getMenu().findItem(R.id.frag_issued_books);

                // Highlight it in the drawer
                menuItemIssuedBooks.setChecked(true);

                // Call the logic needed to set IssuedBooksFragment on FrameLayout
                selectDrawerItem(menuItemIssuedBooks, nvDrawer.getMenu());
            }
            else {
                // Start thread operation to retrieve books
                startGetOutDocsAndCreateBooks();

            }

            //endregion
        }
        else {
            // If no previous logged in user
            // Start login fragment

            // Find the menu item for Issued Books
            MenuItem menuItemIssuedBooks =
                    nvDrawer.getMenu().findItem(R.id.frag_issued_books);

            // Highlight it in the drawer
            menuItemIssuedBooks.setChecked(true);


            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, new LoggerInFragment()).commit();

            // Set action bar title
            setTitle(menuItemIssuedBooks.getTitle());


        }
    }

    private String bookToString(Book book){
        String result =
                book.getAcc_no() + attributeSeperator +
                book.getDueDate() + attributeSeperator +
                book.getFineAmount() + attributeSeperator +
                book.getRenewCount() + attributeSeperator +
                book.getReservations() + attributeSeperator +
                book.getTitle() + attributeSeperator +
                String.valueOf(book.isCanRenew()) + attributeSeperator;

        if(!book.isInpNull()) {
            // If inp tags are not null, then retrieve
            // Store the input tags to retrieve them in reissue
            result += book.getInp_accno().outerHtml() + attributeSeperator +
                    book.getInp_media().outerHtml() + attributeSeperator +
                    book.getInp_chk().outerHtml() + attributeSeperator;
        }
        result += bookSeperator;
        return result;
    }

    private List<Book> stringToBooks(String booksListsString) {
        List<Book> bL = new ArrayList<>();
        if(booksListsString.equals(NO_BOOKS_BORROWED)){
            // Since no books are available, set list to null
            // This null will be handled in the IssuedBooksFragment
            bL = null;
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

                if(attributes.get(6).equals("true")){
                    // if true is written in string
                    book.setCanRenew(true);
                }
                else{
                    book.setCanRenew(false);
                }

                if(attributes.size() > 7) {
                    // If more than 7 elements exist, ie if the input tags are present
                    // Get inp tags back
                    Element inp_acc = Jsoup.parse(attributes.get(7)).select("input").first();
                    Element inp_med = Jsoup.parse(attributes.get(8)).select("input").first();
                    Element inp_chk = Jsoup.parse(attributes.get(9)).select("input").first();

                    book.setInp_accno(inp_acc);
                    book.setInp_media(inp_med);
                    book.setInp_chk(inp_chk);
                }
                bL.add(book);
            }
        }
        return bL;
    }

    private void selectDrawerItem(MenuItem menuItem, Menu menu) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment newFragmentToPutInFrame;

        switch(menuItem.getItemId()) {
            case R.id.frag_issued_books:
            default:
                // default is used, so that IssuedBooksFragment is loaded for invalid menuItme
                if(null != sharedPreferences.getString(KEY_USER_NAME, null)){
                    // if user name is not null, then someone is logged in
                    newFragmentToPutInFrame = IssuedBooksFragment.newInstance(bookList);
                }
                else{
                    // no one is logged in
                    newFragmentToPutInFrame = new LoggerInFragment();
                }

                break;
            case R.id.frag_lib_extras:
                newFragmentToPutInFrame = new LibExtrasFragment();
                // Using default constructor, since no need for custom args

                break;
            case R.id.frag_about:
                newFragmentToPutInFrame = new AboutFragment();
                break;
            case R.id.frag_faq:{
                newFragmentToPutInFrame = new FaqFragment();
                break;
            }
            case R.id.option_feedback:{
                newFragmentToPutInFrame = null;

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(feedback_url));
                startActivity(i);
                menu.getItem(0).setChecked(true);

                break;
            }
            case R.id.option_sign_out:

                signOut = new AlertDialog.Builder(this).create();

                if(null != sharedPreferences.getString(KEY_USER_NAME, null)) {
                    // Set positive negative buttons, when user is logged in

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

                                    // Show LoggerInFragment
                                    getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.fragment_frame, new LoggerInFragment())
                                            .commit();


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
                                    selectDrawerItem(menuItemIssuedBooks, nvDrawer.getMenu());
                                    //endregion

                                    // Dismiss the dialog
                                    dialogInterface.dismiss();
                                }
                            });
                    //endregion

                }
                else {
                    // When user is not logged in
                    signOut.setTitle("You are not logged in!");

                    //region OK button for doing nothing
                    signOut.setButton(DialogInterface.BUTTON_NEUTRAL,
                            "OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // do nothing
                                    dialogInterface.dismiss();
                                }
                            });
                    //endregion
                }

                // Actually show the alert dialog
                signOut.show();

                // No Fragment for sign out option
                return;
        }// switch


        if(newFragmentToPutInFrame != null) {
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, newFragmentToPutInFrame).commit();
            // Set action bar title
            setTitle(menuItem.getTitle());
        }

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

        GoGoGadget gSendReissue = new GoGoGadget(
                this,
                dataHolder.getBundleURLs(),
                GoGoGadget.SEND_REISSUE,
                handler,
                booksToReissue
        );



        new Thread(gSendReissue).start();
    }

    public void startGetOutDocsAndCreateBooks(){

        // Start a indefinite loading dialog
        setLoadingDialog(true);

        GoGoGadget gGetDocs = new GoGoGadget(
                this,
                dataHolder.getBundleURLs(),
                GoGoGadget.GET_OUT_DOCS,
                handler
        );
        new Thread(gGetDocs).start();
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

        // Set the username in header
        TextView nameHeader = nvDrawer.getHeaderView(0).findViewById(R.id.header_name);
        nameHeader.setText(name);

        setLoadingDialog(false);

        // Go to MainActivity
        AlertDialog loginSuccessDialog = new AlertDialog.Builder(
                this).create();
        loginSuccessDialog.setTitle("Login Successful!");
        loginSuccessDialog.setMessage("Welcome "+ name);
        loginSuccessDialog.setCancelable(false);
        loginSuccessDialog.setCanceledOnTouchOutside(false);



        //region Set OK button for loginSuccessDialog
        loginSuccessDialog.setButton(
                Dialog.BUTTON_NEUTRAL,
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int whichButton) {

                        // Dismiss the dialog box
                        dialogInterface.dismiss();

                        // Get the out docs for this user now
                        startGetOutDocsAndCreateBooks();

                    }
                });
        //endregion

        // Actually show the dialog
        loginSuccessDialog.show();
    }

    @Override
    public void postToOutDocsSuccess() {
        // Reissue post request has been success
        setLoadingDialog(false);

        reissueSuccessDialog = new AlertDialog.Builder(this).create();
        reissueSuccessDialog.setTitle("Books Reissued Successfully!");
        reissueSuccessDialog.setMessage("Click OK To Reload !");
        reissueSuccessDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Recreate the activity to reload everything
                        // recreate();

                        // Reload out docs books
                        startGetOutDocsAndCreateBooks();
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
            case ERROR_INCORRECT_PID_OR_PASSWORD:
            case -8:// This line is used only because the if else ladder below gives a warning for
                // ERROR_INCORRECT_PID_OR_PASSWORD always true, since it's the last value for this case
                // So, to avoid that warning, adding a impossible value here.

                //region Create AlertDialog(loginFailedDialog) for network failure
                loginFailedDialog = new AlertDialog.Builder(this).create();
                loginFailedDialog.setCanceledOnTouchOutside(false);
                loginFailedDialog.setCancelable(true);
                if ( errorCode == ERROR_NO_INTERNET) {
                    loginFailedDialog.setTitle("You are not connected to the Internet");
                    loginFailedDialog.setMessage("Please connect to the Internet and try again.");

                } else if ( errorCode == ERROR_SERVER_UNREACHABLE) {
                    loginFailedDialog.setTitle("Connection to the server failed!");
                    loginFailedDialog.setMessage("Server is unreachable.");
                } else if ( errorCode == ERROR_INCORRECT_PID_OR_PASSWORD) {
                    loginFailedDialog.setTitle("The PID/password that you entered was incorrect!");
                    loginFailedDialog.setMessage("Please try again!");
                }

                //endregion

                //region loginFailedDialog OK Button
                loginFailedDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                if(!this.isFinishing()) {
                    loginFailedDialog.show();
                }
                //endregion
                break;


            case ERROR_NOT_LOGGED_IN:
                throw new RuntimeException("If you have reached till MainActivity" +
                        "then these errors will not come.\n" +
                        "Check if you passed the cookies from the correct" +
                        " login, to the MainActivity intent");
            case ERROR_POST_TO_REISSUE_FAILED:
                Toast.makeText(this,
                        "Something went wrong when reissuing the books. Please try again!",
                        Toast.LENGTH_LONG).show();
                break;
            default:
                    Toast.makeText(this,"Error" + errorCode, Toast.LENGTH_SHORT).show();

        }

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
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
