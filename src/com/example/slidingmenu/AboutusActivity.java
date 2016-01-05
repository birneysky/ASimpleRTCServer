package com.example.slidingmenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

public class AboutusActivity extends Activity {
	private RelativeLayout titleBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_about);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_aboutus);
		setupView();
		addListener();
	}

	private void setupView() {
		// TODO Auto-generated method stub
		titleBack = (RelativeLayout)findViewById(R.id.title_back);
	}

	private void addListener() {
		// TODO Auto-generated method stub
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AboutusActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

}
