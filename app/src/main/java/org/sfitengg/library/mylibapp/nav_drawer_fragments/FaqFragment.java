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
    String question;
    String answer;
    int hrColor;
    public QuestionAndAnswer(String question, String answer, int hrColor) {
        this.question = question;
        this.answer = answer;
        this.hrColor = hrColor;
    }
    public String getQuestion() {
        return question;
    }
    public String getAnswer() {
        return answer;
    }
    public int getHrColor(){
        return hrColor;
    }
}

public class FaqFragment extends Fragment {

    private List<QuestionAndAnswer> questionAndAnswers;
    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_faq, container, false);
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        questionAndAnswers = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);

        // todo : add more question and answers here.
        questionAndAnswers.add(new QuestionAndAnswer("Question", "Answer", color("#89f07f")));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new QnAAdapter(questionAndAnswers));
        return view;
    }
    private int color(String colorHex){
        return Color.parseColor(colorHex);
    }
}




class QnAAdapter extends RecyclerView.Adapter<QnAAdapter.QnAViewHolder>{
    List<QuestionAndAnswer> qnAs;
    public QnAAdapter(List<QuestionAndAnswer> qnAs) {
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