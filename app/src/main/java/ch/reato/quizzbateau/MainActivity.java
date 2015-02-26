package ch.reato.quizzbateau;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ch.reato.quizzbateau.filesystem.FileHelper;
import ch.reato.quizzbateau.model.Question;
import ch.reato.quizzbateau.sqlite.DBController;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Map;

public class MainActivity extends Activity {

    private Question currentQuestion;
    private int currentQuizzId;
    private Map<String, Integer> mapCounters;
    private boolean show = true;
    private int qCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggleAnswerVisibility();

        //System.out.println("-------------------- Starting app");

        // Get last quizz ID
        currentQuizzId = DBController.findLastQuizzId(this);

        // Get quizz right/wrong counters
        mapCounters = DBController.findQuizzCounters(this, currentQuizzId);

        // Get questions count
        qCount = DBController.findQuestionsCount(this, currentQuizzId);

        // Get random question and set UI
        boolean success = loadRandomQuestion();
        if(success){
            populateUiWithCurrentQuestion();
        }else{
            populateUiWithNoQuestion();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.quizz_settings) {
            Intent i = new Intent(this, QuizzActivity.class);
            i.putExtra("currentQuizzId", currentQuizzId);
            startActivityForResult(i, 1);
        }else if(id == R.id.view_settings){
            Intent i = new Intent(this, ListViewActivity.class);
            i.putExtra("currentQuizzId", currentQuizzId);
            startActivity(i);
        }else if(id == R.id.sync_settings){
            Intent i = new Intent(this, SyncActivity.class);
            startActivityForResult(i, 2);
        }else if(id == R.id.deleteall_settings){
            DBController.deleteAllQuestions(this);
            DBController.deleteAllQuizz(this);
            FileHelper fileHelper = FileHelper.getInstance(this);
            fileHelper.deleteAllLocalImages();
            DBController.resetLastSyncDate(this);
            populateUiWithNoQuestion();
        }else if(id == R.id.resetcounters_settings){
            DBController.resetAllCounters(this, currentQuizzId);
            currentQuestion.setCountRight(0);
            currentQuestion.setCountWrong(0);
            mapCounters.put("right", 0);
            mapCounters.put("wrong", 0);
            populateUiWithCurrentCounters();
        }else if(id == R.id.about_settings){
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickShow(View view) {
        toggleAnswerVisibility();
    }

    public void onClickRight(View view) {

        // Handle current question
        currentQuestion.incrementCountRight();
        DBController.updateQuestionFromId(this, currentQuestion);

        // Get quizz right/wrong counters
        mapCounters = DBController.findQuizzCounters(this, currentQuizzId);

        // Load new question
        loadAndDisplayNewQuestion();
    }

    public void onClickWrong(View view) {

        // Handle current question
        currentQuestion.incrementCountWrong();
        DBController.updateQuestionFromId(this, currentQuestion);

        // Get quizz right/wrong counters
        mapCounters = DBController.findQuizzCounters(this, currentQuizzId);

        // Load new question
        loadAndDisplayNewQuestion();
    }

    private void loadAndDisplayNewQuestion(){
        //boolean success = loadRandomQuestionDifferentFromCurrent();
        boolean success = loadWeightedRandomQuestionDifferentFromCurrent();
        if(success){
            toggleAnswerVisibility();
            populateUiWithCurrentQuestion();
        }
    }

    private boolean loadRandomQuestion(){
        if(qCount==0){
            return false;
        }
        int rnd = (int) (Math.floor(Math.random() * (qCount)));
        currentQuestion = DBController.findNthQuestion(this, rnd, currentQuizzId);
        return true;
    }

    private boolean loadRandomQuestionDifferentFromCurrent() {
        if(qCount <= 1){
            return false;
        }
        int rnd = (int) (Math.floor(Math.random() * (qCount-1)));
        int currentId = currentQuestion.getId();
        currentQuestion = DBController.findNthQuestionDifferentFromId(this, rnd, currentId, currentQuizzId);
        return true;
    }

    private boolean loadWeightedRandomQuestionDifferentFromCurrent() {
        if(qCount <= 1){
            return false;
        }
        double salt = 4; // Smaller than 5 is better
        double exprnd = Math.log(1 - Math.random ()) / (-1 * salt);
        while(exprnd > 1){
            exprnd = Math.log(1 - Math.random ()) / (-1 * salt);
        }
        int rnd = ( int) Math.round((qCount-2) * exprnd); // -1 to remove same question and -1 because we start at 0
        currentQuestion = DBController.findNthQuestionDifferentFromId(this, rnd, currentQuestion.getId(), currentQuizzId);
        return true;
    }

    private void populateUiWithCurrentQuestion() {
        TextView htmlTextView = (TextView)findViewById(R.id.textQuestion);
        htmlTextView.setText(Html.fromHtml(currentQuestion.getQuestion()));

        TextView htmlAnswer = (TextView)findViewById(R.id.textAnswer);
        htmlAnswer.setText(Html.fromHtml(currentQuestion.getAnswer()));

        populateUiWithCurrentImage();
        populateUiWithCurrentCounters();

        Button buttonShow = (Button)findViewById(R.id.buttonShow);
        buttonShow.setEnabled(true);

    }

    private void populateUiWithNoQuestion(){
        TextView htmlTextView = (TextView)findViewById(R.id.textQuestion);
        htmlTextView.setText(Html.fromHtml("<i><font color='grey'>" + getString(R.string.no_question_text) + "</font></i>"));

        populateUiWithNoImage();

        TextView htmlAnswer = (TextView)findViewById(R.id.textAnswer);
        htmlAnswer.setText("");

        TextView htmlStatsTextView = (TextView)findViewById(R.id.labelStats);
        htmlStatsTextView.setText("");

        Button buttonShow = (Button)findViewById(R.id.buttonShow);
        buttonShow.setEnabled(false);
    }

    private void populateUiWithCurrentCounters() {
        TextView htmlStatsTextView = (TextView)findViewById(R.id.labelStats);
        String text = "<i>Score total: <b><font color='#008000'>" + mapCounters.get("right") + "</font>/<font color='red'>" + mapCounters.get("wrong") + "</font></b>";
        text = text + "<font color='black'>, question: </font><b><font color='#008000'>" + currentQuestion.getCountRight() + "</font>/<font color='red'>" + currentQuestion.getCountWrong() + "</font></b></i>";
        htmlStatsTextView.setText(Html.fromHtml(text));
    }

    private void populateUiWithCurrentImage(){

        ImageView myImage = (ImageView) findViewById(R.id.imgQuestion);
        TextView htmlQuestion = (TextView)findViewById(R.id.textQuestion);
        TextView htmlAnswer = (TextView)findViewById(R.id.textAnswer);
        if(currentQuestion.getImagePath().isEmpty()){
            populateUiWithNoImage();
        }else{
            myImage.setVisibility(View.VISIBLE);
            htmlQuestion.setMinLines(2);
            htmlAnswer.setMinLines(2);
            File imgFile = new File(getFilesDir() + "/" + currentQuestion.getImagePath());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                myImage.setImageBitmap(myBitmap);
            }
        }
    }

