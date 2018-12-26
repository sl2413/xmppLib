package com.shenl.xmpp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import com.shenl.xmpp.R;
import com.shenl.xmpplibrary.utils.ThreadUtils;
import com.shenl.xmpplibrary.utils.XmppUtils;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        XmppUtils.XmppConnect(SplashActivity.this, "192.168.99.5", 5222, new XmppUtils.Listener() {
            @Override
            public void Success() {
                ThreadUtils.runSonThread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void Error(String error) {
            }
        });
    }
}
