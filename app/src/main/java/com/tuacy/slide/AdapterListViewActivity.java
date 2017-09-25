package com.tuacy.slide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.tuacy.slideadapter.listview.ListContentWrap;
import com.tuacy.slideadapter.ItemType;
import com.tuacy.slideadapter.listview.ListItemHolder;
import com.tuacy.slideadapter.listview.SlideListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdapterListViewActivity extends AppCompatActivity {

	public static void startUp(Context context) {
		context.startActivity(new Intent(context, AdapterListViewActivity.class));
	}

	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		initView();
		initEvent();
		initData();
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.list_slide);
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

		ListContentWrap.ContentViewBind<String> contentViewBind = new ListContentWrap.ContentViewBind<String>() {
			@Override
			public void onBindContentView(final ListItemHolder itemView, String data, int position) {
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

		SlideListAdapter adapter = new SlideListAdapter.Builder<String>().data(data)
																		 .item(R.layout.item_content_type_1, 0, 0,
																					   R.layout.item_menu_1, 0.35f, contentViewBind, 10)
																		 .item(R.layout.item_content_type_2, R.layout.item_menu_2,
																					   0.35f, R.layout.item_menu_1, 0.35f, contentViewBind,
																					   20)
																		 .type(itemType)
																		 .into(mListView);

	}
}
