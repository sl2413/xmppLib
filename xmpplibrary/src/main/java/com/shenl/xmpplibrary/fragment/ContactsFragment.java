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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.activiity.ChatActivity;
import com.shenl.xmpplibrary.service.MsgService;
import com.shenl.xmpplibrary.utils.XmppUtils;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.List;

public class ContactsFragment extends Fragment {

    private ListView lv_contact;
    private List<RosterEntry> list;
    private MyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, null);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {
        lv_contact = view.findViewById(R.id.lv_contact);

    }

    private void initData() {
        list = XmppUtils.XmppContacts();
        adapter = new MyAdapter();
        lv_contact.setAdapter(adapter);
    }

    private void initEvent() {
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("user",list.get(position).getUser());
                intent.putExtra("name",list.get(position).getName());
                getContext().startActivity(intent);
            }
        });
        //联系人变化的监听
        Roster roster = MsgService.xmppConnection.getRoster();
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<String> collection) {
                if (!list.isEmpty()){
                    list.clear();
                }
                list = XmppUtils.XmppContacts();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter != null){
                            adapter.notifyDataSetChanged();
                        }else{
                            adapter = new MyAdapter();
                        }
                    }
                });
            }

            @Override
            public void entriesUpdated(Collection<String> collection) {
                if (!list.isEmpty()){
                    list.clear();
                }
                list = XmppUtils.XmppContacts();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter != null){
                            adapter.notifyDataSetChanged();
                        }else{
                            adapter = new MyAdapter();
                        }
                    }
                });
            }

            @Override
            public void entriesDeleted(Collection<String> collection) {
                if (!list.isEmpty()){
                    list.clear();
                }
                list = XmppUtils.XmppContacts();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter != null){
                            adapter.notifyDataSetChanged();
                        }else{
                            adapter = new MyAdapter();
                        }
                    }
                });
            }

            @Override
            public void presenceChanged(Presence presence) {
            }
        });
    }

    class MyAdapter extends BaseAdapter{

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
            if (convertView == null){
                convertView = View.inflate(getContext(), R.layout.item_contact,null);
            }
            ImageView head = convertView.findViewById(R.id.head);
            TextView nickname = convertView.findViewById(R.id.nickname);
            TextView account = convertView.findViewById(R.id.account);
            nickname.setText(list.get(position).getName());
            account.setText(list.get(position).getUser());
            return convertView;
        }
    }
}
