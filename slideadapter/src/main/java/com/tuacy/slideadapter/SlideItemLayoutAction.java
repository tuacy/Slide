package com.tuacy.slideadapter;


public interface SlideItemLayoutAction {

	SlideAdapterAction getSlideAdapter();

	boolean isMenuOpen();

	void setLeftMenuWidth(int width);

	void openLeftMenu();

	void setRightMenuWidth(int width);

	void openRightMenu();

	void closeMenu();

}
