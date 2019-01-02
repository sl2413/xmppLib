package com.shenl.xmpplibrary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.utils.XmppUtils;
import org.jivesoftware.smackx.muc.HostedRoom;
import java.util.List;

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

    }

    class MyAdapter extends BaseAdapter{

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
            tv_roomName.setText(Glist.get(position).getName());
            return convertView;
        }
    }
}
