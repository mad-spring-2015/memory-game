package edu.uncc.mad.memorygame.ui;

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
	private static final int DEFAULT_RADIUS = 100;
	public static final int DEFAULT_ALPHA = 20;

	public Circle(Context context, int color) {
		super(context);
		this.color = color;
		this.radius = DEFAULT_RADIUS;
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
		drawable.getPaint().setAlpha(DEFAULT_ALPHA);
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
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
		/* int desiredWidth = radius;
		    int desiredHeight = radius;

		    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		    int width;
		    int height;

		    //Measure Width
		    if (widthMode == MeasureSpec.EXACTLY) {
		        //Must be this size
		        width = widthSize;
		    } else if (widthMode == MeasureSpec.AT_MOST) {
		        //Can't be bigger than...
		        width = Math.min(desiredWidth, widthSize);
		    } else {
		        //Be whatever you want
		        width = desiredWidth;
		    }

		    //Measure Height
		    if (heightMode == MeasureSpec.EXACTLY) {
		        //Must be this size
		        height = heightSize;
		    } else if (heightMode == MeasureSpec.AT_MOST) {
		        //Can't be bigger than...
		        height = Math.min(desiredHeight, heightSize);
		    } else {
		        //Be whatever you want
		        height = desiredHeight;
		    }

		    //MUST CALL THIS
		    setMeasuredDimension(width, height);*/
		}

	/*
	 * int w = radius; int h = radius; setMeasuredDimension(w, h); }
	 */

	@Override
	protected void onDraw(Canvas canvas) {
		drawable.draw(canvas);
		super.onDraw(canvas);
	}

	public int getColor() {
		return color;
	}
	public int getTransparency(){
		return drawable.getPaint().getAlpha();
	}
	public void setTransparency(int alpha){
		drawable.getPaint().setAlpha(alpha);
		invalidate();
	}
}
