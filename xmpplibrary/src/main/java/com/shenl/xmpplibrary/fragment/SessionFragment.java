package com.shenl.xmpplibrary.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.activiity.ChatActivity;
import com.shenl.xmpplibrary.dao.ChatDao;
import com.shenl.xmpplibrary.utils.ViewHolder;

/**
 * TODO 功能：会话页面fragment
 * <p>
 * 参数说明:
 * 作    者:   沈 亮
 * 创建时间:   2019/1/10
 */
public class SessionFragment extends Fragment {

    private ListView lv_session;
    private MyAdapter adapter;
    private ChatDao dao;
    private Cursor cursor;
    private MsgReceiver msgReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session, null);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(msgReceiver);
    }

    private void initView(View view) {
        lv_session = view.findViewById(R.id.lv_session);
    }

    private void initData() {
        //动态注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.shenl.xmpplibrary.fragment.SessionFragment.MsgReceiver");
        getContext().registerReceiver(msgReceiver, intentFilter);

        dao = new ChatDao(getContext());
        cursor = dao.query(ChatDao.SESSIONLIST);
        adapter = new MyAdapter(getContext(), cursor);
        lv_session.setAdapter(adapter);
    }

    private void initEvent() {
        //会话列表条目点击事件
        lv_session.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                ContentValues values = new ContentValues();
                values.put("UnReadCount", "");
                String isGroup = cursor.getString(7);
                String Jid = cursor.getString(1);
                String name = cursor.getString(2);
                int upd = dao.upd(ChatDao.SESSIONLIST, values, Jid);
                if (upd != 0) {
                    Refresh();
                }
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("isGroup", isGroup);
                intent.putExtra("user", Jid);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
        //会话列表条目长按事件
        lv_session.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                final Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                View DialogView = View.inflate(getContext(), R.layout.dialog_view, null);
                LinearLayout dialogV = DialogView.findViewById(R.id.ll_dialog_view);
                TextView textView = new TextView(getContext());
                textView.setText("删除会话");
                textView.setTextSize(30);
                textView.setPadding(8, 8, 8, 8);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(params);
                dialogV.addView(textView);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(DialogView);
                final AlertDialog dialog = builder.show();
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        int del = dao.del(ChatDao.SESSIONLIST, cursor.getString(0));
                        if (del != 0) {
                            Refresh();
                        }
                    }
                });
                return true;
            }
        });
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
            Refresh();
        }
    }

    /**
     * TODO 功能：会话列表适配器
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/9
     */
    class MyAdapter extends CursorAdapter {

        public MyAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            View view = View.inflate(getContext(), R.layout.item_list, null);
            viewHolder.head = view.findViewById(R.id.head);
            viewHolder.count = view.findViewById(R.id.count);
            viewHolder.nickname = view.findViewById(R.id.nickname);
            viewHolder.Remarks = view.findViewById(R.id.Remarks);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.nickname.setText(cursor.getString(2));
            viewHolder.Remarks.setText(cursor.getString(4));
            if (TextUtils.isEmpty(cursor.getString(6))) {
                viewHolder.count.setVisibility(View.GONE);
            } else {
                viewHolder.count.setVisibility(View.VISIBLE);
                viewHolder.count.setText(cursor.getString(6));
            }
        }
    }
}
