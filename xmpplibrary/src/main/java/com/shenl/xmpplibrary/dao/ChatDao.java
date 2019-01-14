package com.shenl.xmpplibrary.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shenl.xmpplibrary.bean.sessionBean;
import com.shenl.xmpplibrary.db.ChatDB;

import java.util.ArrayList;
import java.util.List;

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
     * TODO 功能：好友列表
     * <p>
     * 参数说明:<br>
     * _id ==>列表id<br>
     * Jid ==>好友的id<br>
     * nickName ==>好友的昵称<br>
     * head ==>好友的头像<br>
     * 作    者:   沈 亮
     * 创建时间:   2019/1/14
     */
    public static final String GOODFRIEND = "GoodFriend";

    /**
     * TODO 功能：消息列表
     * <p>
     * 参数说明:<br>
     * _id ==>列表id<br>
     * FromJid ==>发送消息的人员id<br>
     * FromName ==>发送消息的人员昵称<br>
     * ToJid ==>收到消息的人员id<br>
     * ToName ==>收到消息的人员昵称<br>
     * MsgTime ==>消息的时间<br>
     * Content ==>消息的内容<br>
     * ContentType ==>内容的类型<br>
     * 作    者:   沈 亮
     * 创建时间:   2019/1/14
     */
    public static final String MESSAGE = "Message";

    /**
     * TODO : 临时会话列表
     * <p>
     * 参数说明 :<br>
     * _id ==>列表id<br>
     * Jid ==>好友的id<br>
     * nickName ==> 好友的昵称<br>
     * head ==> 好友的头像<br>
     * content ==> 发来的内容<br>
     * contentType ==> 内容的类型<br>
     * UnReadCount ==> 未读消息条数<br>
     * isGroup  ==> 是否为群聊<br>
     * 作者 : shenl
     * 创建日期 : 2019/1/13
     *
     * @return :
     */
    public static final String SESSIONLIST = "SessionList";

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
     *
     * @return :
     */
    public List<sessionBean> query(String table) {
        //String table,
        //String[] columns,查询的字段
        //String selection,查询的条件
        //String[] selectionArgs,条件的占位符
        //String groupBy,分组
        //String having,
        //String orderBy,排序
        //String limit 分页
        Cursor cursor = db.query(table, null, null, null, null, null, null, null);
        List<sessionBean> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            sessionBean bean = new sessionBean();
            bean.id = cursor.getString(0);
            bean.Jid = cursor.getString(1);
            bean.nickName = cursor.getString(2);
            bean.isGroup = cursor.getString(3);
            list.add(bean);
        }
        return list;
    }

    /**
     * TODO 功能：查询好友的信息
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/14
     */
    public FriendBean queryInfo(String Jid) {
        Cursor cursor = db.query(ChatDao.GOODFRIEND, null, "Jid = ?", new String[]{Jid}, null, null, null);
        cursor.moveToFirst();
        FriendBean bean = new FriendBean();
        bean.id = cursor.getString(0);
        bean.Jid = cursor.getString(1);
        bean.nickName = cursor.getString(2);
        bean.head = cursor.getString(3);
        Log.e("shenl","好友信息..."+bean.toString());
        return bean;
    }

    /**
     * TODO 功能：查询临时列表信息
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/14
     */
    public sessionBean querySession(String Jid) {
        Cursor cursor = db.query(ChatDao.SESSIONLIST, null, "Jid=?", new String[]{Jid}, null, null, null);
        cursor.moveToFirst();
        sessionBean bean = new sessionBean();
        if (cursor.getCount() != 0){
            bean.id = cursor.getString(0);
            bean.Jid = cursor.getString(1);
            bean.nickName = cursor.getString(2);
            bean.head = cursor.getString(3);
            bean.content = cursor.getString(4);
            bean.contentType = cursor.getString(5);
            bean.UnReadCount = cursor.getString(6);
            bean.isGroup = cursor.getString(7);
            Log.e("shenl","临时信息..."+bean.toString());
        }else{
            bean = null;
        }
        return bean;
    }

    public class FriendBean {
        public String id;
        public String Jid;
        public String nickName;
        public String head;

        @Override
        public String toString() {
            return "FriendBean{" +
                    "id='" + id + '\'' +
                    ", Jid='" + Jid + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", head='" + head + '\'' +
                    '}';
        }
    }

    public class sessionBean {
        public String id;
        public String Jid;
        public String nickName;
        public String head;
        public String content;
        public String contentType;
        public String UnReadCount;
        public String isGroup;

        @Override
        public String toString() {
            return "sessionBean{" +
                    "id='" + id + '\'' +
                    ", Jid='" + Jid + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", head='" + head + '\'' +
                    ", content='" + content + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", UnReadCount='" + UnReadCount + '\'' +
                    ", isGroup='" + isGroup + '\'' +
                    '}';
        }
    }

}
