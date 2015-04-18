package edu.uncc.mad.memorygame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;

public class Circle extends View {
	private ShapeDrawable drawable;
	private int color;
	private int radius;
	private static final int default_radius = 50;

	public Circle(Context context, int color) {
		super(context);
		this.color = color;
		this.radius = default_radius;
		initCircle();
	}

	public Circle(Context context, int color, int radius) {
		super(context);
		this.color = color;
		this.radius = radius;
		initCircle();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void initCircle() {
		OvalShape circle = new OvalShape();
		circle.resize(radius, radius);
		drawable = new ShapeDrawable(circle);
		drawable.getPaint().setColor(color);
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			this.setBackgroundDrawable(drawable);
		} else {
			this.setBackground(drawable);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean hasOverlappingRendering() {
		return false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/*
		 * int w = resolveSizeAndState(radius, widthMeasureSpec, 1); int h =
		 * resolveSizeAndState(radius, heightMeasureSpec, 1);
		 */
		int w = radius;
		int h = radius;
		setMeasuredDimension(w, h);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawable.draw(canvas);
		super.onDraw(canvas);
	}

	public int getColor() {
		return color;
	}

}
