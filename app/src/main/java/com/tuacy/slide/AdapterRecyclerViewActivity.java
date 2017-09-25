package com.tuacy.slide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.tuacy.slideadapter.recyclerview.RecyclerContentWrap;
import com.tuacy.slideadapter.recyclerview.RecyclerItemHolder;
import com.tuacy.slideadapter.ItemType;
import com.tuacy.slideadapter.recyclerview.SlideRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdapterRecyclerViewActivity extends AppCompatActivity {

	public static void startUp(Context context) {
		context.startActivity(new Intent(context, AdapterRecyclerViewActivity.class));
	}

	private RecyclerView mRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recyclerview);
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

		RecyclerContentWrap.ContentViewBind<String> contentViewBind = new RecyclerContentWrap.ContentViewBind<String>() {
			@Override
			public void onBindContentView(final RecyclerItemHolder itemView, String data, int position) {
				itemView.setText(R.id.textView, data);
				itemView.getView(R.id.textView).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d("tuacy", "on click view");
					}
				});
				itemView.getView(R.id.like).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d("tuacy", "on click like view");
						itemView.closeMenu();
					}
				});
			}
		};

		ItemType<String> itemType = new ItemType<String>() {
			@Override
			public int getContentType(String s, int position) {
				return position % 5 == 0 ? 10 : 20;
			}
		};

		SlideRecyclerAdapter adapter = new SlideRecyclerAdapter.Builder<String>().data(data)
																				 .item(R.layout.item_content_type_1, 0, 0,
																					   R.layout.item_menu_1, 0.35f, contentViewBind, 10)
																				 .item(R.layout.item_content_type_2, R.layout.item_menu_2,
																					   0.35f, R.layout.item_menu_1, 0.35f, contentViewBind,
																					   20)
																				 .type(itemType)
																				 .padding(1)
																				 .header(R.layout.item_head, 100, null)
																				 .footer(R.layout.item_foot, 100, null)
																				 .loadMore(R.layout.item_load_more, 100, null, null)
																				 .into(mRecyclerView);

	}
}
