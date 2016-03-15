package com.hobbyte.touringandroid.tourdata;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hobbyte.touringandroid.R;
import java.util.ArrayList;

/**
 * Created by Nikita on 15/03/2016.
 */
public class Quiz {

    private String title, solution;
    private ArrayList<String> options;
    public View v;
    private TextView correctView;
    private ArrayList<TextView> optionsList;

    /**
     * Contains the structure for the Quiz
     * @param title the Question set by the Quiz
     * @param option ArrayList of provided Options to choose from
     * @param solution Correct solution for Question
     * @param v The view to add the Quiz to
     */
    public Quiz(String title, ArrayList<String> option, String solution, View v) {
        this.title = title;
        this.options = option;
        this.solution = solution;
        this.v = v;
        setupQuiz();
    }

    /**
     * Sets up the quiz
     * //TODO: Finish documentation
     */
    private void setupQuiz() {
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.optionsLayout);
        LayoutInflater inflater = LayoutInflater.from(v.getContext());
        optionsList = new ArrayList<>();
        for(int i = 0; i < options.size(); i++) {
            TextView quizOption = (TextView) inflater.inflate(R.layout.quiz_option, layout, false);
            quizOption.setText(options.get(i));
            if(options.get(i).equals(solution)) {
                correctView = quizOption;
            }
            quizOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == correctView) {
                        v.setBackgroundColor(Color.parseColor("#B3FCAC"));
                        clearListeners();
                    } else {
                        correctView.setBackgroundColor(Color.parseColor("#B3FCAC"));
                        v.setBackgroundColor(Color.parseColor("#FEB2B3"));
                        clearListeners();
                    }
                }
            });
            optionsList.add(quizOption);
            layout.addView(quizOption);
            layout.addView(inflater.inflate(R.layout.divider, layout, false));
        }

    }

    private void clearListeners() {
        for(int i = 0; i < optionsList.size(); i++) {
            optionsList.get(i).setOnClickListener(null);
        }
    }
}