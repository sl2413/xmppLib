package com.shenl.xmpplibrary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.activiity.ChatActivity;
import com.shenl.xmpplibrary.service.MsgService;
import com.shenl.xmpplibrary.utils.XmppUtils;

import org.jivesoftware.smackx.muc.HostedRoom;

import java.util.List;

/**
 * TODO 功能：服务器所有群页面fragment
 *
 * 参数说明:
 * 作    者:   沈 亮
 * 创建时间:   2019/1/10
 */
public class RoomsFragment extends Fragment {

    private ListView lv_room;
    private List<HostedRoom> Glist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, null);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {
        lv_room = view.findViewById(R.id.lv_room);
    }

    private void initData() {
        XmppUtils.XmppServiceRooms(new XmppUtils.GroupListener() {
            @Override
            public void Success(List<HostedRoom> list) {
                Glist = list;
                lv_room.setAdapter(new MyAdapter());
            }

            @Override
            public void Error(String error) {

            }
        });
    }

    private void initEvent() {
        //列表点击事件
        lv_room.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                XmppUtils.XmppJoinRoom(XmppUtils.XmppGetNickName(), "", Glist.get(position).getName(), new XmppUtils.XmppListener() {
                    @Override
                    public void Success() {
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra("user", Glist.get(position).getJid());
                        intent.putExtra("name", Glist.get(position).getName());
                        intent.putExtra("isGroup", "1");
                        getContext().startActivity(intent);
                    }

                    @Override
                    public void Error(String error) {
                        Log.e("shenl", error);
                    }
                });
            }
        });
    }

    /**
     * TODO 功能：群列表适配器
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/3
     */
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Glist.size();
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
                convertView = View.inflate(getContext(), R.layout.item_room, null);
            }
            TextView tv_roomName = convertView.findViewById(R.id.tv_roomName);
            TextView tv_Jid = convertView.findViewById(R.id.tv_Jid);
            tv_roomName.setText(Glist.get(position).getName());
            tv_Jid.setText(Glist.get(position).getJid());
            return convertView;
        }
    }
}
