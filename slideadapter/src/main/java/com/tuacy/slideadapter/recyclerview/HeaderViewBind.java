package com.tuacy.slideadapter.recyclerview;


public interface HeaderViewBind {

	/**
	 * bind header view 对应的数据
	 *
	 * @param header header view 对应的holder
	 * @param index  header view 的index
	 */
	void onBindHeaderView(ItemHolder header, int index);

}
