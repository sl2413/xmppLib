package com.shenl.xmpp.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shenl.utils.MyUtils.PageUtils;
import com.shenl.xmpp.R;
import com.shenl.xmpplibrary.fragment.ContactsFragment;
import com.shenl.xmpplibrary.fragment.CreateRoomFragment;
import com.shenl.xmpplibrary.fragment.RoomsFragment;
import com.shenl.xmpplibrary.fragment.SessionFragment;
import com.shenl.xmpplibrary.utils.XmppUtils;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private TextView tv_title;
    private ViewPager vp;
    private ArrayList<Fragment> list;
    private LinearLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XmppUtils.XmppDisconnect();
    }

    public void initView() {
        tv_title = findViewById(R.id.tv_title);
        vp = findViewById(R.id.vp);
        tabs = findViewById(R.id.ll_toolbar);


        PageUtils.showLog("用户＝＝"+XmppUtils.XmppGetJid());
    }

    public void initData() {
        list = new ArrayList<Fragment>();
        list.add(new CreateRoomFragment());
        list.add(new SessionFragment());
        list.add(new ContactsFragment());
        list.add(new RoomsFragment());
        vp.setAdapter(new MyAdapter(getSupportFragmentManager()));
    }

    public void initEvent() {

    }


    /**
     * TODO : 页面适配器
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2018/12/26
     * @return :
     */
    class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return list.get(i);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }
}
