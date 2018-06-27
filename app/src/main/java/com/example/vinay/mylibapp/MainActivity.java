package com.example.vinay.mylibapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Parcelable;
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
import android.widget.Toast;

import com.example.vinay.mylibapp.nav_drawer_fragments.IssuedBooksFragment;
import com.example.vinay.mylibapp.nav_drawer_fragments.LibExtrasFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.vinay.mylibapp.GoGoGadget.ERROR_INCORRECT_PID_OR_PASSWORD;
import static com.example.vinay.mylibapp.GoGoGadget.ERROR_NOT_LOGGED_IN;
import static com.example.vinay.mylibapp.GoGoGadget.ERROR_NO_INTERNET;
import static com.example.vinay.mylibapp.GoGoGadget.ERROR_SERVER_UNREACHABLE;
import static com.example.vinay.mylibapp.LoginActivity.KEY_COOKIES;
import static com.example.vinay.mylibapp.LoginActivity.titleSharedPrefs;

public class MainActivity extends AppCompatActivity implements MyCallback{

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    DataHolder dataHolder = new DataHolder(false);
    Handler handler = new Handler();

    // Loading Dialog
    AlertDialog.Builder alertDialogBuilder;
    Dialog loadingDialog;

    Map<String, String> cookies;
    List<Book> bookList;
    GoGoGadget gForBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a loadingDialog instance for the activity to show during network operations
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(R.layout.progress);
        loadingDialog = alertDialogBuilder.create();
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        // Set a Toolbar to replace the ActionBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //region Get outstanding documents for user before he requests it
        Intent intent = getIntent();
        // https://stackoverflow.com/a/7578313/9485900
        cookies = (HashMap<String, String>)intent.getSerializableExtra(KEY_COOKIES);
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



        // Find our drawer view
        mDrawerLayout = findViewById(R.id.drawer_layout);

        //region Nav Drawer Init and Listener
        nvDrawer = findViewById(R.id.nvDrawer);
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

    private void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Bundle bundle = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.frag_issued_books:
            default:

                //fragmentClass = FirstFragment.class;
                // TODO: Insert book list in bundle
                fragment = IssuedBooksFragment.newInstance(bookList);
                break;
            case R.id.frag_lib_extras:
                fragment = new LibExtrasFragment();
                // Using default constructor, since no need for custom args

                break;
//            case R.id.frag_about:
//                fragmentClass = ThirdFragment.class;
//                break;
            case R.id.option_sign_out:
                // TODO: Create sign out alert dialog
                AlertDialog signOut = new AlertDialog.Builder(this).create();
                signOut.setTitle("Do you want to sign out?");
                signOut.setButton(DialogInterface.BUTTON_POSITIVE,
                        "YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences sp = getSharedPreferences(titleSharedPrefs, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.apply();

                                // TODO: Maybe only clear the two keys
                                editor.clear();
                                editor.apply();

                                // Go back to LoginActivity
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                                // End this activity
                                finish();
                            }
                        });
                signOut.setButton(DialogInterface.BUTTON_NEGATIVE,
                        "NO",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // TODO: Load IssuedBooks

                                // Set check item to IssuedBooksFragment
                                nvDrawer.getMenu().getItem(0).setChecked(true);

                                // Dismiss the dialog
                                dialogInterface.dismiss();
                            }
                        });

                // Actually show the alert dialog
                signOut.show();

                // No Fragment for sign out option
                return;


//            default:
               // fragmentClass = FirstFragment.class;
        }

//        try {
//            fragment = (Fragment) fragmentClass.newInstance(bundle);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        //menuItem.setChecked(true); //already done in the listener
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        //mDrawerLayout.closeDrawers(); //already done in the listener


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Set books details
        Fragment fragment = IssuedBooksFragment.newInstance(books);

        fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment).commit();

    }

    @Override
    public void sendStudentNameToCaller(String name) {
        // Used only during login
        // Will not be called in this activity
    }

    @Override
    public void passErrorsToCaller(final int errorCode) {
        setLoadingDialog(false);
        switch (errorCode){
            case ERROR_NO_INTERNET:
            case ERROR_SERVER_UNREACHABLE:
            case -8:

                // Create AlertDialog for network failure
                AlertDialog loginFailedDialog = new AlertDialog.Builder(this).create();
                if ( errorCode == ERROR_NO_INTERNET) {
                    loginFailedDialog.setTitle("You are not connected to the internet");
                    loginFailedDialog.setMessage("Please connect to the internet and try again.");
                } else if ( errorCode == ERROR_SERVER_UNREACHABLE) {
                    loginFailedDialog.setTitle("Connection to the server failed!");
                    loginFailedDialog.setMessage("Server is unreachable.");
                }

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
                break;

            case ERROR_INCORRECT_PID_OR_PASSWORD:
            case ERROR_NOT_LOGGED_IN:
                throw new RuntimeException("If you have reached till MainActivity" +
                        "then these errors will not come.\n" +
                        "Check if you passed the cookies from the correct" +
                        " login, to the MainActivity intent");
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
