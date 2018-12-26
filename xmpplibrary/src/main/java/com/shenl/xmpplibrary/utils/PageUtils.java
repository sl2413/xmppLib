package com.shenl.xmpplibrary.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.shenl.xmpplibrary.MyCallback.DialogCallBack;


public class PageUtils {

    //是否打印log
    public static final boolean PrintFalg = true;
    //自定义吐司
    private static Toast toast = null;

    /**
     * TODO 功能：显示一个吐司
     *
     * @param：
     * @author：沈 亮
     * @Data：下午2:42:33
     */
    public static void showToast(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        if (TextUtils.isEmpty(text)){
            toast.setText("请求超时");
        }else{
            toast.setText(text);
        }
        toast.show();
    }

    /**
     * TODO 功能：网络错误显示吐司
     *
     * @param：
     * @author：沈 亮
     * @Data：下午2:42:51
     */
    public static void showToast(Context context) {
        showToast(context,null);
    }

    /**
     * TODO 功能：在控制台打印log信息
     *
     * @param：
     * @author：沈 亮
     * @Data：上午9:33:14
     */
    public static void showLog(String text) {
        if (PrintFalg) {
            Log.e("shenl", text);
        }
    }

    /**
     * TODO 功能：显示一个不确定进度对话框并且可以自定义提交消息
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2018/4/16
     */
    public static ProgressDialog showDialog(Context context, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        //使得点击对话框外部不消失对话框
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(msg);
        progressDialog.show();
        return progressDialog;
    }

    /**
     * TODO 功能：显示一个不确定进度对话框
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2018/2/8
     */
    public static ProgressDialog showDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        //使得点击对话框外部不消失对话框
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在加载请稍后...");
        progressDialog.show();
        return progressDialog;
    }

    /**
     * TODO 功能：显示一个询问式对话框
     * <p>
     * 参数说明:
     * 作    者:   沈 亮
     * 创建时间:   2018/4/16
     */
    public static AlertDialog showAlertDialog(Context context, String title, String msg, final DialogCallBack callback) {
        AlertDialog AlertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton("取消", callback.onNegativeButton())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onPositiveButton();
                    }
                }).show();
        return AlertDialog;
    }
    /**
     * TODO 功能：判断服务器发来的状态code
     *
     * @param：
     * @author：沈 亮
     * @Data：下午1:44:09
     */
    public static Boolean setCode(Activity activity, String json) {
        return false;
    }
}
