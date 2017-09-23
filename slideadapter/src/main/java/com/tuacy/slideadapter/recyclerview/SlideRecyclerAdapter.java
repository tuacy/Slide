package com.tuacy.slideadapter.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tuacy.slideadapter.R;
import com.tuacy.slideadapter.SlideAdapterAction;
import com.tuacy.slideadapter.SlideItemAction;

import java.util.ArrayList;
import java.util.List;

/**
 * item view 分为三种(header item view、content item view、footer item view)
 */
public class SlideRecyclerAdapter<T> extends RecyclerView.Adapter<ItemHolder> implements SlideAdapterAction {

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
	 * 所有的header view
	 */
	private List<ItemNormal>   mHeaderViews;
	/**
	 * 所有的content view
	 */
	private List<ItemContent>  mContentViews;
	/**
	 * 所有的footer view
	 */
	private List<ItemNormal>   mFooterViews;
	/**
	 * 加载更多的view
	 */
	private ItemNormal         mLoadMoreView;
	/**
	 * content item view 对应的type
	 */
	private ItemType<T>        mItemType;
	/**
	 * 数据源
	 */
	private List<T>            mData;
	/**
	 * 绑定content item数据
	 */
	private ContentViewBind<T> mContentViewBind;
	/**
	 * 绑定header item数据
	 */
	private HeaderViewBind     mHeaderViewBind;
	/**
	 * 绑定footer item数据
	 */
	private FooterViewBind     mFooterViewBind;
	/**
	 * item之间的间距
	 */
	private int                mItemPadding;
	/**
	 * 对应的RecyclerView
	 */
	private RecyclerView       mRecycleView;
	/**
	 * 已经打开的item
	 */
	private SlideItemAction    mOpenedItem;
	/**
	 * 正在控制的item
	 */
	private SlideItemAction    mSlidingItem;
	/**
	 * 加载更多的item holder
	 */
	private ItemHolder         mLoadMoreHolder;
	/**
	 * 绑定load more view控件
	 */
	private LoadMoreViewBind   mLoadMoreViewBind;
	/**
	 * 更多数据是否加载完成
	 */
	private boolean            mIsNoMore;
	/**
	 * 加载更多监听
	 */
	private LoadMoreListener   mLoadMoreListener;
	/**
	 * 是否正在加载更多
	 */
	private boolean            mLoadingMore;

	private SlideRecyclerAdapter(final Builder<T> build, final RecyclerView recyclerView) {
		this.mContentViews = build.mItemContentViews;
		this.mContentViewBind = build.mContentViewBind;
		this.mItemType = build.mContentItemType;
		this.mData = build.mData;
		this.mHeaderViews = build.mHeaderViews;
		this.mFooterViews = build.mFooterViews;
		this.mHeaderViewBind = build.mHeaderViewBind;
		this.mFooterViewBind = build.mFooterViewBind;
		this.mLoadMoreListener = build.mLoadMoreListener;
		this.mLoadMoreViewBind = build.mLoadMoreViewBind;
		this.mItemPadding = build.mItemPadding;
		this.mLoadMoreView = build.mLoadMoreView;
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
				closeOpenItem();
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
		if (mLoadMoreListener != null && !mLoadingMore) {
			mLoadingMore = true;
			mLoadMoreListener.onLoadMore(mLoadMoreHolder, this);
		}
	}

