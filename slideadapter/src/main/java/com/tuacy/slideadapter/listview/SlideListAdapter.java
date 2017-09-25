package com.tuacy.slideadapter.listview;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.tuacy.slideadapter.R;
import com.tuacy.slideadapter.SlideAdapterAction;
import com.tuacy.slideadapter.SlideItemLayoutAction;
import com.tuacy.slideadapter.ItemContent;
import com.tuacy.slideadapter.ItemType;

import java.util.ArrayList;
import java.util.List;


public class SlideListAdapter<T> extends BaseAdapter implements SlideAdapterAction {

	/**
	 * 数据源
	 */
	private List<T>                  mData;
	/**
	 * 所有的content view
	 */
	private List<ListContentWrap<T>> mContentViewWraps;
	/**
	 * content item view 对应的type
	 */
	private ItemType<T>              mItemType;
	/**
	 * 对应的AbsListView
	 */
	private AbsListView              mListView;
	/**
	 * 已经打开的item
	 */
	private SlideItemLayoutAction    mOpenedItem;
	/**
	 * 正在控制的item
	 */
	private SlideItemLayoutAction    mSlidingItem;

	private SlideListAdapter(final Builder<T> build, final AbsListView listView) {
		this.mContentViewWraps = build.mContentViewWraps;
		this.mItemType = build.mContentItemType;
		this.mData = build.mData;
		this.mListView = listView;
		mListView.setAdapter(this);
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public T getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return mItemType == null ? 1 : mItemType.getContentType(mData.get(position), position);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemHolder holder;
		if (convertView == null) {
			holder = ListItemHolder.create(parent.getContext(), parent, getContentWrapByType(getItemViewType(position)).getView());
			convertView = holder.getItemView();
			convertView.setTag(holder);
		} else {
			holder = (ListItemHolder) convertView.getTag();
		}

		View contentView = holder.getContentView();
		LinearLayout.LayoutParams contentParams = (LinearLayout.LayoutParams) contentView.getLayoutParams();
		contentParams.width = getContentViewWidth();
		contentView.setLayoutParams(contentParams);
		initContentMenuView(holder, position);
		if (mContentViewWraps != null && mContentViewWraps.size() > 0) {
			getContentWrapByType(getItemViewType(position)).getBind().onBindContentView(holder, mData.get(position), position);
		}

		return convertView;
	}

	private ListContentWrap<T> getContentWrapByType(int type) {
		if (mContentViewWraps == null || mContentViewWraps.size() == 0) {
			throw new NullPointerException("type no match");
		}
		for (int index = 0; index < mContentViewWraps.size(); index++) {
			ListContentWrap<T> content = mContentViewWraps.get(index);
			if (content.getType() == type) {
				return content;
			}
		}
		throw new NullPointerException("type no match");
	}

	/**
	 * 获取content view的宽度
	 *
	 * @return 宽度
	 */
	private int getContentViewWidth() {
		if (mListView == null) {
			throw new NullPointerException("AbsListView should set");
		}
		int recyclerViewPadding = mListView.getPaddingLeft() + mListView.getPaddingRight();
		return mListView.getWidth() - recyclerViewPadding;
	}

	private void initContentMenuView(ListItemHolder holder, int position) {
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

		List<T>                  mData;
		ItemType<T>              mContentItemType;
		List<ListContentWrap<T>> mContentViewWraps;

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
							ListContentWrap.ContentViewBind<T> bind) {
			this.item(itemLayoutId, leftMenuLayoutId, leftMenuRatio, rightMenuLayoutId, rightMenuRatio, bind, 1);
			return this;
		}

		public Builder item(@NonNull int itemLayoutId,
							@NonNull int leftMenuLayoutId,
							@NonNull float leftMenuRatio,
							@NonNull int rightMenuLayoutId,
							@NonNull float rightMenuRatio,
							ListContentWrap.ContentViewBind<T> bind,
							int type) {
			if (mContentViewWraps == null) {
				mContentViewWraps = new ArrayList<>();
			}
			mContentViewWraps.add(
				new ListContentWrap<>(new ItemContent(itemLayoutId, leftMenuLayoutId, leftMenuRatio, rightMenuLayoutId, rightMenuRatio),
									  bind, type));
			return this;
		}

		public Builder type(@NonNull ItemType<T> itemType) {
			this.mContentItemType = itemType;
			return this;
		}

		public SlideListAdapter into(@NonNull AbsListView absListView) {
			return new SlideListAdapter<>(this, absListView);
		}
	}
}
