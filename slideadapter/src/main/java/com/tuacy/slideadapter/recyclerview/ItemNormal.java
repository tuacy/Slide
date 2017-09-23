package com.tuacy.slideadapter.recyclerview;

/**
 * 没有左右滑动的item(header footer)
 */

public class ItemNormal {

	private int mLayoutId;
	private int mLayoutHeight;

	public ItemNormal(int layoutId, int layoutHeight) {
		mLayoutId = layoutId;
		mLayoutHeight = layoutHeight;
	}

	public int getLayoutId() {
		return mLayoutId;
	}

	public int getLayoutHeight() {
		return mLayoutHeight;
	}
}
