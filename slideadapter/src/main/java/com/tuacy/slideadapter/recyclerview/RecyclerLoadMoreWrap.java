package com.tuacy.slideadapter.recyclerview;


public class RecyclerLoadMoreWrap {

	public interface LoadMoreListener {

		void onLoadMore(RecyclerItemHolder holder, SlideRecyclerAdapter adapter);

	}

	public interface LoadMoreViewBind {

		/**
		 * load more view 数据绑定
		 *
		 * @param holder   holder
		 * @param isNoMore 是否全部加载完成
		 */
		void onBindLoadMoreView(RecyclerItemHolder holder, boolean isNoMore);

	}

	private RecyclerItemNormal mView;
	private LoadMoreViewBind   mBind;
	private LoadMoreListener   mListener;

	public RecyclerLoadMoreWrap(RecyclerItemNormal view, LoadMoreViewBind bind, LoadMoreListener listener) {
		mView = view;
		mBind = bind;
		mListener = listener;
	}

	public RecyclerItemNormal getView() {
		return mView;
	}

	public LoadMoreViewBind getBind() {
		return mBind;
	}

	public LoadMoreListener getListener() {
		return mListener;
	}

}
