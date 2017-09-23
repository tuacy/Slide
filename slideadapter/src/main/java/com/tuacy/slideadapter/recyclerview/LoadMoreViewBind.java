package com.tuacy.slideadapter.recyclerview;


public interface LoadMoreViewBind {

	/**
	 * load more view 数据绑定
	 *
	 * @param holder   holder
	 * @param isNoMore 是否全部加载完成
	 */
	void onBindLoadMoreView(ItemHolder holder, boolean isNoMore);

}
