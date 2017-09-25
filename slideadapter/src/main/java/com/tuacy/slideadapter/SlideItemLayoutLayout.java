package com.tuacy.slideadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;


public class SlideItemLayoutLayout extends HorizontalScrollView implements SlideItemLayoutAction {

	private int     mLeftWidth;
	private int     mRightWidth;
	private boolean mIsOpen;

	public SlideItemLayoutLayout(Context context) {
		this(context, null);
	}

	public SlideItemLayoutLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideItemLayoutLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
	public void computeScroll() {
		super.computeScroll();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (getSlideAdapter().getOpenSlideItem() != null && getSlideAdapter().getOpenSlideItem() != this) {
			closeOpenMenu();
//			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
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
					closeMenu();
				}
				if (scrollX > mLeftWidth + mRightWidth / 2) {
					openRightMenu();
				}
				return false;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean isMenuOpen() {
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
		if (getSlideAdapter() != null) {
			getSlideAdapter().setOpenSlideItem(this);
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
		if (getSlideAdapter() != null) {
			getSlideAdapter().setOpenSlideItem(this);
		}
	}

	@Override
	public void closeMenu() {
		mIsOpen = false;
		this.smoothScrollTo(mLeftWidth, 0);
	}

	@Override
	public SlideAdapterAction getSlideAdapter() {
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
		if (!isMenuOpen() && getSlideAdapter() != null) {
			getSlideAdapter().closeOpenSlideItem();
		}
	}

	private SlideItemLayoutAction getSlidingItem() {
		if (getSlideAdapter() != null) {
			return getSlideAdapter().getActiveSlideItem();
		}
		return null;
	}

	private void setScrollingItem(SlideItemLayoutAction item) {
		if (getSlideAdapter() != null) {
			getSlideAdapter().setActiveSlideItem(item);
		}
	}
}
