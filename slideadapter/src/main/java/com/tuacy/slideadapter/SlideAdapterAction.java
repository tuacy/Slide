package com.tuacy.slideadapter;

public interface SlideAdapterAction {

	/**
	 * 设置侧滑显示菜单的item
	 *
	 * @param item open item
	 */
	void setOpenSlideItem(SlideItemLayoutAction item);

	/**
	 * 获取侧滑显示菜单的item
	 *
	 * @return open item
	 */
	SlideItemLayoutAction getOpenSlideItem();

	/**
	 * 设置正在侧滑操作的item
	 *
	 * @param item active slide item
	 */
	void setActiveSlideItem(SlideItemLayoutAction item);

	/**
	 * 获取正在侧滑操作的item
	 *
	 * @return active slide item
	 */
	SlideItemLayoutAction getActiveSlideItem();

	/**
	 * 关闭侧滑显示的菜单item
	 */
	void closeOpenSlideItem();

}
