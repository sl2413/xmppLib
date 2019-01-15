package com.shenl.xmpplibrary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

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
        String sql3 = "create table " + ChatDao.MESSAGE + " (_id integer primary key autoincrement,FromJid char(30),ToJid char(30),name char(50),data char(20),title char(30),myself char(5),imgPath char(300))";
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
