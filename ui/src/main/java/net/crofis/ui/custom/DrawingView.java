package net.crofis.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Tony Zaitoun on 4/16/2016.
 */
public class DrawingView extends View {
    private boolean haveTouch = false;
    private Rect touchArea;
    private Paint paint;

    private String COLOR ="#3498db";
    private int STROKE = 4;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.parseColor(COLOR));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE);
        haveTouch = false;
    }

    public DrawingView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.parseColor(COLOR));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE);
        haveTouch = false;
    }

    public void setHaveTouch(boolean val, Rect rect) {
        haveTouch = val;
        touchArea = rect;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(haveTouch){
            canvas.drawRect(touchArea.left, touchArea.top, touchArea.right, touchArea.bottom, paint);
            Log.i("CameraActivity","draw");
        }
    }

    public void setShapeColor(String color){
        COLOR = color;
        paint.setColor(Color.parseColor(COLOR));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE);
    }

    public void setStrokeSize(int size){
        STROKE = size;
        paint.setColor(Color.parseColor(COLOR));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE);
    }
}
