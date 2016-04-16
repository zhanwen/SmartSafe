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
	 * �汾��Ϣ��˵��
	 */
	private String description;
	/**
	 * �°汾�����ص�ַ
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
		
		//�������ݿ�
		copyDB();
		
		
		if(update) {
			//�������
			checkUpdate();
		} else {
			//�Զ������Ѿ��ر�
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					//������ҳ��
					enterHome();
				}
			}, 2000);
		}
		
		
		//����
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(500);
		findViewById(R.id.rl_root_splash).startAnimation(aa);
		
	}

	// path ��address.db������ݿ⿽����/data/data/<����>/files/address.db		
	private void copyDB() {
		//ֻҪ�㿽����һ�Σ��Ͳ�Ҫ�ڿ�����
		try {
			File file = new File(getFilesDir(),"address.db");
			if(file.exists() && file.length() > 0) {
				//����������Ҫ����
				Log.i(TAG, "����������Ҫ����");
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
			case ENTER_HOME: //������ҳ��
				enterHome();
				break;
			case SHOW_UPDATE_DIALOG: //��ʾ�����Ի���
				Log.i(TAG,"��ʾ�����Ի���");
				showUpdateDialog();
				break;
			case URL_ERROR: //URL����
				enterHome();
				//����getApplicationContext�൱��SplashActivity.this
				Toast.makeText(getApplicationContext(), "URL����", 0).show();
				break;
			case NETWORK_ERROR: //�����쳣
				enterHome();
				Toast.makeText(getApplicationContext(), "�����쳣", 0).show();
				break;
			case JSON_ERROR: //JSON��������
				enterHome();
				Toast.makeText(SplashActivity.this, "JSON��������", 0).show();
				break;
			default:
				break;
			}
			
		}
		
	};
	
	/**
	 * ���������Ի���
	 */
	private void showUpdateDialog() {
		//this = activity
		AlertDialog.Builder builder = new Builder(SplashActivity.this);
//		builder.setCancelable(false); //ǿ������
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//������ҳ��
				enterHome();
				dialog.dismiss();
			}
		});
		
		builder.setTitle("��ʾ����");
		builder.setMessage(description);
		
		builder.setPositiveButton("��������", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//����APK�������滻��װ
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					//sdk����
					//afinal
					FinalHttp finalHttp = new FinalHttp();
					finalHttp.download(apkurl, Environment.getExternalStorageDirectory().getAbsolutePath()+"/wolfsafe2.0.apk",
							new AjaxCallBack<File>() {
								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									t.printStackTrace();
									Toast.makeText(getApplicationContext(), "����ʧ��", 1).show();
									super.onFailure(t, errorNo, strMsg);
								}
								@Override
								public void onLoading(long count, long current) {
									super.onLoading(count, current);
									tv_update_info.setVisibility(View.VISIBLE);
									//��ǰ���ذٷֱ�
									int progress = (int) (current*100/count);
									tv_update_info.setText("���ؽ���:"+ progress + "%");
								}
								@Override
								public void onSuccess(File t) {
									super.onSuccess(t);
									/**
									 * ��װAPK
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
					Toast.makeText(getApplicationContext(), "û��sdk���밲װ������", 0).show();
				}
				
				
			}
		});
		
		builder.setNegativeButton("�´���˵", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//������ҳ��
				enterHome(); 
			}
		});
		
		builder.show();
	}
	
	private void enterHome() {
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		//�رյ�ǰҳ��
		finish();
	}
	
	/**
	 * ����Ƿ����°汾������о�����
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
					//����
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(4000);
					int code = conn.getResponseCode();
					
					if(code == 200) {
						//�����ɹ�
						InputStream is = conn.getInputStream();
						//����ת�����ַ���������������������
						String result = StreamTools.readFromStream(is);
						Log.i(TAG,"�����ɹ���"+result);
						
						//JSON����
						JSONObject obj = new JSONObject(result);
						//�õ��������İ汾��Ϣ
						String version = (String) obj.get("version");
						
						description = (String) obj.get("description");
						apkurl = (String) obj.get("apkurl");
						
						
						//У���Ƿ����°汾
						if(getAppVersion().equals(version)) {
							//�汾һ�£�û���°汾,������ҳ��
							mess.what = ENTER_HOME;
							
						}else {
							//���°汾������һ�����Ի���
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
					
					//�����Ӧʱ��С��2000
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
	 * ���Ӧ�ó���İ汾��Ϣ
	 * @return �汾��
	 */
	public String getAppVersion() {
		//���������ֻ���apk
		PackageManager pm = getPackageManager();
		
		try {
			//�õ�ָ��apk�Ĺ����嵥
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
		
		
		
		
		
	}
	
}
