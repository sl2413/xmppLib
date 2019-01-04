package com.shenl.xmpplibrary.activiity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.bean.Msg;
import com.shenl.xmpplibrary.emoji.ExpressionGridFragment;
import com.shenl.xmpplibrary.emoji.ExpressionShowFragment;
import com.shenl.xmpplibrary.emoji.widget.ExpressionEditText;
import com.shenl.xmpplibrary.emoji.widget.ExpressionTextView;
import com.shenl.xmpplibrary.service.MsgService;
import com.shenl.xmpplibrary.utils.DateTimeUtils;
import com.shenl.xmpplibrary.utils.ImageUtils;
import com.shenl.xmpplibrary.utils.XmppUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends FragmentActivity implements ExpressionGridFragment.ExpressionClickListener,ExpressionGridFragment.ExpressionDeleteClickListener {

    private ListView listview;
    private ExpressionEditText et_body;
    private TextView title;
    private String user;
    private List<Msg> list;
    private MyAdapter adapter;
    private String name;
    private boolean isGroup;
    private boolean isEmogiShow;
    private boolean keyboardShown;
    private int supportSoftInputHeight;
    private ExpressionShowFragment expressionShowFragment;
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
    private ImageView iv_emogi;
    private FrameLayout fl_emogi;
    private LinearLayout ll_emogi;
    private LinearLayout ll_root;


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
        setListenerToRootView();
        et_body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEmogiShow = false;
                iv_emogi.setImageResource(R.drawable.fabu_biaoqing_icon);
                showKeyboard(ChatActivity.this, et_body);
                fl_emogi.setVisibility(View.GONE);
            }
        });
        //表情按钮点击事件
        iv_emogi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmogiShow) {
                    isEmogiShow = false;
                    showKeyboard(ChatActivity.this, et_body);
                    iv_emogi.setImageResource(R.drawable.fabu_biaoqing_icon);
                    fl_emogi.setVisibility(View.GONE);
                    return;
                } else {
                    iv_emogi.setImageResource(R.drawable.fabu_keyboard_icon);
                    replaceEmogi();
                    hideKeyboard(ChatActivity.this);
                }
            }
        });
        if (isGroup) {
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
     * TODO 功能：表情显示
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/4
     */
    private void replaceEmogi() {
        isEmogiShow = true;
        fl_emogi.setVisibility(View.VISIBLE);
        if (expressionShowFragment == null) {
            expressionShowFragment = ExpressionShowFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_emogi, ExpressionShowFragment.newInstance()).commit();
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
     * TODO 功能：判断键盘弹出状态
     *
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/4
     */
    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    /**
     * TODO 功能：动态监听键盘状态
     *
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/4
     */
    private void setListenerToRootView() {
        ll_root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                keyboardShown = isKeyboardShown(ll_root);
                if (fl_emogi != null && ll_emogi != null) {
                    if (keyboardShown) {
                        if (ll_emogi.getVisibility() != View.VISIBLE || supportSoftInputHeight != getSupportSoftInputHeight()) {
                            iv_emogi.setImageResource(R.drawable.fabu_biaoqing_icon);
                            isEmogiShow = false;
                            ll_root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            supportSoftInputHeight = getSupportSoftInputHeight();
                            fl_emogi.getLayoutParams().height = supportSoftInputHeight;
                            fl_emogi.requestLayout();
                            fl_emogi.setVisibility(View.GONE);
                            ll_emogi.setVisibility(View.VISIBLE);
                            ll_root.getViewTreeObserver().addOnGlobalLayoutListener(this);
                        }
                    } else {
                        if (!isEmogiShow) {
                            iv_emogi.setImageResource(R.drawable.fabu_biaoqing_icon);
                        }
                    }
                }
            }
        });
    }

    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = this.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }
        return softInputHeight;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    @Override
    public void expressionClick(String str) {
        ExpressionShowFragment.input(et_body, str);
    }

    @Override
    public void expressionDeleteClick(View v) {
        ExpressionShowFragment.delete(et_body);
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
            ExpressionTextView tvTitle;
            ExpressionTextView tvTitle2;
            ImageView iv_left;
            ImageView iv_right;
        }
    }
}
