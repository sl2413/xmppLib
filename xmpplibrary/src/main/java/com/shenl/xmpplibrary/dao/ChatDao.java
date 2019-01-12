package com.shenl.xmpplibrary.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shenl.xmpplibrary.db.ChatDB;

import java.util.ArrayList;

/**
 * TODO : 操作ChatMessage.db数据库
 * 参数说明 :
 * 作者 : shenl
 * 创建日期 : 2019/1/13
 *
 * @return :
 */
public class ChatDao {

    /**
     * TODO : 临时会话列表
     * 参数说明 :<br>
     * jid ==>好友的id<br>
     * nick_name ==> 好友的昵称<br>
     * isGroup  ==> 是否为群聊<br>
     * 作者 : shenl
     * 创建日期 : 2019/1/13
     * @return :
     */
    public static final String SESSION = "session_list";

    private final SQLiteDatabase db;

    public ChatDao(Context context) {
        ChatDB chatDB = new ChatDB(context);
        db = chatDB.getWritableDatabase();
    }

    /**
     * TODO : 增加
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2019/1/13
     *
     * @return : -1 表示插入失败， 否则返回条目ID
     */
    public long Add(String table, ContentValues values) {
        return db.insert(table, null, values);
    }

    /**
     * TODO : 删除
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2019/1/13
     *
     * @return : 返回删除满足条件数据的个数。0 为没有删除
     */
    public int del(String table, String id) {
        return db.delete(table, "_id = ?", new String[]{id});
    }

    /**
     * TODO : 更新
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2019/1/13
     *
     * @return : 所影响数据的个数。0 为没有修改
     */
    public int upd(String table, ContentValues values, String id) {
        return db.update(table, values, "_id = ?", new String[]{id});
    }

    /**
     * TODO : 查询
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2019/1/13
     * @return :
     */
    public ArrayList<sessionBean> query(String table){
        //String table,
        //String[] columns,查询的字段
        //String selection,查询的条件
        //String[] selectionArgs,条件的占位符
        //String groupBy,分组
        //String having,
        //String orderBy,排序
        //String limit 分页
        Cursor cursor = db.query(table, null, null, null, null, null, null, null);
        ArrayList<sessionBean> list = new ArrayList<>();
        while (cursor.moveToNext()){
            sessionBean bean = new sessionBean();
            bean.id = cursor.getString(0);
            bean.Jid = cursor.getString(1);
            bean.nick_name = cursor.getString(2);
            bean.isGroup = cursor.getString(3);
            list.add(bean);
        }
        return list;
    }

    public class sessionBean{
        public String id;
        public String Jid;
        public String nick_name;
        public String isGroup;
    }

}
