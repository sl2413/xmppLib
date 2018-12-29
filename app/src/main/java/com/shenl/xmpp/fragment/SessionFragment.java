package com.shenl.xmpp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.shenl.utils.MyUtils.PageUtils;
import com.shenl.xmpp.R;
import com.shenl.xmpplibrary.utils.XmppUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class SessionFragment extends Fragment {

    private EditText et_input;
    private Button btn_send;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session, null);
        et_input = view.findViewById(R.id.et_input);
        btn_send = view.findViewById(R.id.btn_send);
        initData();
        initEvent();
        return view;
    }

    private void initData() {

    }

    private void initEvent() {
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = et_input.getText().toString().trim();
                XmppUtils.XmppGetMessage(new MessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        PageUtils.showLog(message.getBody());
                    }
                });
                Message msg = new Message();
                msg.setBody(s);
                XmppUtils.XmppSendMessage("zhangxq@172.30.4.15", msg, new XmppUtils.XmppListener() {
                    @Override
                    public void Success() {
                        PageUtils.showToast(getContext(),"发送成功");
                    }

                    @Override
                    public void Error(String error) {
                        PageUtils.showLog(error);
                    }
                });
            }
        });
    }
}
