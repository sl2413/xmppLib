package com.shenl.xmpp.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shenl.utils.MyUtils.PageUtils;
import com.shenl.utils.activity.BaseActivity;
import com.shenl.xmpp.R;
import com.shenl.xmpplibrary.service.MsgService;
import com.shenl.xmpplibrary.utils.XmppUtils;

public class LoginActivity extends BaseActivity {

    private EditText et_name;
    private EditText et_pswd;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
        initEvent();
    }

    public void initView() {
        et_name = findViewById(R.id.et_name);
        et_pswd = findViewById(R.id.et_pswd);
        btn_login = findViewById(R.id.btn_login);
        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    public void initData() {

    }

    public void initEvent() {
        /**
         * TODO : 登陆按钮点击事件
         * 参数说明 : []
         * 作者 : shenl
         * 创建日期 : 2018/12/20
         * @return : void
         */
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = et_name.getText().toString().trim();
                final String pswd = et_pswd.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    et_name.setError("用户名不能为空");
                    PageUtils.showToast(LoginActivity.this, "用户名不能为空");
                    return;
                }
                if (TextUtils.isEmpty(pswd)) {
                    et_pswd.setError("密码不能为空");
                    PageUtils.showToast(LoginActivity.this, "密码不能为空");
                    return;
                }

                XmppUtils.XmppLogin(LoginActivity.this, name, pswd, new XmppUtils.XmppListener() {
                    @Override
                    public void Success() {
                        MsgService.nickname = name;
                        openActivity(MainActivity.class);
                        finish();
                    }

                    @Override
                    public void Error(String error) {
                        PageUtils.showToast(LoginActivity.this, error);
                    }
                });
            }
        });
    }
}
