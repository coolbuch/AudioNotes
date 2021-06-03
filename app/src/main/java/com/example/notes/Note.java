package com.example.notes;

public class Note
{
    String date, text;
    int id;

    public Note(int id, String date, String text)
    {
        this.date = date;
        this.text = text;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}
