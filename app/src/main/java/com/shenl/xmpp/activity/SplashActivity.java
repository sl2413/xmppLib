package com.shenl.xmpp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.shenl.utils.MyUtils.ServiceUtils;
import com.shenl.utils.activity.BaseActivity;
import com.shenl.xmpp.R;
import com.shenl.xmpplibrary.service.MsgService;
import com.shenl.xmpplibrary.utils.XmppUtils;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initData();
        initEvent();
    }

    @Override
    public void initView() {
        XmppUtils.XmppConnect(SplashActivity.this, "192.168.99.5", 5222, new XmppUtils.XmppListener() {
            @Override
            public void Success() {
                boolean b = ServiceUtils.isServiceWork(SplashActivity.this, "com.shenl.xmpplibrary.service.MsgService");
                if (!b) {
                    Intent intent = new Intent(SplashActivity.this, MsgService.class);
                    startService(intent);
                }
            }

            @Override
            public void Error(String error) {

            }
        });
    }

    @Override
    public void initData() {
        openActivity(LoginActivity.class);
        finish();
    }

    @Override
    public void initEvent() {

    }
}
