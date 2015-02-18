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

    public static int findQuestionsCount(Context context, int quizzId){
        int questionsCount;
        String query;
        if(quizzId == 0){
            query = "SELECT count(*) FROM question";
        }else {
            query = "SELECT count(*) FROM question WHERE quizz_id = " + quizzId;
        }

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

    public static int findLastQuizzId(Context context){
        int lastQuizzId;
        String query = "SELECT last_quizz_id FROM quizz_config";
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        if(cursor.getCount()==0){
            lastQuizzId = 0;
        }else {
            lastQuizzId = cursor.getInt(0);
        }
        cursor.close();
        return lastQuizzId;
    }

    public static Quizz findQuizz(Context context, int id){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query("quizz",
                new String[]{"id", "name", "description", "datemod"},
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        Quizz quizz = new Quizz();
        quizz.setId(cursor.getInt(0));
        quizz.setName(cursor.getString(1));
        quizz.setDescription(cursor.getString(2));
        quizz.setStringDatemod(cursor.getString(3));
        cursor.close();
        return quizz;
    }

    public static Question findQuestion(Context context, int id){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query("question",
                new String[]{"id", "nb", "question","answer", "image_path", "count_right", "count_wrong", "datemod", "quizz_id"},
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
        question.setImagePath(cursor.getString(4));
        question.setCountRight(cursor.getInt(5));
        question.setCountWrong(cursor.getInt(6));
        question.setStringDatemod(cursor.getString(7));
        question.setQuizzId(cursor.getInt(8));
        cursor.close();
        return question;
    }

    public static Question findNthQuestion(Context context, int n, int quizzId){
        return findNthQuestionDifferentFromId(context, n, 0, quizzId);
    }

    public static Question findNthQuestionDifferentFromId(Context context, int n, int currentId, int quizzId){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT id, nb, question, answer, image_path, count_right, count_wrong, datemod, quizz_id FROM question WHERE 1 = 1 ";
        if(currentId!=0){
            query += "AND id != " + currentId + " ";
        }
        if(quizzId!=0){
            query += "AND quizz_id = " + quizzId + " ";
        }
        query += "ORDER BY count_wrong / ifnull(count_right+count_wrong, 1) DESC, id LIMIT 1 OFFSET " + n;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        Question question = new Question();
        question.setId(cursor.getInt(0));
        question.setNb(cursor.getInt(1));
        question.setQuestion(cursor.getString(2));
        question.setAnswer(cursor.getString(3));
        question.setImagePath(cursor.getString(4));
        question.setCountRight(cursor.getInt(5));
        question.setCountWrong(cursor.getInt(6));
        question.setStringDatemod(cursor.getString(7));
        question.setQuizzId(cursor.getInt(8));

        cursor.close();
        return question;
    }

    public static Cursor fetchAllQuestionsForList(Context context, int quizzId, String constraint){

        String query = "SELECT id as _id, nb, '<b>Q' || nb || ': ' || question || ' <font color=\"#008000\">' || count_right || '</font>/<font color=\"red\">' || count_wrong || '</font></b>' as question, '<b>A: </b>' || answer || '<br><i><font color=\"blue\">' || image_path || '</font></i>' as answer FROM question WHERE 1=1 ";
        if(quizzId!=0){
            query += "and quizz_id = " + quizzId + " ";
        }
        if(constraint!=null){
            query += "and (nb = '" + constraint + "' or question like '%" + constraint + "%' or answer like '%" + constraint + "%') ";
        }
        query += "ORDER BY nb DESC";
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cur = db.rawQuery(query, null);
        if(cur != null){
            cur.moveToFirst();
        }
        return cur;
    }

    public static List<Question> findAllQuestions(Context context){
        List<Question> questions = new ArrayList<Question>();
        String query = "SELECT id, nb, question, answer, image_path, count_right, count_wrong, datemod, quizz_id FROM question";

        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                Question question = new Question();
                question.setId(cursor.getInt(0));
                question.setNb(cursor.getInt(1));
                question.setQuestion(cursor.getString(2));
                question.setAnswer(cursor.getString(3));
                question.setImagePath(cursor.getString(4));
                question.setCountRight(cursor.getInt(5));
                question.setCountWrong(cursor.getInt(6));
                question.setStringDatemod(cursor.getString(7));
                question.setQuizzId(cursor.getInt(8));
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

    public static List<Quizz> findAllQuizz(Context context){
        List<Quizz> quizzes = new ArrayList<Quizz>();
        String query = "SELECT id, name, description, datemod FROM quizz";

        SQLiteDatabase db = SQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                Quizz quizz = new Quizz();
                quizz.setId(cursor.getInt(0));
                quizz.setName(cursor.getString(1));
                quizz.setDescription(cursor.getString(2));
                quizz.setStringDatemod(cursor.getString(3));
                quizzes.add(quizz);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return quizzes;
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
        values.put("image_path", question.getImagePath());
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
        values.put("image_path", question.getImagePath());
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
        values.put("image_path", question.getImagePath());
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

    public static void deleteAllQuizz(Context context){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        db.delete("quizz", null,  null);
        db.close();
    }

    public static void deleteAllQuestions(Context context){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        db.delete("question", null,  null);
        db.close();
    }

    public static void updateLastQuizzId(Context context, int quizzId){
        ContentValues values = new ContentValues();
        values.put("last_quizz_id", quizzId);

        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        db.update("quizz_config", values, null, null);
        db.close();
    }

    public static void resetLastSyncDate(Context context){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("last_sync", "");
        db.update("quizz_config", values, null, null);
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
        db.update("quizz_config", values, null, null);
        db.close();
    }

    public static void resetAllCounters(Context context, int quizzId){
        SQLiteDatabase db = SQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("count_wrong", 0);
        values.put("count_right", 0);
        if(quizzId==0){
            db.update("question", values, null, null);
        }else {
            db.update("question",
                    values,
                    "quizz_id = ?",
                    new String[]{String.valueOf(quizzId)});
        }
        db.close();
    }
}
