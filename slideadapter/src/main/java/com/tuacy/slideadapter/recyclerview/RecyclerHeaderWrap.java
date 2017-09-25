package com.tuacy.slideadapter.recyclerview;


public class RecyclerHeaderWrap {

	private RecyclerItemNormal     mView;
	private RecyclerHeaderViewBind mBind;

	public RecyclerHeaderWrap(RecyclerItemNormal view, RecyclerHeaderViewBind bind) {
		mView = view;
		mBind = bind;
	}

	public RecyclerItemNormal getView() {
		return mView;
	}

	public void setView(RecyclerItemNormal view) {
		mView = view;
	}

	public RecyclerHeaderViewBind getBind() {
		return mBind;
	}

	public void setBind(RecyclerHeaderViewBind bind) {
		mBind = bind;
	}
}
