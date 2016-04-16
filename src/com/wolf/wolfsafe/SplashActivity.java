package com.wolf.wolfsafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.wolf.wolfsafe.utils.StreamTools;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	protected static final int ENTER_HOME = 0;
	protected static final int SHOW_UPDATE_DIALOG = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_splash_version;
	private TextView tv_update_info;
	/**
	 * 版本信息的说明
	 */
	private String description;
	/**
	 * 新版本的下载地址
	 */
	private String apkurl;
	
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("version: " + getAppVersion());
		tv_update_info = (TextView) findViewById(R.id.tv_update_info);
		
		boolean update = sp.getBoolean("update", false);
		
		//拷贝数据库
		copyDB();
		
		
		if(update) {
			//检查升级
			checkUpdate();
		} else {
			//自动升级已经关闭
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					//进入主页面
					enterHome();
				}
			}, 2000);
		}
		
		
		//动画
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(500);
		findViewById(R.id.rl_root_splash).startAnimation(aa);
		
	}

	// path 把address.db这个数据库拷贝到/data/data/<包名>/files/address.db		
	private void copyDB() {
		//只要你拷贝了一次，就不要在拷贝了
		try {
			File file = new File(getFilesDir(),"address.db");
			if(file.exists() && file.length() > 0) {
				//正常，不需要拷贝
				Log.i(TAG, "正常，不需要拷贝");
			}else {
				InputStream is = getAssets().open("address.db");
				
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case ENTER_HOME: //进入主页面
				enterHome();
				break;
			case SHOW_UPDATE_DIALOG: //显示升级对话框
				Log.i(TAG,"显示升级对话框");
				showUpdateDialog();
				break;
			case URL_ERROR: //URL错误
				enterHome();
				//这里getApplicationContext相当于SplashActivity.this
				Toast.makeText(getApplicationContext(), "URL错误", 0).show();
				break;
			case NETWORK_ERROR: //网络异常
				enterHome();
				Toast.makeText(getApplicationContext(), "网络异常", 0).show();
				break;
			case JSON_ERROR: //JSON解析出错
				enterHome();
				Toast.makeText(SplashActivity.this, "JSON解析出错", 0).show();
				break;
			default:
				break;
			}
			
		}
		
	};
	
	/**
	 * 弹出升级对话框
	 */
	private void showUpdateDialog() {
		//this = activity
		AlertDialog.Builder builder = new Builder(SplashActivity.this);
//		builder.setCancelable(false); //强制升级
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//进入主页面
				enterHome();
				dialog.dismiss();
			}
		});
		
		builder.setTitle("提示升级");
		builder.setMessage(description);
		
		builder.setPositiveButton("立刻升级", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//下载APK，并且替换安装
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					//sdk存在
					//afinal
					FinalHttp finalHttp = new FinalHttp();
					finalHttp.download(apkurl, Environment.getExternalStorageDirectory().getAbsolutePath()+"/wolfsafe2.0.apk",
							new AjaxCallBack<File>() {
								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									t.printStackTrace();
									Toast.makeText(getApplicationContext(), "下载失败", 1).show();
									super.onFailure(t, errorNo, strMsg);
								}
								@Override
								public void onLoading(long count, long current) {
									super.onLoading(count, current);
									tv_update_info.setVisibility(View.VISIBLE);
									//当前下载百分比
									int progress = (int) (current*100/count);
									tv_update_info.setText("下载进度:"+ progress + "%");
								}
								@Override
								public void onSuccess(File t) {
									super.onSuccess(t);
									/**
									 * 安装APK
									 */
									installAPK(t);
								}
								private void installAPK(File t) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
									startActivity(intent);
								}
						
					});
				}else {
					Toast.makeText(getApplicationContext(), "没有sdk，请安装上在试", 0).show();
				}
				
				
			}
		});
		
		builder.setNegativeButton("下次再说", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//进入主页面
				enterHome(); 
			}
		});
		
		builder.show();
	}
	
	private void enterHome() {
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		//关闭当前页面
		finish();
	}
	
	/**
	 * 检查是否有新版本，如果有就升级
	 */
	private void checkUpdate() {
		new Thread() {
			@Override
			public void run() {
				
				Message mess = Message.obtain();
				long startTime = System.currentTimeMillis();
				
				//URL http://192.168.11.6:8080/updateinfo.html
				try {
					
					
					URL url = new URL(getString(R.string.serverurl));
					//联网
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(4000);
					int code = conn.getResponseCode();
					
					if(code == 200) {
						//联网成功
						InputStream is = conn.getInputStream();
						//将流转换成字符串，借助第三方工具类
						String result = StreamTools.readFromStream(is);
						Log.i(TAG,"联网成功了"+result);
						
						//JSON解析
						JSONObject obj = new JSONObject(result);
						//得到服务器的版本信息
						String version = (String) obj.get("version");
						
						description = (String) obj.get("description");
						apkurl = (String) obj.get("apkurl");
						
						
						//校验是否有新版本
						if(getAppVersion().equals(version)) {
							//版本一致，没有新版本,进入主页面
							mess.what = ENTER_HOME;
							
						}else {
							//有新版本，弹出一升级对话框
							mess.what = SHOW_UPDATE_DIALOG;
						}
						
						
						
					}
					
				} catch (MalformedURLException e) {
					mess.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					mess.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					mess.what = JSON_ERROR;
					e.printStackTrace();
				}finally {
					
					long endTime = System.currentTimeMillis();
					long dTime = endTime - startTime;
					
					//如果响应时间小于2000
					if(dTime < 2000) {
						try {
							Thread.sleep(2000-dTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					
					handler.sendMessage(mess);
				}

			};
		}.start();
	}



	/**
	 * 获得应用程序的版本信息
	 * @return 版本号
	 */
	public String getAppVersion() {
		//用来管理手机的apk
		PackageManager pm = getPackageManager();
		
		try {
			//得到指定apk的功能清单
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
		
		
		
		
		
	}
	
}
