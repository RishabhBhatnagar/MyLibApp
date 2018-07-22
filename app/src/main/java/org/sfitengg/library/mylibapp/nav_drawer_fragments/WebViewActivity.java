package org.sfitengg.library.mylibapp.nav_drawer_fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sfitengg.library.mylibapp.R;

import java.io.IOException;
import java.util.ArrayList;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();

        String url = getIntent().getExtras().getString("url");
        ArrayList<String> urlStrings = getIntent().getExtras().getStringArrayList("urlList");

        if(url.equals("") || url == null || urlStrings == null){
            Toast.makeText(this, "no url given to load.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            new LoadUrl(url);
        }
    }

    class LoadUrl{
        public LoadUrl(final String url) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder builder = new StringBuilder();

                    try {
                        Document doc = Jsoup.connect(url).get();
                        Elements links = doc.select("div.inner_the");

                        for (Element link : links) {
                            builder.append(link.outerHtml());
                        }
                    } catch (IOException e) {
                        builder.append("Error : ").append(e.getMessage()).append("\n");
                    }

                    String s = new String(builder);
                    s = s.replaceAll("src=\"", "src=\"http://www.sfitengg.org/");
                    final String finalS = s;
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadDataWithBaseURL(null, finalS, "text/html", "utf-8", null);
                        }
                    });
                }// run end
            }//new runnable end
            ).start();//new thread end

        }
    }
}
