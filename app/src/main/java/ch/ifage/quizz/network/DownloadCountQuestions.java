package ch.ifage.quizz.network;

import android.app.Activity;

import ch.ifage.quizz.SyncActivity;

public class DownloadCountQuestions extends ADownloadWebpageTask implements IDownloadWebpage {

    String URL = "http://www.reato.ch/quizz/quizz_count.php";

    public DownloadCountQuestions(Activity activity, String maxDate){
        super(activity);
        loadedBehavior = this;
        urlToLoad = URL;
        if(maxDate!=null && !maxDate.equals("")){
            urlToLoad = urlToLoad + "?since=" + maxDate;
        }
    }

    public void perform(String result) {
        ((SyncActivity)activity).OnLoadedQuestionsCount(result);
    }

}
