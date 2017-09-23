package com.tuacy.slideadapter.recyclerview;

/**
 * 获取content位置对应的type
 */

public interface ItemType<T> {

	int getContentType(T t, int position);

}
