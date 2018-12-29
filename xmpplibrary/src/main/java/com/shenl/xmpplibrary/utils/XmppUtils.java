package com.shenl.xmpplibrary.utils;

import android.content.Context;
import android.os.Handler;

import com.shenl.xmpplibrary.service.MsgService;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XmppUtils {

    private static Handler mhandler = new Handler();

    /**
     * TODO : 初始化连联服务器
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2018/12/26
     *
     * @return :
     */
    public static void XmppConnect(final Context context, final String host, final int port, final XmppListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //开始连接
                try {
                    //创建连接配置对象
                    ConnectionConfiguration config = new ConnectionConfiguration(host, port);
                    //创建连接
                    MsgService.xmppConnection = new XMPPConnection(config);
                    //开始连接
                    MsgService.xmppConnection.connect();
                    listener.Success();
                } catch (final XMPPException e) {
                    e.printStackTrace();
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.Error(e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * TODO : 登陆服务器
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2018/12/26
     *
     * @return :
     */
    public static void XmppLogin(final Context context, String name, String pswd, final XmppListener listener) {
        //开始登陆
        try {
            MsgService.xmppConnection.login(name, pswd, "Android");
            mhandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.Success();
                }
            });
        } catch (final XMPPException e) {
            mhandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.Error(e.getMessage());
                }
            });
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
        MsgService.xmppConnection.disconnect();
    }

    /**
     * TODO : 获取联系人
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2018/12/26
     *
     * @return :
     */
    public static List<RosterEntry> XmppContacts() {
        Roster roster = MsgService.xmppConnection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        List<RosterEntry> list = new ArrayList<>();
        for (RosterEntry entry : entries) {
            list.add(entry);
        }
        return list;
    }

    /**
     * TODO 功能：发送消息
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2018/12/29
     */
    public static void XmppSendMessage(String toUserID, Message msg, XmppListener listener) {
        //获取消息管理类
        ChatManager chatMan = MsgService.xmppConnection.getChatManager();
        //创建消息对象 参数（用户名称，MessageListener消息监听）
        Chat newchat = chatMan.createChat(toUserID, null);
        try {
            // 发送消息
            newchat.sendMessage(msg);
            listener.Success();
        } catch (XMPPException e) {
            e.printStackTrace();
            listener.Error(e.getMessage());
        }
    }

    /**
     * TODO 功能：获取当前好友发来的消息
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2018/12/29
     */
    public static void XmppGetMessage(final MessageListener listener) {
        //获取消息管理类
        ChatManager chatMan = MsgService.xmppConnection.getChatManager();
        //添加聊天监听
        chatMan.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean able) {
                // 添加消息监听
                chat.addMessageListener(listener);
            }
        });
    }


    /**
     * TODO : 监听器
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2018/12/26
     *
     * @return :
     */
    public interface XmppListener {
        void Success();

        void Error(String error);
    }

}
