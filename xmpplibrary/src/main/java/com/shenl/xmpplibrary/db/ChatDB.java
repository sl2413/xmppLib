package com.shenl.xmpplibrary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shenl.xmpplibrary.dao.ChatDao;


public class ChatDB extends SQLiteOpenHelper {

    public ChatDB(Context context) {
        super(context, "ChatMessage.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建好友列表
        String sql1 = "create table " + ChatDao.GOODFRIEND + " (_id integer primary key autoincrement,Jid char(30) unique,nickName char(50),head char(100))";
        db.execSQL(sql1);
        //创建会话列表
        String sql2 = "create table " + ChatDao.SESSIONLIST + " (_id integer primary key autoincrement,Jid char(30) unique,nickName char(50),head char(100),content text,contentType char(3),UnReadCount char(5),isGroup char(3))";
        db.execSQL(sql2);
        //创建消息列表
        String sql3 = "create table " + ChatDao.MESSAGE + " (_id integer primary key autoincrement,FromJid char(30) ,FromName char(50),ToJid cahr(30),ToName char(50),MsgTime char(20),content text,contentType char(3),isGroup char(3))";
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
