package com.limethecoder.data.domain;


import java.util.Date;

public class Review {
    private User user;
    private String text;
    private Date date;
    private int rate;

    public Review() {}

    public Review(User user, String text, Date date) {
        this.user = user;
        this.text = text;
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
