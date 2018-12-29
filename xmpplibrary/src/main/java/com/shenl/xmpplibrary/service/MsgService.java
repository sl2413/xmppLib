package com.shenl.xmpplibrary.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.shenl.xmpplibrary.utils.XmppUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

public class MsgService extends Service {

    public static XMPPConnection xmppConnection;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        XmppUtils.XmppConnect(MsgService.this, "172.30.4.15", 5222, new XmppUtils.XmppListener() {
            @Override
            public void Success() {
                XmppUtils.XmppGetMessage(new MessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        Log.e("shenl",message.getBody());
                    }
                });
            }

            @Override
            public void Error(String error) {

            }
        });
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.e("shenl","服务关闭");
        super.onDestroy();
    }
}
