package com.tuacy.slideadapter.recyclerview;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuacy.slideadapter.R;


public class ItemHolder extends RecyclerView.ViewHolder {

	private SparseArray<View> mViews;
	private View              mContent;
	private View              mLeftMenu;
	private View              mRightMenu;

	private ItemHolder(View itemView, View content, View leftMenu, View rightMenu) {
		super(itemView);
		mContent = content;
		mLeftMenu = leftMenu;
		mRightMenu = rightMenu;
		mViews = new SparseArray<>();
	}

	public static ItemHolder create(@NonNull Context context, @NonNull ViewGroup parent, @NonNull ItemNormal normal) {
		return create(context, parent, new ItemContent(normal.getLayoutId(), 0, 0, 0, 0));
	}

	public static ItemHolder create(@NonNull Context context, @NonNull ViewGroup parent, @NonNull final ItemContent slide) {
		final View itemView = LayoutInflater.from(context).inflate(R.layout.item_slide_content, parent, false);
		LinearLayout linearLayout = (LinearLayout) itemView.findViewById(R.id.layout_slide_item_content);
		View leftView = null;
		View rightView = null;
		if (slide.getLeftLayoutId() != 0) {
			leftView = LayoutInflater.from(context).inflate(slide.getLeftLayoutId(), linearLayout, false);
			linearLayout.addView(linearLayout);
		}
		if (slide.getContentLayoutId() == 0) {
			throw new NullPointerException("please set the content view layout id");
		}
		View contentView = LayoutInflater.from(context).inflate(slide.getContentLayoutId(), linearLayout, false);
		linearLayout.addView(contentView);
		if (slide.getRightLayoutId() != 0) {
			rightView = LayoutInflater.from(context).inflate(slide.getRightLayoutId(), linearLayout, false);
			linearLayout.addView(rightView);
		}
		return new ItemHolder(itemView, contentView, leftView, rightView);
	}

	public View getContentView() {
		return mContent;
	}

	public View getLeftView() {
		return mLeftMenu;
	}

	public View getRightView() {
		return mRightMenu;
	}

	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = itemView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	public ItemHolder setText(int viewId, String text) {
		TextView textView = getView(viewId);
		if (textView != null) {
			textView.setText(text);
		}
		return this;
	}
}
