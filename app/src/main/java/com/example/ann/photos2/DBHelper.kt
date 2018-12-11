package com.example.ann.photos2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper



class DBHelper(context: Context) : SQLiteOpenHelper(context, "myDB", null, 1) {

    override fun onCreate(db:SQLiteDatabase) {
          db.execSQL(
              "create table mytable ("
              + "id integer primary key autoincrement,"
              + "title text,"
              + "link text,"
              + "desc text" + ");"
          )
    }

    override fun onUpgrade(db:SQLiteDatabase, oldVersion:Int, newVersion:Int) {
    }
}