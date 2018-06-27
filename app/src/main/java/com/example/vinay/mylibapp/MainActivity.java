package com.example.vinay.mylibapp;

import android.app.Dialog;
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

import com.example.vinay.mylibapp.nav_drawer_fragments.IssuedBooksFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    // Loading Dialog
    AlertDialog.Builder alertDialogBuilder;
    Dialog loadingDialog;

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
                fragment = IssuedBooksFragment.newInstance(bundle);
                break;
//            case R.id.frag_lib_extras:
//                fragmentClass = SecondFragment.class;
//                break;
//            case R.id.frag_about:
//                fragmentClass = ThirdFragment.class;
//                break;
//            case R.id.option_sign_out:
//                // TODO: Create sign out alert dialog
//                break;

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

}
