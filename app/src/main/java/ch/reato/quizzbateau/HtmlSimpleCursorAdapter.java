package ch.reato.quizzbateau;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class HtmlSimpleCursorAdapter extends SimpleCursorAdapter {

    public HtmlSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void setViewText (TextView view, String text) {
        view.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
    }
}