package com.example.vinay.mylibapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import static com.example.vinay.mylibapp.GoGoGadget.ERROR_INCORRECT_PID_OR_PASSWORD;

public class LoginActivity extends AppCompatActivity implements MyCallback{

    EditText et_pid;
    EditText et_pwd;
    Button btn_login_submit;
    TextView tv_result_login;
    String pid;
    String pwd;


    // Make this true, to run on flask server
    // If testing=true, then accordingly change urls in the DataHolder constructor
    DataHolder dataHolder = new DataHolder(false);


    Handler handler = new Handler();


    AlertDialog.Builder alertDialogBuilder;
    Dialog loadingDialog;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    final String titleSharedPrefs = "my_prefs";
    final String KEY_PID = "pid";
    final String KEY_PWD = "pwd";

    // In the full_screen_loading layout
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        // set ContentView is done later, depending on wheter previous logged in user exists

        // Create a loadingDialog instance for the activity to show during network operations
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(R.layout.progress);
        loadingDialog = alertDialogBuilder.create();
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);


        // Create SharedPreferences
        sharedPreferences = getApplicationContext().getSharedPreferences(titleSharedPrefs, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
        pid = sharedPreferences.getString(KEY_PID, null);
        pwd = sharedPreferences.getString(KEY_PWD, null);

        if(pid != null && pwd != null){
            //region Previous logged in user exists

            // Then set loading screen as background
            setContentView(R.layout.full_screen_loading);

            progressBar = findViewById(R.id.network_progress);

            GoGoGadget goGoGadget = new GoGoGadget((MyCallback) LoginActivity.this,
                    dataHolder.getBundleURLs(),
                    GoGoGadget.LOGIN_AND_GET_COOKIES,
                    handler);
            new Thread(goGoGadget).start();
            //endregion
        }
        else {
            //region No Logged in user

            // If there is no logged in user,
            // show the login form
            setContentView(R.layout.activity_login);

            et_pid = findViewById(R.id.et_pid);
            et_pwd = findViewById(R.id.et_pwd);
            btn_login_submit = findViewById(R.id.btn_login_submit);
            tv_result_login = findViewById(R.id.tv_login_result);

            // TODO: Debug
            btn_login_submit.setText("BBBB");
            // if button has this text, even after incorrect login, then its working correct

            //region btn_login_submit OnClickListener
            btn_login_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    setLoadingDialog(true);

                    pid = et_pid.getText().toString();
                    pwd = et_pwd.getText().toString();


                    final GoGoGadget goGoGadget = new GoGoGadget((MyCallback) LoginActivity.this,
                            dataHolder.getBundleURLs(),
                            GoGoGadget.LOGIN_AND_GET_COOKIES,
                            handler);
                    new Thread(goGoGadget).start();
                }
            });
            //endregion
            //endregion
        }//else
    }

    @Override
    public void sendBooksToCaller(List<Book> books) {

    }

    @Override
    public void sendStudentNameToCaller(String name) {
        // This method will be called when login is correct

        // Do this only in the full_screen_loading layout, ie when it's not null
        // Make the progressbar disappear
        if(progressBar != null)
            progressBar.setVisibility(View.GONE);

        // Add the correct pid/pwd to shared prefs
        editor.putString(KEY_PID, pid);
        editor.putString(KEY_PWD, pwd);
        editor.apply();

        setLoadingDialog(false);
//        tv_result_login.setText("SUCCESS LOGIN");

        // Go to MainActivity
        final AlertDialog loginSuccessDialog = new AlertDialog.Builder(
                this).create();
        loginSuccessDialog.setTitle("Login Successful!");
        loginSuccessDialog.setMessage("Welcome " + name);
        loginSuccessDialog.setButton(Dialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int whichButton) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
        loginSuccessDialog.show();
    }

    @Override
    public void passErrorsToCaller(int errorCode) {
        setLoadingDialog(false);
//        tv_result_login.setText("Some error happened"+ errorCode);

        switch (errorCode){
            case ERROR_INCORRECT_PID_OR_PASSWORD:

                // Clear incorrect pid/pwd if present
                // TODO: Maybe change editor.clear() to making null for those specific keys
                editor.clear();
                editor.apply();


                // Create AlertDialog for login failure
                AlertDialog loginFailedDialog = new AlertDialog.Builder(this).create();
                loginFailedDialog.setTitle("Login Failed!");
                loginFailedDialog.setMessage("Incorrect PID or password");
                loginFailedDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        // Restart activity
                        recreate();
                        // Now, since we have cleared the shared prefs,
                        // The activity will have no previous user
                        // It will now go into the block for no previous user
                    }
                });

                // Show the dialog
                loginFailedDialog.show();
                break;
            //TODO: Other errors' handling remains

        }
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public String getPwd() {
        return pwd;
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

    @Override
    public void userHasBorrowedNoBooks() {

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
