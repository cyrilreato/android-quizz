package ch.ifage.quizz.network;

import android.app.Activity;

import ch.ifage.quizz.SyncActivity;

public class DownloadDeletedQuestions extends ADownloadWebpageTask implements IDownloadWebpage {

    String URL = "http://www.reato.ch/quizz/quizz_deleted.php";

    public DownloadDeletedQuestions(Activity activity, String maxDate){
        super(activity);
        loadedBehavior = this;
        urlToLoad = URL + "?since=" + maxDate;
    }

    public void perform(String result) {
        ((SyncActivity)activity).onLoadedDeletedQuestions(result);
    }

}
