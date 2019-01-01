package com.shenl.xmpplibrary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.utils.XmppUtils;

import org.jivesoftware.smack.packet.Message;

public class SessionFragment extends Fragment {

    private EditText et_input;
    private Button btn_send;
    private EditText et_toId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session, null);
        et_input = view.findViewById(R.id.et_input);
        btn_send = view.findViewById(R.id.btn_send);
        et_toId = view.findViewById(R.id.et_toId);
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
                String jid = et_toId.getText().toString().trim();
                Message msg = new Message();
                msg.setBody(s);
                XmppUtils.XmppSendMessage(jid+"@172.30.4.15", msg, new XmppUtils.XmppListener() {
                    @Override
                    public void Success() {
//                        PageUtils.showToast(getContext(),"发送成功");
                    }

                    @Override
                    public void Error(String error) {
//                        PageUtils.showLog(error);
                    }
                });
            }
        });
    }
}
