package com.shenl.xmpplibrary.activiity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.bean.Msg;
import com.shenl.xmpplibrary.dao.ChatDao;
import com.shenl.xmpplibrary.fragment.EmojiFragment;
import com.shenl.xmpplibrary.service.MsgService;
import com.shenl.xmpplibrary.utils.DateTimeUtils;
import com.shenl.xmpplibrary.utils.ImageUtils;
import com.shenl.xmpplibrary.utils.SystemInfo;
import com.shenl.xmpplibrary.utils.XmppUtils;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * TODO 功能：聊天通讯页面activity
 * <p>
 * 参数说明:
 * 作    者:   沈 亮
 * 创建时间:   2019/1/10
 */
public class ChatActivity extends FragmentActivity {

    private ListView listview;
    private EditText et_body;
    private TextView title;
    private String user;
    private MyAdapter adapter;
    private String name;
    private String isGroup;
    private boolean isEmogiShow;
    private ImageView iv_emogi;
    private FrameLayout fl_emogi;
    private LinearLayout ll_emogi;
    private LinearLayout ll_root;
    private ImageView iv_roomPerson;
    private ChatDao dao;
    private Cursor cursor;
    private ChatDao.sessionBean sessionBean;
    private MsgReceiver msgReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgReceiver);
        if (sessionBean != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            //移除标记为id的通知 (只是针对当前Context下的所有Notification)
            notificationManager.cancel(Integer.parseInt(sessionBean.id));
        }
        ChatDao dao = new ChatDao(ChatActivity.this);
        ContentValues values = new ContentValues();
        values.put("UnReadCount", "");
        int upd = dao.upd(ChatDao.SESSIONLIST, values, user);
        if (upd != 0) {
            Intent intent = new Intent("com.shenl.xmpplibrary.fragment.SessionFragment.MsgReceiver");
            sendBroadcast(intent);
        }
    }

    private void initView() {
        title = findViewById(R.id.title);
        listview = findViewById(R.id.listView);
        et_body = findViewById(R.id.et_body);
        iv_emogi = findViewById(R.id.iv_emogi);
        fl_emogi = findViewById(R.id.fl_emogi);
        ll_emogi = findViewById(R.id.ll_emogi);
        ll_root = findViewById(R.id.ll_root);
        iv_roomPerson = findViewById(R.id.iv_roomPerson);
    }

    private void initData() {
        //动态注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.shenl.xmpplibrary.fragment.SessionFragment.MsgReceiver");
        registerReceiver(msgReceiver, intentFilter);

        dao = new ChatDao(ChatActivity.this);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_emogi, new EmojiFragment());
        ft.commit();
        Intent intent = getIntent();
        isGroup = intent.getStringExtra("isGroup");
        user = intent.getStringExtra("user");
        if (user.indexOf("@") == -1) {
            user = user + "@" + XmppUtils.sName;
        }
        name = intent.getStringExtra("name");
        if ("1".equals(isGroup)) {
            title.setText("在" + name + " 聊天室");
            iv_roomPerson.setVisibility(View.VISIBLE);
        } else {
            title.setText("与 " + name + " 聊天中");
        }
        cursor = dao.query(user, XmppUtils.XmppGetJid());
        adapter = new MyAdapter(ChatActivity.this, cursor);
        listview.setAdapter(adapter);
        listview.setSelection(ListView.FOCUS_DOWN);// 刷新到底部
        sessionBean = dao.querySession(user);
        if (sessionBean != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            //移除标记为id的通知 (只是针对当前Context下的所有Notification)
            notificationManager.cancel(Integer.parseInt(sessionBean.id));
        }
    }


    private void initEvent() {
        //表情按钮点击事件
        iv_emogi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmogiShow) {
                    fl_emogi.setVisibility(View.GONE);
                    iv_emogi.setImageResource(R.drawable.fabu_biaoqing_icon);
                    showKeyboard(ChatActivity.this, et_body);
                } else {
                    fl_emogi.setVisibility(View.VISIBLE);
                    iv_emogi.setImageResource(R.drawable.fabu_keyboard_icon);
                    hideKeyboard(ChatActivity.this);
                }
                isEmogiShow = !isEmogiShow;
            }
        });
        //内容输入框点击事件
        et_body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_emogi.setImageResource(R.drawable.fabu_biaoqing_icon);
                showKeyboard(ChatActivity.this, et_body);
                fl_emogi.setVisibility(View.GONE);
            }
        });

        if ("1".equals(isGroup)) {
            //群成员列表查看按钮
            iv_roomPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChatActivity.this, RoomPersonActivity.class);
                    intent.putExtra("title", name);
                    startActivity(intent);
                }
            });
            //监听群消息
            XmppUtils.XmppGroupMessage(new PacketListener() {
                @Override
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    // 接收来自聊天室的聊天信息
                    String groupName = message.getFrom();
                    String[] nameOrGroup = groupName.split("/");
                    //判断是否是本人发出的消息 不是则显示
                    if (!nameOrGroup[1].equals(MsgService.nickname)) {
                        String[] args = new String[]{nameOrGroup[1], message.getBody()};
                    }
                }
            });
        }
    }

    /**
     * TODO : 为输入框添加表情
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2019/1/7
     *
     * @return :
     */
    public void setBody(String emoji) {
        et_body.append(emoji);
    }

    /**
     * TODO 功能：发送文件按钮点击事件
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/2
     */
    public void SendFile(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        Uri uri = data.getData();
        final String path = ImageUtils.getRealPathFromUri(this, uri);
        Log.d("shenl", "realPath = " + path);
        new Thread(new Runnable() {
            @Override
            public void run() {
                XmppUtils.XmppSendFile(user + "/Spark", new File(path), new XmppUtils.XmppListener() {
                    @Override
                    public void Success() {
                        String[] args = new String[]{MsgService.nickname, path};
                        Log.e("shenl", "发送成功");
                    }

                    @Override
                    public void Error(String error) {
                        Log.e("shenl", "发送失败..." + error);
                    }
                });

            }
        }).start();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * TODO 功能：发送按钮点击事件
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/2
     */
    public void send(View v) {
        final String body = et_body.getText().toString().trim();
        final String dateStr = DateTimeUtils.formatDate(new Date());
        if ("1".equals(isGroup)) {
            XmppUtils.XmppSendGroupMessage(body, new XmppUtils.XmppListener() {
                @Override
                public void Success() {

                }

                @Override
                public void Error(String error) {

                }
            });
        } else {
            final Message msg = new Message();
            msg.setBody(body);// 输入框里面的内容
            new Thread(new Runnable() {
                @Override
                public void run() {
                    XmppUtils.XmppSendMessage(user, msg, new XmppUtils.XmppListener() {
                        @Override
                        public void Success() {

                        }

                        @Override
                        public void Error(String error) {

                        }
                    });
                }
            }).start();
        }

        ContentValues sessionValue = new ContentValues();
        sessionValue.put("Jid", user);
        sessionValue.put("nickName", name);
        sessionValue.put("head", "");
        sessionValue.put("content", body);
        sessionValue.put("contentType", SystemInfo.TEXT);
        sessionValue.put("isGroup", isGroup);
        sessionValue.put("UnReadCount", 0);
        long add = dao.Add(ChatDao.SESSIONLIST, sessionValue);

        //缓存聊天记录
        ContentValues values = new ContentValues();
        values.put("FromJid", user);
        values.put("ToJid", XmppUtils.XmppGetJid());
        values.put("name", XmppUtils.XmppGetNickName());
        values.put("data", dateStr);
        values.put("title", body);
        values.put("myself", "OUT");
        values.put("imgPath", "");
        dao.Add(ChatDao.MESSAGE, values);
        Refresh();
        et_body.setText("");
    }

    /**
     * TODO 功能：刷新页面
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/15
     */
    private void Refresh() {
        cursor.requery();
        adapter.notifyDataSetChanged();
        listview.setSelection(ListView.FOCUS_DOWN);// 刷新到底部
    }

    /**
     * TODO 功能：接收消息广播
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/15
     */
    public class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (sessionBean == null) {
                sessionBean = dao.querySession(user);
            }
            Refresh();
        }
    }


    /**
     * TODO 功能：隐藏键盘
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/4
     */
    private void hideKeyboard(Activity context) {
        if (context == null) return;
        final View v = context.getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * TODO 功能：显示键盘
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/4
     */
    private void showKeyboard(Activity context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    /**
     * TODO : 聊天适配器
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2019/1/1
     *
     * @return :
     */
    class MyAdapter extends CursorAdapter {

        public MyAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            ViewHolder holder = new ViewHolder();
            View view = View.inflate(ChatActivity.this, R.layout.item_chat_list, null);
            holder.llLeft = view.findViewById(R.id.ll_chat_left);
            holder.llRight = view.findViewById(R.id.ll_chat_right);
            holder.rec_name = view.findViewById(R.id.rec_name);
            holder.tvDate = view.findViewById(R.id.tv_chat_date);
            holder.tvTitle = view.findViewById(R.id.tv_chat_title);
            holder.tvTitle2 = view.findViewById(R.id.tv_chat_title2);
            holder.iv_left = view.findViewById(R.id.iv_left);
            holder.iv_right = view.findViewById(R.id.iv_right);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
//            final Msg msg = list.get(position);
            String name = cursor.getString(3);
            String data = cursor.getString(4);
            String title = cursor.getString(5);
            String myself = cursor.getString(6);
            final String imgPath = cursor.getString(7);
            holder.tvDate.setText(data);
            if (myself.equals("IN")) {
                holder.llLeft.setVisibility(View.VISIBLE);
                holder.llRight.setVisibility(View.GONE);
                holder.rec_name.setText(name);
                if (!TextUtils.isEmpty(title)) {
                    holder.tvTitle.setText(title);
                    holder.tvTitle.setVisibility(View.VISIBLE);
                    holder.iv_left.setVisibility(View.GONE);
                } else {
                    holder.tvTitle.setVisibility(View.GONE);
                    holder.iv_left.setVisibility(View.VISIBLE);
                    Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
                    holder.iv_left.setImageBitmap(bitmap);
                    //查看收到图片
                    holder.iv_left.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ChatActivity.this, SeeImgActivity.class);
                            intent.putExtra("imgPath", imgPath);
                            startActivity(intent);
                        }
                    });
                }
            } else if (myself.equals("OUT")) {
                holder.llLeft.setVisibility(View.GONE);
                holder.llRight.setVisibility(View.VISIBLE);
                holder.tvTitle2.setText(title);
                if (!TextUtils.isEmpty(title)) {
                    holder.tvTitle2.setText(title);
                    holder.tvTitle2.setVisibility(View.VISIBLE);
                    holder.iv_right.setVisibility(View.GONE);
                } else {
                    holder.tvTitle2.setVisibility(View.GONE);
                    holder.iv_right.setVisibility(View.VISIBLE);
                    Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
                    holder.iv_right.setImageBitmap(bitmap);
                    //查看发出图片
                    holder.iv_right.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ChatActivity.this, SeeImgActivity.class);
                            intent.putExtra("imgPath", imgPath);
                            startActivity(intent);
                        }
                    });
                }
            }
        }

        class ViewHolder {
            RelativeLayout llLeft;
            LinearLayout llRight;
            TextView rec_name;
            TextView tvDate;
            TextView tvTitle;
            TextView tvTitle2;
            ImageView iv_left;
            ImageView iv_right;
        }
    }
}
