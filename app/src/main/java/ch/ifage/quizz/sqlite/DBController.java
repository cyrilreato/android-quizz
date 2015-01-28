package ch.ifage.quizz.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ch.ifage.quizz.model.Question;
import ch.ifage.quizz.model.Quizz;

public class DBController {

    public static int findQuestionsCount(Context context){
        int questionsCount;
        String query = "SELECT count(*) FROM question";

        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        questionsCount = cursor.getInt(0);
        cursor.close();
        return questionsCount;
    }

    public static String findLastSyncDate(Context context){
        String lastSync;
        String query = "SELECT last_sync FROM quizz_config";
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        if(cursor.getCount()==0){
            lastSync = null;
        }else {
            lastSync = cursor.getString(0);
        }
        cursor.close();
        return lastSync;
    }

    public static Question findQuestion(Context context, int id){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query("question",
                new String[]{"id", "nb", "question","answer", "count_right", "count_wrong", "datemod", "quizz_id"},
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        Question question = new Question();
        question.setId(cursor.getInt(0));
        question.setNb(cursor.getInt(1));
        question.setQuestion(cursor.getString(2));
        question.setAnswer(cursor.getString(3));
        question.setCountRight(cursor.getInt(4));
        question.setCountWrong(cursor.getInt(5));
        question.setStringDatemod(cursor.getString(6));
        question.setQuizzId(cursor.getInt(7));
        cursor.close();
        return question;
    }

    public static Question findNthQuestion(Context context, int n){
        return findNthQuestionDifferentFromId(context, n, 0);
    }

    public static Question findNthQuestionDifferentFromId(Context context, int n, int currentId){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        String query;
        if(currentId==0){
            query = "SELECT id, nb, question, answer, count_right, count_wrong, datemod, quizz_id FROM question ORDER BY count_wrong / ifnull(count_right+count_wrong, 1), id LIMIT 1 OFFSET " + n;
        }else{
            query = "SELECT id, nb, question, answer, count_right, count_wrong, datemod, quizz_id FROM question WHERE id != " + currentId + " ORDER BY count_wrong / ifnull(count_right+count_wrong, 1), id LIMIT 1 OFFSET " + n;
        }

        Cursor cursor = db.rawQuery(query, null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        Question question = new Question();
        question.setId(cursor.getInt(0));
        question.setNb(cursor.getInt(1));
        question.setQuestion(cursor.getString(2));
        question.setAnswer(cursor.getString(3));
        question.setCountRight(cursor.getInt(4));
        question.setCountWrong(cursor.getInt(5));
        question.setStringDatemod(cursor.getString(6));
        question.setQuizzId(cursor.getInt(7));

        cursor.close();
        return question;
    }

    public static List<Question> findAllQuestions(Context context){
        List<Question> questions = new LinkedList<Question>();
        String query = "SELECT id, nb, question, answer, count_right, count_wrong, datemod, quizz_id FROM question";

        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Question question = new Question();
        if(cursor.moveToFirst()){
            do{
                question.setId(cursor.getInt(0));
                question.setNb(cursor.getInt(1));
                question.setQuestion(cursor.getString(2));
                question.setAnswer(cursor.getString(3));
                question.setCountRight(cursor.getInt(4));
                question.setCountWrong(cursor.getInt(5));
                question.setStringDatemod(cursor.getString(6));
                question.setQuizzId(cursor.getInt(7));
                questions.add(question);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return questions;
    }

    public static List<Integer> findAllQuestionsNb(Context context){
        List<Integer> questions_nb = null;
        String query = "SELECT nb FROM question";

        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            questions_nb = new ArrayList<Integer>();
            do{
                questions_nb.add(cursor.getInt(0));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return questions_nb;
    }

    public static List<Integer> findAllQuizzId(Context context){
        List<Integer> quizz_id = null;
        String query = "SELECT id FROM quizz";

        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            quizz_id = new ArrayList<Integer>();
            do{
                quizz_id.add(cursor.getInt(0));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return quizz_id;
    }

    public static void addQuizz(Context context, Quizz quizz){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", quizz.getId());
        values.put("name", quizz.getName());
        values.put("description", quizz.getDescription());
        if(quizz.getDatemod()!=null) {
            values.put("datemod", quizz.getStringDatemod());
        }
        db.insert("quizz", null, values);
        db.close();
    }

    public static void addQuestion(Context context, Question question){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nb", question.getNb());
        values.put("question", question.getQuestion());
        values.put("answer", question.getAnswer());
        values.put("quizz_id", question.getQuizzId());
        if(question.getDatemod()!=null) {
            values.put("datemod", question.getStringDatemod());
        }
        db.insert("question", null, values);
        db.close();
    }

    public static int updateQuestionFromId(Context context, Question question){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nb", question.getNb());
        values.put("question", question.getQuestion());
        values.put("answer", question.getAnswer());
        values.put("count_right", question.getCountRight());
        values.put("count_wrong", question.getCountWrong());
        values.put("datemod", question.getStringDatemod());
        values.put("quizz_id", question.getQuizzId());

        int i = db.update("question",
                values,
                "id = ?",
                new String[]{String.valueOf(question.getId())});
        db.close();
        return i;
    }

    public static int updateQuizzFromId(Context context, Quizz quizz){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", quizz.getId());
        values.put("name", quizz.getName());
        values.put("description", quizz.getDescription());
        values.put("datemod", quizz.getStringDatemod());

        int i = db.update("quizz",
                values,
                "id = ?",
                new String[]{String.valueOf(quizz.getId())});
        db.close();
        return i;
    }

    public static int updateQuestionFromNb(Context context, Question question){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nb", question.getNb());
        values.put("question", question.getQuestion());
        values.put("answer", question.getAnswer());
        values.put("count_right", question.getCountRight());
        values.put("count_wrong", question.getCountWrong());
        values.put("datemod", question.getStringDatemod());
        values.put("quizz_id", question.getQuizzId());

        int i = db.update("question",
                values,
                "nb = ?",
                new String[]{String.valueOf(question.getNb())});
        db.close();
        return i;
    }

    public static void deleteQuestionFromId(Context context, Question question){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        db.delete("question",
                "id=?",
                new String[]{ String.valueOf(question.getId())} );
        db.close();
    }

    public static void deleteQuizzFromId(Context context, Integer id){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        db.delete("quizz",
                "id=?",
                new String[]{ String.valueOf(id)} );
        db.close();
    }

    public static void deleteQuestionFromNb(Context context, Integer nb){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        db.delete("question",
                "nb=?",
                new String[]{ String.valueOf(nb)} );
        db.close();
    }

    public static void deleteAllQuestions(Context context){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        db.delete("question", null,  null);
        db.close();
    }

    public static void updateLastSyncDate(Context context){
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowString = sdf.format(now);
        ContentValues values = new ContentValues();
        values.put("last_sync", nowString);

        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        db.update("quizz_config",
                values,
                null,
                null);
        db.close();
    }

    public static void resetLastSyncDate(Context context){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("last_sync", "");
        db.update("quizz_config",
                values,
                null,
                null);
        db.close();
    }

    public static void resetAllCounters(Context context){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("count_wrong", 0);
        values.put("count_right", 0);
        db.update("question",
                values,
                null,
                null);
        db.close();
    }
}
