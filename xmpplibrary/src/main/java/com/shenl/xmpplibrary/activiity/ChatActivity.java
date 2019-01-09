package com.shenl.xmpplibrary.activiity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.bean.Msg;
import com.shenl.xmpplibrary.bean.sessionBean;
import com.shenl.xmpplibrary.fragment.EmojiFragment;
import com.shenl.xmpplibrary.service.MsgService;
import com.shenl.xmpplibrary.utils.DateTimeUtils;
import com.shenl.xmpplibrary.utils.ImageUtils;
import com.shenl.xmpplibrary.utils.XmppUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends FragmentActivity {

    private ListView listview;
    private EditText et_body;
    private TextView title;
    private String user;
    private List<Msg> list;
    private MyAdapter adapter;
    private String name;
    private boolean isGroup;
    private boolean isEmogiShow;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String[] args = (String[]) msg.obj;
            switch (msg.what) {
                case 1:
                    String dateStr = DateTimeUtils.formatDate(new Date());
                    Msg m = new Msg(dateStr, args[0], args[1], "IN");
                    list.add(m);
                    adapter.notifyDataSetChanged();
                    listview.setSelection(ListView.FOCUS_DOWN);// 刷新到底部
                    break;
                case 2:
                    String dateStr2 = DateTimeUtils.formatDate(new Date());
                    Msg m2 = new Msg(dateStr2, args[0], "", "IN", args[1]);
                    list.add(m2);
                    adapter.notifyDataSetChanged();
                    listview.setSelection(ListView.FOCUS_DOWN);// 刷新到底部
                    break;
                case 3:
                    String dateStr3 = DateTimeUtils.formatDate(new Date());
                    Msg m3 = new Msg(dateStr3, args[0], "", "OUT", args[1]);
                    list.add(m3);
                    adapter.notifyDataSetChanged();
                    listview.setSelection(ListView.FOCUS_DOWN);// 刷新到底部

                    break;
            }
        }
    };
    private ImageView iv_emogi;
    private FrameLayout fl_emogi;
    private LinearLayout ll_emogi;
    private LinearLayout ll_root;
    private ImageView iv_roomPerson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        initData();
        initEvent();
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
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_emogi, new EmojiFragment());
        ft.commit();
        Intent intent = getIntent();
        isGroup = intent.getBooleanExtra("isGroup", false);
        user = intent.getStringExtra("user");
        if (user.indexOf("@") == -1) {
            user = user + "@" + XmppUtils.sName;
        }
        name = intent.getStringExtra("name");
        if (isGroup) {
            title.setText("在" + name + " 聊天室");
            iv_roomPerson.setVisibility(View.VISIBLE);
        } else {
            title.setText("与 " + name + " 聊天中");
        }
        sessionBean sessionBean = new sessionBean();
        sessionBean.isGroup = isGroup;
        sessionBean.user = user;
        sessionBean.name = name;
        boolean isAdd = true;
        for (int i = 0; i < MsgService.sessionList.size(); i++) {
            if (sessionBean.user.equals(MsgService.sessionList.get(i).user)) {
                isAdd = false;
            }
        }
        if (isAdd) {
            MsgService.sessionList.add(sessionBean);
        }
        list = new ArrayList<>();
        adapter = new MyAdapter();
        listview.setAdapter(adapter);
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
        //文件接收监听器
        XmppUtils.XmppGetFile(new FileTransferListener() {
            @Override
            public void fileTransferRequest(final FileTransferRequest request) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //文件接收
                        IncomingFileTransfer transfer = request.accept();
                        //获取文件名字
                        String fileName = transfer.getFileName();
                        //本地创建文件
                        File sdCardDir = new File(getCacheDir().getPath() + "/xmpp");
                        if (!sdCardDir.exists()) {//判断文件夹目录是否存在
                            sdCardDir.mkdir();//如果不存在则创建
                        }
                        String save_path = sdCardDir + "/" + fileName;
                        File file = new File(save_path);
                        //接收文件
                        try {
                            transfer.recieveFile(file);
                            while (!transfer.isDone()) {
                                if (transfer.getStatus().equals(FileTransfer.Status.error)) {
                                    System.out.println("ERROR!!! " + transfer.getError());
                                } else {
                                    System.out.println(transfer.getStatus());
                                    System.out.println(transfer.getProgress());
                                }
                            }
                            //判断是否完全接收文件
                            if (transfer.isDone()) {
                                String[] args = new String[]{name, save_path};
                                android.os.Message msg = handler.obtainMessage();
                                msg.what = 2;
                                msg.obj = args;
                                //发送msg,刷新adapter显示图片
                                msg.sendToTarget();
                            }
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        if (isGroup) {
            iv_roomPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    XmppUtils.XmppGetRoomPerson(XmppUtils.muc, new XmppUtils.RoomPersonListener() {
                        @Override
                        public void Success(List<String> list) {

                        }

                        @Override
                        public void Error(String error) {
                            Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
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
                        // 在handler里取出来显示消息
                        android.os.Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = args;
                        msg.sendToTarget();
                    }
                }
            });
        } else {
            //消息监听器
            XmppUtils.XmppGetMessage(new MessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    Log.e("shenl", message.getBody());
                    // 获取自己好友发来的信息
                    if (message.getBody().length() > 0) {
                        // 获取用户、消息、时间、IN
                        String from = message.getFrom().substring(0, message.getFrom().indexOf("@"));
                        String[] args = new String[]{from, message.getBody()};
                        // 在handler里取出来显示消息
                        android.os.Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = args;
                        msg.sendToTarget();
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
                Log.e("shenl", user);
                XmppUtils.XmppSendFile(user + "/Spark", new File(path), new XmppUtils.XmppListener() {
                    @Override
                    public void Success() {
                        String[] args = new String[]{MsgService.nickname, path};
                        android.os.Message msg = handler.obtainMessage();
                        msg.what = 3;
                        msg.obj = args;
                        msg.sendToTarget();
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
        String dateStr = DateTimeUtils.formatDate(new Date());
        if (isGroup) {
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
                    Log.e("shenl", user);
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
        // 发送消息
        list.add(new Msg(dateStr, MsgService.nickname, body, "OUT"));
        // 刷新适配器
        adapter.notifyDataSetChanged();
        listview.setSelection(ListView.FOCUS_DOWN);// 刷新到底部
        et_body.setText("");
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
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(ChatActivity.this, R.layout.item_chat_list, null);
                holder.llLeft = convertView.findViewById(R.id.ll_chat_left);
                holder.llRight = convertView.findViewById(R.id.ll_chat_right);
                holder.rec_name = convertView.findViewById(R.id.rec_name);
                holder.tvDate = convertView.findViewById(R.id.tv_chat_date);
                holder.tvTitle = convertView.findViewById(R.id.tv_chat_title);
                holder.tvTitle2 = convertView.findViewById(R.id.tv_chat_title2);
                holder.iv_left = convertView.findViewById(R.id.iv_left);
                holder.iv_right = convertView.findViewById(R.id.iv_right);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Msg msg = list.get(position);
            holder.tvDate.setText(msg.getDate());
            String myself = msg.getMyself();
            if (myself.equals("IN")) {
                holder.llLeft.setVisibility(View.VISIBLE);
                holder.llRight.setVisibility(View.GONE);
                holder.rec_name.setText(msg.getName());
                if (msg.getTitle() != null && !msg.getTitle().isEmpty()) {
                    holder.tvTitle.setText(msg.getTitle());
                    holder.tvTitle.setVisibility(View.VISIBLE);
                    holder.iv_left.setVisibility(View.GONE);
                } else {
                    holder.tvTitle.setVisibility(View.GONE);
                    holder.iv_left.setVisibility(View.VISIBLE);
                    Bitmap bitmap = BitmapFactory.decodeFile(msg.getImg_path());
                    holder.iv_left.setImageBitmap(bitmap);
                    //查看收到图片
                    holder.iv_left.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ChatActivity.this, SeeImgActivity.class);
                            intent.putExtra("imgPath", msg.getImg_path());
                            startActivity(intent);
                        }
                    });
                }
            } else if (myself.equals("OUT")) {
                holder.llLeft.setVisibility(View.GONE);
                holder.llRight.setVisibility(View.VISIBLE);
                holder.tvTitle2.setText(msg.getTitle());
                if (msg.getTitle() != null && !msg.getTitle().isEmpty()) {
                    holder.tvTitle2.setText(msg.getTitle());
                    holder.tvTitle2.setVisibility(View.VISIBLE);
                    holder.iv_right.setVisibility(View.GONE);
                } else {
                    holder.tvTitle2.setVisibility(View.GONE);
                    holder.iv_right.setVisibility(View.VISIBLE);
                    Bitmap bitmap = BitmapFactory.decodeFile(msg.getImg_path());
                    holder.iv_right.setImageBitmap(bitmap);
                    //查看发出图片
                    holder.iv_right.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ChatActivity.this, SeeImgActivity.class);
                            intent.putExtra("imgPath", msg.getImg_path());
                            startActivity(intent);
                        }
                    });
                }
            }
            return convertView;
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
