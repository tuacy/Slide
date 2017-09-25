package com.tuacy.slideadapter.recyclerview;


public class RecyclerFooterWrap {

	public interface FooterViewBind {

		/**
		 * bind footer view 对应的数据
		 *
		 * @param footer footer view 对应的holder
		 * @param index  footer view 的index
		 */
		void onBindFooterView(RecyclerItemHolder footer, int index);

	}

	private RecyclerItemNormal mView;
	private FooterViewBind     mBind;

	public RecyclerFooterWrap(RecyclerItemNormal view, FooterViewBind bind) {
		mView = view;
		mBind = bind;
	}

	public RecyclerItemNormal getView() {
		return mView;
	}


	public FooterViewBind getBind() {
		return mBind;
	}

}
