package com.tuacy.slideadapter.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tuacy.slideadapter.ItemContent;
import com.tuacy.slideadapter.ItemType;
import com.tuacy.slideadapter.R;
import com.tuacy.slideadapter.SlideAdapterAction;
import com.tuacy.slideadapter.SlideItemLayoutAction;

import java.util.ArrayList;
import java.util.List;

/**
 * item view 分为三种(header item view、content item view、footer item view)
 */
public class SlideRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerItemHolder> implements SlideAdapterAction {

	/**
	 * header view type的开始值(header view 可能有多个)
	 */
	private static final int TYPE_HEADER_ORIGIN = 1001;
	/**
	 * footer view type的开始值(footer view 可能有多个)
	 */
	private static final int TYPE_FOOTER_ORIGIN = 2001;
	/**
	 * footer view type的开始值(footer view 可能有多个)
	 */
	private static final int TYPE_LOAD_MORE     = 3001;

	/**
	 * 所有的header view + bind
	 */
	private List<RecyclerHeaderWrap>     mHeaderViewWraps;
	/**
	 * 所有的content view
	 */
	private List<RecyclerContentWrap<T>> mContentViewWraps;
	/**
	 * 所有的footer view
	 */
	private List<RecyclerFooterWrap>     mFooterViewWraps;
	/**
	 * 更多数据是否加载完成
	 */
	private boolean                      mIsNoMore;
	/**
	 * 加载更多的view
	 */
	private RecyclerLoadMoreWrap         mRecyclerLoadMoreWrap;
	/**
	 * content item view 对应的type
	 */
	private ItemType<T>                  mItemType;
	/**
	 * 数据源
	 */
	private List<T>                      mData;
	/**
	 * item之间的间距
	 */
	private int                          mItemPadding;
	/**
	 * 对应的RecyclerView
	 */
	private RecyclerView                 mRecycleView;
	/**
	 * 已经打开的item
	 */
	private SlideItemLayoutAction        mOpenedItem;
	/**
	 * 正在控制的item
	 */
	private SlideItemLayoutAction        mSlidingItem;
	/**
	 * 加载更多的item holder
	 */
	private RecyclerItemHolder           mLoadMoreHolder;

	/**
	 * 是否正在加载更多
	 */
	private boolean mLoadingMore;

	private SlideRecyclerAdapter(final Builder<T> build, final RecyclerView recyclerView) {
		this.mContentViewWraps = build.mContentViewWraps;
		this.mItemType = build.mContentItemType;
		this.mData = build.mData;
		this.mHeaderViewWraps = build.mHeaderViewWraps;
		this.mFooterViewWraps = build.mFooterViewWraps;
		this.mItemPadding = build.mItemPadding;
		this.mRecyclerLoadMoreWrap = build.mRecyclerLoadMoreWrap;
		this.mRecycleView = recyclerView;
		init();
	}

