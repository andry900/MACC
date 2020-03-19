package com.example.macc;

public class Reviews {
    private String idUser;
    private String idReview;
    private String exam;
    private String professor;
    private String mark;
    private String niceness;
    private String comment;

    public Reviews(String idUser, String idReview, String exam, String professor,
                   String mark, String niceness, String comment) {
        this.idUser = idUser;
        this.idReview = idReview;
        this.exam = exam;
        this.professor = professor;
        this.mark = mark;
        this.niceness = niceness;
        this.comment = comment;
    }

    public Reviews(){}

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdReview() {
        return idReview;
    }

    public void setIdReview(String idReview) {
        this.idReview = idReview;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getNiceness() {
        return niceness;
    }

    public void setNiceness(String niceness) {
        this.niceness = niceness;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
