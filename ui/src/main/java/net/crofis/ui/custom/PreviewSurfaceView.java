package net.crofis.ui.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import net.crofis.ui.camera.CameraActivity;
import net.crofis.ui.camera.SquareCameraActivity;
import net.crofis.ui.dialog.CameraDialog;

/**
 * Created by Tony Zaitoun on 4/16/2016.
 */
public class PreviewSurfaceView extends SurfaceView {

    /**The parent class that is holding the surface view**/
    private Object camPreview;

    /**Tracker that checks if a lister was set or not**/
    private boolean listenerSet = false;

    /**Tracker that checks if a frame was set or not**/
    private boolean frame_set;

    /**The frame that holds the surface view**/
    private View frame;

    /**Tracker that checks if the drawing was removed, is used to prevent multiple drawings at a time**/
    private boolean isRemoved=true;

    /**The drawing object**/
    private DrawingView dv;

    /**Flag that checks if interaction is enabled**/
    public boolean allow_touch=true;

    /**The color of the drawing view**/
    private String focus_border_color;

    /**The stroke size of the drawing view**/
    private int focus_border_stroke = 0;

    /**The type of the camPreview (ranges from 0~2)**/
    private int type = 0;

    public int squareSize = 100;

    /**
     * Default constructors
     */
    public PreviewSurfaceView(Context context) {
        super(context);
    }

    public PreviewSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PreviewSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    /**
     * onTouchEvent, overrided to handle foucs touchs.
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!allow_touch) return false; //check if interaction is enabled.

        if(!listenerSet) return false; //check if surface has parent.

        //case action type is ACTION_DOWN
        if(event.getAction() == MotionEvent.ACTION_DOWN){

            //get touch location
            float x = event.getX();
            float y = event.getY();
            squareSize = this.getWidth()/12;
            //create rect
            Rect touchRect = new Rect(
                    (int)(x - squareSize),
                    (int)(y - squareSize),
                    (int)(x + squareSize),
                    (int)(y + squareSize));


            final Rect targetFocusRect = new Rect(
                    touchRect.left * 2000/this.getWidth() - 1000,
                    touchRect.top * 2000/this.getHeight() - 1000,
                    touchRect.right * 2000/this.getWidth() - 1000,
                    touchRect.bottom * 2000/this.getHeight() - 1000);

            //check which kind of method to call according to the type of the parent class.
            switch(type){
                /**
                 * init the focus callback from the parent class.
                 */
                case 0:
                    ((CameraActivity) camPreview).doTouchFocus(targetFocusRect);
                    break;
                case 1:
                    ((SquareCameraActivity)camPreview).doTouchFocus(targetFocusRect);
                    break;
                case 2:
                    ((CameraDialog) camPreview).doTouchFocus(targetFocusRect);
                    break;
            }

            //check if a frame was set and if the drawing view was already removed.
            if(frame_set&&isRemoved){
                isRemoved=false; //set isRemoved to false to avoid multiple focus views.
                dv = new DrawingView(getContext()); //initialize the drawing view.
                //noinspection ResourceType
                dv.setShapeColor(getResources().getString(android.R.color.white)); //set the shape color.
                if(focus_border_color!=null) dv.setShapeColor(focus_border_color);
                if(focus_border_stroke>0) dv.setStrokeSize(focus_border_stroke);//set the shape stroke size.

                dv.setHaveTouch(true, touchRect); //set the position of the drawing view object.
                ((FrameLayout) frame).addView(dv); //add the drawing view object to the FrameLayout that is wrapping the SurfaceView.

                //Special case fix for Camera Dialog
                if(type==2) {
                    //Create a FrameLayout Parameter object with the height of the current SurfaceView.
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getLayoutParams();
                    params.height = this.getHeight();
                    //Set the params on the FrameLayout, this will force it to stay in the same size and not expand.
                    frame.setLayoutParams(params);
                }

                //Invalidate the view.
                dv.invalidate();

                //Create handler object to remove the object.
                Handler handler = new Handler();

                //set post delay 1.5 seconds.
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Re-position the view.
                        dv.setHaveTouch(false, new Rect(0, 0, 0, 0));
                        dv.invalidate();

                        //remove the view from the frame layout.
                        ((FrameLayout) frame).removeView(dv);

                        //change the isRemoved flag to true.
                        isRemoved = true;
                    }
                }, 1500);

            }
        }

        return false;
    }


    public void setListener(CameraActivity camPreview) {
        this.camPreview = camPreview;
        listenerSet = true;
        type = 0;
    }

    public void setListener(SquareCameraActivity camPreview){
        this.camPreview = camPreview;
        listenerSet = true;
        type = 1;
    }

    public void setListener(CameraDialog camPreview){
        this.camPreview = camPreview;
        listenerSet = true;
        type = 2;
    }

    public void setFrame(View frame){
        this.frame = frame;
        frame_set= true;
    }



}

