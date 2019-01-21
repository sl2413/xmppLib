package com.shenl.xmpp.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.shenl.utils.activity.BaseActivity;
import com.shenl.xmpp.R;
import com.shenl.xmpplibrary.bean.Msg;
import com.shenl.xmpplibrary.dao.ChatDao;
import com.shenl.xmpplibrary.utils.XmppUtils;

import java.util.List;

public class SplashActivity extends BaseActivity {

//    public static final String sarviceName = "172.30.4.15";
    public static final String ip = "192.168.99.3";
    public static final String sName = "user-20180811pi";

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
        XmppUtils.XmppConnect(SplashActivity.this, ip, 5222,sName,new XmppUtils.XmppListener() {
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
