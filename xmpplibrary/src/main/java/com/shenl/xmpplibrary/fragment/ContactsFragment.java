package com.shenl.xmpplibrary.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.activiity.ChatActivity;
import com.shenl.xmpplibrary.service.MsgService;
import com.shenl.xmpplibrary.utils.ViewHolder;
import com.shenl.xmpplibrary.utils.XmppUtils;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;

/**
 * TODO 功能：好友列表页面fragment
 *
 * 参数说明:
 * 作    者:   沈 亮
 * 创建时间:   2019/1/10
 */
public class ContactsFragment extends Fragment {

    private ListView lv_contact;
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
        Cursor cursor = XmppUtils.XmppContacts(getContext());
        adapter = new MyAdapter(getContext(),cursor);
        lv_contact.setAdapter(adapter);
    }

    private void initEvent() {
        //好友列表条目点击事件
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("user", cursor.getString(1));
                intent.putExtra("name", cursor.getString(2));
                intent.putExtra("isGroup", "0");
                getContext().startActivity(intent);
            }
        });
        //好友列表条目条长按事件
        lv_contact.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                final Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                View DialogView = View.inflate(getContext(), R.layout.dialog_view, null);
                LinearLayout dialogV = DialogView.findViewById(R.id.ll_dialog_view);
                /*  S 所添加的条目　*/
                TextView textView = new TextView(getContext());
                textView.setText("删除好友");
                textView.setTextSize(30);
                textView.setPadding(8, 8, 8, 8);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(params);
                /*  E 所添加的条目　*/
                dialogV.addView(textView);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(DialogView);
                final AlertDialog dialog = builder.show();
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        XmppUtils.XmppDelUser(cursor.getString(cursor.getColumnIndex("Jid")), new XmppUtils.XmppListener() {
                            @Override
                            public void Success() {
                                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void Error(String error) {
                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                return true;
            }
        });
        //联系人变化的监听
        Roster roster = MsgService.xmppConnection.getRoster();
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<String> collection) {
                RefreshList();
            }

            @Override
            public void entriesUpdated(Collection<String> collection) {
                RefreshList();
            }

            @Override
            public void entriesDeleted(Collection<String> collection) {
                RefreshList();
            }

            @Override
            public void presenceChanged(Presence presence) {
            }
        });
    }

    /**
     * TODO : 刷新页面
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2019/1/14
     * @return :
     */
    private void RefreshList() {
        final Cursor cursor = XmppUtils.XmppContacts(getContext());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new MyAdapter(getContext(),cursor);
                }
            }
        });
    }

    /**
     * TODO 功能：好友列表适配器
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/10
     */
    class MyAdapter extends CursorAdapter {

        public MyAdapter(Context context, Cursor c) {
            super(context, c,0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            ViewHolder viewHolder= new ViewHolder();
            View view=View.inflate(getContext(),R.layout.item_list ,null);

            viewHolder.head=view.findViewById(R.id.head );
            viewHolder.count=view.findViewById(R.id.count );
            viewHolder.nickname=view.findViewById(R.id.nickname );
            viewHolder.Remarks=view.findViewById(R.id.Remarks );
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder=(ViewHolder) view.getTag();
            viewHolder.nickname.setText(cursor.getString(2));
            viewHolder.Remarks.setText(cursor.getString(1));
            viewHolder.count.setVisibility(View.GONE);
        }
    }
}
