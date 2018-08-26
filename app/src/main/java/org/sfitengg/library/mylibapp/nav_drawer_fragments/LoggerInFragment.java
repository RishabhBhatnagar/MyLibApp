package org.sfitengg.library.mylibapp.nav_drawer_fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.sfitengg.library.mylibapp.MainActivity;
import org.sfitengg.library.mylibapp.R;

public class LoggerInFragment extends Fragment {

    private EditText et_pid;
    private EditText et_pwd;
    private Button btn_login_submit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // This fragment will be called when user is not logged in

        View rootLayout = inflater.inflate(R.layout.activity_login, container, false);
        TextView txt;


        et_pid = rootLayout.findViewById(R.id.et_pid);
        et_pwd = rootLayout.findViewById(R.id.et_pwd);
        btn_login_submit = rootLayout.findViewById(R.id.btn_login_submit);


        //region btn_login_submit OnClickListener
        btn_login_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pid = et_pid.getText().toString();
                String pwd = et_pwd.getText().toString();

                ((MainActivity)getActivity()).startLoginFromFragment(pid, pwd);

            }
        });
        //endregion

        txt = rootLayout.findViewById(R.id.home); //txt is object of TextView
        txt.setText("Click here to \nchange your password");
        txt.setMovementMethod(LinkMovementMethod.getInstance());
        txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("http://115.248.171.105:82/webopac/chgpas.asp"));
                startActivity(browserIntent);
            }
        });
        return rootLayout;
    }
}