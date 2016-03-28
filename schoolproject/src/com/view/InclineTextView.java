package com.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class InclineTextView extends TextView {

	public InclineTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// ��б��45,�������Ҿ���
		canvas.rotate(45, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
		super.onDraw(canvas);
	}

}