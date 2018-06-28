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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vinay.mylibapp.data.Book;
import com.example.vinay.mylibapp.data.DataHolder;

import java.util.HashMap;
import java.util.List;

import static com.example.vinay.mylibapp.GoGoGadget.ERROR_INCORRECT_PID_OR_PASSWORD;
import static com.example.vinay.mylibapp.GoGoGadget.ERROR_NOT_LOGGED_IN;
import static com.example.vinay.mylibapp.GoGoGadget.ERROR_NO_INTERNET;
import static com.example.vinay.mylibapp.GoGoGadget.ERROR_SERVER_UNREACHABLE;

public class LoginActivity extends AppCompatActivity implements MyCallback{

    public static final String KEY_COOKIES = "cookies";

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

    // For loading dialog
    AlertDialog.Builder alertDialogBuilder;
    Dialog loadingDialog;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String titleSharedPrefs = "my_prefs";
    public static final String KEY_PID = "pid";
    public static final String KEY_PWD = "pwd";

    // In the loading_screen layout
    ProgressBar progressBar;

    GoGoGadget goGoGadget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        // set ContentView is done later, depending on wheter previous logged in user exists

        // Create a loadingDialog instance for the activity to show during network operations
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(R.layout.loading_dialog);
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
            setContentView(R.layout.loading_screen);

            progressBar = findViewById(R.id.network_progress);

            goGoGadget = new GoGoGadget((MyCallback) LoginActivity.this,
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


                    goGoGadget = new GoGoGadget((MyCallback) LoginActivity.this,
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
        // Do nothing, since this activity will never request the books
    }

    @Override
    public void sendStudentNameToCaller(String name) {
        // This method will be called when login is correct

        // Do this only in the loading_screen layout, ie when it's not null
        // Make the progressbar disappear
        if(progressBar != null)
            progressBar.setVisibility(View.GONE);

        // Add the correct pid/pwd to shared prefs
        editor.putString(KEY_PID, pid);
        editor.putString(KEY_PWD, pwd);
        editor.apply();

        setLoadingDialog(false);

        // Go to MainActivity
        final AlertDialog loginSuccessDialog = new AlertDialog.Builder(
                this).create();
        loginSuccessDialog.setTitle("Login Successful!");
        loginSuccessDialog.setMessage("Welcome " + name);
        //region Set OK button for loginSuccessDialog
        loginSuccessDialog.setButton(
                Dialog.BUTTON_NEUTRAL,
                "OK",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int whichButton) {

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                // https://stackoverflow.com/a/7578313/9485900
                // Convert logged in cookies to HashMap since
                // HashMap is Serializable, but Map isn't
                intent.putExtra(KEY_COOKIES,(HashMap<String, String>) goGoGadget.getCookies());

                startActivity(intent);

                // Since we don't want users to come back to this activity, after being logged in
                finish();
            }
        });
        //endregion
        loginSuccessDialog.show();
    }

    @Override
    public void passErrorsToCaller(final int errorCode) {
        setLoadingDialog(false);
//        tv_result_login.setText("Some error happened"+ errorCode);

        switch (errorCode){
            case ERROR_INCORRECT_PID_OR_PASSWORD:
            case ERROR_NO_INTERNET:
            case ERROR_SERVER_UNREACHABLE:
            case -8: // This line is used only because the if else ladder below gives a warning for
                // ERROR_SERVER_UNREACHABLE always true, since it's the last value for this case
                // So, to avoid that warning, adding a impossible value here.

                //region Handle errors by setting message on alert dialog

                // Clear incorrect pid/pwd if present
                // TODO: Maybe change editor.clear() to making null for those specific keys
                if(errorCode == ERROR_INCORRECT_PID_OR_PASSWORD) {
                    editor.clear();
                    editor.apply();
                }


                // Create AlertDialog for login failure
                AlertDialog loginFailedDialog = new AlertDialog.Builder(this).create();
                loginFailedDialog.setTitle("Login Failed!");

                // Set reason for failure
                if(errorCode == ERROR_INCORRECT_PID_OR_PASSWORD)
                    loginFailedDialog.setMessage("Incorrect PID or password");
                else if ( errorCode == ERROR_NO_INTERNET) {
                    loginFailedDialog.setTitle("You are not connected to the internet");
                    loginFailedDialog.setMessage("Please connect to the internet and try again.");
                } else if ( errorCode == ERROR_SERVER_UNREACHABLE) {
                    loginFailedDialog.setMessage("Server is unreachable.");
                }



                loginFailedDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        if(errorCode == ERROR_NO_INTERNET || errorCode == ERROR_SERVER_UNREACHABLE){
                            // Quit the app
                            // If we don't quit here, and let the activity be recreated,
                            // Then it will be stuck in a loop while there is no internet
                            finish();
                            System.exit(0);
                        }


                        // This should be the last statement in this block
                        // Restart activity
                        recreate();
                        // Now, since we have cleared the shared prefs,
                        // The activity will have no previous user
                        // It will now go into the block for no previous user
                    }
                });

                // Show the dialog
                loginFailedDialog.show();
            //endregion
            break;

            case ERROR_NOT_LOGGED_IN:
                throw new RuntimeException("This error will arise if requesting outstanding docs" +
                        "logging in.\n" +
                        "You should not be trying to do that from this activity.;");
            default:
                throw new RuntimeException("Unknown Error code");
        }// switch
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
