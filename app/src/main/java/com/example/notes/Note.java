package com.example.notes;

import com.microsoft.cognitiveservices.speech.AudioDataStream;
public class Note
{
    String date,  text, path;
    int id;
    public Note(int id, String date, String text)
    {
        this.date = date;
        this.text = text;
        this.id = id;
    }

    public Note(int id, String date, String text, String path)
    {
        this.date = date;
        this.text = text;
        this.id = id;
        this.path = path;
    }

    public Note(String text)
    {
        this.text = text;
    }
    public Note(String text, String path)
    {
        this.text = text;
        this.date = path;
        this.path = Integer.toString(path.hashCode());
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

    public String getPath() { return path; }
}
