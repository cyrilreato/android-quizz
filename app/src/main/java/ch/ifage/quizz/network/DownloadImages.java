package ch.ifage.quizz.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ch.ifage.quizz.filesystem.FileHelper;
import ch.ifage.quizz.model.Image;

public class DownloadImages extends AsyncTask<String, Integer, List<Image>>{

    final static String URL_PREFIX = "http://www.reato.ch/quizz/images/";
    private ProgressBar pb;
    private String url;
    private Button save;
    private ImageView img;
    private TextView percent;
    private Context context;
    private Bitmap bmp;
    //private ImageLoaderListener listener;
    private int progress;
    List<Image> bitmaps;

    public DownloadImages(Context context){ // , String url, ImageLoaderListener listener
        this.context = context;
    }

    //public interface ImageLoaderListener {
    //    void onImageDownloaded(Bitmap bmp);
    //}

    @Override
    protected void onPreExecute() {
//        System.out.println("Pre execute");
        progress = 0;
//        pb.setVisibility(View.VISIBLE);
//        percent.setVisibility(View.VISIBLE);
//        Toast.makeText(c, "starting download", Toast.LENGTH_SHORT).show();

        super.onPreExecute();
    }

    @Override
    protected List<Image> doInBackground(String... urls) {
        bitmaps = new ArrayList<Image>();
        for(String url : urls) {
            String fullUrl = URL_PREFIX + url;
            bmp = getBitmapFromURL(fullUrl);
            bitmaps.add(new Image(url, bmp));
        }

        //while (progress < 100) {
        //    progress += 1;
        //    publishProgress(progress);
        //    SystemClock.sleep(20);
        //}

        return bitmaps;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {

        System.out.println("Progress " + values[0]);
        /*--- show download progress on main UI thread---*/
        //pb.setProgress(values[0]);
        //percent.setText(values[0] + "%");

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<Image> result) {
        FileHelper fileHelper = FileHelper.getInstance(context);
        for(Image img : result) {
            fileHelper.storeImage(img.getName(), img.getBitmap());
        }
        //if (listener != null) {
        //    listener.onImageDownloaded(bmp);
        //}
        //img.setImageBitmap(bmp);
        //save.setEnabled(true);
        //Toast.makeText(c, "download complete", Toast.LENGTH_SHORT).show();

        super.onPostExecute(result);
    }

    public static Bitmap getBitmapFromURL(String link) {
    /*--- this method downloads an Image from the given URL, then decodes and returns a Bitmap object  ---*/
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("getBmpFromUrl error: ", e.getMessage().toString());
            return null;
        }
    }


}
