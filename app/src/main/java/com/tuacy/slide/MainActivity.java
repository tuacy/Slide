package com.tuacy.slide;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		initView();
		initEvent();
		initData();
	}

	private void initView() {
		findViewById(R.id.button_recycler).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AdapterRecyclerViewActivity.startUp(mContext);
			}
		});

		findViewById(R.id.button_list).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AdapterListViewActivity.startUp(mContext);
			}
		});
	}

	private void initEvent() {

	}

	private void initData() {

	}
}
