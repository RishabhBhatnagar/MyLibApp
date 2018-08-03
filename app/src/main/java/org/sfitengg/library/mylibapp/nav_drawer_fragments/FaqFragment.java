package org.sfitengg.library.mylibapp.nav_drawer_fragments;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sfitengg.library.mylibapp.R;

import java.util.ArrayList;
import java.util.List;

class QuestionAndAnswer{
    private String question;
    private String answer;
    private int hrColor;
    QuestionAndAnswer(String question, String answer, int hrColor) {
        this.question = question;
        this.answer = answer;
        this.hrColor = hrColor;
    }
    String getQuestion() {
        return question;
    }
    String getAnswer() {
        return answer;
    }
    int getHrColor(){
        return hrColor;
    }
}

public class FaqFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_faq, container, false);
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        List<QuestionAndAnswer> questionAndAnswers = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        // todo : add more question and answers here.

        questionAndAnswers.add(new QuestionAndAnswer("WHY DID I DOWNLOAD THIS APP ?", "This app lets users view their issued books with the days left for returning them and users also can re-issue their books via this app as well as get timely notification " +
                "to return the issued books", color("#ed044e")));
        questionAndAnswers.add(new QuestionAndAnswer("I FORGOT MY PASSWORD", "Contact the Library employees", color("#89f07f")));
        questionAndAnswers.add(new QuestionAndAnswer("WHY IS MY RE-ISSUE CHECKBOX DISABLED ? ", "There are two reasons for this. " +
                "a) You cannot re-issue the books on the same day you issued/re-issued those books." +
                "b)Once you cross the due date you cannot re-issue the books", color("#89f07f")));
        questionAndAnswers.add(new QuestionAndAnswer("DOES THIS APP REQUIRE AN ACTIVE INTERNET CONNECTION ?", "Without Internet you'll still be able to use some basic functions of the app and also be able to view your issued books",color("#ed044e")));
        questionAndAnswers.add(new QuestionAndAnswer("WHY DOES AN ERROR APPEAR ?", "Error will be displayed if you are not connected to the internet or when the Library servers are down", color("#1295C9")));
        questionAndAnswers.add(new QuestionAndAnswer("HOW MANY TIMES CAN I RE-ISSUE THE BOOKS ?", " You can re-issue the books 3 times. (Limit applicable to only BE students)", color("#89f07f")));
       // questionAndAnswers.add(new QuestionAndAnswer("WHEN WILL THE APP START NOTIFYING ME OF THE SUBMISSION DATE ?", "The app will remind the user of the submission date from 3 days prior to the due date", color("#89f07f")));
        questionAndAnswers.add(new QuestionAndAnswer("CAN I LOG IN WITH THE SAME PID ON MULTIPLE DEVICES ?", " Yes", color("#89f07f")));
        questionAndAnswers.add(new QuestionAndAnswer("CAN I RE-ISSUE MULTIPLE BOOKS AT THE SAME TIME ?", "Yes , just hold a book then select multiple books to Re-issue ", color("#89f07f")));



        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new QnAAdapter(questionAndAnswers));
        return view;
    }
    private int color(String colorHex){
        return Color.parseColor(colorHex);
    }
}




class QnAAdapter extends RecyclerView.Adapter<QnAAdapter.QnAViewHolder>{
    private List<QuestionAndAnswer> qnAs;
    QnAAdapter(List<QuestionAndAnswer> qnAs) {
        this.qnAs = qnAs;
    }
    @NonNull
    @Override
    public QnAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_qna, parent, false);
        return new QnAViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull QnAViewHolder holder, int position) {
        holder.questionTv.setText(qnAs.get(position).getQuestion());
        holder.answerTv.setText(qnAs.get(position).getAnswer());
        holder.hr.setBackgroundColor(qnAs.get(position).getHrColor());
    }
    @Override
    public int getItemCount() {
        return qnAs.size();
    }
    public class QnAViewHolder extends RecyclerView.ViewHolder{
        TextView questionTv;
        TextView answerTv;
        LinearLayout hr;

        public QnAViewHolder(View itemView) {
            super(itemView);
            questionTv = itemView.findViewById(R.id.questionTv);
            answerTv = itemView.findViewById(R.id.answerTv);
            hr = itemView.findViewById(R.id.hr);
        }
    }
}