package com.tuacy.slideadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;


public class SlideItemLayout extends HorizontalScrollView implements SlideItemAction {

	private int     mLeftWidth;
	private int     mRightWidth;
	private boolean mIsOpen;

	public SlideItemLayout(Context context) {
		this(context, null);
	}

	public SlideItemLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setOverScrollMode(View.OVER_SCROLL_NEVER);
		setHorizontalScrollBarEnabled(false);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		scrollTo(mLeftWidth, 0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		closeOpenMenu();
		if (getSlidingItem() != null && getSlidingItem() != this) {
			return false;
		}
		setScrollingItem(this);
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				closeOpenMenu();
				break;
			case MotionEvent.ACTION_UP:
				setScrollingItem(null);
				int scrollX = getScrollX();
				if (scrollX < mLeftWidth / 2) {
					openLeftMenu();
				}
				if (scrollX >= mLeftWidth / 2 && scrollX <= mLeftWidth + mRightWidth / 2) {
					close();
				}
				if (scrollX > mLeftWidth + mRightWidth / 2) {
					openRightMenu();
				}
				return false;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean isOpen() {
		return mIsOpen;
	}

	@Override
	public void setLeftMenuWidth(int width) {
		mLeftWidth = width;
	}

	@Override
	public void openLeftMenu() {
		mIsOpen = true;
		this.smoothScrollTo(0, 0);
		if (getAdapter() != null) {
			getAdapter().setOpenItem(this);
		}
	}

	@Override
	public void setRightMenuWidth(int width) {
		mRightWidth = width;
	}

	@Override
	public void openRightMenu() {
		mIsOpen = true;
		this.smoothScrollBy(mRightWidth + mLeftWidth + mRightWidth, 0);
		if (getAdapter() != null) {
			getAdapter().setOpenItem(this);
		}
	}

	@Override
	public void close() {
		mIsOpen = false;
		this.smoothScrollTo(mLeftWidth, 0);
	}

	@Override
	public SlideAdapterAction getAdapter() {
		View view = this;
		while (true) {
			view = (View) view.getParent();
			if (view == null) {
				break;
			}
			if (view instanceof RecyclerView || view instanceof AbsListView) {
				break;
			}
		}
		if (view == null) {
			return null;
		}
		if (view instanceof RecyclerView) {
			return (SlideAdapterAction) ((RecyclerView) view).getAdapter();
		} else {
			return (SlideAdapterAction) ((AbsListView) view).getAdapter();
		}
	}

	private void closeOpenMenu() {
		if (!isOpen() && getAdapter() != null) {
			getAdapter().closeOpenItem();
		}
	}

	private SlideItemAction getSlidingItem() {
		if (getAdapter() != null) {
			return getAdapter().getSlidingItem();
		}
		return null;
	}

	private void setScrollingItem(SlideItemAction item) {
		if (getAdapter() != null) {
			getAdapter().setSlidingItem(item);
		}
	}
}
