package ch.ifage.quizz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import ch.ifage.quizz.model.Quizz;
import ch.ifage.quizz.sqlite.DBController;

public class QuizzActivity extends Activity {

    int currentQuizzId;
    TextView txtCurrentQuizz;
    Spinner spinner;
    CheckBox chkAllQuizz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        List<Quizz> quizzes = DBController.findAllQuizz(this);

        txtCurrentQuizz = (TextView)findViewById(R.id.labelCurrentQuizz);

        spinner = (Spinner)findViewById(R.id.quizz_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, quizzes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        chkAllQuizz = (CheckBox)findViewById(R.id.chkAllQuizz);

        Intent i = getIntent();
        currentQuizzId = i.getIntExtra("currentQuizzId", 0);
        if(currentQuizzId != 0){
            Quizz q = DBController.findQuizz(this, currentQuizzId);
            txtCurrentQuizz.setText(q.getName());
        }else{
            txtCurrentQuizz.setText("All Quizz");
            chkAllQuizz.setChecked(true);
            spinner.setEnabled(false);
        }
    }

    public void onClickAllQuizz(View view){

        if(chkAllQuizz.isChecked()) {
            spinner.setEnabled(false);
        }else{
            spinner.setEnabled(true);
        }
    }

    public void onClickSetQuizz(View view){
        if(chkAllQuizz.isChecked()){
            txtCurrentQuizz.setText("All Quizz");
            Intent returnIntent = new Intent();
            returnIntent.putExtra("quizzId", "0");
            setResult(RESULT_OK, returnIntent);
        }else {
            txtCurrentQuizz.setText(((Quizz) spinner.getSelectedItem()).getName());
            Intent returnIntent = new Intent();
            returnIntent.putExtra("quizzId", String.valueOf(((Quizz) spinner.getSelectedItem()).getId()));
            setResult(RESULT_OK, returnIntent);
        }
    }
}
