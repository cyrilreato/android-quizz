package ch.ifage.quizz;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import ch.ifage.quizz.sqlite.DBController;


public class ListViewActivity extends Activity {

    private HtmlSimpleCursorAdapter dataAdapter;
    private Cursor cursor;
    private int currentQuizzId;
    private Activity listActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        Intent i = getIntent();
        currentQuizzId = i.getIntExtra("currentQuizzId", 0);

        displayListView();

        listActivity = this;

        // Filter
        EditText filter = (EditText) this.findViewById(R.id.myFilter);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return DBController.fetchAllQuestionsForList(listActivity, currentQuizzId, constraint.toString());
            }
        });

    }

    private void displayListView() {

        String constraint = null;
        cursor = DBController.fetchAllQuestionsForList(this, currentQuizzId, constraint);
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