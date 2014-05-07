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

public class MainActivity extends Activity implements SurfaceHolder.Callback,
		OnTouchListener, OnClickListener {
	private static int SurfaceWidth = 0;
	private static int SurfaceHeight = 0;
	private static final int SCORE_PER_BLOCK = 5;
	private int sizeW;
	private int sizeH;
	private SurfaceView surface;
	private GameLoop gameLoop;
	snakechatsnake mainbody;
	List<snakechataxis> axisbody;
	snakechataxis consumed;

	private Bitmap mSnakeBody;
	private Bitmap mSnakeHead;
	private SurfaceHolder holder;
	private Paint backgroundPaint;
	float ballX = 0, ballY = 0;

	TextView txtScore;
	private int mScore = 0;
	private Handler mHandler;
	Button btnThread;
	Button butLeft, butRight, butUp, butDown;
	private Object mPauseLock;
	private boolean mPaused;
	private WebView webView;
	

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

		txtScore = (TextView) findViewById(R.id.score);

		surface = (SurfaceView) findViewById(R.id.snake_surface);
		holder = surface.getHolder();
		surface.getHolder().addCallback(this);
		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.WHITE);

		mSnakeBody = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.body);
		mSnakeHead = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.body);

		sizeW = mSnakeBody.getWidth();
		sizeH = mSnakeBody.getHeight();

		surface.setOnTouchListener(this);

		mainbody = new snakechatsnake();
		// 3 Snake blocks to start with
		mainbody.AddNew(sizeW, sizeH);
		mainbody.AddNew(sizeW, sizeH + sizeH);
		mainbody.AddNew(sizeW, sizeH + sizeH * 2);

		mainbody.onResume(this);
		mPauseLock = new Object();
		mPaused = false;
		btnThread = (Button) findViewById(R.id.btnThread);
		btnThread.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.play));
		btnThread.setOnClickListener(this);

		butLeft = (Button) findViewById(R.id.butLeft);
		butLeft.setOnClickListener(this);

		butRight = (Button) findViewById(R.id.butRight);
		butRight.setOnClickListener(this);

		butUp = (Button) findViewById(R.id.butUp);
		butUp.setOnClickListener(this);

		butDown = (Button) findViewById(R.id.butDown);
		butDown.setOnClickListener(this);

		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				txtScore.setText("Score:" + String.valueOf(mScore));
			}
		};
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	protected void onPause() {
		super.onPause();

		synchronized (mPauseLock) {
			mPaused = true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		synchronized (mPauseLock) {
			mPaused = false;
			mPauseLock.notifyAll();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// Get the width and height of SurfaceView
		getSurfaceDimension();

		// Pass the width/height into Model
		mainbody.init(SurfaceWidth, SurfaceHeight, sizeW);
		// Start game loop
		gameLoop = new GameLoop();
		gameLoop.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		try {
			gameLoop.safeStop();
		} finally {
			gameLoop = null;
		}
		Log.v("surfaceDestroyed", "surfaceDestroyed");
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

	// Drawing method of the Game
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

	// This is where actual drawing goes on !!
	private void doDraw(Canvas c) {
		int width = c.getWidth();
		int height = c.getHeight();
		c.drawRect(0, 0, width, height, backgroundPaint);
		axisbody = mainbody.getSnakeBlocks();
		int size = axisbody.size();

		mScore = (size - 3) * SCORE_PER_BLOCK;
		// Exception will be thrown if you try to do setText here
		// Only the main thread that created TextView can do setText.
		// So, we need to use handlers for the same
		mHandler.sendEmptyMessage(size);

		// Draw Snake Head with Different bitmap
		c.drawBitmap(mSnakeHead, axisbody.get(0).x_axis, axisbody.get(0).y_axis,
				null);

		// Draw rest of the Snake Body
		for (int index = 1; index < size; index++) {
			c.drawBitmap(mSnakeBody, axisbody.get(index).x_axis,
					axisbody.get(index).y_axis, null);
		}
		// Draw the block to be eaten up..
		consumed = mainbody.getconsume();
		c.drawBitmap(mSnakeBody, consumed.x_axis, consumed.y_axis, null);
	}

	// Create a private class for GameLoop
	// This will call draw() method of the main class at regular interval
	private class GameLoop extends Thread {
		
		private volatile boolean running = true;

		public void run() {
			
			while (running) {
				try {
					// Here is one Hard coding..
					TimeUnit.MILLISECONDS.sleep(100);

					draw();
					running = !mainbody.isTailTouch();
					mainbody.updateSnake();
					
				} catch (InterruptedException ie) {
					running = false;
				}
				synchronized (mPauseLock) {
					while (mPaused) {
						try {
							mPauseLock.wait();
						} catch (InterruptedException e) {
						}
					}
				}
			}
		}

		public void safeStop() {
			running = false;
			interrupt();
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// On every touch send the touch coordinates to Model class AddNew()
		// Model will create a new SnakeBlock and add it to the list.

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mainbody.updateDirection(event.getX(), event.getY());
		}
		return false;
	}

	@Override
	public void onClick(View arg0) {
		final int UP = 0;
		final int DOWN = 1;
		final int LEFT = 2;
		final int RIGHT = 3;

		switch (arg0.getId()) {
		case R.id.btnThread:
			if (mPaused == false) {
				btnThread.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.pause));
				synchronized (mPauseLock) {
					mPaused = true;
				}
			} else {
				btnThread.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.play));
				synchronized (mPauseLock) {
					mPaused = false;
					mPauseLock.notifyAll();
				}
			}
			break;
		case R.id.butDown:
			mainbody.updateDirectionOnClick(DOWN);
			break;
		case R.id.butLeft:
			mainbody.updateDirectionOnClick(LEFT);
			break;
		case R.id.butRight:
			mainbody.updateDirectionOnClick(RIGHT);
			break;
		case R.id.butUp:
			mainbody.updateDirectionOnClick(UP);
			break;
		default:
			break;
		}

	}
}