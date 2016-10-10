package com.maxi.chatdemo.widget.pulltorefresh.base;

import android.content.Context;
import android.view.View;

import com.maxi.chatdemo.widget.pulltorefresh.PullToRefreshListView;
import com.maxi.chatdemo.widget.pulltorefresh.PullToRefreshRecyclerView;

/**
 * Created by Mao Jiqing on 2016/10/10.
 */
public class PullToRefreshView extends View {

    public static final int LISTVIEW = 0;
    public static final int RECYCLERVIEW = 1;

    public PullToRefreshView(Context context) {
        super(context);
    }

    public View getSlideView(int slideViewType) {
        View baseView = null;
        switch (slideViewType) {
            case LISTVIEW:
                baseView = new PullToRefreshListView(getContext());
                break;
            case RECYCLERVIEW:
                baseView = new PullToRefreshRecyclerView(getContext());
                break;
            default:
                baseView = null;
                break;
        }
        return baseView;
    }
}
