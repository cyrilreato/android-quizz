package ch.reato.quizzbateau.network;

import ch.reato.quizzbateau.SyncActivity;

public class DownloadCountQuestions extends ADownloadWebpageTask implements IDownloadWebpage {

    String URL = "http://www.reato.ch/quizz/json_quizz_count.php";

    public DownloadCountQuestions(SyncActivity activity, String maxDate){
        super(activity);
        loadedBehavior = this;
        urlToLoad = NetworkHelper.forgeUrl(URL, maxDate, SyncActivity.BOAT_ONLY);
    }

    public void perform(String result) {
        ((SyncActivity)activity).onLoadedQuestionsCount(result);
    }

}
