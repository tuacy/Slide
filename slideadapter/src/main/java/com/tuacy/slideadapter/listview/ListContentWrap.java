package com.tuacy.slideadapter.listview;


import com.tuacy.slideadapter.ItemContent;

public class ListContentWrap<T> {

	public interface ContentViewBind<T> {

		/**
		 * bind content view 对应的数据
		 *
		 * @param holder   content view 对应的holder
		 * @param t        content view 原始数据
		 * @param position position
		 */
		void onBindContentView(ListItemHolder holder, T t, int position);

	}

	private ItemContent        mView;
	private ContentViewBind<T> mBind;
	private int                mType;

	public ListContentWrap(ItemContent view, ContentViewBind<T> bind) {
		this(view, bind, 1);
	}

	public ListContentWrap(ItemContent view, ContentViewBind<T> bind, int type) {
		mView = view;
		mBind = bind;
		mType = type;
	}

	public ItemContent getView() {
		return mView;
	}

	public ContentViewBind<T> getBind() {
		return mBind;
	}

	public int getType() {
		return mType;
	}

}
