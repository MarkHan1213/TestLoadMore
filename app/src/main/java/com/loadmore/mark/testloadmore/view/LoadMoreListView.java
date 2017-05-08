package com.loadmore.mark.testloadmore.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.loadmore.mark.testloadmore.R;

/**
 * 上拉更多的ListView
 * Created by Mark.Han on 2017/5/8.
 */
public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener {


    private boolean isLoading = false;

    private View mFooterView;

    private int mFooterHeight;

    private OnLoadMoreListener mListener;
    private LayoutInflater inflater;


    // 上拉头
    private View loadmoreView;
    // 上拉的箭头
    private View pullUpView;
    // 正在加载的图标
    private View loadingView;
    // 加载结果图标
    private View loadStateImageView;
    // 加载结果：成功或失败
    private TextView loadStateTextView;


    public LoadMoreListView(Context context) {
        this(context, null);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initFootView();
    }

    private void initView() {
        loadmoreView = LayoutInflater.from(getContext()).inflate(R.layout.load_more, null);
    }

    /**
     * 初始化脚布局
     */
    private void initFootView() {
        // 初始化上拉布局
        pullUpView = loadmoreView.findViewById(R.id.pullup_icon);
        loadStateTextView = (TextView) loadmoreView
                .findViewById(R.id.loadstate_tv);
        loadingView = loadmoreView.findViewById(R.id.loading_icon);
        loadStateImageView = loadmoreView.findViewById(R.id.loadstate_iv);
    }


    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        mFooterView = inflater.inflate(R.layout.load_more, null);
        mFooterView.measure(0, 0);
        mFooterHeight = mFooterView.getMeasuredHeight();
        mFooterView.setPadding(0, -mFooterHeight, 0, 0);
        this.addFooterView(mFooterView);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollstate) {
        if (this.getLastVisiblePosition() == this.getAdapter().getCount() - 1
                && !isLoading && (scrollstate == SCROLL_STATE_FLING || scrollstate == SCROLL_STATE_IDLE)) {
            setLoadState(true);
            if (this.mListener != null) {
                this.mListener.loadMore();
            }
        }
    }

    /**
     * 设置状态
     *
     * @param b
     */
    public void setLoadState(boolean b) {
        this.isLoading = b;
        if (isLoading) {
            mFooterView.setPadding(0, 0, 0, 0);
            this.setSelection(this.getAdapter().getCount() + 1);
        } else {
            mFooterView.setPadding(0, -mFooterHeight, 0, 0);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mListener = listener;
    }

    public interface OnLoadMoreListener {
        void loadMore();
    }

}
