package ch.ifage.quizz.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ch.ifage.quizz.model.Question;

public class DBController {

    public static String findQuestionMaxDate(Context context){
        String maxDate;
        String query = "SELECT max(datemod) FROM question";

        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        maxDate = cursor.getString(0);
        cursor.close();
        return maxDate;
    }

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

    public static Question getQuestion(Context context, int id){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query("question",
                new String[]{"id", "nb", "question","answer", "datemod"},
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
        question.setStringDatemod(cursor.getString(4));
        cursor.close();
        return question;
    }

    public static Question getNthQuestion(Context context, int n){
        return getNthQuestionDifferentFromId(context, n, 0);
    }

    public static Question getNthQuestionDifferentFromId(Context context, int n, int currentId){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        String query;
        if(currentId==0){
            query = "SELECT id, nb, question, answer, datemod FROM question ORDER BY id LIMIT 1 OFFSET " + n;
        }else{
            query = "SELECT id, nb, question, answer, datemod FROM question WHERE id != " + currentId + " ORDER BY id LIMIT 1 OFFSET " + n;
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
        question.setStringDatemod(cursor.getString(4));
        cursor.close();
        return question;
    }


    public static List<Question> getAllQuestions(Context context){
        List<Question> questions = new LinkedList<Question>();
        String query = "SELECT id, nb, question, answer, datemod FROM question";

        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Question question = new Question();
        if(cursor.moveToFirst()){
            do{
                question.setId(cursor.getInt(0));
                question.setNb(cursor.getInt(1));
                question.setQuestion(cursor.getString(2));
                question.setAnswer(cursor.getString(3));
                question.setStringDatemod(cursor.getString(4));
                questions.add(question);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return questions;
    }


    public static void addQuestion(Context context, Question question){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nb", question.getNb());
        values.put("question", question.getQuestion());
        values.put("answer", question.getAnswer());
        values.put("datemod", question.getStringDatemod());
        db.insert("question", null, values);
        db.close();
    }


    public static int updateQuestion(Context context, Question question){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nb", question.getNb());
        values.put("question", question.getQuestion());
        values.put("answer", question.getAnswer());
        values.put("datemod", question.getStringDatemod());

        int i = db.update("question",
                values,
                "id = ?",
                new String[]{String.valueOf(question.getId())});
        db.close();
        return i;
    }

    public static void deleteQuestion(Context context, Question question){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        db.delete("question",
                "id=?",
                new String[]{ String.valueOf(question.getId())} );
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

}
