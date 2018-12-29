package com.shenl.xmpp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.shenl.utils.MyUtils.ServiceUtils;
import com.shenl.utils.activity.BaseActivity;
import com.shenl.xmpp.R;
import com.shenl.xmpplibrary.service.MsgService;

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
        boolean b = ServiceUtils.isServiceWork(SplashActivity.this, "com.shenl.xmpplibrary.service.MsgService");
        if (!b) {
            Intent intent = new Intent(SplashActivity.this, MsgService.class);
            startService(intent);
        }
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
