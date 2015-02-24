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

public class MainActivity extends Activity {

    private Question currentQuestion;
    private int currentQuizzId;
    private boolean show = true;
    private int qCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ch.reato.quizzbateau.R.layout.activity_main);
        toggleAnswerVisibility();

        //System.out.println("-------------------- Starting app");

        // Get last quizz ID
        currentQuizzId = DBController.findLastQuizzId(this);

        // Get questions count
        qCount = DBController.findQuestionsCount(this, currentQuizzId);

        // Get random question and set UI
        boolean success = loadRandomQuestion();
        if(success){
            populateUiWithCurrentQuestion();
        }


        // -------------------------------------------------------------------
        // Load image part

        //String url = "http://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Drymoreomys_albimaculatus_002.jpg/150px-Drymoreomys_albimaculatus_002.jpg";
        //DownloadImages mDownloader = new DownloadImages(MainActivity.this, url, new DownloadImages.ImageLoaderListener() {
        //    @Override
        //    public void onImageDownloaded(Bitmap bmp) {
        //        System.out.println("Image loaded :)");
        //        System.out.println(bmp.getByteCount());
        //        ImageView imgQuestion = (ImageView)findViewById(R.id.imgQuestion);
        //        imgQuestion.setImageBitmap(bmp);
        //    }
        //});
        //mDownloader.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(ch.reato.quizzbateau.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == ch.reato.quizzbateau.R.id.quizz_settings) {
            Intent i = new Intent(this, QuizzActivity.class);
            i.putExtra("currentQuizzId", currentQuizzId);
            startActivityForResult(i, 1);
        }else if(id == ch.reato.quizzbateau.R.id.view_settings){
            Intent i = new Intent(this, ListViewActivity.class);
            i.putExtra("currentQuizzId", currentQuizzId);
            startActivity(i);
        }else if(id == ch.reato.quizzbateau.R.id.sync_settings){
            Intent i = new Intent(this, SyncActivity.class);
            startActivityForResult(i, 2);
        }else if(id == ch.reato.quizzbateau.R.id.deleteall_settings){
            DBController.deleteAllQuestions(this);
            DBController.deleteAllQuizz(this);
            FileHelper fileHelper = FileHelper.getInstance(this);
            fileHelper.deleteAllLocalImages();
            DBController.resetLastSyncDate(this);
            populateUiWithNoQuestion();
        }else if(id == ch.reato.quizzbateau.R.id.resetcounters_settings){
            DBController.resetAllCounters(this, currentQuizzId);
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

    private boolean loadWeightedQuestionDifferentFromCurrent() {
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
        TextView htmlTextView = (TextView)findViewById(ch.reato.quizzbateau.R.id.textQuestion);
        htmlTextView.setText(Html.fromHtml(currentQuestion.getQuestion()));

        TextView htmlAnswer = (TextView)findViewById(ch.reato.quizzbateau.R.id.textAnswer);
        htmlAnswer.setText(Html.fromHtml(currentQuestion.getAnswer()));

        populateUiWithCurrentImage();
        populateUiWithCurrentCounters();

        Button buttonShow = (Button)findViewById(ch.reato.quizzbateau.R.id.buttonShow);
        buttonShow.setEnabled(true);

    }

    private void populateUiWithNoQuestion(){
        TextView htmlTextView = (TextView)findViewById(ch.reato.quizzbateau.R.id.textQuestion);
        htmlTextView.setText(Html.fromHtml("No question"));

        TextView htmlAnswer = (TextView)findViewById(ch.reato.quizzbateau.R.id.textAnswer);
        htmlAnswer.setText("");

        TextView htmlStatsTextView = (TextView)findViewById(ch.reato.quizzbateau.R.id.labelStats);
        htmlStatsTextView.setText("");

        Button buttonShow = (Button)findViewById(ch.reato.quizzbateau.R.id.buttonShow);
        buttonShow.setEnabled(false);
    }

    private void populateUiWithCurrentCounters() {
        TextView htmlStatsTextView = (TextView)findViewById(ch.reato.quizzbateau.R.id.labelStats);
        String text = "<b><font color='#008000'>" + currentQuestion.getCountRight() + "</font> / <font color='red'>" + currentQuestion.getCountWrong() + "</font></b>";
        htmlStatsTextView.setText(Html.fromHtml(text)); // , TextView.BufferType.SPANNABLE
    }

    private void populateUiWithCurrentImage(){

        ImageView myImage = (ImageView) findViewById(R.id.imgQuestion);
        TextView htmlQuestion = (TextView)findViewById(R.id.textQuestion);
        TextView htmlAnswer = (TextView)findViewById(R.id.textAnswer);
        if(currentQuestion.getImagePath().isEmpty()){
            myImage.setVisibility(View.GONE);
            htmlQuestion.setMinLines(6);
            htmlAnswer.setMinLines(6);
            myImage.setImageResource(0);
            return;
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

    private void toggleAnswerVisibility() {
        TextView labelAnswer = (TextView)findViewById(ch.reato.quizzbateau.R.id.labelAnswer);
        TextView textAnswer = (TextView)findViewById(ch.reato.quizzbateau.R.id.textAnswer);
        Button buttonRight = (Button)findViewById(ch.reato.quizzbateau.R.id.buttonRight);
        Button buttonWrong = (Button)findViewById(ch.reato.quizzbateau.R.id.buttonWrong);

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