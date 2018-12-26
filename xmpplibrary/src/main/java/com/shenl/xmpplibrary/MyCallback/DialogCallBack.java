package com.shenl.xmpplibrary.MyCallback;

import android.content.DialogInterface;

public abstract class DialogCallBack {
    // 确定按钮回调函数
    public abstract void onPositiveButton();

    //取消按钮回调函数
    public DialogInterface.OnClickListener onNegativeButton(){
        return null;
    }
}
