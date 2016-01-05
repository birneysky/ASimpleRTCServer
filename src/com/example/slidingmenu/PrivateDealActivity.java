package com.example.slidingmenu;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PrivateDealActivity extends Activity {
	
	private TextView tvTitle;
	private RelativeLayout titleBack;
	private TextView tvDealTitle, tvDealContent, tvPrivateTitle, tvPrivateContent;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_private_deal);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_question);
		
		setupView();
		addListener();
	}

	private void setupView() {
		tvTitle = (TextView)findViewById(R.id.tv_title_bar_title);
		tvTitle.setText(getResources().getText(R.string.title_activity_private_deal_title));
		titleBack = (RelativeLayout)findViewById(R.id.menu_back_construct);
		tvDealTitle = (TextView)findViewById(R.id.tv_private_deal_title_deal);
		tvDealContent = (TextView)findViewById(R.id.tv_private_deal_content_deal);
		StringBuffer contentDeal = getData("deal");
		tvDealContent.setText(contentDeal.toString());
		tvPrivateTitle = (TextView)findViewById(R.id.tv_private_deal_title_private);
		tvPrivateContent = (TextView)findViewById(R.id.tv_private_deal_content_private);
		StringBuffer contentPrivate = getData("private");
		tvPrivateContent.setText(contentPrivate.toString());
	}

	/**
	 * 从assets中读取文本信息,保存为string类型
	 */
	public StringBuffer getData(String name) {
		StringBuffer strbuf = new StringBuffer();
		String encoding = "utf-8";
		InputStream in = null;
		BufferedReader reader = null;
		try {
			// 通过AssetManager读取文件
			in = getResources().getAssets().open(name + ".txt", AssetManager.ACCESS_BUFFER);
			// 构造BufferedReader对象，以便逐行读取
			reader = new BufferedReader(new InputStreamReader(in, encoding));
			int line;
			// 逐行读取文件内容，读取一行，就把这一行数据进行拆分，然后保存进数据库
			char[] tempchars  = new char[1024];
			while ((line = reader.read(tempchars)) != -1) {
				strbuf.append(tempchars, 0, line);
			}
			return strbuf;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	private void addListener() {
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PrivateDealActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.private_deal, menu);
		return true;
	}

}
