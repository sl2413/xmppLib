package com.shenl.xmpplibrary.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.utils.XmppUtils;

public class CreateRoomFragment extends Fragment {

    private EditText et_roomName;
    private Button btn_createroom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_room, null);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {
        et_roomName = view.findViewById(R.id.et_roomName);
        btn_createroom = view.findViewById(R.id.btn_createroom);
    }

    private void initData() {

    }

    private void initEvent() {
        btn_createroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomName = et_roomName.getText().toString().trim();
                if (TextUtils.isEmpty(roomName)){
                    et_roomName.setError("群名称不能为空");
                    return;
                }
                XmppUtils.XmppCreateRoom(roomName, "", new XmppUtils.XmppListener() {
                    @Override
                    public void Success() {
                        Toast.makeText(getContext(), "创建成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void Error(String error) {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
