package com.example.administrator.myapplication.xListView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.myapplication.foot.adapter.NewsMenuAdapter;
import com.example.administrator.myapplication.R;


import java.util.Date;

/**
 * Created by kkkkk on 2016/1/18.
 */
public class MyListView extends ListView implements AbsListView.OnScrollListener {

	private static final int RELEASE_To_REFRESH = 0;// 下拉过程的状态值
	private static final int PULL_NOT_REFRESH = 1; // 从下拉返回到不刷新的状态值
	private static final int REFRESHING = 2;// 正在刷新的状态值
	private static final int DONE = 3;//头部原始状态
	private static final int LOADING = 4;
	private static final int FOOT_DONE=5;//底部原始状态
	private static final int PULL_NOT_LOAD=6;//上拉过程
	private static final int READY_TO_LOAD=7;//松开加载

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;
	private LayoutInflater inflater;

	// ListView头部下拉刷新的布局
	private View headerView;
	private TextView mTextRefreshState;
	private TextView mTextLastTime;
	private ImageView mImageArrow;
	private ProgressBar mProgressBarHeader;

	//ListView底部加载的布局
	private View footView;
	private ProgressBar mProgressBarFoot;
	private TextView mTextLoad;

	// 定义头部下拉刷新的布局的高度
	private int headerContentHeight;
	private int footContentHeight;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY,startYFoot;
	public int state,stateFoot;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored,isFootRecored;

	private OnRefreshListener refreshListener;
	private OnLoadListener loadListener;

	private boolean isRefreshable;
	private boolean isLoadable;

