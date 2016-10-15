package com.maxi.chatdemo.db;

import com.maxi.chatdemo.db.base.BaseManager;

import org.greenrobot.greendao.AbstractDao;

/**
 * Created by Mao Jiqing on 2016/10/15.
 */

public class ChatDbManager extends BaseManager<ChatMessageBean,Long> {
    @Override
    public AbstractDao<ChatMessageBean, Long> getAbstractDao() {
        return daoSession.getChatMessageBeanDao();
    }
}
