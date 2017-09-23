package com.tuacy.slideadapter;


public interface SlideItemAction {

	SlideAdapterAction getAdapter();

	boolean isOpen();

	void setLeftMenuWidth(int width);

	void openLeftMenu();

	void setRightMenuWidth(int width);

	void openRightMenu();

	void close();

}
