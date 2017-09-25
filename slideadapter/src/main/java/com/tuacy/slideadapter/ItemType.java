package com.tuacy.slideadapter;

/**
 * 指定item type的生成规则(注意:生成的item type 范围 1~10000)
 */

public interface ItemType<T> {

	int getContentType(T t, int position);

}
