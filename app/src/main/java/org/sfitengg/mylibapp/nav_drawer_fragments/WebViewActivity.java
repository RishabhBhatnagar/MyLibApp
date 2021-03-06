package org.sfitengg.mylibapp.nav_drawer_fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sfitengg.mylibapp.R;
import org.sfitengg.mylibapp.data.UrlAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create only a single webview object
        // We will set this to content in setContentToWebViewAndLoadData
        webView = new WebView(this);


        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);



        // Originally display a loading screen,
        // This will be changed to webView when data is received
        setContentView(R.layout.loading_screen);


        String url = Objects.requireNonNull(getIntent().getExtras()).getString(UrlAdapter.K_URL);
        ArrayList<String> urlStrings = getIntent().getExtras().getStringArrayList(UrlAdapter.K_URL_LIST);

        if(url.equals("") || urlStrings == null){
            Toast.makeText(this, "Url could not be loaded.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else if(!isConnectedToInternet()){
                // If not connected to internet

                //region Create and show no internet alert dialog
                AlertDialog noInternetDialog = new AlertDialog.Builder(this).create();
                noInternetDialog.setTitle("You are not connected to the internet!");
                noInternetDialog.setMessage("Please try again when you have internet connectivity.");
                noInternetDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                        "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // Dimiss the dialog to prevent window leak
                                dialogInterface.dismiss();

                                // Go back to MainActiviyt
                                finish();
                            }
                        });
                noInternetDialog.show();
                //endregion
            }
        else {

            loadUrl(url);
        }
    }

    private void loadUrl(final String url) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final StringBuilder builder = new StringBuilder();

                    try {
                        Document doc = Jsoup.connect(url).get();

                        Element singleDiv = doc.select("div.inner_the").first();

                        //region Convert relative urls to absolute for <a> and <img>
                        Elements select = singleDiv.select("a");
                        for (Element e : select){
                            // baseUri will be used by absUrl
                            String absUrl = e.absUrl("href");
                            e.attr("href", absUrl);
                        }

                        //now we process the imgs
                        select = singleDiv.select("img");
                        for (Element e : select){
                            e.attr("src", e.absUrl("src"));
                        }
                        //endregion

                        // Set the final parsed div tag to builder to send to webview
                        builder.append(singleDiv.outerHtml());


                    } catch (IOException e) {
                        e.printStackTrace();
                        builder.append("Error : ").append(e.getMessage()).append("\n");
                    }


                    if(url.equals(LibExtrasFragment.institute_repo_link)){
                        builder.append("<i><b>Note : The links in this page can accessed only by using IntraNet.</b></i>");
                    }
                    // Finally send the data back to UI thread to draw webview
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setContentToWebViewAndLoadData(builder.toString());
                        }
                    });


                }// run end
            }//new runnable end
            ).start();//new thread end

    }

    private void setContentToWebViewAndLoadData(String data){
        // Now that data is arrived, setContentView to webview
        setContentView(webView);

        // Set the data to webview
        webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
