package ch.ifage.quizz;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import ch.ifage.quizz.sqlite.DBController;


public class ListViewActivity extends Activity {

    private HtmlSimpleCursorAdapter dataAdapter;
    private Cursor cursor;
    private int currentQuizzId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        Intent i = getIntent();
        currentQuizzId = i.getIntExtra("currentQuizzId", 0);

        displayListView();
    }

    private void displayListView() {

        cursor = DBController.fetchAllQuestionsForList(this, currentQuizzId);
        String[] columns = new String[]{
                "question",
                "answer"
        };
        int[] to = new int[]{
                R.id.question,
                R.id.answer
        };

        dataAdapter = new HtmlSimpleCursorAdapter(this, R.layout.list_view_item, cursor, columns, to, 0);
        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(dataAdapter);
    }

    @Override
    protected void onDestroy() {
        //System.out.println("Destroy list view activity");
        cursor.close();
        super.onDestroy();
    }
}