package com.dnake.widget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class RoundData {
	public Bitmap bmp;
	public Bitmap bmp2;
	public int delta;
	public float x, y;

	public String label;
	public Intent intent;

	public Activity parent;

	public void onBitmapSelected() {
		parent.startActivity(intent);
	}

	public void drawText() {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.rgb(0x60, 0x60, 0x60));
		paint.setTextSize(20);

		Rect bounds = new Rect();
		paint.getTextBounds(label, 0, label.length(), bounds);

		Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight()+bounds.height(), android.graphics.Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(bmp, 0, 0, paint);

		int x = (bitmap.getWidth() - bounds.width())/2;
		canvas.drawText(label, x, bitmap.getHeight()-8, paint);

		bmp = bitmap;
	} 
}
