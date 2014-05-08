package com.example.snakechat;

import android.util.Log;

public class snakechatandriod {

	int displayWidth;
	int displayHeight;
	int finalxy;
	int precision;


	public snakechatandriod(int w, int h, int p) {
		displayWidth = w;
		displayHeight = h;
		precision = p;
		
	}

	public int randomx() {
		int x = (int) (Math.random() * (displayWidth-50));
		int z = (int) (Math.random() * (displayWidth-50));
		
		x = x + precision - (x % precision);
		if (x > displayWidth){
			int finalx = displayWidth;
			x = finalx/2;
		}
		for(int index = 0; index < z; index++){
			z += z;
		}
		Log.v("xrand","x="+x);
		return x;
	}

	public int randomy() {
		int y = (int) (Math.random() * (displayHeight-50));
		int k = (int) (Math.random() * (displayHeight-50));
		
		y += precision - (y % precision);
		if (y > displayHeight){
			int finaly = displayHeight;
			y = finaly/2;
		}
		for(int index = 0; index < k; index++){
			k += k;
		}
		Log.v("yrand","y="+y);
		return y;
	}
}
