package com.shenl.xmpplibrary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ChatDB extends SQLiteOpenHelper {

    public ChatDB(Context context) {
        super(context,"ChatMessage.db", null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table session_list(_id integer primary key autoincrement,jid char(30) unique,nick_name char(50),isGroup char(3))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