    private void populateUiWithNoImage(){
        ImageView myImage = (ImageView) findViewById(R.id.imgQuestion);
        TextView htmlQuestion = (TextView)findViewById(R.id.textQuestion);
        TextView htmlAnswer = (TextView)findViewById(R.id.textAnswer);

        myImage.setVisibility(View.GONE);
        htmlQuestion.setMinLines(6);
        htmlAnswer.setMinLines(6);
        myImage.setImageResource(0);
    }

    private void toggleAnswerVisibility() {
        TextView labelAnswer = (TextView)findViewById(ch.reato.quizzbateau.R.id.labelAnswer);
        TextView textAnswer = (TextView)findViewById(ch.reato.quizzbateau.R.id.textAnswer);
        Button buttonRight = (Button)findViewById(ch.reato.quizzbateau.R.id.buttonRight);
        Button buttonWrong = (Button)findViewById(ch.reato.quizzbateau.R.id.buttonWrong);

        if(show){
            labelAnswer.setVisibility(View.INVISIBLE);
            textAnswer.setVisibility(View.INVISIBLE);
            buttonRight.setVisibility(View.INVISIBLE);
            buttonWrong.setVisibility(View.INVISIBLE);
        }else{
            labelAnswer.setVisibility(View.VISIBLE);
            textAnswer.setVisibility(View.VISIBLE);
            buttonRight.setVisibility(View.VISIBLE);
            buttonWrong.setVisibility(View.VISIBLE);
        }
        show = !show;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){ // Back from Quizz selection
            if(resultCode == RESULT_OK){
                int returnQuizzId = Integer.parseInt(data.getStringExtra("quizzId"));
                if(returnQuizzId != currentQuizzId) {
                    currentQuizzId = returnQuizzId;
                    qCount = DBController.findQuestionsCount(this, currentQuizzId);
                    boolean success = loadRandomQuestion();
                    if (success) {
                        populateUiWithCurrentQuestion();
                    }
                }
            }
        }else if(requestCode == 2){ // Back from Sync settings
            if(resultCode == RESULT_OK){
                int syncDone = data.getIntExtra("syncDone", 0);
                if(syncDone == 1){
                    qCount = DBController.findQuestionsCount(this, currentQuizzId);
                    boolean success = loadRandomQuestion();
                    if (success) {
                        populateUiWithCurrentQuestion();
                    }
                }
            }
        }
    }

}
