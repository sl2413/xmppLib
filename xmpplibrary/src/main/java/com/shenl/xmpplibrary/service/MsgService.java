package com.shenl.xmpplibrary.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.dao.ChatDao;
import com.shenl.xmpplibrary.utils.DateTimeUtils;
import com.shenl.xmpplibrary.utils.SystemInfo;
import com.shenl.xmpplibrary.utils.XmppUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import java.util.Date;

public class MsgService extends Service {

    public static XMPPConnection xmppConnection;
    public static String nickname;
    private Intent intent = new Intent("com.shenl.xmpplibrary.fragment.SessionFragment.MsgReceiver");


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //返回 START_STICKY或START_REDELIVER_INTENT
        return START_STICKY;
    }


    @Override
    public void onCreate() {
        XmppUtils.XmppGetMessage(new MessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                if (!TextUtils.isEmpty(message.getBody())) {
                    String user = message.getFrom();
                    user = user.substring(0, user.indexOf("/"));
                    ChatDao dao = new ChatDao(MsgService.this);
                    ChatDao.FriendBean friendBean = dao.queryInfo(user);
                    //缓存聊天记录
                    final String dateStr = DateTimeUtils.formatDate(new Date());
                    ContentValues sessionValues = new ContentValues();
                    sessionValues.put("FromJid",user);
                    sessionValues.put("ToJid",XmppUtils.XmppGetJid());
                    sessionValues.put("name",friendBean.nickName);
                    sessionValues.put("data",dateStr);
                    sessionValues.put("title",message.getBody());
                    sessionValues.put("myself","IN");
                    sessionValues.put("imgPath","");
                    dao.Add(ChatDao.MESSAGE,sessionValues);
                    ChatDao.sessionBean sessionBean = dao.querySession(user);
                    showNotification("收到一条新消息......", "好友消息", message.getBody(), Integer.parseInt(sessionBean.id));
                    if (sessionBean == null) {
                        ContentValues sessionValue = new ContentValues();
                        sessionValue.put("Jid", user);
                        sessionValue.put("nickName", friendBean.nickName);
                        sessionValue.put("head", friendBean.head);
                        sessionValue.put("content", message.getBody());
                        sessionValue.put("contentType", SystemInfo.TEXT);
                        sessionValue.put("isGroup", "0");
                        sessionValue.put("UnReadCount", 1);
                        long add = dao.Add(ChatDao.SESSIONLIST, sessionValue);
                        if (add != -1){
                            sendBroadcast(intent);
                        }
                    } else {
                        ContentValues sessionValue = new ContentValues();
                        sessionValue.put("content", message.getBody());
                        if (TextUtils.isEmpty(sessionBean.UnReadCount)){
                            sessionValue.put("UnReadCount",1);
                        }else{
                            sessionValue.put("UnReadCount",Integer.parseInt(sessionBean.UnReadCount)+1);
                        }
                        int upd = dao.upd(ChatDao.SESSIONLIST, sessionValue, user);
                        if (upd != 0){
                            sendBroadcast(intent);
                        }
                    }
                }
            }
        });
        addSubscriptionListener();
        super.onCreate();
    }

    /**
     * TODO 功能：监听好友添加请求
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/10
     */
    private void addSubscriptionListener() {
        //创建包过滤器
        PacketFilter filter = new PacketFilter() {
            @Override
            public boolean accept(Packet packet) {
                if (packet instanceof Presence) {
                    Presence presence = (Presence) packet;
                    //是好友邀请状态就返回true 向下执行
                    if (presence.getType().equals(Presence.Type.subscribe)) {
                        return true;
                    }
                }
                return false;
            }
        };
        xmppConnection.addPacketListener(subscriptionPacketListener, filter);
    }

    /**
     * TODO 功能：好友监听
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/10
     */
    private PacketListener subscriptionPacketListener = new PacketListener() {

        @Override
        public void processPacket(final Packet packet) {
            //过滤自己加自己的状态
            if (packet.getFrom().contains(XmppUtils.XmppGetJid()))
                return;
            showNotification("好友添加提醒......", "好友添加", "来自" + packet.getFrom() + "的好友请求", 2);
            Log.e("shenl", "来自" + packet.getFrom() + "的好友请求");
        }
    };

    /**
     * TODO 功能：在手机状态栏显示通知
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/10
     */
    private void showNotification(String Ticker, String Title, String Text, int flag) {
        NotificationManager manger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //为了版本兼容  选择V7包下的NotificationCompat进行构造
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MsgService.this);
        builder.setTicker(Ticker);
        builder.setContentTitle(Title);
        builder.setContentText(Text);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        manger.notify(flag, notification);

        /*Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (uri == null) return;
        Ringtone r = RingtoneManager.getRingtone(MsgService.this, uri);
        r.play();*/
    }

    @Override
    public void onDestroy() {
        Log.e("shenl", "服务关闭");
        super.onDestroy();
    }
}
