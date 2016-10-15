package com.maxi.chatdemo.app;

import android.app.Application;

import com.maxi.chatdemo.db.base.BaseManager;

/**
 * Created by Mao Jiqing on 2016/9/28.
 */
public class ChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BaseManager.initOpenHelper(this);
    }

}
