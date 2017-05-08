package com.loadmore.mark.testloadmore.a;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.loadmore.mark.testloadmore.R;

/**
 * Created by Mark.Han on 2017/5/8.
 */
public class LoadMoreView extends ListView {
    private boolean isLoadMoreEnable = true;//是否加载更多
    private View footerView;//根部View
    private FrameLayout footerLayout;//根部ViewGroup
    private int loadMoreCount = 0;//设置倒数第几个开始响应加载更多
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading = false;//是否正在刷新
    private boolean isNoMoreData = false;//是否还有数据
    private boolean isMoreThanScreen = false;//是否满屏

    private Context mContext;

    private OnScrollListener onScrollListener;

    public LoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.footerLayout = new FrameLayout(context);
    }

    public LoadMoreView(Context context) {
        this(context, null);
    }

    public void addOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /**
     * 加载更多相应
     */
    public interface OnLoadMoreListener {
        void onLoadingMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    /**
     * 设置adapter
     */
    public void setAdapter(ListAdapter listAdapter) {
        if (isLoadMoreEnable)
            initListenter();
        super.setAdapter(listAdapter);
    }

    /**
     * 上拉加载结束调用
     */
    public void onFinishLoading() {
        isLoading = false;
    }


    public boolean isLoading() {
        return isLoading;
    }

    /**
     * 没有更多数据调用
     */
    public void onNoMoreData() {
        isNoMoreData = true;
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_no_more_item, null);
        updateFooterView(view);
//        if (view.)
//        scrollBy(0, -view.getHeight());
    }

    public void onNoMoreData(@LayoutRes int layoutId) {
        isNoMoreData = true;
        if (layoutId > 0) {
            updateFooterView(LayoutInflater.from(mContext).inflate(layoutId, null));
        }
    }

    public void onNoMoreData(View noMoreFooterView) {
        isNoMoreData = true;
        if (noMoreFooterView != null)
            updateFooterView(noMoreFooterView);
    }

    /**
     * 移除footerview
     */
    public void removeFooterView() {
        if (footerView != null) {
            this.removeFooterView(footerLayout);
        }
    }

    /**
     * 设置footerview可见性
     */
    public void setFooterViewVisibility(int visibility) {
        if (footerView != null) {
            footerView.setVisibility(visibility);
        }
    }

    /**
     * 设置footerview为可见
     */
    public void setFooterViewVisibility() {
        if (isMoreThanScreen) {
            footerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新footerview
     */
    public void updateFooterView(View footerView) {
        footerLayout.removeAllViews();
        footerLayout.addView(footerView);
    }

    /**
     * 是否支持上拉加载更多
     */
    public void setLoadMoreEnable(boolean loadMoreEnable) {
        isLoadMoreEnable = loadMoreEnable;
    }

    /**
     * 设置footerview，但是一定要在setAdapter()之前调用。
     */
    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    private void initListenter() {
        this.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (onScrollListener != null)
                    onScrollListener.onScrollStateChanged(view, scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (onScrollListener != null)
                    onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);

                if (totalItemCount > visibleItemCount) {//是否占满一个屏幕
                    isMoreThanScreen = true;
                    if (footerView != null && footerView.getVisibility() == View.GONE) {
                        footerView.setVisibility(VISIBLE);
                    }
                }

                int lastVisibleItem = firstVisibleItem + visibleItemCount;
//                Log.e("---->OUT", "first" + firstVisibleItem + "visible" + visibleItemCount + "tatal" + totalItemCount + "loadmo" + loadMoreCount + isMoreThanScreen);
                //footerview也算一个
                if (lastVisibleItem == totalItemCount - loadMoreCount && isMoreThanScreen) {
                    if (!isNoMoreData && !isLoading && onLoadMoreListener != null && visibleItemCount != 0) {
                        isLoading = true;
                        onLoadMoreListener.onLoadingMore();
                    }

                }

                //设置loadmorecount的时候由于快速滑动，可能lastvisible会出现跳跃，从而无法调用回调方法
                //所以在滑动到底部的时候需要，进行检测是否需要滑动。
                if (lastVisibleItem == totalItemCount && isMoreThanScreen) {
                    if (!isNoMoreData && !isLoading && onLoadMoreListener != null && visibleItemCount != 0) {
                        isLoading = true;
                        onLoadMoreListener.onLoadingMore();
                    }
                }

                if (isNoMoreData && footerView.getVisibility()==VISIBLE) {

                }
            }
        });

        /**
         * 添加footerview
         */
        if (footerView == null)
            //默认footer
            footerView = LayoutInflater.from(mContext).inflate(R.layout.list_loading_footer, null, false);
        footerLayout.addView(footerView, 0, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        //不满一屏不进行上拉加载
        if (!isMoreThanScreen) {
            setFooterViewVisibility(View.GONE);
        }
        super.addFooterView(footerLayout);
    }

}
