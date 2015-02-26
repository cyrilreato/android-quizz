package ch.reato.quizzbateau.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import ch.reato.quizzbateau.SyncActivity;
import ch.reato.quizzbateau.model.Question;
import ch.reato.quizzbateau.model.Quizz;
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

    public static void doQuizzAndImagesSync(SyncActivity syncActivity, String maxDate) {
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

    public static void doSyncImages(SyncActivity syncActivity, List<String> images){
        if(isNetworkAvailable(syncActivity)){
            String[] arImages = images.toArray(new String[images.size()]);
            new DownloadImages(syncActivity).execute(arImages);
        }
    }

    public static ArrayList<Quizz> parseNewQuizzJson(String downloadedString){

        ArrayList<Quizz> new_quizz = null;

        try {
            JSONObject json = new JSONObject(downloadedString);
            if(!json.has("quizz_new")){
                return null;
            }
            JSONArray jquizz = (JSONArray) json.get("quizz_new");
            if(jquizz.length()>0){
                new_quizz = new ArrayList<Quizz>();
            }
            for(int i=0;i<jquizz.length();i++){
                Quizz quizz = new Quizz();
                JSONObject q = (JSONObject) jquizz.get(i);

                quizz.setId(Integer.parseInt(q.get("id").toString()));
                quizz.setName(q.getString("name").toString());
                quizz.setDescription(q.getString("description").toString());
                quizz.setStringDatemod(q.getString("datemod").toString());
                new_quizz.add(quizz);

            }
        } catch (Exception e) {
            System.out.println("JSON parsing crashed");
            System.out.println(e.getMessage());
        }

        return new_quizz;
    }

    public static ArrayList<Question> parseNewQuestionsJson(String downloadedString){
        ArrayList<Question> new_questions = null;

        try {
            JSONObject json = new JSONObject(downloadedString);
            if(!json.has("questions_new")){
                return null;
            }
            JSONArray jquestions = (JSONArray) json.get("questions_new");
            if(jquestions.length()>0){
                new_questions = new ArrayList<Question>();
            }
            for(int i=0;i<jquestions.length();i++){
                Question question = new Question();
                JSONObject q = (JSONObject) jquestions.get(i);

                question.setNb(Integer.parseInt(q.get("nb").toString()));
                question.setQuizzId(Integer.parseInt(q.get("quizz_id").toString()));
                question.setQuestion(q.getString("question").toString());
                question.setAnswer(q.getString("answer").toString());
                question.setImagePath(q.getString("image_path").toString());
                question.setStringDatemod(q.getString("datemod").toString());
                new_questions.add(question);

            }
        } catch (Exception e) {
            System.out.println("JSON parsing crashed");
            System.out.println(e.getMessage());
        }

        return new_questions;
    }

    public static ArrayList<String> parseNewImagesJson(String downloadedString){

        ArrayList<String> newImages = null;

        try {
            JSONObject json = new JSONObject(downloadedString);
            if (!json.has("questions_new")) {
                return null;
            }

            JSONArray jquestions = (JSONArray) json.get("questions_new");
            if (jquestions.length() > 0) {
                newImages = new ArrayList<String>();
            }

            for (int i = 0; i < jquestions.length(); i++) {
                Question question = new Question();
                JSONObject q = (JSONObject) jquestions.get(i);
                if(!q.getString("image_path").isEmpty()) {
                    newImages.add(q.getString("image_path").toString());
                }
            }

        }catch(Exception e){
                System.out.println("JSON parsing crashed");
                System.out.println(e.getMessage());
        }

        return newImages;
    }

    public static ArrayList<Integer> parseDeletedQuestionsJson(String downloadedString){
        ArrayList<Integer> deleted_questions = null;

        try {
            JSONObject json = new JSONObject(downloadedString);
            if(!json.has("questions_deleted")){
                return deleted_questions;
            }
            JSONArray jquestions = (JSONArray) json.get("questions_deleted");
            if(jquestions.length()>0){
                deleted_questions = new ArrayList<Integer>();
            }
            for(int i=0;i<jquestions.length();i++){
                JSONObject q = (JSONObject) jquestions.get(i);
                deleted_questions.add(Integer.parseInt(q.get("nb").toString()));
            }
        } catch (Exception e) {
            System.out.println("JSON parsing crashed");
            System.out.println(e.getMessage());
        }

        return deleted_questions;
    }

    public static ArrayList<Integer> parseDeletedQuizzJson(String downloadedString){
        ArrayList<Integer> deleted_quizz = null;

        try {
            JSONObject json = new JSONObject(downloadedString);
            if(!json.has("quizz_deleted")){
                return deleted_quizz;
            }
            JSONArray jquizz = (JSONArray) json.get("quizz_deleted");
            if(jquizz.length()>0){
                deleted_quizz = new ArrayList<Integer>();
            }
            for(int i=0;i<jquizz.length();i++){
                JSONObject q = (JSONObject) jquizz.get(i);
                deleted_quizz.add(Integer.parseInt(q.get("id").toString()));
            }
        } catch (Exception e) {
            System.out.println("JSON parsing crashed");
            System.out.println(e.getMessage());
        }

        return deleted_quizz;
    }

    public static HashMap<String, Integer> parseQuestionsCountJson(String downloadedString) {
        HashMap<String, Integer> questions_counts = null;
        try {
            JSONObject json = new JSONObject(downloadedString);
            questions_counts = new HashMap<String, Integer>();
            questions_counts.put("quizz_new_count", json.getInt("quizz_new_count"));
            questions_counts.put("quizz_deleted_count", json.getInt("quizz_deleted_count"));
            questions_counts.put("questions_new_count", json.getInt("questions_new_count"));
            questions_counts.put("questions_deleted_count", json.getInt("questions_deleted_count"));
        } catch (Exception e) {
            System.out.println("JSON parsing crashed");
            System.out.println(e.getMessage());
        }
        return questions_counts;
    }

    public static String forgeUrl(String url, String maxDate, boolean boatOnly) {
        String params = null;
        if(maxDate!=null && !maxDate.equals("")){
            params = "since=" + maxDate;
        }
        if(boatOnly){
            if(params != null) {
                params = params + "&boatonly=1";
            }else {
                params = "boatonly=1";
            }
        }
        if(params != null){
            params = "?" + params;
        }
        if(params != null) {
            url = url + params;
        }
        return url;
    }
}
