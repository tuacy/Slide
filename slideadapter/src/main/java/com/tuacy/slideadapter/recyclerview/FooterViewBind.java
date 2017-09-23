package com.tuacy.slideadapter.recyclerview;


public interface FooterViewBind {

	/**
	 * bind footer view 对应的数据
	 *
	 * @param footer footer view 对应的holder
	 * @param index  footer view 的index
	 */
	void onBindFooterView(ItemHolder footer, int index);

}
