package com.shenl.xmpp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.shenl.utils.MyUtils.ServiceUtils;
import com.shenl.xmpp.R;
import com.shenl.xmpplibrary.service.MsgService;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        boolean b = ServiceUtils.isServiceWork(SplashActivity.this, "com.shenl.xmpplibrary.service.MsgService");
        if (!b) {
            Intent intent1 = new Intent(SplashActivity.this, MsgService.class);
            startService(intent1);
        }

        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
