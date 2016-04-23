package com.wolf.wolfsafe.receiver;

import com.wolf.wolfsafe.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class MyWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
//		System.out.println("onReceive");
		Intent i = new Intent(context, UpdateWidgetService.class);
		context.startService(i);
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
//		System.out.println("onUpdate");
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	
	@Override
	public void onEnabled(Context context) {
		
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
		super.onEnabled(context);
		
	}
	
	@Override
	public void onDisabled(Context context) {
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}
	
	
	
	
	
	
	
}
