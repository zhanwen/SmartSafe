package com.wolf.wolfsafe;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wolf.wolfsafe.db.dao.AntivirusDao;

public class AntiVirusActivity extends Activity {

	protected static final int SCANING = 0;
	protected static final int FINISH = 1;
	private ImageView iv_scan;
	private ProgressBar progressBar1;
	private PackageManager pm;
	private TextView tv_scan_status;
	private LinearLayout ll_container;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				tv_scan_status.setText("正在扫描:" + scanInfo.name);
				TextView tv = new TextView(getApplicationContext());
				if(scanInfo.isvirus) {
					tv.setTextColor(Color.RED);
					tv.setText("发现病毒:" + scanInfo.name);
				}else {
					tv.setTextColor(Color.BLACK);
					tv.setText("扫描安全:" + scanInfo.name);
				}
				ll_container.addView(tv, 0);
				break;
			case FINISH:
				tv_scan_status.setText("扫描完毕");
				iv_scan.clearAnimation();
				break;
			}
		};
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);

		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(1000);
		ra.setRepeatCount(Animation.INFINITE);
		iv_scan.startAnimation(ra);

		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);

		scanVirus();
	}

	/**
	 * 扫描病毒
	 */
	private void scanVirus() {

		pm = getPackageManager();

		tv_scan_status.setText("正在初始化8核杀毒引擎...");
		
		new Thread(){
			@Override
			public void run() {
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				progressBar1.setMax(infos.size());
				int progress = 0;
				for (PackageInfo info : infos) {
					// apk文件的完整路径
					String sourceDir = info.applicationInfo.sourceDir;  //apk,apk图片
//					//zip包
//					ZipFile zipFile = new ZipFile(file);
//					zipFile.getEntry(entryName);
					
					String md5 = getFileMd5(sourceDir);
					
					ScanInfo scanInfo = new ScanInfo();
					scanInfo.name = info.applicationInfo.loadLabel(pm).toString();
					scanInfo.packname = info.packageName;
					System.out.println(md5);
					//查询md5的值，是否在病毒数据库里面存在
					if(AntivirusDao.isVirus(md5)) {
						//发现病毒
						scanInfo.isvirus = true;
					}else {
						//扫描安全
						scanInfo.isvirus = false;
					}
					
					Message msg = Message.obtain();
					msg.obj = scanInfo;
					msg.what = SCANING;
					handler.sendMessage(msg);
					
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					progress++;
					progressBar1.setProgress(progress);
				}
				
				Message msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);
				
			};
		}.start();
	}

	/**
	 * 扫描信息的内部类
	 * @author 02
	 *
	 */
	class ScanInfo {
		String packname;
		String name;
		boolean isvirus;
	}
	
	
	/**
	 * 获取文件的md5值
	 * 
	 * @param path
	 *            文件的全路径名称
	 * @return
	 */
	private String getFileMd5(String path) {
		StringBuffer sb;
		try {
			// 获取一个文件的特征信息，签名信息
			File file = new File(path);
			// md5算法
			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(file);

			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}

			byte[] result = digest.digest();
			sb = new StringBuffer();
			for (byte b : result) {
				// 与运算
				int number = b & 0xff; // 加盐，就是不是非传统的加密了，比如 b & 0xfff
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}
}
