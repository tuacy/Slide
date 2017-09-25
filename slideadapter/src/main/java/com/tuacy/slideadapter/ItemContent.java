package com.tuacy.slideadapter;

/**
 * 有左右滑动
 */

public class ItemContent {

	private int   mContentLayoutId;
	private int   mLeftLayoutId;
	private float mLeftLayoutRatio;
	private int   mRightLayoutId;
	private float mRightLayoutRatio;

	public ItemContent(int contentLayoutId, int leftLayoutId, float leftLayoutRatio, int rightLayoutId, float rightLayoutRatio) {
		mContentLayoutId = contentLayoutId;
		mLeftLayoutId = leftLayoutId;
		mLeftLayoutRatio = leftLayoutRatio;
		mRightLayoutId = rightLayoutId;
		mRightLayoutRatio = rightLayoutRatio;
	}

	public int getContentLayoutId() {
		return mContentLayoutId;
	}

	public int getLeftLayoutId() {
		return mLeftLayoutId;
	}

	public float getLeftLayoutRatio() {
		return mLeftLayoutRatio;
	}

	public int getRightLayoutId() {
		return mRightLayoutId;
	}

	public float getRightLayoutRatio() {
		return mRightLayoutRatio;
	}
}
