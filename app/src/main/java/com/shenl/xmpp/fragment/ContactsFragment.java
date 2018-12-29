package com.shenl.xmpp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shenl.utils.MyUtils.PageUtils;
import com.shenl.xmpplibrary.utils.XmppUtils;

import org.jivesoftware.smack.RosterEntry;
import java.util.List;

public class ContactsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setText("联系人页面");
        List<RosterEntry> list = XmppUtils.XmppContacts();
        for (int i=0;i<list.size();i++){
            PageUtils.showLog("jid:"+list.get(i).getUser());
        }
        /*for(RosterEntry entry:entries){
            PageUtils.showLog("姓名:"+entry.getName());
            PageUtils.showLog("jid:"+entry.getUser());
            PageUtils.showLog("状态:"+entry.getStatus()+"");
            PageUtils.showLog("类型:"+entry.getType()+"");
            PageUtils.showLog("分组:"+entry.getGroups()+"");
        }*/
        return textView;
    }
}
