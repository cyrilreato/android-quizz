package ch.reato.quizzbateau.network;

import android.app.Activity;
import ch.reato.quizzbateau.SyncActivity;

public class DownloadNewQuestions extends ADownloadWebpageTask implements IDownloadWebpage {

    String URL = "http://www.reato.ch/quizz/json_quizz.php";

    public DownloadNewQuestions(Activity activity, String maxDate){
        super(activity);
        loadedBehavior = this;
        urlToLoad = NetworkHelper.forgeUrl(URL, maxDate, SyncActivity.BOAT_ONLY);
    }

    public void perform(String result) {
        ((SyncActivity)activity).onLoadedNewQuestions(result);
    }

}
