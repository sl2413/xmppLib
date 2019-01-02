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
        XmppUtils.XmppGetMessage(new MessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                Log.e("shenl",message.getBody());
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
