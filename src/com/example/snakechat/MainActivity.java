package com.example.snakechat;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements SurfaceHolder.Callback, OnTouchListener, OnClickListener {

	//float ballX = 0;
	//float ballY = 0;
	TextView scorepoints;
	
	private int scoreincr = 0;
	private Handler handlers;
	Button btnThread;

	private Bitmap snakeb;
	private Bitmap snakeh;
	private SurfaceHolder holder;
	private Paint backgroundPaint;
	
	private WebView webView;
	
	private static int SurfaceWidth = 0;
	private static int SurfaceHeight = 0;
	private static final int score = 5;
	private int sizeW;
	private int sizeH;
	private SurfaceView surface;
	private GameLoop gameLoop;
	snakechatsnake mainbody;
	List<snakechataxis> axisbody;
	snakechataxis consumed;
	Button butLeft, butRight, butUp, butDown;
	private Object pausepbj;
	private boolean paused;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.fragment_main);

		scorepoints = (TextView) findViewById(R.id.score);
		
		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.BLUE);

		surface = (SurfaceView) findViewById(R.id.snake_surface);
		holder = surface.getHolder();
		surface.getHolder().addCallback(this);
		
		mainbody = new snakechatsnake();
		//start
		mainbody.AddNew(sizeW, sizeH);
		mainbody.AddNew(sizeW, sizeH + sizeH);

		mainbody.pauseR(this);

		snakeb = BitmapFactory.decodeResource(this.getResources(),R.drawable.body);
		snakeh = BitmapFactory.decodeResource(this.getResources(),R.drawable.body);

		sizeW = snakeb.getWidth();
		sizeH = snakeb.getHeight();

		surface.setOnTouchListener(this);


		
		btnThread = (Button) findViewById(R.id.btnThread);
		btnThread.setBackgroundDrawable(getResources().getDrawable(R.drawable.play));
		btnThread.setOnClickListener(this);
		butUp = (Button) findViewById(R.id.butUp);
		butUp.setOnClickListener(this);
		butDown = (Button) findViewById(R.id.butDown);
		butDown.setOnClickListener(this);
		butLeft = (Button) findViewById(R.id.butLeft);
		butLeft.setOnClickListener(this);
		butRight = (Button) findViewById(R.id.butRight);
		butRight.setOnClickListener(this);



		pausepbj = new Object();
		paused = false;
		
		handlers = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				scorepoints.setText("Point:" + String.valueOf(scoreincr));
				while(true){
					/*super.handleMessage(msg);
				scorepoints.setText("Point:" + String.valueOf(scoreincr));
					 */
					break;
				}
				}
		};
	}

	@Override
	public void onClick(View arg0) {
		final int UP = 0;
		final int DOWN = 1;
		final int LEFT = 2;
		final int RIGHT = 3;
		
		switch (arg0.getId()) {
		case R.id.btnThread:
			if (paused != false) {
				btnThread.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause));
				synchronized (pausepbj) {
					paused = false;
					pausepbj.notifyAll();
				}
			} else {
				btnThread.setBackgroundDrawable(getResources().getDrawable(R.drawable.play));
				synchronized (pausepbj) {
					paused = true;
				}
			}
			break;
		case R.id.butLeft:
			mainbody.clickdirectionaction(LEFT);
			break;
		case R.id.butDown:
			mainbody.clickdirectionaction(DOWN);
			break;
		case R.id.butUp:
			mainbody.clickdirectionaction(UP);
			break;
		case R.id.butRight:
			mainbody.clickdirectionaction(RIGHT);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		
		int pause123 = 10;
		
		for(int i = 0; i < pause123; i++){
			break;
		}
		
		synchronized (pausepbj) {
			paused = true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		int resume123 = 10;
		
		for(int i = 0; i < resume123; i++){
			break;
		}
		
		synchronized (pausepbj) {
			paused = false;
			pausepbj.notifyAll();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {

		getSurfaceDimension();

		mainbody.init(SurfaceWidth, SurfaceHeight, sizeW);

		gameLoop = new GameLoop();
		gameLoop.start();
	}
	

	private void getSurfaceDimension() {
		Canvas c = null;
		try {
			c = holder.lockCanvas();
			if (c != null) {
				SurfaceWidth = c.getWidth();
				SurfaceHeight = c.getHeight();
			}
		} finally {
			if (c != null) {
				holder.unlockCanvasAndPost(c);
			}
		}
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}




	private void draw() {

		Canvas c = null;
		try {
			c = holder.lockCanvas();
			if (c != null) {
				doDraw(c);
			}
		} finally {
			if (c != null) {
				holder.unlockCanvasAndPost(c);
			}
		}
	}


	// drawing
	private void doDraw(Canvas c) {
		int width = c.getWidth();
		int height = c.getHeight();
		c.drawRect(0, 0, width, height, backgroundPaint);
		axisbody = mainbody.bringblock();
		int size = axisbody.size();
		int scoredecr = 0;


		handlers.sendEmptyMessage(size);

		scoreincr = (size - 3) * score;
		scoredecr = (size - 4) * score;


		/*for (int index = 1; index < size; index++) {
			c.drawBitmap(snakeb, axisbody.get(index).x_axis, axisbody.get(index).y_axis, null);
		}
				for (int index = 1; index < size; index++) {
			c.drawBitmap(snakeb, axisbody.get(index).x_axis, axisbody.get(index).y_axis, null);
		}

		consumed = mainbody.getconsume();
		c.drawBitmap(snakeb, consumed.x_axis, consumed.y_axis, null);
	}
		*/
		c.drawBitmap(snakeh, axisbody.get(0).x_axis, axisbody.get(0).y_axis, null);


		for (int index = 1; index < size; index++) {
			c.drawBitmap(snakeb, axisbody.get(index).x_axis, axisbody.get(index).y_axis, null);
		}

		consumed = mainbody.getconsume();
		c.drawBitmap(snakeb, consumed.x_axis, consumed.y_axis, null);
	}


	private class GameLoop extends Thread {
		
		private volatile boolean on = true;

		public void run() {
			
			while (on) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);

					draw();
					on = !mainbody.ggtouch();
					while(true){
						int array = 1;
						int count = 5;
						array = count;
						break;
					}
					mainbody.snakeaddi();
					
				} catch (InterruptedException ie) {
					on = false;
					/*	TimeUnit.MILLISECONDS.sleep(100);

					draw();
					on = !mainbody.ggtouch();
					while(true){
						int array = 1;
						int count = 5;
						array = count;
						break;
					}
					mainbody.snakeaddi();
					*/
				}
				synchronized (pausepbj) {
					while (paused) {
						try {
							pausepbj.wait();
						} catch (InterruptedException e) {
						}
					}
				}
			}
		}

		public void interr() {
			on = false;
			interrupt();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		try {
			gameLoop.interr();
		} finally {
			gameLoop = null;
		}
		Log.v("off", "off");
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mainbody.changedirection(event.getX(), event.getY());
		}
		return false;
	}
}
	