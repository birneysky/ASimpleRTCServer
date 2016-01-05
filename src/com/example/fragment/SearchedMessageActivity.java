package com.example.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.slidingmenu.R;

public class SearchedMessageActivity extends FragmentActivity implements OnClickListener{
	
	private RelativeLayout  menuBackConstruct;
	private NewViewPager viewPager;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private int currentIndex = 1;
	private Button[] btnArray = new Button[2];
	private Fragment helpFragment, mineFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			setContentView(R.layout.activity_searched_message);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_searched_message);
			setupView();
			addListener();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setupView() {
		menuBackConstruct = (RelativeLayout)findViewById(R.id.menu_back_construct);
		btnArray[0] = (Button)findViewById(R.id.btn_searched_help);
		btnArray[1] = (Button)findViewById(R.id.btn_searched_my);
		viewPager = (NewViewPager)findViewById(R.id.view_pager);
		helpFragment = new SearchMessageHelpFragment();
		mineFragment = new SearchMessageMineFragment();
		fragments.add(helpFragment);
		fragments.add(mineFragment);
		FragmentViewPagerAdapter adapter = 
				new FragmentViewPagerAdapter(getApplicationContext(),
						this.getSupportFragmentManager(), 
						viewPager, 
						fragments);
		viewPager.setAdapter(adapter);
		updateFragemnt(1);//默认显示我的众寻
	}

	private void addListener() {
		menuBackConstruct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SearchedMessageActivity.this.finish();
			}
		});
		
		for(Button btn:btnArray) {
			btn.setOnClickListener(this);
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.searched_message, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_searched_help:
			this.currentIndex=0;
			break;
		case R.id.btn_searched_my:
			this.currentIndex=1;
			break;
		}
		updateFragemnt(currentIndex);
	}
	
	public void updateFragemnt(int curentInd){
		//显示另外一个fragment
		viewPager.setCurrentItem(curentInd);
		updateButtonColor();
	}

	private void updateButtonColor() {
		for(int i=0; i<btnArray.length; i++) {
			if (i == this.currentIndex) {
				btnArray[i].setTextColor(getResources().getColor(R.color.white));
				if(this.currentIndex == 0){
					btnArray[0].setBackground(getResources().getDrawable(R.drawable.bg_btn_left_selected));
					btnArray[1].setBackground(getResources().getDrawable(R.drawable.bg_btn_right_unselected));
					btnArray[0].setPadding(13, 5, 10, 5);
					btnArray[1].setPadding(10, 5, 13, 5);
				}
				if(this.currentIndex == 1){
					btnArray[0].setBackground(getResources().getDrawable(R.drawable.bg_btn_left_unselected));
					btnArray[1].setBackground(getResources().getDrawable(R.drawable.bg_btn_right_selected));
					btnArray[0].setPadding(13, 5, 10, 5);
					btnArray[1].setPadding(10, 5, 13, 5);
				}
			}else {
				btnArray[i].setTextColor(getResources().getColor(R.color.black));
			}
		}
	}

}
