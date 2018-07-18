package org.sfitengg.library.mylibapp.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintDocumentInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sfitengg.library.mylibapp.R;
import org.sfitengg.library.mylibapp.nav_drawer_fragments.WebViewActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.UrlViewHolder> {

    private Context context;
    private List<Url> urlList;

    public UrlAdapter(Context context, List<Url> urlList) {
        this.context = context;
        this.urlList = urlList;
    }

    @NonNull
    @Override
    public UrlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.individual_url, null);

        return new UrlViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UrlViewHolder holder, final int position) {
        holder.textView.setText(urlList.get(position).getName());

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = urlList.get(position).getUrl();
                String name = urlList.get(position).getName();
                loadUrl(url, name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    class UrlViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public UrlViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.url_name);
        }
    }

    public void loadUrl(final String url, String name){

        List<String> urlStrings = new ArrayList<>();
        List<String> urlNames = new ArrayList<>();

        for(Url url1 : urlList){
            urlStrings.add(url1.getUrl());
            urlNames.add(url1.getName());
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("name", name);
        intent.putStringArrayListExtra("urlList", (ArrayList<String>) urlStrings);
        intent.putStringArrayListExtra("urlNames", (ArrayList<String>) urlNames);
        context.startActivity(intent);
    }
}