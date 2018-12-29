package com.shenl.xmpp.utils;

import android.os.Handler;

public class ThreadUtils {
    /**
     * TODO : 运行在子线程
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2018/12/26
     * @return :
     */
    public static void runSonThread(Runnable runnable){
        new Thread(runnable).start();
    }

    private static Handler mhandler = new Handler();

    /**
     * TODO :  运行在主线程
     * 参数说明 :
     * 作者 : shenl
     * 创建日期 : 2018/12/26
     * @return :
     */
    public static void runMainThread(Runnable runnable){
        mhandler.post(runnable);
    }
}
