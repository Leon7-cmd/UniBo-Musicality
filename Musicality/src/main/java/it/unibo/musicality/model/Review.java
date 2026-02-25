package it.unibo.musicality.model;

public class Review {
    private String evalType;
    private String evalName;
    private int grade;
    private java.util.Date date;
    private int idContent; 

    public Review(String evalType, String evalName, int grade, java.util.Date date, int idContent){
        this.evalType = evalType;
        this.evalName = evalName;
        this.grade = grade;
        this.date = date;
        this.idContent = idContent;
    }

    public Review(){}

    public String getEvalType() {return evalType;}
    public void setEvalType(String evalType) {this.evalType = evalType;}
    public String getEvalName() {return evalName;}
    public void setEvalName(String evalName) {this.evalName = evalName;}
    public int getGrade() {return grade;}
    public void setGrade(int grade) {this.grade = grade;}
    public java.util.Date getDate() {return date;}
    public void setDate(java.util.Date date) {this.date = date;}
    public int getIdContent() {return idContent;}
    public void setIdContent(int idContent) {this.idContent = idContent;}
}