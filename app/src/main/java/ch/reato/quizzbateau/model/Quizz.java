package ch.reato.quizzbateau.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Quizz {

    private int id;
    private String name;
    private String description;
    private Date datemod;

    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public Quizz(){}

    public String toString(){
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDatemod() {
        return datemod;
    }

    public void setDatemod(Date datemod) {
        this.datemod = datemod;
    }

    public String getStringDatemod(){
        return formatter.format(datemod);
    }

    public void setStringDatemod(String datemod){
        try {
            this.datemod = formatter.parse(datemod);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
