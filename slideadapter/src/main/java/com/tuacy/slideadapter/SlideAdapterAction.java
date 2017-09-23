package com.tuacy.slideadapter;

public interface SlideAdapterAction {

	void setOpenItem(SlideItemAction item);

	SlideItemAction getOpenItem();

	void setSlidingItem(SlideItemAction item);

	SlideItemAction getSlidingItem();

	void closeOpenItem();

}
