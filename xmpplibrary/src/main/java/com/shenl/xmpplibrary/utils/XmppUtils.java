package com.shenl.xmpplibrary.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.shenl.xmpplibrary.service.MsgService;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class XmppUtils {

    private static Handler mhandler = new Handler();
    public static String host;
    public static int port;
    public static String sName;
    private static FileTransferManager fileManager;
    private static MultiUserChat muc;


    /**
     * TODO : 初始化连联服务器
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2018/12/26
     *
     * @return :
     */
    public static void XmppConnect(final Context context, final String host, final int port, String sName, final XmppListener listener) {
        XmppUtils.host = host;
        XmppUtils.port = port;
        XmppUtils.sName = sName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //开始连接
                try {
                    //创建连接配置对象
                    ConnectionConfiguration config = new ConnectionConfiguration(host, port);
                    //设置断网重连 默认为true
                    config.setReconnectionAllowed(true);
                    //设置登录状态 true-为在线
                    config.setSendPresence(true);
                    config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                    //设置不需要SAS验证
                    //config.setSASLAuthenticationEnabled(false);
                    // 是否启用调试
                    config.setDebuggerEnabled(false);
                    //创建连接
                    MsgService.xmppConnection = new XMPPConnection(config);
                    //开始连接
                    MsgService.xmppConnection.connect();
                    listener.Success();
                } catch (final Exception e) {
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
        } catch (final Exception e) {
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
     * TODO 功能：获取服务器群列表
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/2
     */
    public static void XmppServiceRooms(final GroupListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<HostedRoom> list = new ArrayList<>();
                try {
                    Log.e("shenl",MsgService.xmppConnection.getServiceName());
                    Collection<HostedRoom> hostedRooms = MultiUserChat.getHostedRooms(MsgService.xmppConnection, MsgService.xmppConnection.getServiceName());
                    if (hostedRooms.size() != 0) {
                        //遍历每个人所创建的群
                        for (HostedRoom host : hostedRooms) {
                            //遍历某个人所创建的群
                            for (HostedRoom singleHost : MultiUserChat.getHostedRooms(MsgService.xmppConnection, host.getJid())) {
                                RoomInfo info = MultiUserChat.getRoomInfo(MsgService.xmppConnection, singleHost.getJid());
                                if (singleHost.getJid().indexOf("@") > 0) {
                                    list.add(singleHost);
                                }
                            }
                        }
                    }
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.Success(list);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /*public static void XmppServiceRooms(final GroupListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<HostedRoom> list = new ArrayList<>();
                try {
                    Log.e("shenl",MsgService.xmppConnection.getServiceName());
                    Collection<HostedRoom> hostedRooms = MultiUserChat.getHostedRooms(MsgService.xmppConnection, MsgService.xmppConnection.getServiceName());
                    if (hostedRooms.size() != 0) {
                        //遍历每个人所创建的群
                        for (HostedRoom host : hostedRooms) {
                            //遍历某个人所创建的群
                            for (HostedRoom singleHost : MultiUserChat.getHostedRooms(MsgService.xmppConnection, host.getJid())) {
                                RoomInfo info = MultiUserChat.getRoomInfo(MsgService.xmppConnection, singleHost.getJid());
                                if (singleHost.getJid().indexOf("@") > 0) {
                                    list.add(singleHost);
                                }
                            }
                        }
                    }
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.Success(list);
                        }
                    });
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/

    /**
     * TODO 功能：发送单聊消息
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2018/12/29
     */
    public static void XmppSendMessage(final String toUserID, final Message msg, final XmppListener listener) {
        //获取消息管理类
        ChatManager chatMan = MsgService.xmppConnection.getChatManager();
        //创建消息对象 参数（用户名称，MessageListener消息监听）
        Chat newchat = chatMan.createChat(toUserID, null);
        try {
            // 发送消息
            newchat.sendMessage(msg);
            listener.Success();
        } catch (final XMPPException e) {
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
            public void chatCreated(final Chat chat, boolean able) {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 添加消息监听
                        chat.addMessageListener(listener);
                    }
                });
            }
        });
    }

    /**
     * TODO 功能：加入群聊
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/3
     */
    public static void XmppJoinRoom(String nickname, String password, String roomName, XmppListener listener) {
        try {
            // 使用XMPPConnection创建一个MultiUserChat窗口
            muc = new MultiUserChat(MsgService.xmppConnection, roomName
                    + "@conference." + MsgService.xmppConnection.getServiceName());
            // 聊天室服务将会决定要接受的历史记录数量
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);
            //history.setSince(new Date());
            // 用户加入聊天室
            muc.join(nickname, password, history, SmackConfiguration.getPacketReplyTimeout());
            listener.Success();
        } catch (XMPPException e) {
            listener.Error(e.getMessage());
            muc = null;
        }
    }

    /**
     * TODO 功能：获取加入过的群
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/3
     */
    public static void XmppGetJoinRooms() {
        Iterator<String> joinedRooms = MultiUserChat.getJoinedRooms(MsgService.xmppConnection, MsgService.xmppConnection.getServiceName());
    }

    /**
     * TODO 功能：发送群聊消息
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/3
     */
    public static void XmppSendGroupMessage(String msg, XmppListener listener) {
        try {
            muc.sendMessage(msg);
            listener.Success();
        } catch (XMPPException e) {
            listener.Error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * TODO 功能：获取当前群组发来的消息
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/3
     */
    public static void XmppGroupMessage(final PacketListener listener) {
        // 使用XMPPConnection创建一个MultiUserChat窗口
        muc.addMessageListener(listener);
    }

    /**
     * TODO 功能：发送文件
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/2
     */
    public static void XmppSendFile(final String toUser, final File file, final XmppListener listener) {
        try {
            FileTransferManager fileTransferManager = new FileTransferManager(MsgService.xmppConnection);
            if (file.exists() == false) {
                return;
            }
            OutgoingFileTransfer outfile = fileTransferManager.createOutgoingFileTransfer(toUser);
            outfile.sendFile(file, "文件来自" + toUser);
            while (!outfile.isDone()) {
                if (outfile.getStatus().equals(FileTransfer.Status.error)) {
                    Log.e("shenl", "ERROR!!! " + outfile.getError());
                } else {
                    Log.e("shenl", "Status=" + outfile.getStatus());
                    Log.e("shenl", "Progress=" + outfile.getProgress());
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //文件发送完毕
            if (outfile.isDone()) {
                listener.Success();
            }
        } catch (XMPPException e) {
            listener.Error(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * TODO : 文件接收监听器
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2019/1/3
     *
     * @return :
     */
    public static void XmppGetFile(FileTransferListener listener) {
        ServiceDiscoveryManager sdManager = ServiceDiscoveryManager
                .getInstanceFor(MsgService.xmppConnection);
        if (sdManager == null) {
            sdManager = new ServiceDiscoveryManager(MsgService.xmppConnection);
        }
        sdManager.addFeature("http://jabber.org/protocol/disco#info");
        sdManager.addFeature("jabber:iq:privacy");
        FileTransferNegotiator.setServiceEnabled(MsgService.xmppConnection, true);
        fileManager = new FileTransferManager(MsgService.xmppConnection);
        fileManager.addFileTransferListener(listener);
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

    /**
     * TODO 功能：
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/2
     */
    public interface GroupListener {
        void Success(List<HostedRoom> list);

        void Error(String error);
    }

}
