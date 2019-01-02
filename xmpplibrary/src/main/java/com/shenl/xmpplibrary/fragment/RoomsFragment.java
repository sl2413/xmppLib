package com.shenl.xmpplibrary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.shenl.xmpplibrary.R;

public class RoomsFragment extends Fragment {

    private ListView lv_room;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, null);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {
        lv_room = view.findViewById(R.id.lv_room);
    }

    private void initData() {

    }

    private void initEvent() {

    }
}
