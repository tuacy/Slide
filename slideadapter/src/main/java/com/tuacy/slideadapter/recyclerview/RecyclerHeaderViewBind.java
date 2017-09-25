package com.tuacy.slideadapter.recyclerview;


public interface RecyclerHeaderViewBind {

	/**
	 * bind header view 对应的数据
	 *
	 * @param header header view 对应的holder
	 * @param index  header view 的index
	 */
	void onBindHeaderView(RecyclerItemHolder header, int index);

}
