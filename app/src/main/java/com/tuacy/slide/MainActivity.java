package com.tuacy.slide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tuacy.slideadapter.recyclerview.FooterViewBind;
import com.tuacy.slideadapter.recyclerview.HeaderViewBind;
import com.tuacy.slideadapter.recyclerview.ContentViewBind;
import com.tuacy.slideadapter.recyclerview.ItemHolder;
import com.tuacy.slideadapter.recyclerview.SlideRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private RecyclerView mRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initEvent();
		initData();
	}

	private void initView() {
		mRecyclerView = (RecyclerView) findViewById(R.id.recycler_slide);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
	}

	private void initEvent() {

	}

	private void initData() {

		List<String> data = new ArrayList<>();
		data.add("Android");
		data.add("Python");
		data.add("Java");
		data.add("Android");
		data.add("Python");
		data.add("Java");
		data.add("Android");
		data.add("Python");
		data.add("Java");
		data.add("Android");
		data.add("Python");
		data.add("Java");

		ContentViewBind<String> contentViewBind = new ContentViewBind<String>() {
			@Override
			public void onBindContentView(final ItemHolder itemView, String data, int position) {
				itemView.setText(R.id.textView, data);
			}
		};

		SlideRecyclerAdapter adapter = new SlideRecyclerAdapter.Builder<String>().data(data)
																				 .item(R.layout.item_content_1, 0, 0, R.layout.item_menu_1,
																					   0.35f)
																				 .padding(1)
																				 .header(R.layout.item_head, 100)
																				 .footer(R.layout.item_foot, 100)
																				 .loadMore(R.layout.item_load_more, 100, null)
																				 .bind(contentViewBind)
																				 .bind(new HeaderViewBind() {
																					 @Override
																					 public void onBindHeaderView(ItemHolder header,
																												  int order) {

																					 }
																				 })
																				 .bind(new FooterViewBind() {
																					 @Override
																					 public void onBindFooterView(ItemHolder footer,
																												  int order) {

																					 }
																				 })
																				 .into(mRecyclerView);

	}
}
