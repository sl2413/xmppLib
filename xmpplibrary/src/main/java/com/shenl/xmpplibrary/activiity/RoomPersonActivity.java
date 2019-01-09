package com.shenl.xmpplibrary.activiity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.utils.XmppUtils;

import java.util.ArrayList;
import java.util.List;

public class RoomPersonActivity extends Activity {

    private TextView title;
    private List<String> PersonList = new ArrayList<>();
    private ListView lv_roomPerson;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_person);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        title = findViewById(R.id.title);
        lv_roomPerson = findViewById(R.id.lv_roomPerson);
    }

    private void initData() {
        title.setText(getIntent().getStringExtra("title"));
        XmppUtils.XmppGetRoomPerson(XmppUtils.muc, new XmppUtils.RoomPersonListener() {
            @Override
            public void Success(List<String> list) {
                if (!PersonList.isEmpty()) {
                    PersonList.clear();
                }
                PersonList.addAll(list);
                adapter = new MyAdapter();
                lv_roomPerson.setAdapter(adapter);
            }

            @Override
            public void Error(String error) {
                Toast.makeText(RoomPersonActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initEvent() {
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * TODO 功能：人员列表适配器
     *
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2019/1/9
     */
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return PersonList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null){
                view = View.inflate(RoomPersonActivity.this,R.layout.item_roomperson,null);
            }
            TextView tv_room_name = view.findViewById(R.id.tv_room_name);
            tv_room_name.setText(PersonList.get(i));
            return view;
        }
    }
}
