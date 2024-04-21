package com.dnake.widget;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("HandlerLeak")
public class RoundView extends View {
	private Handler e_timer = null;
	private Context mCtx;

	public List<RoundData> mData = new LinkedList<RoundData>();

	public RoundView(Context context) {
		super(context);
		mCtx = context;

		mPaint.setColor(Color.RED);
		mPaint.setStrokeWidth(2);

		e_timer = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				if (mCircleV > 0) {
					for(int i=0; i<mData.size(); i++) {
						RoundData r = mData.get(i);
						r.delta += mDirect*mCircleV+360;
						r.delta %= 360;
					}
					processBitmap();
					invalidate();

					mCircleV -= 1;
				} else {
					if (mTimer != null) {
						mTimer.cancel();
						mTimer = null;
					}
				}
			}
		};
	}

	private Paint mPaint = new Paint();
	private Matrix mMatrix = new Matrix();
	
	//???????
	private Point mPoint = new Point();

	//??
	private int mRadiusW = 100, mRadiusH = 100;
	private float mScale = 0.7f;

	public void setup(int w, int h) {
		mPoint.x = w/2;
		mPoint.y = h/2;

		mRadiusW = w/2;
		mRadiusH = h/2;

		if (mData.size() > 0) {
			RoundData r = mData.get(0);
			mRadiusW = (w-r.bmp.getWidth())/2;
			mRadiusH = (h-r.bmp.getHeight())/2;
		}
	}

	public void add(RoundData data) {
		mData.add(data);

		int d = 0, d2 = 360/mData.size();
		for(int i=0; i<mData.size(); i++) {
			RoundData r = mData.get(i);
			r.delta = d;
			d += d2;
		}
	}

	public void processBitmap() {
		for(int i=0; i<mData.size(); i++) {
			RoundData r = mData.get(i);

			r.x = mPoint.x + (float)(mRadiusW * Math.cos(r.delta*Math.PI/180));
			r.y = mPoint.y + (float)(mRadiusH * Math.sin(r.delta*Math.PI/180));

			float s = (r.y-mPoint.y)/(mRadiusH);
			s = mScale+(1-mScale)*(s+1)/2;
			mMatrix.setScale(s, s);

			r.bmp2 = Bitmap.createBitmap(r.bmp, 0, 0, r.bmp.getWidth(), r.bmp.getHeight(), mMatrix, true);
		}
	}

	private Point mTouch = new Point();
	private long mTs = 0;
	private float x2 = 0, y2 = 0;

	private void processTouch(float x, float y) {
		float d = Math.abs(x-x2)+Math.abs(y-y2);
		float s = this.doSpeed(d, 0);
		if (x-x2 > 0)
			s = -s;

		for(int i=0; i<mData.size(); i++) {
			RoundData r = mData.get(i);
			r.delta += s+360;
			r.delta %= 360;
		}
	}

	public void playPress() {
		AudioManager a = (AudioManager)mCtx.getSystemService(Context.AUDIO_SERVICE); 
        a.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN); 
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			processTouch(event.getX(), event.getY());
			processBitmap();
			invalidate();
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mTouch.x = (int) event.getX();
			mTouch.y = (int) event.getY();
			mTs = System.currentTimeMillis();

			if (mTimer != null) {
				mTimer.cancel();
				mTimer = null;
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			int d = (int) Math.abs(event.getX()-mTouch.x)+(int) Math.abs(event.getY()-mTouch.y);
			Boolean bClicked = false;

			if (d < 30) {
				int x, y, w, h;
				for(int i=0; i<mData.size(); i++) {
					RoundData r = mData.get(i);
					w = r.bmp2.getWidth()/2;
					h = r.bmp2.getHeight()/2;
					x = (int) Math.abs(event.getX()-r.x);
					y = (int) Math.abs(event.getY()-r.y);
					if (x<w && y<h) {
						playPress();
						r.onBitmapSelected();
						bClicked = true;
						break;
					}
				}
			}
			if (!bClicked) //??д???????,??????
				startCircle(event);
		}

		x2 = event.getX();
		y2 = event.getY();

		return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		for(int i=0; i<mData.size(); i++) {
			RoundData r = mData.get(i);
			if (r.delta < 180)
				continue;
			Bitmap b = r.bmp2;
			canvas.drawBitmap(b, r.x-b.getWidth()/2, r.y-b.getHeight()/2, null);
			//?????к????????????????
			//canvas.drawLine(mPoint.x, mPoint.y, r.x, r.y, mPaint);
		}
		for(int i=0; i<mData.size(); i++) {
			RoundData r = mData.get(i);
			if (r.delta >= 180)
				continue;
			Bitmap b = r.bmp2;
			canvas.drawBitmap(b, r.x-b.getWidth()/2, r.y-b.getHeight()/2, null);
		}
	}

	class RoundBitmap {
		Bitmap bmp;
		Bitmap bmp2;

		int delta;

		float x, y;
	}

	private float doSpeed(float s, long ts) {
		s = (s/mPoint.x/2)*180;
		if (ts > 0)
			s = 80*s/ts;
		if (Math.abs(s) > 30)
			s = 30;
		return s;
	}

	private float mCircleV = 0;
	private int mDirect = 1;

	private Timer mTimer;
	private void startCircle(MotionEvent e) {
		long ts = Math.abs(System.currentTimeMillis()-mTs);
		if (ts < 500) {
			if (e.getX()-mTouch.x > 0)
				mDirect = -1;
			else
				mDirect = 1;
			float d = Math.abs(e.getX()-mTouch.x)+Math.abs(e.getY()-mTouch.y);
			mCircleV = this.doSpeed(d, ts);

			mTimer = new Timer();
			mTimer.schedule(new tRun(), 80, 80);
		}
	}

	private class tRun extends java.util.TimerTask {
    	@Override
    	public void run() {
    		if (e_timer != null)
    			e_timer.sendMessage(e_timer.obtainMessage());
    	}
    }
}
