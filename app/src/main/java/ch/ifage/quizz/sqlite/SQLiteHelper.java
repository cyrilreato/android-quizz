package ch.ifage.quizz.sqlite;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper sInstance;
    private final Context myContext;
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "QuizzDB";


    public static SQLiteHelper getInstance(Context context){
        if(sInstance == null){
            sInstance = new SQLiteHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    private SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("onCreate database");
        String CREATE_QUESTION_TABLE = "CREATE TABLE question (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nb INTEGER, " +
                "question TEXT, " +
                "answer TEXT, " +
                "datemod TEXT " +
                ")";
        db.execSQL(CREATE_QUESTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("onUpgrade database");
        db.execSQL("DROP TABLE IF EXISTS question");
        this.onCreate(db);
    }


}