package com.tuacy.slideadapter.recyclerview;


public interface ContentViewBind<T> {

	/**
	 * bind content view 对应的数据
	 *
	 * @param holder   content view 对应的holder
	 * @param t        content view 原始数据
	 * @param position position
	 */
	void onBindContentView(ItemHolder holder, T t, int position);

}
