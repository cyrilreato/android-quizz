package ch.reato.quizzbateau;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.File;

public class ListViewItemCursorAdapter extends SimpleCursorAdapter {

    private final Context context;

    public ListViewItemCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
    }

    @Override
    public void setViewText (TextView view, String text) {
        view.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
    }

    @Override
    public void setViewImage(ImageView view, String value){
        if(!value.equals("")) {
            File imgFile = new File(context.getFilesDir() + "/" + value);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                view.setImageBitmap(myBitmap);
            }
        }else{
        //    view.setVisibility(View.GONE);
            view.setImageResource(0);
        }

    }


}