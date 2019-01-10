package com.shenl.xmpplibrary.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.shenl.xmpplibrary.R;
import com.shenl.xmpplibrary.activiity.ChatActivity;

/**
 * TODO 功能：聊天表情页面fragment
 *
 * 参数说明:
 * 作    者:   沈 亮
 * 创建时间:   2019/1/10
 */
public class EmojiFragment extends Fragment {

    //emoji表情地址
    private String[] emojis = {"1F601","1F602","1F603","1F604","1F605","1F606","1F607","1F608","1F609",};
    private GridView gv_emoji;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emoji, null);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {
        gv_emoji = view.findViewById(R.id.gv_emoji);
    }

    private void initData() {
        gv_emoji.setAdapter(new MyAdapter());
    }

    private void initEvent() {
        gv_emoji.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatActivity activity = (ChatActivity) getActivity();
                activity.setBody(getEmoji(emojis[position]));
            }
        });
    }

    /**
     * TODO : 表情适配器
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2019/1/7
     * @return :
     */
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return emojis.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_emoji, null);
            }
            TextView tv_emoji = convertView.findViewById(R.id.tv_emoji);
            tv_emoji.setText(getEmoji(emojis[position]));
            return convertView;
        }
    }

    /**
     * TODO : 转换emoji编码
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2019/1/7
     * @return :
     */
    private String getEmoji(String emoji) {
        int unicodeCry = Integer.parseInt(emoji, 16);
        return new String(Character.toChars(unicodeCry));
    }
}
