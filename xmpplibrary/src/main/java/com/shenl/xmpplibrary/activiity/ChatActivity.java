package com.shenl.xmpplibrary.activiity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.bean.MsgBean;
import com.shenl.xmpplibrary.service.MsgService;
import com.shenl.xmpplibrary.utils.ImageUtils;
import com.shenl.xmpplibrary.utils.XmppUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Activity {

    private ListView listview;
    private EditText et_body;
    private TextView title;
    private String user;
    private List<MsgBean> list;
    private MyAdapter adapter;
    private String name;
    private boolean isGroup;


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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    XmppUtils.XmppGroupMessage(new PacketListener() {
                        @Override
                        public void processPacket(Packet packet) {
                            Message message = (Message) packet;
                            // 接收来自聊天室的聊天信息
                            String groupName = message.getFrom();
                            //判断是否是本人发出的消息 不是则显示
                            if (!groupName.contains(MsgService.nickname)) {
                                Log.e("shenl", "from=" + message.getFrom() + "...to=" + message.getTo());
                                Log.e("shenl", message.getBody());
                                MsgBean msgBean = new MsgBean();
                                msgBean.body = message.getBody();
                                msgBean.type = "1";
                                list.add(msgBean);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                        listview.smoothScrollToPosition(list.size() - 1);
                                    }
                                });
                            }
                        }
                    });
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    XmppUtils.XmppGetMessage(new MessageListener() {
                        @Override
                        public void processMessage(Chat chat, Message message) {
                            final String from = message.getFrom().substring(0, message.getFrom().indexOf("@"));
                            final String to = message.getTo();
                            String body = message.getBody();
                            String account = "";
                            if (user.indexOf("@") != -1) {
                                account = user.substring(0, user.indexOf("@"));
                            } else {
                                account = user;
                            }
                            if (!TextUtils.isEmpty(body) && from.equals(account)) {
                                MsgBean msgBean = new MsgBean();
                                msgBean.body = body;
                                msgBean.type = "1";
                                list.add(msgBean);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                        Log.e("shenl", "from11=" + from + "...to11=" + to);
                                        listview.smoothScrollToPosition(list.size() - 1);
                                    }
                                });
                            }
                        }
                    });
                }
            }).start();
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
        Uri uri =  data.getData();
        Log.d("shenl", "Uri = " + uri);
        String path = ImageUtils.getRealPathFromUri(this, uri);
        Log.d("shenl", "realPath = " + path);
        XmppUtils.XmppSendFile(user,new File(path));
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
        final Message msg = new Message();
        msg.setTo(user);
        msg.setBody(body);// 输入框里面的内容

        if (isGroup) {
            XmppUtils.XmppSendGroupMessage(body, new XmppUtils.XmppListener() {
                @Override
                public void Success() {
                    MsgBean msgBean = new MsgBean();
                    msgBean.body = body;
                    msgBean.type = "0";
                    list.add(msgBean);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            et_body.setText("");
                            listview.smoothScrollToPosition(list.size() - 1);
                        }
                    });
                }

                @Override
                public void Error(String error) {

                }
            });
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    msg.setType(Message.Type.chat);// 类型就是chat
//		msg.setProperty("key", "value");// 额外属性-->额外的信息,这里我们用不到
                    XmppUtils.XmppSendMessage(user, msg, new XmppUtils.XmppListener() {
                        @Override
                        public void Success() {
                            MsgBean msgBean = new MsgBean();
                            msgBean.body = body;
                            msgBean.type = "0";
                            list.add(msgBean);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    et_body.setText("");
                                    listview.smoothScrollToPosition(list.size() - 1);
                                }
                            });
                        }

                        @Override
                        public void Error(String error) {

                        }
                    });
                }
            }).start();
        }
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
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(ChatActivity.this, R.layout.item_chat, null);
            }
            RelativeLayout rec = convertView.findViewById(R.id.rec);
            RelativeLayout send = convertView.findViewById(R.id.send);
            TextView rec_body = convertView.findViewById(R.id.rec_body);
            TextView send_body = convertView.findViewById(R.id.send_body);
            if ("0".equals(list.get(position).type)) {
                rec.setVisibility(View.GONE);
                send_body.setText(list.get(position).body);
            } else {
                send.setVisibility(View.GONE);
                rec_body.setText(list.get(position).body);
            }
            return convertView;
        }
    }
}
