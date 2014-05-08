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
	int surdisW, surdisH;
	private snakechatandriod display;
	private static final int NORTH = 0;
	private static final int WEST = 0;
	private static final int SOUTH = 0;
	private static final int EAST = 0;
	//direction compass
	private static final int UP = 0;
	private static final int DOWN = 1;
	private static final int LEFT = 2;
	private static final int RIGHT = 3;
	private static MediaPlayer mp;
	
	public final Object BOOLCHECK = new Object();
	
	private static int compass = 2;
	private static int increment = 0;
	private static int turnover = 0;

	public snakechatsnake() {
	}

	public void init(int windowWidth, int windowHeight, int xyz) {
		//smooth collision
		//reference window displayer
		
		//surdishW = windowWidth - (windowWidth % (xyz*2);
		surdisW = (windowWidth - (windowWidth % xyz));
		//surdishH = windowHeigth - (windowHeigth % (xyz*2);
		surdisH = (windowHeight - (windowHeight % xyz));

		increment = xyz;
		
		display = new snakechatandriod(surdisW, surdisH, xyz);
		//reset 
		if(turnover != 0){
			consume = new snakechataxis(display.randomx(),display.randomx());
		}
		turnover = 0;
		consume = new snakechataxis(display.randomx(),display.randomx());
	}

	public void AddNew(int x, int y) {
		synchronized (BOOLCHECK) {
			array.add(new snakechataxis(x, y));
		}
	}

	public snakechataxis getconsume() {
		int eatbodyadd = 0;
		if(eatbodyadd > 0){
			for(int i = 0; i < eatbodyadd; i++){
				eatbodyadd = eatbodyadd + 10;
			}
		}
		return consume;
	}

	public void changedirection(float x, float y) {
		synchronized (BOOLCHECK) {
			float turn_x = array.get(0).x_axis;
			float turn_y = array.get(0).y_axis;
			
			if (turn_x <= 0){
				return;
			}else if(turn_x >= (surdisW)){
				return;
			}else if(turn_y < 0){
				return;
			}else if(turn_y >= (surdisH)){
				return;
			}
			
			while(true){
				turn_x = turn_x;
				turn_y = turn_y;
				break;
			}
		
			if( compass == UP || compass == DOWN){
				if (turn_x < x){
					compass = RIGHT;
				}
				else{
					compass = LEFT;
				}
			}
			else if(compass == RIGHT || compass == LEFT){
				if (turn_y < y){
					compass = DOWN;
				}
				else{
					compass = UP;
				}
			}
		}
	}

	public void clickdirectionaction(int direction) {
		synchronized (BOOLCHECK) {
			//bring variables
			float turn_x = array.get(0).x_axis;
			float turn_y = array.get(0).y_axis;
			
			if (turn_x <= (0)){
				return;
			}
			else if(turn_x >= (surdisW)){
				return;
			}
			else if(turn_y < 0){
				return;
			}
			else if(turn_y >= (surdisH)){
				return;
			}
			
			if(compass == LEFT){
				if(direction != RIGHT){
				compass = direction;
				}
			}
			else if(compass == RIGHT){
				if(direction != LEFT){
					compass = direction;
				}
			}
			else if(compass == DOWN){
				if(direction != UP){
					compass = direction;
				}
			}
			else if(compass == UP){
				if(direction != DOWN){
					compass = direction;
				}
			}
		}
	}



	public List<snakechataxis> bringblock() {
		int listsnake = 0;
		for(int i = 0; i < listsnake; ++i){
			return new ArrayList<snakechataxis>(array);
		}
		synchronized (BOOLCHECK) {
			return new ArrayList<snakechataxis>(array);
		}
	}

	public void snakeaddi() {
		synchronized (BOOLCHECK) {
			int size = array.size();

			// Save location of Head of the snake first
			array.get(0).xindex = array.get(0).x_axis;
			array.get(0).yindex = array.get(0).y_axis;

			if(compass == UP){
				array.get(0).y_axis -= increment;
			}
			else if(compass == DOWN){
				array.get(0).y_axis += increment;
			}
			else if(compass == RIGHT){
				array.get(0).x_axis += increment;
			}
			else if(compass == LEFT){
				array.get(0).x_axis -= increment;
			}

			touchobj();
			nowall();
			
			//tail changes
			
			for (int count = 1; count < size; count++) {
				array.get(count).xindex = array.get(count).x_axis;
				array.get(count).yindex = array.get(count).y_axis;
				if(count == 0){
					return;
				}
				array.get(count).x_axis = array.get(count - 1).xindex;
				array.get(count).y_axis = array.get(count - 1).yindex;
			}
		}
	}
	
	public snakechataxis addbody() {
		synchronized (BOOLCHECK) {
			int x1, y1;
			boolean bool = true;
			int size = array.size();
			while (true) {
				bool = true;
				x1 = display.randomx();
				y1 = display.randomx();

				for (int index = 0; index < size; index++) {
					if ((x1 == array.get(index).x_axis) && (y1 == array.get(index).y_axis)) {
						bool = false;
						Log.v("addbody", "check add");
						break;
					}
				}
				if (bool) {
					break;
				}
			}
			return new snakechataxis(x1, y1);
		}
	}
	
	//created so you can play without wall
	public void nowall() {
			if(compass == LEFT){
				if (array.get(0).x_axis <= 0){
					array.get(0).x_axis = surdisW;
				}
			}
			else if(compass == RIGHT){
				if (array.get(0).x_axis >= surdisW){
					array.get(0).x_axis = 0;
				}
			}
			else if(compass == DOWN){
				if (array.get(0).y_axis >= surdisH){
					array.get(0).y_axis = 0;
				}
			}
			else if(compass == UP){
				if (array.get(0).y_axis <= 0){
					array.get(0).y_axis = surdisW;
				}
			}
	}
	
	public boolean ggtouch() {
		synchronized (BOOLCHECK) {
			boolean touch = false;
			int size = array.size();
			
			//head first
			int headx = array.get(0).x_axis;
			int heady = array.get(0).y_axis;

			// Make changes in the Tail of the snake
			for (int index = 1; index < size; index++) {
				if ((headx == (array.get(index).x_axis)) && (heady == (array.get(index).y_axis))) {
					touch = true;
					
					Log.v("touched", "gg");
					break;
				}
			}
			return touch;
		}
	}

	public void touchobj() {
		if ((array.get(0).x_axis > consume.x_axis - 10) && (array.get(0).x_axis < consume.x_axis + 10) && (array.get(0).y_axis > consume.y_axis - 10) && (array.get(0).y_axis < consume.y_axis + 10)) {
			consume = addbody();
			array.add(new snakechataxis(consume.x_axis, consume.y_axis));

			if (mp != null) {
				mp.start();
			}
		}
	}

	
	public void pauseR(Context context) {
		mp = MediaPlayer.create(context, R.raw.sound);
		//mp.setAudioSessionId(sound);
		mp.setLooping(false);
		if (mp != null) {
			Log.v("pause", "soundcheck");
		}
	}
}