	public MyListView(Context context) {
		super(context);
		init(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}


	private void init(Context context) {
		inflater = LayoutInflater.from(context);
		headerView = inflater.inflate(R.layout.listview_header, null);
		footView=inflater.inflate(R.layout.listview_foot, null);
		mTextLoad= (TextView) footView.findViewById(R.id.text_loadmore);//上拉加载的progressbar
		mProgressBarFoot= (ProgressBar) footView.findViewById(R.id.progressbar_load);//上拉加载文字
		mTextRefreshState = (TextView) headerView.findViewById(R.id.pull_to_refresh);//下拉刷新的文字
		mTextLastTime = (TextView) headerView.findViewById(R.id.refresh_time);//显示上次刷新的时间
		mImageArrow = (ImageView) headerView.findViewById(R.id.arrow);//下拉的箭头
		// 设置下拉刷新图标的最小高度和宽度
		mImageArrow.setMinimumWidth(70);
		mImageArrow.setMinimumHeight(50);
		mProgressBarHeader = (ProgressBar) headerView.findViewById(R.id.progressbar);
		measureView(headerView);
		measureView(footView);
		headerContentHeight = headerView.getMeasuredHeight();
		footContentHeight=footView.getMeasuredHeight();
		// 设置内边距，正好距离顶部为一个负的整个布局的高度，正好把头部隐藏
		//left top right bottom
		headerView.setPadding(0, -1 * headerContentHeight, 0, 0);
		footView.setPadding(0, 0, 0, -1 * footContentHeight);
		// 重绘一下
		headerView.invalidate();
		footView.invalidate();
		// 将下拉刷新和上拉加载的布局加入ListView的顶部
		addHeaderView(headerView, null, false);
		addFooterView(footView, null, false);
		// 设置滚动监听事件
		setOnScrollListener(this);

		// 设置旋转动画事件
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		//箭头翻转
		reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		// 一开始的状态就是下拉刷新完的状态，所以为DONE
		state = DONE;
		stateFoot=FOOT_DONE;
		// 是否刷新
		isRefreshable = false;
		isLoadable=false;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (view.getFirstVisiblePosition() == 0) {
			isRefreshable = true;
		} else {
			isRefreshable = false;
		}
		if (view.getLastVisiblePosition()==view.getCount()-1){
			isLoadable=true;
		}else {
			isLoadable=false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isRefreshable){
			switch (ev.getAction()){
				case MotionEvent.ACTION_DOWN:
					if (!isRecored) {
						//记录y开始的位置
						isRecored = true;
						startY = (int) ev.getY();// 手指按下时记录当前位置
					}
					break;
				case MotionEvent.ACTION_MOVE:
					int tempY = (int) ev.getY();
					if (!isRecored) {
						isRecored = true;
						startY = tempY;
					}
					if (state != REFRESHING && isRecored ) {
						// done状态下
						if (state == DONE) {
							if (tempY - startY > 0) {
								state = PULL_NOT_REFRESH;
								changeViewByState();
							}
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == PULL_NOT_REFRESH) {
						// 更新headView的size
						headerView.setPadding(0, -1 * headerContentHeight + (tempY - startY) / RATIO, 0, 0);
						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headerContentHeight) {// 由done或者下拉刷新状态转变到松开刷新
							state = RELEASE_To_REFRESH;
							changeViewByState();
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {// 由DOne或者下拉刷新状态转变到done状态
							state = DONE;
							changeViewByState();
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					if (state != REFRESHING ) {
						if (state == PULL_NOT_REFRESH) {
							state = DONE;
							changeViewByState();
						}
						if (state == RELEASE_To_REFRESH) {
							state = REFRESHING;
							changeViewByState();
							onListviewRefresh();
						}
					}
					isRecored = false;
					break;
				default:
					break;
			}
		}
		if (isLoadable){
			switch (ev.getAction()){
				case MotionEvent.ACTION_DOWN:
					if (!isFootRecored) {
						//记录y开始的位置
						isFootRecored = true;
						startYFoot = (int) ev.getY();// 手指按下时记录当前位置
					}
					break;
				case MotionEvent.ACTION_MOVE:
					int tempY = (int) ev.getY();
					if (!isFootRecored) {
						isFootRecored = true;
						startYFoot = tempY;
					}
					if (stateFoot != LOADING && isFootRecored) {
						// done状态下
						if (stateFoot == FOOT_DONE) {
							if (startYFoot - tempY > 0) {
								stateFoot = PULL_NOT_LOAD;
								changeViewByState();
							}
						}
						// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
						if (stateFoot == PULL_NOT_LOAD) {
							// 更新headView的size
							footView.setPadding(0, 0, 0,  -1*footContentHeight + (startY - tempY) / RATIO);
							// 下拉到可以进入RELEASE_TO_REFRESH的状态
							if ((startYFoot - tempY) / RATIO >= footContentHeight) {// 由done或者下拉刷新状态转变到松开刷新
								stateFoot = READY_TO_LOAD;
								changeViewByState();
							}
							// 上推到顶了
							else if (startYFoot - tempY <= 0) {// 由DOne或者下拉刷新状态转变到done状态
								stateFoot = FOOT_DONE;
								changeViewByState();
							}
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					if (stateFoot != LOADING ) {
						if (stateFoot == PULL_NOT_LOAD) {
							stateFoot = FOOT_DONE;
							changeViewByState();
						}
						if (stateFoot == READY_TO_LOAD) {
							stateFoot = LOADING;
							changeViewByState();
							onListviewLoad();
						}
					}
					isFootRecored = false;
					break;
				default:
					break;
			}
		}
		return super.onTouchEvent(ev);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeViewByState() {
		switch (state) {
			case RELEASE_To_REFRESH:
				mImageArrow.setVisibility(View.VISIBLE);
				mProgressBarHeader.setVisibility(View.GONE);
				mTextRefreshState.setVisibility(View.VISIBLE);
				mTextLastTime.setVisibility(View.VISIBLE);
				mImageArrow.startAnimation(animation);// 开始动画效果
				mTextRefreshState.setText("松开刷新");
				break;

			case PULL_NOT_REFRESH:
				mProgressBarHeader.setVisibility(View.GONE);
				mTextRefreshState.setVisibility(View.VISIBLE);
				mTextLastTime.setVisibility(View.VISIBLE);
				mImageArrow.setVisibility(View.VISIBLE);
				break;

			case REFRESHING:
				//left top right bottom
				headerView.setPadding(0, 0, 0, 0);
				mProgressBarHeader.setVisibility(View.VISIBLE);
				mImageArrow.clearAnimation();
				mImageArrow.setVisibility(View.GONE);
				mTextRefreshState.setText("正在刷新...");
				mTextLastTime.setVisibility(View.VISIBLE);
				break;
			case DONE:
				headerView.setPadding(0, -1 * headerContentHeight, 0, 0);
				mProgressBarHeader.setVisibility(View.GONE);
				mImageArrow.clearAnimation();
				mImageArrow.setImageResource(R.mipmap.arrow);
				mTextRefreshState.setText("下拉刷新");
				mTextLastTime.setVisibility(View.VISIBLE);
				break;


		}
		switch (stateFoot){
			case PULL_NOT_LOAD:
			case FOOT_DONE:
				footView.setPadding(0, 0, 0, -1 * footContentHeight);
				mProgressBarFoot.setVisibility(View.GONE);
				break;
			case READY_TO_LOAD:
				mTextLoad.setText("松开加载");
				break;
			case LOADING:
				mTextLoad.setText("正在加载。。。");
				mProgressBarFoot.setVisibility(View.VISIBLE);
				break;
		}
	}

	public Boolean isRefreshing () {
		if (state == REFRESHING) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean isLoading () {
		if (stateFoot == LOADING) {
			return true;
		} else {
			return false;
		}
	}
	private void measureView(View child) {
		ViewGroup.LayoutParams params = child.getLayoutParams();
		if (params == null) {
			//params=new ViewGroup.LayoutParams(width,height);
			params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		//ViewGroup.getChildMeasureSpec(int spec, int padding, int childDimension);
		//可以设置子View的内外边距。并且记录预定大小,若spec，padding均为0，则子布局为实际大小。
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);
		int lpHeight = params.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
			//EXACTLY(完全)，父元素决定自元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			//UNSPECIFIED(未指定),父元素部队自元素施加任何束缚
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		mTextLastTime.setText("最近更新:" + new Date().toLocaleString());
		changeViewByState();
	}

	private void onListviewRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}
	public void setonLoadListener(OnLoadListener loadListener) {
		this.loadListener = loadListener;
		isLoadable = true;
	}

	public interface OnLoadListener {
		void onLoad();
	}

	public void onLoadComplete() {
		stateFoot = FOOT_DONE;
		changeViewByState();
	}

	private void onListviewLoad() {
		if (loadListener != null) {
			loadListener.onLoad();
		}
	}
	public void setAdapter(NewsMenuAdapter adapter) {
		mTextLastTime.setText("最近更新:" + new Date().toLocaleString());
		super.setAdapter(adapter);
	}

}