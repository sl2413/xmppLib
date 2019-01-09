package com.shenl.xmpplibrary.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.activiity.ChatActivity;
import com.shenl.xmpplibrary.bean.sessionBean;
import com.shenl.xmpplibrary.service.MsgService;

public class SessionFragment extends Fragment {

    private ListView lv_session;
    private MyAdapter adapter;

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
        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    private void initView(View view) {
        lv_session = view.findViewById(R.id.lv_session);
    }

    private void initData() {
        adapter = new MyAdapter();
        lv_session.setAdapter(adapter);
    }

    private void initEvent() {
        //会话列表条目点击事件
        lv_session.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sessionBean sessionBean = MsgService.sessionList.get(i);
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("isGroup",sessionBean.isGroup);
                intent.putExtra("user",sessionBean.user);
                intent.putExtra("name",sessionBean.name);
                startActivity(intent);
            }
        });
        //会话列表条目长按事件
        lv_session.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                TextView textView = new TextView(getContext());
                textView.setText("删除条目");
                textView.setTextSize(30);
                textView.setPadding(8,8,8,8);
                final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setView(textView);
                final AlertDialog dialog = builder.show();
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        MsgService.sessionList.remove(i);
                        adapter.notifyDataSetChanged();
                    }
                });
                return true;
            }
        });
    }

    /**
     * TODO 功能：会话列表适配器
     *
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/9
     */
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return MsgService.sessionList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null){
                view = View.inflate(getContext(), R.layout.item_session,null);
            }
            TextView nickname = view.findViewById(R.id.nickname);
            TextView account = view.findViewById(R.id.account);
            nickname.setText(MsgService.sessionList.get(i).user);
            account.setText(MsgService.sessionList.get(i).name);
            return view;
        }
    }
}
