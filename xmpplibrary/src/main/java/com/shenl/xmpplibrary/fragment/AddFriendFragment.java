package com.shenl.xmpplibrary.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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


/**
 * TODO 功能：添加好友页面fragment
 *
 * 参数说明:
 * 作    者:   沈 亮
 * 创建时间:   2019/1/10
 */
public class AddFriendFragment extends Fragment {

    private EditText et_jid;
    private EditText et_nickname;
    private Button btn_addFirend;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addfriend, null);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {
        et_jid = view.findViewById(R.id.et_Jid);
        et_nickname = view.findViewById(R.id.et_Nickname);
        btn_addFirend = view.findViewById(R.id.btn_addFirend);
    }

    private void initData() {

    }

    private void initEvent() {
        //添加好友按钮
        btn_addFirend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Jid = et_jid.getText().toString().trim();
                String nickName = et_nickname.getText().toString().trim();
                if (TextUtils.isEmpty(Jid)){
                    et_jid.setError("好友Jid不能为空");
                    return;
                }
                XmppUtils.XmppAddFriend(getContext(),Jid, nickName, new XmppUtils.XmppListener() {
                    @Override
                    public void Success() {
                        et_jid.setText("");
                        et_nickname.setText("");
                        Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();
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
