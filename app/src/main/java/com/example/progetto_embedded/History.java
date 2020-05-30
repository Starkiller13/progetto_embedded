package com.example.progetto_embedded;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName="history_table")
public class History {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "data")
    private String data;

    @NonNull
    @ColumnInfo(name = "testo")
    private String text;

    public History(String text)
    {
        this.data = new Date().toString();
        this.text=text;
        Log.v("History",text);
    }
    public int getId(){
        return this.id;
    }
    public History getHistory(){
        return this;
    }
    public String getData(){
        return this.data;
    }

    public String getText() {
        return this.text;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setText(String text) {
        this.text = text;
    }
}