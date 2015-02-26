package ch.reato.quizzbateau;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import ch.reato.quizzbateau.model.Quizz;
import ch.reato.quizzbateau.sqlite.DBController;

public class QuizzActivity extends Activity {

    int currentQuizzId;
    TextView txtCurrentQuizz;
    Spinner spinner;
    CheckBox chkAllQuizz;
    Button btShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        txtCurrentQuizz = (TextView)findViewById(R.id.labelCurrentQuizz);
        spinner = (Spinner)findViewById(R.id.quizz_spinner);
        btShow = (Button)findViewById(R.id.buttonShow);

        List<Quizz> quizzes = DBController.findAllQuizz(this);

        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.layout_spinner, quizzes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        chkAllQuizz = (CheckBox)findViewById(R.id.chkAllQuizz);

        Intent i = getIntent();
        currentQuizzId = i.getIntExtra("currentQuizzId", 0);
        if(currentQuizzId != 0){
            Quizz q = DBController.findQuizz(this, currentQuizzId);
            txtCurrentQuizz.setText(Html.fromHtml("<b>" + q.getName() + "</b><br>" + Html.fromHtml(q.getDescription())));
        }else{
            if(quizzes.size()==0){
                txtCurrentQuizz.setText(Html.fromHtml("<i><font color='grey'>" + getString(R.string.no_quizz_text) + "</font></i>"));
                chkAllQuizz.setEnabled(false);
                btShow.setEnabled(false);
            }else {
                txtCurrentQuizz.setText("All Quizz");
                chkAllQuizz.setChecked(true);
                chkAllQuizz.setEnabled(true);
                btShow.setEnabled(true);
            }
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

            DBController.updateLastQuizzId(this, 0);

        }else {
            txtCurrentQuizz.setText(((Quizz) spinner.getSelectedItem()).getName());
            Intent returnIntent = new Intent();
            returnIntent.putExtra("quizzId", String.valueOf(((Quizz) spinner.getSelectedItem()).getId()));
            setResult(RESULT_OK, returnIntent);

            DBController.updateLastQuizzId(this, ((Quizz) spinner.getSelectedItem()).getId());
        }
    }

}
