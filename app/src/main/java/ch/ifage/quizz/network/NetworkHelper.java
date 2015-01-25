package ch.ifage.quizz.network;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import ch.ifage.quizz.SyncActivity;
import ch.ifage.quizz.model.Question;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelper {

    public static boolean isNetworkAvailable(Activity act) {

        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static void doSync(SyncActivity syncActivity, String maxDate) {
        if(isNetworkAvailable(syncActivity)){
            new DownloadNewQuestions(syncActivity, maxDate).execute();
        } else {
            System.out.println("No network connection available.");
        }
    }

    public static void doQuestionsCountSync(SyncActivity syncActivity, String maxDate) {
        if(isNetworkAvailable(syncActivity)){
            new DownloadCountQuestions(syncActivity, maxDate).execute();
        } else {
            System.out.println("No network connection available.");
        }
    }

    public static ArrayList<Question> parseQuestionsJson(String downloadedString){

        System.out.println("JSON parsing");

        ArrayList<Question> questions = null;

        try {
            JSONObject json = new JSONObject(downloadedString);

            JSONArray jquestions = (JSONArray) json.get("questions");
            if(jquestions.length()>0){
                questions = new ArrayList<Question>();
            }
            for(int i=0;i<jquestions.length();i++){
                Question question = new Question();
                JSONObject q = (JSONObject) jquestions.get(i);

                question.setNb(Integer.parseInt(q.get("nb").toString()));
                question.setQuestion(q.getString("question").toString());
                question.setAnswer(q.getString("answer").toString());
                question.setStringDatemod(q.getString("datemod").toString());
                questions.add(question);

            }
        } catch (Exception e) {
            System.out.println("JSON parsing crashed");
            System.out.println(e.getMessage());
        }

        return questions;
    }

    public static ArrayList<Integer> parseDeletedQuestionsJson(String downloadedString){

        System.out.println("JSON parsing");

        ArrayList<Integer> deleted_nb = null;

        try {
            JSONArray json = new JSONArray(downloadedString);

            if(json.length()>0){
                deleted_nb = new ArrayList<Integer>();
            }
            for(int i=0;i<json.length();i++){
                System.out.println(json.get(i));
                //deleted_nb.add(Integer.parseInt(q.get("nb").toString()));
            }
        } catch (Exception e) {
            System.out.println("JSON parsing crashed");
            System.out.println(e.getMessage());
        }

        return deleted_nb;
    }


    public static HashMap<String, Integer> parseQuestionsCountJson(String downloadedString) {
        HashMap<String, Integer> questions_counts = null;
        try {
            JSONObject json = new JSONObject(downloadedString);
            //JSONObject jcount = (JSONArray) json.get(0);
            questions_counts = new HashMap<String, Integer>();
            questions_counts.put("questions_count", json.getInt("questions_count"));
            questions_counts.put("deleted_count", json.getInt("deleted_count"));
        } catch (Exception e) {
            System.out.println("JSON parsing crashed");
            System.out.println(e.getMessage());
        }
        return questions_counts;
    }

}
