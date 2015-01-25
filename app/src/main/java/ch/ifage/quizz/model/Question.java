package ch.ifage.quizz.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Question {

    private int id;
    private int nb;
    private String question;
    private String answer;
    private Date datemod;

    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public Question(){}

    public Question(String question, String answer){
        super();
        this.question = question;
        this.answer = answer;
    }

    public String toString(){
        return "Question [id=" + id + ", nb=" + nb + ", datemod=" + getStringDatemod() + ", question=" + question + ", answer=" + answer + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNb() {
        return nb;
    }

    public void setNb(int nb) {
        this.nb = nb;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getDatemod(){
        return datemod;
    }

    public String getStringDatemod(){
        return formatter.format(datemod);
    }

    public void setDatemod(Date datemod){
        this.datemod = datemod;
    }

    public void setStringDatemod(String datemod){
        try {
            this.datemod = formatter.parse(datemod);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(datemod + " " + this.datemod.toString());
    }
}
