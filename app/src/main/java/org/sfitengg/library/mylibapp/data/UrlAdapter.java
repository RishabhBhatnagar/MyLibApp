package org.sfitengg.library.mylibapp.data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.sfitengg.library.mylibapp.R;
import org.sfitengg.library.mylibapp.nav_drawer_fragments.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.UrlViewHolder> {

    private Context context;
    private List<Url> urlList;

    public static final String K_URL = "url";
    private static final String K_NAME = "name1";
    public static final String K_URL_LIST = "urllist1";
    private static final String K_URL_NAMES = "urlnames1";

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

        UrlViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.url_name);
        }
    }

    private void loadUrl(final String url, String name){

        List<String> urlStrings = new ArrayList<>();
        List<String> urlNames = new ArrayList<>();

        for(Url url1 : urlList){
            urlStrings.add(url1.getUrl());
            urlNames.add(url1.getName());
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(K_URL, url);
        intent.putExtra(K_NAME, name);
        intent.putStringArrayListExtra(K_URL_LIST, (ArrayList<String>) urlStrings);
        intent.putStringArrayListExtra(K_URL_NAMES, (ArrayList<String>) urlNames);
        context.startActivity(intent);
    }
}