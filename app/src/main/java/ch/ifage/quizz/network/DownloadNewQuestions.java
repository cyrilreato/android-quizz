package ch.ifage.quizz.network;

import android.app.Activity;
import ch.ifage.quizz.SyncActivity;

public class DownloadNewQuestions extends ADownloadWebpageTask implements IDownloadWebpage {

    String URL = "http://www.reato.ch/quizz/questions.php";

    public DownloadNewQuestions(Activity activity, String maxDate){
        super(activity);
        loadedBehavior = this;
        urlToLoad = URL;
        if(maxDate!=null && !maxDate.equals("")){
            urlToLoad = urlToLoad + "?since=" + maxDate;
        }
        System.out.println(urlToLoad);
    }

    public void perform(String result) {
        ((SyncActivity)activity).onLoadedNewQuestions(result);
    }

}
