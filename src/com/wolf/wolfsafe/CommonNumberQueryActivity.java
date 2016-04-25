package com.wolf.wolfsafe;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.wolf.wolfsafe.db.dao.CommonNumberDao;

public class CommonNumberQueryActivity extends Activity {
	private ExpandableListView elv;
	private SQLiteDatabase db;
	public static final String path = "/data/data/com.wolf.wolfsafe/files/commonnum.db";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		elv = (ExpandableListView) findViewById(R.id.elv);
		elv.setAdapter(new CommonNumberAdapter());

		elv.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				TextView tv = (TextView) v;
				String phone = tv.getText().toString().split("\n")[1];
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:"+phone));
				startActivity(intent);
				return false;
			}
		});
	}

	private class CommonNumberAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return CommonNumberDao.getGroupCount(db);
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tv;
			if(convertView!=null){
				tv = (TextView) convertView;
			}else{
				tv  = new TextView(getApplicationContext());
			}
			tv.setText("       " + CommonNumberDao.getGroupName(db,groupPosition));
			tv.setTextSize(20);
			tv.setTextColor(Color.RED);
			return tv;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tv;
			if(convertView!=null){
				tv = (TextView) convertView;
			}else{
				tv  = new TextView(getApplicationContext());
			}
			tv.setText(CommonNumberDao.getChildNameByPosition(db,groupPosition,
					childPosition));
			tv.setTextSize(16);
			tv.setTextColor(Color.BLACK);
			return tv;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return CommonNumberDao.getChildCountByPosition(db,groupPosition);
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}
	
}
