package ch.ifage.quizz;

import ch.ifage.quizz.sqlite.DBController;
import ch.ifage.quizz.network.NetworkHelper;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.text.Html;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;

import ch.ifage.quizz.model.Question;


public class SyncActivity extends Activity {

    private Button syncButton;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        syncButton = (Button)findViewById(R.id.buttonSync);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);

        showSpinner();
        disableSyncButton();
        String maxDate = findLocalQuestionMaxDate();

        syncQuestionsCount(maxDate);

    }


    public void onClickSynchronize(View view) {
        showSpinner();
        disableSyncButton();
        String maxDate = findLocalQuestionMaxDate();
        syncQuestions(maxDate);
    }

    private void syncQuestionsCount(String maxDate){
        maxDate = maxDate.replaceAll("[^0-9]","");
        NetworkHelper.doQuestionsCountSync(this, maxDate);
    }

    private void syncQuestions(String maxDate){
        maxDate = maxDate.replaceAll("[^0-9]","");
        NetworkHelper.doSync(this, maxDate);
    }

    public void OnLoadedQuestionsCount(String result) {
        HashMap<String, Integer> questions_counts = NetworkHelper.parseQuestionsCountJson(result);

        TextView htmlTextView = (TextView)findViewById(R.id.labelSyncStatus);
        htmlTextView.setText(Html.fromHtml("Questions à synchroniser: " + questions_counts.get("questions_count") + "<br>" + "Questions à effacer: " + questions_counts.get("deleted_count")));

        hideSpinner();
        if(questions_counts.get("questions_count") > 0) {
            enableSyncButton();
        }
    }

    public void onLoadedNewQuestions(String result){
        ArrayList<Question> questions = NetworkHelper.parseQuestionsJson(result);
        for(Question q : questions){
            DBController.addQuestion(this, q);
        }

        TextView htmlTextView = (TextView)findViewById(R.id.labelSyncStatus);
        htmlTextView.setText(Html.fromHtml("<b>Synchronization réussie !</b>"));

        hideSpinner();

        MainActivity.newQuestionOnBack = true;

    }

    public void onLoadedDeletedQuestions(String result){
        ArrayList<Question> questions = NetworkHelper.parseQuestionsJson(result);
        for(Question q : questions){
            DBController.addQuestion(this, q);
        }

        TextView htmlTextView = (TextView)findViewById(R.id.labelSyncStatus);
        htmlTextView.setText(Html.fromHtml("<b>Synchronization réussie !</b>"));

        hideSpinner();

        MainActivity.newQuestionOnBack = true;

    }

    private void enableSyncButton(){
        syncButton = (Button)findViewById(R.id.buttonSync);
        syncButton.setEnabled(true);
    }

    private String findLocalQuestionMaxDate(){
        String  maxDate = DBController.findQuestionMaxDate(getApplicationContext());
        if(maxDate==null){
            maxDate = "";
        }
        return maxDate;
    }

    private void disableSyncButton(){
        syncButton.setEnabled(false);
    }

    private void hideSpinner(){
        spinner.setVisibility(View.GONE);
    }

    private void showSpinner(){
        spinner.setVisibility(View.VISIBLE);
    }

}
