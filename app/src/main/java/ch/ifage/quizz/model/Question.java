package ch.ifage.quizz.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Question {

    private int id;
    private int quizzId;
    private int nb;
    private String question;
    private String answer;
    private String imagePath;
    private int countRight;
    private int countWrong;
    private Date datemod;

    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public Question(){}

    public Question(int nb, String question, String answer){
        super();
        this.nb = nb;
        this.question = question;
        this.answer = answer;
    }

    public String toString(){
        return "Question [id=" + id + ", quizz_id=" + quizzId + ", nb=" + nb + ", datemod=" + getStringDatemod() + ", question=" + question + ", answer=" + answer + ", right/wrong" + countRight + "/" + countWrong + "]";
    }

    public void incrementCountRight(){
        countRight++;

    }
    public void incrementCountWrong(){
        countWrong++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuizzId(){
        return this.quizzId;
    }

    public void setQuizzId(int quizzId){
        this.quizzId = quizzId;
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

    public String getImagePath(){ return imagePath; }

    public void setImagePath(String imagePath){ this.imagePath = imagePath; }

    public Date getDatemod(){
        return datemod;
    }

    public int getCountRight(){
        return countRight;
    }

    public void setCountRight(int countRight){
        this.countRight = countRight;
    }

    public int getCountWrong(){
        return countWrong;
    }

    public void setCountWrong(int countWrong){
        this.countWrong = countWrong;
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
    }
}
