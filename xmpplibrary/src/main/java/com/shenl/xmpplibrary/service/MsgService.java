package com.shenl.xmpplibrary.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.shenl.xmpplibrary.bean.sessionBean;
import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.List;

public class MsgService extends Service {

    public static XMPPConnection xmppConnection;
    public static String nickname;
    public static List<sessionBean> sessionList = new ArrayList<>();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        XmppUtils.XmppGetMessage(new MessageListener() {
//            @Override
//            public void processMessage(Chat chat, Message message) {
//                Log.e("shenl",message.getBody());
//            }
//        });
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.e("shenl","服务关闭");
        super.onDestroy();
    }
}
