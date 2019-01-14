package com.shenl.xmpp.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.shenl.utils.activity.BaseActivity;
import com.shenl.xmpp.R;
import com.shenl.xmpplibrary.dao.ChatDao;
import com.shenl.xmpplibrary.utils.XmppUtils;

public class SplashActivity extends BaseActivity {

//    public static final String sarviceName = "172.30.4.15";
    public static final String sarviceName = "192.168.99.3";

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
        XmppUtils.XmppConnect(SplashActivity.this, sarviceName, 5222,sarviceName,new XmppUtils.XmppListener() {
            @Override
            public void Success() {

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