	@Override
	public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (isLoadMoreViewType(viewType)) {
			return ItemHolder.create(parent.getContext(), parent, mLoadMoreView);
		}
		if (isHeaderViewType(viewType)) {
			return ItemHolder.create(parent.getContext(), parent, mHeaderViews.get(viewType - TYPE_HEADER_ORIGIN));
		}
		if (isFooterViewType(viewType)) {
			return ItemHolder.create(parent.getContext(), parent, mFooterViews.get(viewType - TYPE_FOOTER_ORIGIN));
		}
		return ItemHolder.create(parent.getContext(), parent, mContentViews.get(viewType - 1));
	}

	@Override
	public void onBindViewHolder(ItemHolder holder, int position) {
		View contentView = holder.getContentView();
		LinearLayout.LayoutParams contentParams = (LinearLayout.LayoutParams) contentView.getLayoutParams();
		if (isLoadMoreViewType(getItemViewType(position))) {
			if (mLoadMoreView.getLayoutHeight() > 0) {
				contentParams.height = mLoadMoreView.getLayoutHeight();
				contentParams.width = getHeaderViewWidth();
				contentView.setLayoutParams(contentParams);
			}
			if (mLoadMoreViewBind != null) {
				mLoadMoreViewBind.onBindLoadMoreView(holder, mIsNoMore);
			}
			return;
		}
		if (isHeaderViewType(getItemViewType(position))) {
			if (mHeaderViews.get(position).getLayoutHeight() > 0) {
				contentParams.height = mHeaderViews.get(position).getLayoutHeight();
				contentParams.width = getHeaderViewWidth();
				contentView.setLayoutParams(contentParams);
			}
			if (mHeaderViewBind != null) {
				mHeaderViewBind.onBindHeaderView(holder, position);
			}
			return;
		}
		if (isFooterViewType(getItemViewType(position))) {
			if (mFooterViews.get(position - getHeaderViewNum() - mData.size()).getLayoutHeight() > 0) {
				contentParams.height = mFooterViews.get(position - getHeaderViewNum() - mData.size()).getLayoutHeight();
				contentParams.width = getHeaderViewWidth();
				contentView.setLayoutParams(contentParams);
			}
			if (mFooterViewBind != null) {
				mFooterViewBind.onBindFooterView(holder, position - getHeaderViewNum() - mData.size());
			}
			if (position == getHeaderViewNum() + mData.size() + getFooterViewNum() - 1) {
				mLoadMoreHolder = holder;
			}
			return;
		}
		contentParams.width = getContentViewWidth();
		contentView.setLayoutParams(contentParams);
		initContentMenuView(holder, position);
		if (mContentViewBind != null) {
			mContentViewBind.onBindContentView(holder, mData.get(position - getHeaderViewNum()), position - getHeaderViewNum());
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

		return mItemType == null || mContentViews.size() == 1 ? 1 :
			   mItemType.getContentType(mData.get(position - getHeaderViewNum()), position - getHeaderViewNum());
	}

	@Override
	public void onViewAttachedToWindow(ItemHolder holder) {
		super.onViewAttachedToWindow(holder);
		ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
		if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
			StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
			int position = holder.getLayoutPosition();
			p.setFullSpan(isHeaderViewType(getItemViewType(position)) || isFooterViewType(getItemViewType(position)));
		}
	}

	private void initContentMenuView(ItemHolder holder, int position) {
		final ItemContent item = mContentViews.get(getItemViewType(position) - 1);
		View rightMenu = holder.getRightView();
		if (rightMenu != null) {
			LinearLayout.LayoutParams rightMenuParams = (LinearLayout.LayoutParams) rightMenu.getLayoutParams();
			rightMenuParams.width = (int) (getContentViewWidth() * item.getRightLayoutRatio());
			rightMenu.setLayoutParams(rightMenuParams);
			((SlideItemAction) holder.getView(R.id.slide_item_parent)).setRightMenuWidth(rightMenuParams.width);
		}
		View leftMenu = holder.getLeftView();
		if (leftMenu != null) {
			LinearLayout.LayoutParams leftMenuParams = (LinearLayout.LayoutParams) leftMenu.getLayoutParams();
			leftMenuParams.width = (int) (getContentViewWidth() * item.getLeftLayoutRatio());
			leftMenu.setLayoutParams(leftMenuParams);
			holder.getView(R.id.slide_item_parent).scrollTo(leftMenuParams.width, 0);
			((SlideItemAction) holder.getView(R.id.slide_item_parent)).setLeftMenuWidth(leftMenuParams.width);
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
			viewWidth = (ScreenSize.w(mRecycleView.getContext()) - recyclerViewPadding -
						 mItemPadding * ((GridLayoutManager) layoutManager).getSpanCount() * 2) /
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
		return mHeaderViews == null ? 0 : mHeaderViews.size();
	}

	/**
	 * 获取footer view的个数
	 *
	 * @return footer view的个数
	 */
	private int getFooterViewNum() {
		return mFooterViews == null ? 0 : mFooterViews.size();
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
		return mLoadMoreView != null && mData != null && mData.size() > 0;
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
	public void setOpenItem(SlideItemAction item) {
		mOpenedItem = item;
	}

	@Override
	public SlideItemAction getOpenItem() {
		return mOpenedItem;
	}

	@Override
	public void setSlidingItem(SlideItemAction item) {
		mSlidingItem = item;
	}

	@Override
	public SlideItemAction getSlidingItem() {
		return null;
	}

	@Override
	public void closeOpenItem() {
		if (mOpenedItem != null && mOpenedItem.isOpen()) {
			mOpenedItem.close();
			mOpenedItem = null;
		}
	}


	public static class Builder<T> {

		List<T>            mData;
		List<ItemContent>  mItemContentViews;
		ContentViewBind<T> mContentViewBind;
		ItemType<T>        mContentItemType;
		List<ItemNormal>   mHeaderViews;
		List<ItemNormal>   mFooterViews;
		HeaderViewBind     mHeaderViewBind;
		FooterViewBind     mFooterViewBind;
		int                mItemPadding;
		ItemNormal         mLoadMoreView;
		LoadMoreViewBind   mLoadMoreViewBind;
		LoadMoreListener   mLoadMoreListener;


		public Builder data(List<T> data) {
			this.mData = data;
			return this;
		}

		public Builder item(@NonNull int itemLayoutId) {
			this.item(itemLayoutId, 0, 0, 0, 0);
			return this;
		}

		public Builder item(@NonNull int itemLayoutId,
							@NonNull int leftMenuLayoutId,
							@NonNull float leftMenuRatio,
							@NonNull int rightMenuLayoutId,
							@NonNull float rightMenuRatio) {
			if (mItemContentViews == null) {
				mItemContentViews = new ArrayList<>();
			}
			mItemContentViews.add(new ItemContent(itemLayoutId, leftMenuLayoutId, leftMenuRatio, rightMenuLayoutId, rightMenuRatio));
			return this;
		}


		public Builder header(@NonNull int layoutId) {
			this.header(layoutId, 0);
			return this;
		}

		public Builder header(@NonNull int layoutId, @NonNull int height) {
			if (mHeaderViews == null) {
				mHeaderViews = new ArrayList<>();
			}
			mHeaderViews.add(new ItemNormal(layoutId, height));
			return this;
		}

		public Builder footer(@NonNull int layoutId) {
			this.footer(layoutId, 0);
			return this;
		}

		public Builder footer(@NonNull int layoutId, @NonNull int height) {
			if (mFooterViews == null) {
				mFooterViews = new ArrayList<>();
			}
			mFooterViews.add(new ItemNormal(layoutId, height));
			return this;
		}

		public Builder loadMore(@NonNull int layoutId, @NonNull int height, @NonNull LoadMoreListener loadMoreListener) {
			mLoadMoreView = new ItemNormal(layoutId, height);
			mLoadMoreListener = loadMoreListener;
			return this;
		}

		public Builder padding(int itemPadding) {
			this.mItemPadding = itemPadding;
			return this;
		}

		public Builder bind(@NonNull ContentViewBind<T> contentViewBind) {
			this.mContentViewBind = contentViewBind;
			return this;
		}

		public Builder bind(@NonNull HeaderViewBind headerViewBind) {
			this.mHeaderViewBind = headerViewBind;
			return this;
		}

		public Builder bind(@NonNull FooterViewBind footerViewBind) {
			this.mFooterViewBind = footerViewBind;
			return this;
		}

		public Builder bind(@NonNull LoadMoreViewBind loadMoreViewBind) {
			this.mLoadMoreViewBind = loadMoreViewBind;
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
