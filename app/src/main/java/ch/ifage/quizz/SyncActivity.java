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
import java.util.List;

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
        String maxDate = findLocalLastSyncDate();

        syncQuestionsCount(maxDate);

    }


    public void onClickSynchronize(View view) {
        showSpinner();
        disableSyncButton();
        String maxDate = findLocalLastSyncDate();
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
        if(questions_counts.get("questions_count") > 0 || questions_counts.get("deleted_count") > 0) {
            enableSyncButton();
        }
    }

    public void onLoadedNewQuestions(String result){

        // Deleted questions
        ArrayList<Integer> deleted_questions = NetworkHelper.parseDeletedQuestionsJson(result);
        if(deleted_questions != null) {
            for (Integer nb : deleted_questions) {
                DBController.deleteQuestionFromNb(this, nb);
            }
        }

        // New/edit questions
        ArrayList<Question> new_questions = NetworkHelper.parseNewQuestionsJson(result);
        if(new_questions != null) {
            List<Integer> nb_list = DBController.findAllQuestionsNb(this);
            for (Question q : new_questions) {
                if(nb_list != null && nb_list.contains(q.getNb())){
                    //System.out.println("Update question " + q.getNb());
                    DBController.updateQuestionFromNb(this, q);
                }else{
                    //System.out.println("Add question " + q.getNb());
                    DBController.addQuestion(this, q);
                }

            }
        }

        DBController.updateLastSyncDate(this);

        TextView htmlTextView = (TextView)findViewById(R.id.labelSyncStatus);
        htmlTextView.setText(Html.fromHtml("<b>Synchronization réussie !</b>"));

        hideSpinner();

        MainActivity.newQuestionOnBack = true;

    }

    private String findLocalLastSyncDate(){
        String maxDate = DBController.findLastSyncDate(getApplicationContext());
        if(maxDate==null){
            maxDate = "";
        }
        return maxDate;
    }

    private void enableSyncButton(){
        syncButton.setEnabled(true);
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
