package org.sfitengg.mylibapp.nav_drawer_fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.sfitengg.mylibapp.R;
import org.sfitengg.mylibapp.data.Url;
import org.sfitengg.mylibapp.data.UrlAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LibExtrasFragment extends Fragment {

    public static final String institute_repo_link = "http://sfitengg.org/library_inst_repo.php";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_lib_extras, container, false);
        Objects.requireNonNull(getActivity()).setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        //getting recycler view ready.
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        List<Url> dataSet = new ArrayList<>();

        initialiseUrlNames(dataSet);

        RecyclerView.Adapter adapter = (new UrlAdapter(getContext(), dataSet));
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void initialiseUrlNames(List<Url> dataSet) {
        dataSet.add(new Url(institute_repo_link, "Institute Repository"));
        dataSet.add(new Url("http://www.sfitengg.org/library_virt_ref.php", "Virtual Reference"));
        dataSet.add(new Url("http://www.sfitengg.org/library_resources.php", "Library Resources"));
        dataSet.add(new Url("http://www.sfitengg.org/library_digital.php", "Digital Library"));
        dataSet.add(new Url("http://www.sfitengg.org/library_about.php", "About Library"));
        dataSet.add(new Url("http://www.sfitengg.org/library_services.php", "Services"));
        dataSet.add(new Url("http://www.sfitengg.org/library_download.php", "Syllabus download"));
        dataSet.add(new Url("http://www.sfitengg.org/library_iitb.php", "IIT-B Membership"));
        dataSet.add(new Url("http://www.sfitengg.org/library_statistics.php", "Library Statistics"));
        dataSet.add(new Url("http://www.sfitengg.org/library_ejournals.php", "E-Journals"));

    }

}