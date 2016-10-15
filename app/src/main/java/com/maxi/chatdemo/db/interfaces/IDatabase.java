package com.maxi.chatdemo.db.interfaces;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Mao Jiqing on 2016/10/15.
 */

public interface IDatabase<M, K> {
    boolean insert(M m);

    boolean delete(M m);

    boolean deleteByKey(K key);

    boolean deleteList(List<M> mList);

    boolean deleteByKeyInTx(K... key);

    boolean deleteAll();

    boolean insertOrReplace(@NonNull M m);

    boolean update(M m);

    boolean updateInTx(M... m);

    boolean updateList(List<M> mList);

    M selectByPrimaryKey(K key);

    List<M> loadAll();

    /**
     * 分页加载
     * @param page 设定当前页数
     * @param number 设定一页显示数量
     * @return
     */
    List<M> loadPages(int page, int number);

    /**
     * 获取分页数
     * @param number 设定一页显示数量
     * @return
     */
    long getPages(int number);

    boolean refresh(M m);

    /**
     * 清理缓存
     */
    void clearDaoSession();

    /**
     * Delete all tables and content from our database
     */
    boolean dropDatabase();

    /**
     * 事务
     */
    void runInTx(Runnable runnable);

    /**
     * 添加集合
     *
     * @param mList
     */
    boolean insertList(List<M> mList);

    /**
     * 添加集合
     *
     * @param mList
     */
    boolean insertOrReplaceList(List<M> mList);

    /**
     * 自定义查询
     *
     * @return
     */
    QueryBuilder<M> getQueryBuilder();

    /**
     * @param where
     * @param selectionArg
     * @return
     */
    List<M> queryRaw(String where, String... selectionArg);

}
