package com.shenl.xmpplibrary.activiity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.bean.Msg;
import com.shenl.xmpplibrary.bean.MsgBean;
import com.shenl.xmpplibrary.service.MsgService;
import com.shenl.xmpplibrary.utils.DateTimeUtils;
import com.shenl.xmpplibrary.utils.ImageUtils;
import com.shenl.xmpplibrary.utils.XmppUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends Activity {

    private ListView listview;
    private EditText et_body;
    private TextView title;
    private String user;
    private List<Msg> list;
    private MyAdapter adapter;
    private String name;
    private boolean isGroup;
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
            }

        }
    };


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
    }

    private void initData() {
        Intent intent = getIntent();
        isGroup = intent.getBooleanExtra("isGroup", false);
        user = intent.getStringExtra("user");
        if (user.indexOf("@") == -1) {
            user = user + "@" + XmppUtils.sName;
        }
        name = intent.getStringExtra("name");
        if (isGroup) {
            title.setText("在" + name + " 聊天室");
        } else {
            title.setText("与 " + name + " 聊天中");
        }

        list = new ArrayList<>();
        adapter = new MyAdapter();
        listview.setAdapter(adapter);
    }


    private void initEvent() {
        if (isGroup) {

        } else {
            //消息监听器
            XmppUtils.XmppGetMessage(new MessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    Log.e("shenl",message.getBody());
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
     * TODO 功能：发送文件按钮点击事件
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/2
     */
    public void SendFile(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();
        Log.d("shenl", "Uri = " + uri);
        String path = ImageUtils.getRealPathFromUri(this, uri);
        Log.d("shenl", "realPath = " + path);
        XmppUtils.XmppSendFile(user, new File(path), new XmppUtils.XmppListener() {
            @Override
            public void Success() {
                Log.e("shenl", "发送成功");
            }

            @Override
            public void Error(String error) {
                Log.e("shenl", "发送失败..." + error);
            }
        });
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

        } else {
            final Message msg = new Message();
            msg.setBody(body);// 输入框里面的内容
//            msg.setType(Message.Type.chat);// 类型就是chat
            //msg.setProperty("key", "value");// 额外属性-->额外的信息,这里我们用不到
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
        // 发送消息
        list.add(new Msg(dateStr, MsgService.nickname, body, "OUT"));
        // 刷新适配器
        adapter.notifyDataSetChanged();
        listview.setSelection(ListView.FOCUS_DOWN);// 刷新到底部
        et_body.setText("");
    }


    class MyHodler {
        RelativeLayout rec, send;
        ImageView rec_head, send_head;
        TextView rec_body, send_body;
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

        private MyHodler hodler;

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
            Msg msg = list.get(position);
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
//                    Glide.with(ChatActivity.this).load(ALBUM_PATH + msg.getImg_path()).into(holder.iv_left);
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
//                    Glide.with(ChatActivity.this).load(msg.getImg_path()).into(holder.iv_right);
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
