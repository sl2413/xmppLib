package com.shenl.xmpplibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;

import com.shenl.xmpplibrary.service.MsgService;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

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
    public static MultiUserChat muc;
    private static String user;
    private static VCard vCard;


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
                    // 配置各种Provider，如果不配置，则会无法解析数据
                    configureConnection(ProviderManager.getInstance());
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
            XmppUtils.user = name + "@" + sName;
            mhandler.post(new Runnable() {
                @Override
                public void run() {
                    boolean b = ServiceUtils.isServiceWork(context, "com.shenl.xmpplibrary.service.MsgService");
                    if (!b) {
                        Intent intent = new Intent(context, MsgService.class);
                        context.startService(intent);
                    }
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
     * TODO 功能：获取当前登陆人昵称
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/9
     */
    public static String XmppGetNickName() {
        try {
            if (vCard == null) {
                vCard = new VCard();
            }
            vCard.load(MsgService.xmppConnection);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        String nickName = vCard.getNickName();
        if (TextUtils.isEmpty(nickName)){
            String s = XmppGetJid();
            nickName = s.substring(0,s.indexOf("@"));
        }
        return nickName;
    }

    /**
     * TODO : 获取当前登陆人Jid
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2019/1/9
     *
     * @return :
     */
    public static String XmppGetJid() {
        try {
            if (vCard == null) {
                vCard = new VCard();
            }
            vCard.load(MsgService.xmppConnection);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        String FullJid = vCard.getFrom();
        if (FullJid.indexOf("/") != -1) {
            FullJid = FullJid.substring(0, FullJid.indexOf("/"));
        }
        return FullJid;
    }

    /**
     * TODO 功能：注册账号
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/10
     */
    public static void XmppRegister(String account, String nickName,String password, XmppListener listener) {
        Registration reg = new Registration();
        reg.setType(IQ.Type.SET);
        reg.setTo(MsgService.xmppConnection.getServiceName());
        // 注意这里createAccount注册时，参数是UserName，不是jid，是"@"前面的部分。
        reg.setUsername(account);
        reg.setPassword(password);
        //设置昵称（其余属性）
        if (TextUtils.isEmpty(nickName)){
            nickName = account;
        }
        reg.addAttribute("name", nickName);
        // 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
        reg.addAttribute("android", "geolo_createUser_android");
        PacketFilter filter = new AndFilter(new PacketIDFilter(
                reg.getPacketID()), new PacketTypeFilter(IQ.class));
        PacketCollector collector = MsgService.xmppConnection.createPacketCollector(filter);
        MsgService.xmppConnection.sendPacket(reg);
        IQ result = (IQ) collector.nextResult(SmackConfiguration
                .getPacketReplyTimeout());
        // Stop queuing results停止请求results（是否成功的结果）
        collector.cancel();
        if (result == null) {
            Log.e("regist", "No response from server.");
            listener.Error("服务器没有返回结果");
        } else if (result.getType() == IQ.Type.RESULT) {
            Log.v("regist", "regist success.");
            listener.Success();
        } else { // if (result.getType() == IQ.Type.ERROR)
            if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
                Log.e("regist", "IQ.Type.ERROR: " + result.getError().toString());
                listener.Error("账号已经存在");
            } else {
                Log.e("regist", "IQ.Type.ERROR: " + result.getError().toString());
                listener.Error("注册失败");
            }
        }
    }

    /**
     * TODO 功能：添加好友
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/10
     */
    public static void XmppAddFriend(String Jid, String Nickname,XmppListener listener) {
        try {
            if (!Jid.contains("@")){
                Jid = Jid + "@"+sName;
            }
            if (TextUtils.isEmpty(Nickname)){
                Nickname = Jid.substring(0,Jid.indexOf("@"));
            }
            MsgService.xmppConnection.getRoster().createEntry(Jid, Nickname, null);
            //设置添加好友请求
            Presence subscription = new Presence(Presence.Type.subscribe);
            //拼接好友全称
            subscription.setTo(Jid);
            //发送请求
            MsgService.xmppConnection.sendPacket(subscription);
            listener.Success();
        } catch (XMPPException e) {
            listener.Error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * TODO 功能：删除好友
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/10
     */
    public static void XmppDelUser(String Jid,XmppListener listener) {
        try {
            RosterEntry entry = null;
            if (Jid.contains("@"))
                entry = MsgService.xmppConnection.getRoster().getEntry(Jid);
            else
                entry = MsgService.xmppConnection.getRoster().getEntry(Jid);
            if (entry == null)
                entry = MsgService.xmppConnection.getRoster().getEntry(Jid);
            MsgService.xmppConnection.getRoster().removeEntry(entry);
            listener.Success();
        } catch (Exception e) {
            listener.Error(e.getMessage());
            e.printStackTrace();
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
     * TODO 功能：创建群聊
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/9
     */
    public static void XmppCreateRoom(final String roomName, final String password, final XmppListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建一个MultiUserChat
                    MultiUserChat muc = new MultiUserChat(MsgService.xmppConnection, roomName + "@conference."
                            + MsgService.xmppConnection.getServiceName());
                    // 创建聊天室
                    muc.create(roomName);
                    // 获得聊天室的配置表单
                    Form form = muc.getConfigurationForm();
                    // 根据原始表单创建一个要提交的新表单。
                    Form submitForm = form.createAnswerForm();
                    // 向要提交的表单添加默认答复
                    for (Iterator<FormField> fields = form.getFields(); fields
                            .hasNext(); ) {
                        FormField field = (FormField) fields.next();
                        if (!FormField.TYPE_HIDDEN.equals(field.getType())
                                && field.getVariable() != null) {
                            // 设置默认值作为答复
                            submitForm.setDefaultAnswer(field.getVariable());
                        }
                    }
                    // 设置聊天室的新拥有者
                    List<String> owners = new ArrayList<String>();
                    owners.add(MsgService.xmppConnection.getUser());// 用户JID
                    submitForm.setAnswer("muc#roomconfig_roomowners", owners);
                    // 设置聊天室是持久聊天室，即将要被保存下来
                    submitForm.setAnswer("muc#roomconfig_persistentroom", true);
                    // 房间仅对成员开放
                    submitForm.setAnswer("muc#roomconfig_membersonly", false);
                    // 允许占有者邀请其他人
                    submitForm.setAnswer("muc#roomconfig_allowinvites", true);
                    if (!password.equals("")) {
                        // 进入是否需要密码
                        submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",
                                true);
                        // 设置进入密码
                        submitForm.setAnswer("muc#roomconfig_roomsecret", password);
                    }
                    // 能够发现占有者真实 JID 的角色
                    // submitForm.setAnswer("muc#roomconfig_whois", "anyone");
                    // 登录房间对话
                    submitForm.setAnswer("muc#roomconfig_enablelogging", true);
                    // 仅允许注册的昵称登录
                    submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
                    // 允许使用者修改昵称
                    submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
                    // 允许用户注册房间
                    submitForm.setAnswer("x-muc#roomconfig_registration", false);
                    // 发送已完成的表单（有默认值）到服务器来配置聊天室
                    muc.sendConfigurationForm(submitForm);
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.Success();
                        }
                    });
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
     * TODO 功能：获取服务器群聊列表
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
                    new ServiceDiscoveryManager(MsgService.xmppConnection);
                    Collection<HostedRoom> hostrooms = MultiUserChat.getHostedRooms(MsgService.xmppConnection, "conference." + sName);
                    for (HostedRoom entry : hostrooms) {
                        list.add(entry);
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
    }

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
     * TODO 功能：获取群聊成员
     * <p>
     * 参数说明:必须在加入的房间内部调用，否则muc为空
     * 作    者:   沈 亮
     * 创建时间:   2019/1/9
     */
    public static void XmppGetRoomPerson(MultiUserChat muc, RoomPersonListener listener) {
        if (muc == null) {
            Log.e("shenl", "注:请在群聊内部查看成员");
        }
        List<String> listUser = new ArrayList<String>();
        Iterator<String> it = muc.getOccupants();
        // 遍历出聊天室人员名称
        while (it.hasNext()) {
            // 聊天室成员名字
            String name = StringUtils.parseResource(it.next());
            listUser.add(name);
        }
        if (listUser.size() > 0) {
            listener.Success(listUser);
        } else {
            listener.equals("未查到群成员列表");
        }
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
     * TODO 功能：获取群列表回调接口
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/2
     */
    public interface GroupListener {
        void Success(List<HostedRoom> list);

        void Error(String error);
    }

    /**
     * TODO 功能：获取群成员列表
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/9
     */
    public interface RoomPersonListener {
        void Success(List<String> list);

        void Error(String error);
    }

    /**
     * 加入providers的函数 ASmack在/META-INF缺少一个smack.providers 文件
     *
     * @param pm
     */
    public static void configureConnection(ProviderManager pm) {

        // Private Data Storage
        pm.addIQProvider("query", "jabber:iq:private",
                new PrivateDataManager.PrivateDataIQProvider());

        // Time
        try {
            pm.addIQProvider("query", "jabber:iq:time",
                    Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            Log.w("TestClient",
                    "Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        // Roster Exchange
        pm.addExtensionProvider("x", "jabber:x:roster",
                new RosterExchangeProvider());

        // Message Events
        pm.addExtensionProvider("x", "jabber:x:event",
                new MessageEventProvider());

        // Chat State
        pm.addExtensionProvider("active",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());

        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
                new XHTMLExtensionProvider());

        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference",
                new GroupChatInvitation.Provider());

        // Service Discovery # Items
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
                new DiscoverItemsProvider());

        // Service Discovery # Info
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
                new DiscoverInfoProvider());

        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
                new MUCUserProvider());

        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
                new MUCAdminProvider());

        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
                new MUCOwnerProvider());

        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay",
                new DelayInformationProvider());

        // Version
        try {
            pm.addIQProvider("query", "jabber:iq:version",
                    Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            // Not sure what's happening here.
        }

        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
                new OfflineMessageRequest.Provider());

        // Offline Message Indicator
        pm.addExtensionProvider("offline",
                "http://jabber.org/protocol/offline",
                new OfflineMessageInfo.Provider());

        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup",
                "http://www.jivesoftware.org/protocol/sharedgroup",
                new SharedGroupsInfo.Provider());

        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses",
                "http://jabber.org/protocol/address",
                new MultipleAddressesProvider());

        // FileTransfer
        pm.addIQProvider("si", "http://jabber.org/protocol/si",
                new StreamInitiationProvider());

        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());

        // Privacy
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.SessionExpiredError());
    }

}
