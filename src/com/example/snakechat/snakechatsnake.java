package com.example.snakechat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class snakechatsnake {

	private final List<snakechataxis> array = new LinkedList<snakechataxis>();
	private snakechataxis consume;
	int surfaceWidth, surfaceHeight;
	private snakechatandriod display;
	// Direction Sense Flags
	private static final int UP = 0;
	private static final int DOWN = 1;
	private static final int LEFT = 2;
	private static final int RIGHT = 3;
	private static int compass = 2;
	private static int increment = 0;
	private static boolean isBlockconsume = false;
	private static MediaPlayer mp;
	public final Object LOCK = new Object();

	public snakechatsnake() {
	}

	public void init(int windowWidth, int windowHeight, int xyz) {
		// Align surfaceWidth/surfaceHeight on SnakeBlock boundary
		// This will allow for perfect one-on-one collision with new SnakeBlock
		surfaceWidth = windowWidth - (windowWidth % xyz);
		surfaceHeight = windowHeight - (windowHeight % xyz);

		increment = xyz;
		display = new snakechatandriod(surfaceWidth, surfaceHeight, xyz);
		consume = new snakechataxis(display.GetRandPositionX(),
				display.GetRandPositionY());
	}

	public void onResume(Context context) {
		mp = MediaPlayer.create(context, R.raw.sound);
		mp.setLooping(false);
		if (mp != null) {
			Log.v("onResume", "mp not null");
		}
	}

	public snakechataxis getconsume() {
		return consume;
	}

	public void updateDirection(float x, float y) {
		synchronized (LOCK) {
			float cur_x = array.get(0).x_axis;
			float cur_y = array.get(0).y_axis;
			// Log.v("updateDirection","cur_x="+cur_x+"cur_y="+cur_y);
			// Skip touch events if Snake head goes past a boundary
			// Otherwise your snake will not be visible at times when
			// user press a button at the instance it crosses the boundary
			if ((cur_x <= (0)) || (cur_x >= (surfaceWidth)) || (cur_y < 0)
					|| (cur_y >= (surfaceHeight)))
				return;

			// Take decision based on current direction of the snake head
			switch (compass) {
			case UP:
			case DOWN:
				if (cur_x < x)
					compass = RIGHT;
				else
					compass = LEFT;
				break;
			case LEFT:
			case RIGHT:
				if (cur_y < y)
					compass = DOWN;
				else
					compass = UP;
				break;
			}
		}
	}

	public void updateDirectionOnClick(int direction) {
		synchronized (LOCK) {
			float cur_x = array.get(0).x_axis;
			float cur_y = array.get(0).y_axis;
			// Log.v("updateDirectionOnClick","cur_x="+cur_x+"cur_y="+cur_y);
			// Skip touch events if Snake head goes past a boundary
			// Otherwise your snake will not be visible at times when
			// user press a button at the instance it crosses the boundary
			if ((cur_x <= (0)) || (cur_x >= (surfaceWidth)) || (cur_y < 0)
					|| (cur_y >= (surfaceHeight)))
				return;

			// Take decision based on current direction of the snake head
			switch (compass) {
			case UP:
				if (direction != DOWN)
					compass = direction;
				break;
			case DOWN:
				if (direction != UP)
					compass = direction;
				break;
			case LEFT:
				if (direction != RIGHT)
					compass = direction;
				break;
			case RIGHT:
				if (direction != LEFT)
					compass = direction;
				break;
			}
		}
	}

	public void AddNew(int x, int y) {
		synchronized (LOCK) {
			array.add(new snakechataxis(x, y));
		}
	}

	public List<snakechataxis> getSnakeBlocks() {
		synchronized (LOCK) {
			return new ArrayList<snakechataxis>(array);
		}
	}

	public void updateSnake() {
		synchronized (LOCK) {
			int size = array.size();

			// Save location of Head of the snake first
			array.get(0).xindex = array.get(0).x_axis;
			array.get(0).yindex = array.get(0).y_axis;

			// Log.v("updateSnake","cur_x="+array.get(0).x_axis+"cur_y="+array.get(0).y_axis);

			switch (compass) {
			case LEFT:
				array.get(0).x_axis -= increment;
				break;
			case RIGHT:
				array.get(0).x_axis += increment;
				break;
			case UP:
				array.get(0).y_axis -= increment;
				break;
			case DOWN:
				array.get(0).y_axis += increment;
				break;
			}
			objectCollision();
			WallCollision();
			// Make changes in the Tail of the snake
			for (int index = 1; index < size; index++) {
				array.get(index).xindex = array.get(index).x_axis;
				array.get(index).yindex = array.get(index).y_axis;
				array.get(index).x_axis = array.get(index - 1).xindex;
				array.get(index).y_axis = array.get(index - 1).yindex;
			}
		}
	}

	public boolean isTailTouch() {
		synchronized (LOCK) {
			boolean touch = false;
			int size = array.size();
			// Save location of Head of the snake first
			int headx = array.get(0).x_axis;
			int heady = array.get(0).y_axis;

			// Make changes in the Tail of the snake
			for (int index = 1; index < size; index++) {
				if ((headx == (array.get(index).x_axis))
						&& (heady == (array.get(index).y_axis))) {
					touch = true;
					
					Log.v("isTailTouch", "Touched");
					break;
				}
			}
			return touch;
		}
	}

	public void objectCollision() {
		if (array.get(0).x_axis > consume.x_axis - 10
				&& array.get(0).x_axis < consume.x_axis + 10
				&& array.get(0).y_axis > consume.y_axis - 10
				&& array.get(0).y_axis < consume.y_axis + 10) {
			isBlockconsume = true;
			// consume = new SnakeBlock(display.GetRandPositionX(),
			// display.GetRandPositionY());
			consume = addbody();
			array.add(new snakechataxis(consume.x_axis, consume.y_axis));
			// mp.setLooping(true);

			if (mp != null) {
				// Log.v("objectCollision", "play music");
				mp.start();
			}
		}
		isBlockconsume = false;
	}

	public snakechataxis addbody() {
		synchronized (LOCK) {
			int x, y;
			boolean isOut = true;
			int size = array.size();
			while (true) {
				isOut = true;
				x = display.GetRandPositionX();
				y = display.GetRandPositionY();

				for (int index = 0; index < size; index++) {
					if ((x == array.get(index).x_axis)
							&& (y == array.get(index).y_axis)) {
						isOut = false;
						Log.v("CreateNewSnakeBlock", "Inside Block");
						break;
					}
				}
				if (isOut) {
					// Log.v("CreateNewSnakeBlock", "OutSide Block");
					break;
				}
			}
			return new snakechataxis(x, y);
		}
	}

	// Make the snake to come out from the other end instead of ending the game
	// It will give touch screen user time to play happily!!
	public void WallCollision() {
		switch (compass) {

		case LEFT:
			if (array.get(0).x_axis <= (0))
				array.get(0).x_axis = surfaceWidth;
			break;
		case RIGHT:
			if (array.get(0).x_axis >= (surfaceWidth))
				array.get(0).x_axis = 0;
			break;
		case UP:
			if (array.get(0).y_axis <= 0)
				array.get(0).y_axis = surfaceHeight;
			break;
		case DOWN:
			if (array.get(0).y_axis >= (surfaceHeight))
				array.get(0).y_axis = 0;
			break;

		}
		isBlockconsume = false;
	}
}
