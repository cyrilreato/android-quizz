package ch.reato.quizzbateau;

import ch.reato.quizzbateau.model.Image;
import ch.reato.quizzbateau.model.Quizz;
import ch.reato.quizzbateau.sqlite.DBController;
import ch.reato.quizzbateau.network.NetworkHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.text.Html;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.reato.quizzbateau.model.Question;

public class SyncActivity extends Activity {

    public final static boolean BOAT_ONLY = true;
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
        NetworkHelper.doQuizzAndImagesSync(this, maxDate);

        TextView htmlTextView = (TextView)findViewById(R.id.labelSyncStatus);
        htmlTextView.setText(Html.fromHtml("<b>" + getString(R.string.sync_ongoing_text) + "</b>"));

    }

    public void onLoadedQuestionsCount(String result) {

        HashMap<String, Integer> questions_counts = NetworkHelper.parseQuestionsCountJson(result);

        TextView htmlTextView = (TextView)findViewById(ch.reato.quizzbateau.R.id.labelSyncStatus);
        String text = "Quizz à synchroniser: " + questions_counts.get("quizz_new_count") + "<br>" + "Quizz à effacer: " + questions_counts.get("quizz_deleted_count") + "<br>";
        text = text + "Questions à synchroniser: " + questions_counts.get("questions_new_count") + "<br>" + "Questions à effacer: " + questions_counts.get("questions_deleted_count") + "<br>";
        if(questions_counts.get("questions_new_count")>0) {
            text = text + "<br><i><font color='blue'>La synchronisation est plus rapide en Wifi et peut prendre quelques secondes en fonction du nombre de questions.</font></i>";
        }
        htmlTextView.setText(Html.fromHtml(text));

        hideSpinner();
        if(questions_counts.get("quizz_new_count") > 0 || questions_counts.get("quizz_deleted_count") > 0 || questions_counts.get("questions_new_count") > 0 || questions_counts.get("questions_deleted_count") > 0) {
            enableSyncButton();
        }
    }

    public void onLoadedImages(List<Image> result){
        TextView htmlTextView = (TextView)findViewById(R.id.labelSyncStatus);
        htmlTextView.setText(Html.fromHtml("<b>" + getString(R.string.sync_ok_text) + "</b>"));

        hideSpinner();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("syncDone", 1);
        setResult(RESULT_OK, returnIntent);
    }
    public void onLoadedNewQuestions(String result){
        // Deleted questions
        ArrayList<Integer> deleted_questions = NetworkHelper.parseDeletedQuestionsJson(result);
        if(deleted_questions != null) {
            for (Integer nb : deleted_questions) {
                //System.out.println("Delete question " + nb);
                DBController.deleteQuestionFromNb(this, nb);
            }
        }

        // Deleted quizz
        ArrayList<Integer> deleted_quizz = NetworkHelper.parseDeletedQuizzJson(result);
        if(deleted_quizz != null) {
            for (Integer id : deleted_quizz) {
                //System.out.println("Delete quizz " + id);
                DBController.deleteQuizzFromId(this, id);
            }
        }

        // New quizz
        ArrayList<Quizz> new_quizz = NetworkHelper.parseNewQuizzJson(result);
        if(new_quizz != null){
            List<Integer> id_list = DBController.findAllQuizzId(this);
            for(Quizz q : new_quizz){
                if(id_list != null && id_list.contains(q.getId())){
                    DBController.updateQuizzFromId(this, q);
                }else{
                    DBController.addQuizz(this, q);
                }
            }
        }

        // New/edit questions
        ArrayList<Question> new_questions = NetworkHelper.parseNewQuestionsJson(result);
        if(new_questions != null) {
            List<Integer> nb_list = DBController.findAllQuestionsNb(this);
            for (Question q : new_questions) {
                if(nb_list != null && nb_list.contains(q.getNb())){
                    DBController.updateQuestionFromNb(this, q);
                }else{
                    DBController.addQuestion(this, q);
                }
            }
        }


        DBController.updateLastSyncDate(this);

        // Load images
        ArrayList<String> newImages = NetworkHelper.parseNewImagesJson(result);
        NetworkHelper.doSyncImages(this, newImages);

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
