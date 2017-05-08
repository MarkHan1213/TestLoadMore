package com.loadmore.mark.testloadmore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class LoadMoreListView extends ListView implements OnScrollListener {

	private static final String TAG = "LoadMoreListView";
	private final int COLOR_TRANSPARENCY = 0x00000000;
	private View mHeadView;
	public boolean mIsOpenAlpha = true;
	/**
	 * The Alpha disappeared .The height of the gradient disappeared.You can set
	 * the value,if you need.
	 */
	private int mFadingHeight = 500;
	/** Your Layout need set The drawable implementation Color */
	private Drawable mAlphaDrawable;
	/** The view at the top of the color */
	// private int mTopColor = 0xff000cff;
	private int mTopColor = COLOR_TRANSPARENCY;
	/** Starting the transparency */
	private static final int START_ALPHA = 0;
	/** Termination of transparency */
	private static final int END_ALPHA = 255;
	/** At the top of the gradient to monitor */
	private TopAlphaListener mTopAlphaListener;
	private OnTopAlphaListener mOnTopAlphaListener;
	/**
	 * Listener that will receive notifications every time the list scrolls.
	 */
	private OnScrollListener mOnScrollListener;
	private LayoutInflater mInflater;

	// footer view
	private RelativeLayout mFooterView;
	// private TextView mLabLoadMore;
	// private ProgressBar mProgressBarLoadMore;
	private ImageView mProgressBarLoadMore;

	// Listener to process load more items when user reaches the end of the list
	private OnLoadMoreListener mOnLoadMoreListener;
	// To know if the list is loading more items
	private boolean mIsLoadingMore = false;
	/** This is NoLoad State */
	private boolean mIsSetNoLoad = false;
	private int mCurrentScrollState;
	/** The ListView Is Measure his Child's Height */
	private boolean mIsMeasure;
	/** null */
	private TextView mLoadMoreAllContents;
	/** When you don't have any content to load */
	private TextView mAllContents;
	private RelativeLayout mAllContentsLayout;
	/** If you don't need to load more,set this value is false */
	private boolean mIsHasLoadMoreFunction = false;

	public LoadMoreListView(Context context) {
		super(context);
		init(context);
	}

	public LoadMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	@SuppressLint("NewApi")
	private void init(Context context) {
		if (mIsHasLoadMoreFunction)
			return;
		setFooterDividersEnabled(false);
		setOverScrollMode(OVER_SCROLL_NEVER);

		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// footer
		mFooterView = (RelativeLayout) mInflater.inflate(
				R.layout.load_more_footer, this, false);
		// footer nothing
		mAllContentsLayout = (RelativeLayout) mInflater.inflate(
				R.layout.load_more_footer_nothing, this, false);

		/*
		 * mLabLoadMore = (TextView) mFooterView
		 * .findViewById(R.id.load_more_lab_view);
		 */
		// mProgressBarLoadMore = (ProgressBar) mFooterView
		// .findViewById(R.id.load_more_progressBar);
		mProgressBarLoadMore = (ImageView) mFooterView
				.findViewById(R.id.load_more_progressBar);
		mLoadMoreAllContents = (TextView) mFooterView
				.findViewById(R.id.load_more_all_contents);
		mProgressBarLoadMore.setImageResource(R.anim.loadmore_loading_anim);

		addFooterView(mFooterView,null,false);

		super.setOnScrollListener(this);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
	}

	/**
	 * Set the listener that will receive notifications every time the list
	 * scrolls.
	 * 
	 * @param l
	 *            The scroll listener.
	 */
	@Override
	public void setOnScrollListener(AbsListView.OnScrollListener l) {
		mOnScrollListener = l;
	}

	/**
	 * The top view transparency setting;
	 * 
	 * @param y
	 */
	private void setTopAlpha(int y) {
		y = y < 0 ? -y : y;
		if (y > 600) {
			return;
		}
		if (y > mFadingHeight) {
			y = mFadingHeight;
		}

		if (mAlphaDrawable == null) {

			initAlphaDrawable();
		}
		int alpha = y * (END_ALPHA - START_ALPHA) / mFadingHeight + START_ALPHA
		/* (y * (START_ALPHA - END_ALPHA) / mFadingHeight + START_ALPHA) */;
//		Log.i(TAG, "alpha--:" + alpha);
		mAlphaDrawable.setAlpha(alpha);
		if (mTopAlphaListener != null) {
			mTopAlphaListener.showTopAlphaDrawable(mAlphaDrawable);
		}
	}

	/**
	 * The top view transparency setting;
	 * 
	 * @param y
	 */
	public void setTopAlphaOpen(int y) {
		y = y < 0 ? -y : y;
		// if (y > mFadingHeight) {
		// return;
		// }
		if (y > mFadingHeight) {
			y = mFadingHeight;
		}

		if (mAlphaDrawable == null) {

			initAlphaDrawable();
		}
		int alpha = y * (END_ALPHA - START_ALPHA) / mFadingHeight + START_ALPHA
		/* (y * (START_ALPHA - END_ALPHA) / mFadingHeight + START_ALPHA) */;
//		Log.i(TAG, "alpha--:" + alpha);
		mAlphaDrawable.setAlpha(END_ALPHA - alpha);
		if (mOnTopAlphaListener != null) {
			mOnTopAlphaListener.showTopAlphaDrawable(mAlphaDrawable, alpha);
		}
	}

	/**
	 * Set the view at the top of the Alpha listener;
	 * 
	 * @param listener
	 */
	public void setTopAlphaListener(TopAlphaListener listener) {
		mTopAlphaListener = listener;
	}

	/**
	 * Set the view at the top of the Alpha listener;
	 * 
	 * @param listener
	 */
	public void setOnTopAlphaListener(OnTopAlphaListener listener) {
		mOnTopAlphaListener = listener;
	}

	private void initAlphaDrawable() {
		mAlphaDrawable = new ColorDrawable(mTopColor);
		// mFootView.setVisibility(View.GONE);
	}

	/**
	 * Set the view at the top of color;
	 * 
	 * @param topColor
	 *            The default is transparent,values is 0x00000000;
	 */
	public void setTopColor(int topColor) {
		mTopColor = topColor;
		initAlphaDrawable();
	}

	/**
	 * The listener to monitor at the top of the color change ;
	 * 
	 * @author HeAs
	 */
	public interface TopAlphaListener {

		public void showTopAlphaDrawable(Drawable alphaDrawable);

	}

	/**
	 * The listener to monitor at the top of the color change ;
	 * 
	 * @author HeAs
	 */
	public interface OnTopAlphaListener {

		public void showTopAlphaDrawable(Drawable alphaDrawable, int alpha);

	}

	/**
	 * A view to the ListView's head;
	 * 
	 * @param view
	 * @return
	 */
	public LoadMoreListView setHeadView(View view) {
		if (view == null) {
			// empty!
		} else {
			mHeadView = view;
			this.addHeaderView(view, null, false);
		}
		return this;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		if (mScrollChangedListener != null) {
			mScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
		}

		super.onScrollChanged(l, t, oldl, oldt);
	}

	/**
	 * This is called in response to an internal scroll in this view (i.e., the
	 * view scrolled its own contents). This is typically as a result of
	 * scrollBy(int, int) or scrollTo(int, int) having been called.
	 * 
	 * @param l
	 *            Current horizontal scroll origin.
	 * @param t
	 *            Current vertical scroll origin.
	 * @param oldl
	 *            Current vertical scroll origin.
	 * @param oldt
	 *            Previous vertical scroll origin.
	 */
	public interface onScrollChangedListener {
		void onScrollChanged(int l, int t, int oldl, int oldt);
	}

	private onScrollChangedListener mScrollChangedListener;

	public void setOnScrollChangedListener(onScrollChangedListener scl) {
		this.mScrollChangedListener = scl;
	}

	/**
	 * Register a callback to be invoked when this list reaches the end (last
	 * item be visible)
	 * 
	 * @param onLoadMoreListener
	 *            The callback to run.
	 */

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		mOnLoadMoreListener = onLoadMoreListener;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		View c = view.getChildAt(0);
		if (c != null) {
			// assuming all list items have same height
			// if (c instanceof ViewGroup) {
			// ViewGroup viewG = (ViewGroup)c;
			// int childCount = viewG.getChildCount();
			// for (int i = 0; i < childCount; i++) {
			// View childAt = viewG.getChildAt(i);
			// if (childAt instanceof ViewPager) {
			//
			// break;
			// }
			// }
			// }
			// int scrolly = -c.getTop() + view.getPaddingTop()
			// + view.getFirstVisiblePosition() * c.getHeight();
			// Log.i("LoadMoreListViewOnScroll", "scrolly-->:"+scrolly);
			int top = getTop();
			int top2 = 0;
			if (mHeadView != null) {
				top2 = mHeadView.getTop();
			}
			if (mIsOpenAlpha) {
				setTopAlpha(top2);
			}
		}

		if (mOnScrollListener != null) {
			mOnScrollListener.onScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}

		if (mOnLoadMoreListener != null && !mIsSetNoLoad) {

			if (visibleItemCount == totalItemCount) {
				mProgressBarLoadMore.setVisibility(View.GONE);
				// mLabLoadMore.setVisibility(View.GONE);
				return;
			}

			boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

			if (!mIsLoadingMore && loadMore
					&& mCurrentScrollState != SCROLL_STATE_IDLE) {
				// mProgressBarLoadMore.setVisibility(View.VISIBLE);
				mProgressBarLoadMore.setVisibility(View.VISIBLE);
				AnimationDrawable drawable = (AnimationDrawable) mProgressBarLoadMore
						.getDrawable();
				drawable.start();
				// mLabLoadMore.setVisibility(View.VISIBLE);
				mIsLoadingMore = true;
				onLoadMore();
			}

		}

	}

	private OnListViewScrollStateChangedListener mStateChangedListenr;

	public void setStateChangedListenr(
			OnListViewScrollStateChangedListener stateChangedListenr) {
		this.mStateChangedListenr = stateChangedListenr;
	}

	public interface OnListViewScrollStateChangedListener {

		public void onScrollStateChanged(AbsListView view, int scrollState);

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (mStateChangedListenr != null) {
			mStateChangedListenr.onScrollStateChanged(view, scrollState);
		}
		// bug fix: listview was not clickable after scroll
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			view.invalidateViews();
		}

		mCurrentScrollState = scrollState;

		if (mOnScrollListener != null) {
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}

	}

	public void onLoadMore() {
		if (BuildConfig.DEBUG) {
//			Log.d(TAG, "onLoadMore");
		}
		if (mOnLoadMoreListener != null) {
			mOnLoadMoreListener.onLoadMore();
		}
	}

	/**
	 * Notify the loading more operation has finished
	 */
	public void onLoadMoreComplete() {
		mIsLoadingMore = false;
		mProgressBarLoadMore.setVisibility(View.GONE);
		AnimationDrawable drawable = (AnimationDrawable) mProgressBarLoadMore
				.getDrawable();
		drawable.stop();
	}

	/**
	 * Interface definition for a callback to be invoked when list reaches the
	 * last item (the user load more items in the list)
	 */
	public interface OnLoadMoreListener {
		/**
		 * Called when the list reaches the last item (the last item is visible
		 * to the user)
		 */
		public void onLoadMore();
	}

	/**
	 * Get Current LoadingMore State;
	 * 
	 * @return true is Loading;
	 */
	public boolean getIsLoadingMore() {
		return mIsLoadingMore;
	}

	/**
	 * Get The LoadMoreView is Set No To Load State?
	 * 
	 * @return true is set!
	 */
	public boolean getIsSetNoLoad() {
		return mIsSetNoLoad;
	}

	/**
	 * Set NO More To Load. When you don't need the LoadMoreView;
	 */
	public void setNoMoreToLoad() {
		mIsSetNoLoad = true;
		mIsLoadingMore = false;
		try {
			removeFooterView(mFooterView);
			addFooterView(mAllContentsLayout,null,false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set NO More To Load. When you need the LoadMoreView to Load More
	 * Contents;
	 */
	public void setOkToLoad() {
		mIsSetNoLoad = false;
		mIsLoadingMore = false;
		try {
			if (mAllContentsLayout != null) {

				removeFooterView(mAllContentsLayout);
			}
			addFooterView(mFooterView,null,false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setIsMeasure(boolean isMeasure) {

		mIsMeasure = isMeasure;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Prevent display only a single data
		if (mIsMeasure) {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// getParent().requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void scrollBy(int x, int y) {
		Log.i("mmmmmmmmmmmmmmmmm1", "scrollBy" + "x-->" + x + "y-->" + y);
		super.scrollBy(x, y);
	}

	@Override
	public void scrollTo(int x, int y) {
		Log.i("mmmmmmmmmmmmmmmmm2", "scrollTo" + "x-->" + x + "y-->" + y);
		super.scrollTo(x, y);
	}

	@Override
	public void scrollListBy(int y) {
		Log.i("mmmmmmmmmmmmmmmmm3", "scrollListBy" + "y-->" + y);
		super.scrollListBy(y);
	}

	public void setFadingHeight(int fadingHeight) {

		this.mFadingHeight = fadingHeight;

	}

	public void setNoLoadMoreFunction(boolean isHasLoadMoreFunction) {

		mIsSetNoLoad = true;
		mIsLoadingMore = false;
		try {
			removeFooterView(mFooterView);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.mIsHasLoadMoreFunction = isHasLoadMoreFunction;
		invalidate();
	}
}
