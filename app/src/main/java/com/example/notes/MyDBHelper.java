package com.example.notes;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatatypeMismatchException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.notes.Note;

import java.util.ArrayList;
import java.util.Date;

public class MyDBHelper
{
    DBHelper dbHelper;
    String theme;
    public MyDBHelper(Context context, String theme)
    {
        this.theme = theme;
        dbHelper = new DBHelper(context);
    }

    public void addWord(String theme ,Note w)
    {
        String text = w.getText();
        String date;
        if (w.date == null)
            date = new Date().toString();
        else
            date = w.date;
        ContentValues cv = new ContentValues();
        cv.put("txt", text);
        cv.put("date", date);
        cv.put("path",w.getPath());
        Log.d("Inserting", "addWord: "+ cv.toString());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert(theme, null, cv);
        dbHelper.close();
    }

    public ArrayList<Note> getWords(String theme)
    {
        ArrayList<Note> out = new ArrayList<Note>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(theme, null, null, null, null, null, null);
        if (c.moveToFirst())
        {
            int id_col = c.getColumnIndex("id");
            int txt_col = c.getColumnIndex("txt");
            int date_col = c.getColumnIndex("date");
            int path_col = c.getColumnIndex("path");
            int vis_col = c.getColumnIndex("visible");
            do {
                out.add(new Note(c.getInt(id_col), c.getString(date_col), c.getString(txt_col), c.getString(path_col)));
            } while(c.moveToNext());
        }
        else
        {
            c.close();
            return null;
        }
        c.close();

        return out;
    }
    public boolean isCurrentDBAvailable(String theme)
    {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try
        {
            //db.execSQL("drop table" + theme+ ";");
            db.query(theme, null, null, null, null, null, null);
            return true;
        }
        catch (SQLiteException e)
        {
            return false;
        }
    }

    public void updateValue(String theme, Note w)
    {
        String text = w.getText();
        String date = w.getDate();
        ContentValues cv = new ContentValues();
        cv.put("txt", text);
        cv.put("date", date);
        Log.d("Updating", "update word: "+ cv.toString());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update(theme, cv, "id=?", new String[]{Integer.toString(w.getId())});
        dbHelper.close();
    }

    public void dropTable()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //db.execSQL("DROP TABLE IF EXISTS " + theme+ ";");
        db.execSQL("create table "+ theme + " ("
                + "id integer primary key autoincrement,"
                + "txt text, "
                + "date date, "
                + "path text " + ");");
    }

    class DBHelper extends SQLiteOpenHelper
    {

        public DBHelper(Context context)
        {
            // конструктор суперкласса
            super(context, "dict", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            Log.d("LOG_TAG", "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table "+ theme + " ("
                    + "id integer primary key autoincrement,"
                    + "txt text, "
                    + "date date, "
                    + "path text " + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {

        }


    }
}

