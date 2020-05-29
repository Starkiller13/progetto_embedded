package com.example.progetto_embedded;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="history_table")
public class History {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String text;

    public History(int id,String text)
    {
        this.id=id;
        this.text=text;
    }

    public String getHisory() { return this.text;}
}