	private void init() {
		mRecycleView.setAdapter(this);
		mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				mSlidingItem = null;
				closeOpenSlideItem();
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					if (!recyclerView.canScrollVertically(1) && isShowLoadMoreView()) {
						/**
						 * 不能在向上滑动了，加载更多
						 */
						onLoadMoreAction();
					}
				}
			}
		});
		if (mItemPadding > 0) {
			mRecycleView.addItemDecoration(new SlideItemDecoration(mItemPadding));
		}
		final RecyclerView.LayoutManager layoutManager = mRecycleView.getLayoutManager();
		if (layoutManager instanceof GridLayoutManager) {
			((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
				@Override
				public int getSpanSize(int position) {
					return isHeaderViewType(getItemViewType(position)) || isFooterViewType(getItemViewType(position)) ?
						   ((GridLayoutManager) layoutManager).getSpanCount() : 1;
				}
			});
		}
	}


	/**
	 * 滑动了底部，加载更多
	 */
	private void onLoadMoreAction() {
		if (mRecyclerLoadMoreWrap != null && mRecyclerLoadMoreWrap.getListener() != null && !mLoadingMore) {
			mLoadingMore = true;
			mRecyclerLoadMoreWrap.getListener().onLoadMore(mLoadMoreHolder, this);
		}
	}

	@Override
	public RecyclerItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (isLoadMoreViewType(viewType)) {
			return RecyclerItemHolder.create(parent.getContext(), parent, mRecyclerLoadMoreWrap.getView());
		}
		if (isHeaderViewType(viewType)) {
			return RecyclerItemHolder.create(parent.getContext(), parent, mHeaderViewWraps.get(viewType - TYPE_HEADER_ORIGIN).getView());
		}
		if (isFooterViewType(viewType)) {
			return RecyclerItemHolder.create(parent.getContext(), parent, mFooterViewWraps.get(viewType - TYPE_FOOTER_ORIGIN).getView());
		}
		return RecyclerItemHolder.create(parent.getContext(), parent, getContentWrapByType(viewType).getView());
	}

	@Override
	public void onBindViewHolder(RecyclerItemHolder holder, int position) {
		View contentView = holder.getContentView();
		LinearLayout.LayoutParams contentParams = (LinearLayout.LayoutParams) contentView.getLayoutParams();
		if (isLoadMoreViewType(getItemViewType(position))) {
			if (mRecyclerLoadMoreWrap.getView().getLayoutHeight() > 0) {
				contentParams.height = mRecyclerLoadMoreWrap.getView().getLayoutHeight();
				contentParams.width = getHeaderViewWidth();
				contentView.setLayoutParams(contentParams);
			}
			if (mRecyclerLoadMoreWrap.getBind() != null) {
				mRecyclerLoadMoreWrap.getBind().onBindLoadMoreView(holder, mIsNoMore);
			}
			return;
		}
		if (isHeaderViewType(getItemViewType(position))) {
			if (mHeaderViewWraps.get(position).getView().getLayoutHeight() > 0) {
				contentParams.height = mHeaderViewWraps.get(position).getView().getLayoutHeight();
				contentParams.width = getHeaderViewWidth();
				contentView.setLayoutParams(contentParams);
			}
			if (mHeaderViewWraps.get(position).getBind() != null) {
				mHeaderViewWraps.get(position).getBind().onBindHeaderView(holder, position);
			}
			return;
		}
		if (isFooterViewType(getItemViewType(position))) {
			if (mFooterViewWraps.get(position - getHeaderViewNum() - mData.size()).getView().getLayoutHeight() > 0) {
				contentParams.height = mFooterViewWraps.get(position - getHeaderViewNum() - mData.size()).getView().getLayoutHeight();
				contentParams.width = getHeaderViewWidth();
				contentView.setLayoutParams(contentParams);
			}
			if (mFooterViewWraps.get(position - getHeaderViewNum() - mData.size()).getBind() != null) {
				mFooterViewWraps.get(position - getHeaderViewNum() - mData.size())
								.getBind()
								.onBindFooterView(holder, position - getHeaderViewNum() - mData.size());
			}
			if (position == getHeaderViewNum() + mData.size() + getFooterViewNum() - 1) {
				mLoadMoreHolder = holder;
			}
			return;
		}
		contentParams.width = getContentViewWidth();
		contentView.setLayoutParams(contentParams);
		initContentMenuView(holder, position);
		if (mContentViewWraps != null && mContentViewWraps.size() > 0) {
			getContentWrapByType(getItemViewType(position)).getBind()
														   .onBindContentView(holder, mData.get(position - getHeaderViewNum()),
																			  position - getHeaderViewNum());
		}
	}

	@Override
	public int getItemCount() {
		int validCount = mData == null ? 0 : mData.size();
		if (isShowLoadMoreView()) {
			return getHeaderViewNum() + validCount + getFooterViewNum() + 1;
		} else {
			return getHeaderViewNum() + validCount + getFooterViewNum();
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (isShowLoadMoreView() && position == getItemCount() - 1) {
			return TYPE_LOAD_MORE;
		}

		if (getHeaderViewNum() > 0 && position < getHeaderViewNum()) {
			return TYPE_HEADER_ORIGIN + position;
		}

		if (getFooterViewNum() > 0 && position >= getHeaderViewNum() + mData.size()) {
			return TYPE_FOOTER_ORIGIN + position - getHeaderViewNum() - mData.size();
		}

		return mItemType == null ? 1 : mItemType.getContentType(mData.get(position - getHeaderViewNum()), position - getHeaderViewNum());
	}

	@Override
	public void onViewAttachedToWindow(RecyclerItemHolder holder) {
		super.onViewAttachedToWindow(holder);
		ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
		if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
			StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
			int position = holder.getLayoutPosition();
			p.setFullSpan(isHeaderViewType(getItemViewType(position)) || isFooterViewType(getItemViewType(position)));
		}
	}

	private RecyclerContentWrap<T> getContentWrapByType(int type) {
		if (mContentViewWraps == null || mContentViewWraps.size() == 0) {
			throw new NullPointerException("type no match");
		}
		for (int index = 0; index < mContentViewWraps.size(); index++) {
			RecyclerContentWrap<T> content = mContentViewWraps.get(index);
			if (content.getType() == type) {
				return content;
			}
		}
		throw new NullPointerException("type no match");
	}

	private void initContentMenuView(RecyclerItemHolder holder, int position) {
		final ItemContent item = getContentWrapByType(getItemViewType(position)).getView();
		View rightMenu = holder.getRightView();
		if (rightMenu != null) {
			LinearLayout.LayoutParams rightMenuParams = (LinearLayout.LayoutParams) rightMenu.getLayoutParams();
			rightMenuParams.width = (int) (getContentViewWidth() * item.getRightLayoutRatio());
			rightMenu.setLayoutParams(rightMenuParams);
			((SlideItemLayoutAction) holder.getView(R.id.slide_item_parent)).setRightMenuWidth(rightMenuParams.width);
		}
		View leftMenu = holder.getLeftView();
		if (leftMenu != null) {
			LinearLayout.LayoutParams leftMenuParams = (LinearLayout.LayoutParams) leftMenu.getLayoutParams();
			leftMenuParams.width = (int) (getContentViewWidth() * item.getLeftLayoutRatio());
			leftMenu.setLayoutParams(leftMenuParams);
			holder.getView(R.id.slide_item_parent).scrollTo(leftMenuParams.width, 0);
			((SlideItemLayoutAction) holder.getView(R.id.slide_item_parent)).setLeftMenuWidth(leftMenuParams.width);
		}
	}

	/**
	 * 获取header view 和 footer view的宽度(header view 和 footer view的宽度是一样的)
	 *
	 * @return 宽度
	 */
	private int getHeaderViewWidth() {
		if (mRecycleView == null) {
			throw new NullPointerException("RecycleView should set");
		}
		int recyclerViewPadding = mRecycleView.getPaddingLeft() + mRecycleView.getPaddingRight();
		final RecyclerView.LayoutManager layoutManager = mRecycleView.getLayoutManager();
		int headerViewWidth = mRecycleView.getWidth() - recyclerViewPadding;
		if (layoutManager instanceof GridLayoutManager) {
			headerViewWidth -= mItemPadding * 2;
		}
		return headerViewWidth;
	}

	/**
	 * 获取content view的宽度
	 *
	 * @return 宽度
	 */
	private int getContentViewWidth() {
		if (mRecycleView == null) {
			throw new NullPointerException("RecycleView should set");
		}
		int recyclerViewPadding = mRecycleView.getPaddingLeft() + mRecycleView.getPaddingRight();
		final RecyclerView.LayoutManager layoutManager = mRecycleView.getLayoutManager();
		int viewWidth = mRecycleView.getWidth() - recyclerViewPadding;
		if (layoutManager instanceof GridLayoutManager) {
			viewWidth = mRecycleView.getWidth() - recyclerViewPadding -
						mItemPadding * ((GridLayoutManager) layoutManager).getSpanCount() * 2 /
						((GridLayoutManager) layoutManager).getSpanCount();
		}
		return viewWidth;
	}

	/**
	 * 获取header view的个数
	 *
	 * @return header view的个数
	 */
	private int getHeaderViewNum() {
		return mHeaderViewWraps == null ? 0 : mHeaderViewWraps.size();
	}

	/**
	 * 获取footer view的个数
	 *
	 * @return footer view的个数
	 */
	private int getFooterViewNum() {
		return mFooterViewWraps == null ? 0 : mFooterViewWraps.size();
	}

	/**
	 * 对应的type 是否是header view
	 *
	 * @param viewType type
	 * @return 是否是header
	 */
	private boolean isHeaderViewType(int viewType) {
		return viewType >= TYPE_HEADER_ORIGIN && viewType < TYPE_FOOTER_ORIGIN;
	}

	/**
	 * 对应的type 是否是footer view
	 *
	 * @param viewType type
	 * @return 是否是footer
	 */
	private boolean isFooterViewType(int viewType) {
		return viewType >= TYPE_FOOTER_ORIGIN;
	}

	/**
	 * 是否显示load more view
	 *
	 * @return 是否显示load more view
	 */
	private boolean isShowLoadMoreView() {
		return mRecyclerLoadMoreWrap != null && mData != null && mData.size() > 0;
	}

	/**
	 * 对应的type 是否是load more view
	 *
	 * @param viewType type
	 * @return 是否是load more view
	 */
	private boolean isLoadMoreViewType(int viewType) {
		return viewType == TYPE_LOAD_MORE;
	}

	@Override
	public void setOpenSlideItem(SlideItemLayoutAction item) {
		mOpenedItem = item;
	}

	@Override
	public SlideItemLayoutAction getOpenSlideItem() {
		return mOpenedItem;
	}

	@Override
	public void setActiveSlideItem(SlideItemLayoutAction item) {
		mSlidingItem = item;
	}

	@Override
	public SlideItemLayoutAction getActiveSlideItem() {
		return mSlidingItem;
	}

	@Override
	public void closeOpenSlideItem() {
		if (mOpenedItem != null && mOpenedItem.isMenuOpen()) {
			mOpenedItem.closeMenu();
		}
		mOpenedItem = null;
	}


	public static class Builder<T> {

		List<T>                      mData;
		ItemType<T>                  mContentItemType;
		List<RecyclerHeaderWrap>     mHeaderViewWraps;
		List<RecyclerContentWrap<T>> mContentViewWraps;
		List<RecyclerFooterWrap>     mFooterViewWraps;
		int                          mItemPadding;
		RecyclerLoadMoreWrap         mRecyclerLoadMoreWrap;


		public Builder data(List<T> data) {
			this.mData = data;
			return this;
		}

		public Builder item(@NonNull int itemLayoutId) {
			this.item(itemLayoutId, 0, 0, 0, 0, null, 1);
			return this;
		}

		public Builder item(@NonNull int itemLayoutId, int type) {
			this.item(itemLayoutId, 0, 0, 0, 0, null, type);
			return this;
		}

		public Builder item(@NonNull int itemLayoutId,
							@NonNull int leftMenuLayoutId,
							@NonNull float leftMenuRatio,
							@NonNull int rightMenuLayoutId,
							@NonNull float rightMenuRatio,
							RecyclerContentWrap.ContentViewBind<T> bind) {
			this.item(itemLayoutId, leftMenuLayoutId, leftMenuRatio, rightMenuLayoutId, rightMenuRatio, bind, 1);
			return this;
		}

		public Builder item(@NonNull int itemLayoutId,
							@NonNull int leftMenuLayoutId,
							@NonNull float leftMenuRatio,
							@NonNull int rightMenuLayoutId,
							@NonNull float rightMenuRatio,
							RecyclerContentWrap.ContentViewBind<T> bind,
							int type) {
			if (mContentViewWraps == null) {
				mContentViewWraps = new ArrayList<>();
			}
			mContentViewWraps.add(
				new RecyclerContentWrap<>(new ItemContent(itemLayoutId, leftMenuLayoutId, leftMenuRatio, rightMenuLayoutId, rightMenuRatio),
										  bind, type));
			return this;
		}


		public Builder header(@NonNull int layoutId) {
			this.header(layoutId, 0, null);
			return this;
		}

		public Builder header(@NonNull int layoutId, RecyclerHeaderViewBind bind) {
			this.header(layoutId, 0, bind);
			return this;
		}

		public Builder header(@NonNull int layoutId, @NonNull int height, RecyclerHeaderViewBind bind) {
			if (mHeaderViewWraps == null) {
				mHeaderViewWraps = new ArrayList<>();
			}
			mHeaderViewWraps.add(new RecyclerHeaderWrap(new RecyclerItemNormal(layoutId, height), bind));
			return this;
		}

		public Builder footer(@NonNull int layoutId) {
			this.footer(layoutId, 0, null);
			return this;
		}

		public Builder footer(@NonNull int layoutId, RecyclerFooterWrap.FooterViewBind bind) {
			this.footer(layoutId, 0, bind);
			return this;
		}

		public Builder footer(@NonNull int layoutId, @NonNull int height, RecyclerFooterWrap.FooterViewBind bind) {
			if (mFooterViewWraps == null) {
				mFooterViewWraps = new ArrayList<>();
			}
			mFooterViewWraps.add(new RecyclerFooterWrap(new RecyclerItemNormal(layoutId, height), bind));
			return this;
		}

		public Builder loadMore(@NonNull int layoutId,
								@NonNull int height,
								RecyclerLoadMoreWrap.LoadMoreViewBind bind,
								RecyclerLoadMoreWrap.LoadMoreListener listener) {
			mRecyclerLoadMoreWrap = new RecyclerLoadMoreWrap(new RecyclerItemNormal(layoutId, height), bind, listener);
			return this;
		}

		public Builder padding(int itemPadding) {
			this.mItemPadding = itemPadding;
			return this;
		}


		public Builder type(@NonNull ItemType<T> itemType) {
			this.mContentItemType = itemType;
			return this;
		}

		public SlideRecyclerAdapter into(@NonNull RecyclerView recyclerView) {
			return new SlideRecyclerAdapter<>(this, recyclerView);
		}
	}
}
