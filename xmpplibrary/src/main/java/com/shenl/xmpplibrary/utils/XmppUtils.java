package com.shenl.xmpplibrary.utils;

import android.content.Context;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.Iterator;

public class XmppUtils {

    private static XMPPConnection xmppConnection;

    /**
     * TODO : 初始化连联服务器
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2018/12/26
     *
     * @return :
     */
    public static void XmppConnect(final Context context, final String host, final int port, final Listener listener) {
        ThreadUtils.runSonThread(new Runnable() {
            @Override
            public void run() {
                //开始连接
                try {
                    //创建连接配置对象
                    ConnectionConfiguration config = new ConnectionConfiguration(host, port);
                    //创建连接
                    xmppConnection = new XMPPConnection(config);
                    //开始连接
                    xmppConnection.connect();
                    listener.Success();
                } catch (final XMPPException e) {
                    e.printStackTrace();
                    ThreadUtils.runMainThread(new Runnable() {
                        @Override
                        public void run() {
                            PageUtils.showToast(context, e.getMessage());
                            listener.Error(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    /**
     * TODO : 登陆服务器
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2018/12/26
     *
     * @return :
     */
    public static void XmppLogin(final Context context, String name, String pswd, final Listener listener) {
        //开始登陆
        try {
            xmppConnection.login(name, pswd, "Android");
            ThreadUtils.runMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.Success();
                }
            });
        } catch (XMPPException e) {
            listener.Error(e.getMessage());
        }
    }

    /**
     * TODO : 断开连接
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2018/12/26
     *
     * @return :
     */
    public static void XmppDisconnect() {
        xmppConnection.disconnect();
    }

    /**
     * TODO : 获取联系人
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2018/12/26
     *
     * @return :
     */
    public static Collection<RosterEntry> XmppContacts() {
        Roster roster = xmppConnection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        return entries;
    }


    /**
     * TODO : 监听器
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2018/12/26
     *
     * @return :
     */
    public interface Listener {
        void Success();

        void Error(String error);
    }

}
