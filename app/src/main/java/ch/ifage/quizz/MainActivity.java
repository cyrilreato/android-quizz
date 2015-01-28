package ch.ifage.quizz;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import ch.ifage.quizz.model.Question;
import ch.ifage.quizz.sqlite.DBController;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Question currentQuestion;
    private boolean show = true;
    public static boolean newQuestionOnBack = false;
    private int qCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggleAnswerVisibility();

        System.out.println("-------------------- Starting app");

        // Database init
        //resetTableWithDummyQuestions();

        // Get questions count
        qCount = DBController.findQuestionsCount(this);

        // Get random question and set UI
        boolean success = loadRandomQuestion();
        if(success){
            populateUiWithCurrentQuestion();
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
        if(id == R.id.quizz_settings){
            Intent i = new Intent(this, QuizzActivity.class);
            startActivity(i);
        }
        if(id == R.id.sync_settings){
            Intent i = new Intent(this, SyncActivity.class);
            //i.putExtra("originator", this.getClass().getName());
            startActivity(i);
        }
        if(id == R.id.deleteall_settings){
            DBController.deleteAllQuestions(this);
            DBController.resetLastSyncDate(this);
            populateUiWithNoQuestion();
        }
        if(id == R.id.resetcounters_settings){
            DBController.resetAllCounters(this);
            currentQuestion.setCountRight(0);
            currentQuestion.setCountWrong(0);
            populateUiWithCurrentCounters();
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

        // Load new question
        loadAndDisplayNewQuestion();
    }

    public void onClickWrong(View view) {

        // Handle current question
        currentQuestion.incrementCountWrong();
        DBController.updateQuestionFromId(this, currentQuestion);

        // Load new question
        loadAndDisplayNewQuestion();
    }

    private void loadAndDisplayNewQuestion(){
        //boolean success = loadRandomQuestionDifferentFromCurrent();
        boolean success = loadWeightedQuestionDifferentFromCurrent();
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
        currentQuestion = DBController.findNthQuestion(this, rnd);
        return true;
    }

    private boolean loadRandomQuestionDifferentFromCurrent() {
        if(qCount <= 1){
            return false;
        }
        int rnd = (int) (Math.floor(Math.random() * (qCount-1)));
        int currentId = currentQuestion.getId();
        currentQuestion = DBController.findNthQuestionDifferentFromId(this, rnd, currentId);
        return true;
    }

    private boolean loadWeightedQuestionDifferentFromCurrent() {
        if(qCount <= 1){
            return false;
        }

        double salt = 4; // Smaller than 5 is better
        double exprnd = Math.log(1 - Math.random ()) / (-1 * salt);
        int rnd = ( int) Math.floor((qCount-1) * exprnd);
        currentQuestion = DBController.findNthQuestionDifferentFromId(this, rnd, currentQuestion.getId());
        return true;
    }

    private void populateUiWithCurrentQuestion() {
        TextView htmlTextView = (TextView)findViewById(R.id.textQuestion);
        htmlTextView.setText(Html.fromHtml(currentQuestion.getQuestion()));

        TextView htmlAnswer = (TextView)findViewById(R.id.textAnswer);
        htmlAnswer.setText(Html.fromHtml(currentQuestion.getAnswer()));

        populateUiWithCurrentCounters();

        Button buttonShow = (Button)findViewById(R.id.buttonShow);
        buttonShow.setEnabled(true);

    }

    private void populateUiWithNoQuestion(){
        TextView htmlTextView = (TextView)findViewById(R.id.textQuestion);
        htmlTextView.setText(Html.fromHtml("No question"));

        TextView htmlAnswer = (TextView)findViewById(R.id.textAnswer);
        htmlAnswer.setText("");

        TextView htmlStatsTextView = (TextView)findViewById(R.id.labelStats);
        htmlStatsTextView.setText("");

        Button buttonShow = (Button)findViewById(R.id.buttonShow);
        buttonShow.setEnabled(false);
    }

    private void populateUiWithCurrentCounters() {
        TextView htmlStatsTextView = (TextView)findViewById(R.id.labelStats);
        String text = "<b><font color='#008000'>" + currentQuestion.getCountRight() + "</font> / <font color='red'>" + currentQuestion.getCountWrong() + "</font></b>";
        htmlStatsTextView.setText(Html.fromHtml(text)); // , TextView.BufferType.SPANNABLE
    }

    private void toggleAnswerVisibility() {
        TextView labelAnswer = (TextView)findViewById(R.id.labelAnswer);
        TextView textAnswer = (TextView)findViewById(R.id.textAnswer);
        Button buttonRight = (Button)findViewById(R.id.buttonRight);
        Button buttonWrong = (Button)findViewById(R.id.buttonWrong);

        if(show==true){
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
    public void onRestart(){
        //System.out.println("Main activity restart");
        if(newQuestionOnBack) {
            // Get questions count
            qCount = DBController.findQuestionsCount(this);

            // Get random question and set UI
            boolean success = loadRandomQuestion();
            if (success) {
                populateUiWithCurrentQuestion();
            }
            newQuestionOnBack = false;
        }
        super.onRestart();
    }



}
