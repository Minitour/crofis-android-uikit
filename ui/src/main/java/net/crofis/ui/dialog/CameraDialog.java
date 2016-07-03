package net.crofis.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionButton;

import net.crofis.ui.R;
import net.crofis.ui.camera.CameraParams;
import net.crofis.ui.custom.PreviewSurfaceView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static android.view.Surface.ROTATION_0;
import static android.view.Surface.ROTATION_270;
import static android.view.Surface.ROTATION_90;

/**
 * Created by Tony Zaitoun on 4/13/2016.
 */
public class CameraDialog implements SurfaceHolder.Callback {

    /**Application context**/
    private Context context;

    /**Capture Button**/
    private FloatingActionButton captureButton;

    /**The Surface View**/
    private PreviewSurfaceView surfaceView;

    /**The Surface Holder**/
    private SurfaceHolder surfaceHolder;

    /**The Camera**/
    private Camera camera;

    /**The View of the dialog**/
    private View dialogView;

    /**The Root AlertDialog**/
    private AlertDialog dialog;

    /**In charge of the slide in animation**/
    private boolean allowAnimation = true;

    /**The image that was taken**/
    private Bitmap imageTaken= null;

    /**The view that covers the surface when in preview mode**/
    private View surfaceCover;

    /**The Image preview**/
    private ImageView previewIv;

    /**The confirm button**/
    private FloatingActionButton confirm;

    /**The cancel/delete button**/
    private FloatingActionButton cancel;

    /**The callback that is used when image is taken**/
    Camera.PictureCallback jpegCallback;

    /**The code to be executed after an image was taken**/
    private Runnable postImageTaken;

    /**The code to be executed after an image was deleted**/
    private Runnable postImageDeleted;

    /**Height and Width of the SurfaceView - will be overrided**/
    private int height= 1;
    private int width = 1;

    /**The resolution of the picture when taken**/
    private CameraParams.PICTURE_SIZE picture_size = CameraParams.PICTURE_SIZE.s1024x768;

    /**The resolution of the SurfaceView's preview**/
    private CameraParams.PREVIEW_SIZE preview_size = CameraParams.PREVIEW_SIZE.s800x600;

    /**The camera's angle - 90 degrees by default - for portrait**/
    private CameraParams.CAMERA_ANGLE camera_angle = CameraParams.CAMERA_ANGLE.A90;

    /**Boolean flag that indicates if the dialog is of type preview or of type capture, this does not change**/
    private boolean preview_mode = false;

    /**An integer that helps the dialog know if it is in capture mode or preview mode**/
    private int current_mode = 0; //0 = camera, 1 = preview

    /**Right side landscape orientation id**/
    private static final int ORIENTATION_LANDSCAPE_RIGHT = 0;

    /**Normal portrait orientation id**/
    private static final int ORIENTATION_PORTRAIT = 1;

    /**Left side landscape orientation id**/
    private static final int ORIENTATION_LANDSCAPE_LEFT = 2;

    /**The Current device orientation**/
    private int Orientation = 1;

    /**
     * Camera auto focus call back, used when user taps the screen, if interaction is enabled.
     */
    private Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            if (arg0){
                camera.cancelAutoFocus();
            }
        }
    };




    /**
     * Normal constructor.
     * @param context is the Application context.
     */
    public CameraDialog(Context context){
        this.context = context;
        setupOrientation();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        dialogView = inflater.inflate(R.layout.ui_camera_dialog, null);
        confirm = (FloatingActionButton) dialogView.findViewById(R.id.pos);
        cancel = (FloatingActionButton) dialogView.findViewById(R.id.neg);
        confirm.hide(false);
        cancel.hide(false);
        previewIv = (ImageView) dialogView.findViewById(R.id.preview);
        surfaceView = (PreviewSurfaceView) dialogView.findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceCover = dialogView.findViewById(R.id.surfaceCover);


        dialogView.findViewById(R.id.dialog_frame).post(new Runnable() {
            @Override
            public void run() {
                postDialogCreated();

            }
        });

        captureButton = (FloatingActionButton) dialogView.findViewById(R.id.camera);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_mode == 0) {
                    camera.takePicture(null, null, jpegCallback);
                    captureButton.hide(true);
                    current_mode = 1;
                }
            }
        });
        surfaceView.setListener(this);
        surfaceView.setFrame(dialogView.findViewById(R.id.frame));
        surfaceView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Display display = ((WindowManager) CameraDialog.this.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int orientation = display.getRotation();
                if(Orientation!=convertSystemOrientation(orientation)) {
                    dismiss();
                    show();
                }

            }
        });
        dialogView.findViewById(R.id.background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialog = dialogBuilder.create();
    }

    /**
     *
     * @param context is the Application context.
     * @param params is the camera settings.
     */
    public CameraDialog(Context context,CameraParams params){
        this.context = context;
        setupOrientation();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        dialogView = inflater.inflate(R.layout.ui_camera_dialog, null);
        confirm = (FloatingActionButton) dialogView.findViewById(R.id.pos);
        cancel = (FloatingActionButton) dialogView.findViewById(R.id.neg);
        confirm.hide(false);
        cancel.hide(false);
        previewIv = (ImageView) dialogView.findViewById(R.id.preview);
        surfaceView = (PreviewSurfaceView) dialogView.findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceCover = dialogView.findViewById(R.id.surfaceCover);
        if(params!=null) {
            this.picture_size = params.getPicture_size();
            this.preview_size = params.getPreview_size();
            this.camera_angle = params.getAngle();
        }


        dialogView.findViewById(R.id.dialog_frame).post(new Runnable() {
            @Override
            public void run() {
                postDialogCreated();

            }
        });

        captureButton = (FloatingActionButton) dialogView.findViewById(R.id.camera);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_mode == 0) {
                    camera.takePicture(null, null, jpegCallback);
                    captureButton.hide(true);
                    current_mode = 1;
                }
            }
        });
        surfaceView.setListener(this);
        surfaceView.setFrame(dialogView.findViewById(R.id.frame));
        surfaceView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Display display = ((WindowManager) CameraDialog.this.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int orientation = display.getRotation();
                if (Orientation != convertSystemOrientation(orientation)) {
                    dismiss();
                    show();
                }

            }
        });

        dialogBuilder.setView(dialogView);
        dialog = dialogBuilder.create();
    }

    /**
     * Image preview constructor, SurfaceView does not get initialized.
     * @param context is the application context.
     * @param image is the image that will be displayed on preview.
     */
    public CameraDialog(Context context, Bitmap image){
        this.context = context;
        setupOrientation();
        this.preview_mode = true;
        this.current_mode = 1;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        dialogView = inflater.inflate(R.layout.ui_camera_dialog, null);
        confirm = (FloatingActionButton) dialogView.findViewById(R.id.pos);
        cancel = (FloatingActionButton) dialogView.findViewById(R.id.neg);
        confirm.hide(false);
        cancel.hide(false);
        imageTaken = image;
        previewIv = (ImageView) dialogView.findViewById(R.id.preview);
        dialogView.findViewById(R.id.dialog_frame).post(new Runnable() {
            @Override
            public void run() {
                postDialogCreated();

            }
        });
        surfaceView.setListener(this);
        surfaceView.setFrame(dialogView.findViewById(R.id.frame));
        surfaceView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Display display = ((WindowManager) CameraDialog.this.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int orientation = display.getRotation();
                if (Orientation != convertSystemOrientation(orientation)) {
                    dismiss();
                    show();
                }

            }
        });
        surfaceCover = dialogView.findViewById(R.id.surfaceCover);
        captureButton = (FloatingActionButton) dialogView.findViewById(R.id.camera);
        dialogBuilder.setView(dialogView);
        dialog = dialogBuilder.create();
    }

    /**
     * Displays the dialog.
     * If CameraDialog was initialized with image, the SurfaceView's holder will not have a callback.
     * else, CameraDialog will be called normally.
     */
    public void show(){
        if (android.os.Build.VERSION.SDK_INT >= 23){
            String[] perms = {"android.permission.CAMERA"};
            int permsRequestCode = 200;
            if(!((context).checkSelfPermission(perms[0])==PackageManager.PERMISSION_GRANTED)) {
                ((Activity) context).requestPermissions(perms,permsRequestCode);
                return;
            }
        }

        this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.getWindow().getAttributes().dimAmount = 0;
        this.dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        if (allowAnimation) dialog.getWindow().getAttributes().windowAnimations = R.style.customDialogAnimation;

        onRotate();
        dialog.show();

        surfaceView.setVisibility(View.GONE);
        surfaceView.setVisibility(View.VISIBLE);
        if(imageTaken==null) {

            surfaceView.getHolder().addCallback(CameraDialog.this);

        }
        else{
            captureButton.hide(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPreview(imageTaken,true);
                }
            },100);


        }
        jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                CameraDialog.this.imageTaken = RotateBitmap(bmp,getAngle(camera_angle));
                showPreview(imageTaken);
            }
        };
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_mode == 1) {
                    CameraDialog.this.dismiss();
                    if (!preview_mode) if (postImageTaken != null)
                        (new Handler(Looper.getMainLooper())).post(postImageTaken);
                    current_mode = 0;
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_mode==1) {
                    if (!preview_mode) {
                        hidePreview();
                        imageTaken = null;
                    } else {
                        dismiss();
                    }
                    if (postImageDeleted != null)
                        (new Handler(Looper.getMainLooper())).post(postImageDeleted);
                    current_mode = 0;
                }
            }
        });
    }

    /**
     * Will preview the image that was taken.
     * @param image is the image that was taken.
     */
    private void showPreview(Bitmap image){

        previewIv.setImageBitmap(image);
        Animation animation = getGrowAnimation();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                previewIv.setVisibility(View.VISIBLE);
                surfaceCover.setVisibility(View.VISIBLE);
                confirm.show(true);
                cancel.show(true);
                //camera.stopPreview();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        previewIv.startAnimation(animation);

    }

    /**
     * showPreview with boolean 'withimage' is called only when CameraDialog is in preview mode only
     * and does the following:
     * - Hides the 'confirm' button.
     * - Changes the icon of the cancel button to 'ic_delete_white_24dp'
     * @param image
     * @param withimage
     */
    private void showPreview(Bitmap image,final boolean withimage){

        previewIv.setImageBitmap(image);
        Animation animation = getGrowAnimation();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                previewIv.setVisibility(View.VISIBLE);
                surfaceCover.setVisibility(View.VISIBLE);
                if(withimage) confirm.show(true);
                if(withimage) cancel.setImageResource(R.drawable.ic_delete_white_24dp);
                cancel.show(true);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //preview_frame.setVisibility(View.VISIBLE);
        //surfaceView.setVisibility(View.GONE);
        previewIv.startAnimation(animation);

    }

    /**
     * This method does the complete opposite of showPreview().
     * it hides the Preview ImageView and the cancel/confirm buttons,
     * and then shows the Camera Capture button.
     */
    private void hidePreview(){
        Animation animation = getShrinkAnimation();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // preview_frame.setVisibility(View.GONE);
                previewIv.setVisibility(View.GONE);
                surfaceCover.setVisibility(View.GONE);
                captureButton.show(true);
                confirm.hide(true);
                cancel.hide(true);
                camera.startPreview();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        previewIv.startAnimation(animation);

    }

    /**
     * returns shrink animation, only used by the Preview ImageView.
     * @return Animation object - shrink animation
     */
    private Animation getShrinkAnimation(){
        final float growTo = 0f;
        final long duration = 300;

        ScaleAnimation shrink = new ScaleAnimation(1, growTo, 1, growTo,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(duration / 2);

        AnimationSet growAndShrink = new AnimationSet(true);
        growAndShrink.setInterpolator(new LinearInterpolator());
        growAndShrink.addAnimation(shrink);
        return shrink;
    }

    /**
     * returns grow animation, only used by the Preview ImageView.
     * @return Animation object - grow animation
     */
    private Animation getGrowAnimation(){
        final float growTo = 1f;
        final long duration = 300;

        ScaleAnimation grow = new ScaleAnimation(0, growTo, 0, growTo,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        grow.setDuration(duration / 2);
        ScaleAnimation shrink = new ScaleAnimation(growTo, 1, growTo, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(duration / 2);
        shrink.setStartOffset(duration / 2);
        AnimationSet growAndShrink = new AnimationSet(true);
        growAndShrink.setInterpolator(new LinearInterpolator());
        growAndShrink.addAnimation(grow);
        growAndShrink.addAnimation(shrink);
        return grow;
    }

    /**
     * Dismiss the dialog.
     */
    public void dismiss(){
        dialog.dismiss();
    }

    /**
     * Run extra code after image was captured by setting a runnable on postImageTaken.
     * @param code this runnable will be executed after an image was captured.
     */
    public void setPostImageTaken(Runnable code){
        this.postImageTaken = code;
    }

    /**
     * Run code after image was deleted.
     * @param code this runnable will be executed after an image was deleted.
     */
    public void setPostImageDeleted(Runnable code){
        this.postImageDeleted = code;
    }

    /**
     * Refresh the surface view, private method.
     */
    private void refreshCamera() {
        if (surfaceHolder.getSurface() == null || camera == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        Camera.Parameters param;
        param = camera.getParameters();
        setUpCameraParams(param);
        camera.setParameters(param);

        camera.setDisplayOrientation(getAngle(this.camera_angle));
        try {

            camera.setPreviewDisplay(surfaceHolder);


            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        camera.startPreview();
                    } catch (NullPointerException e) {
                        refreshCamera();
                    }
                }
            });

        } catch (Exception e) {

        }
    }

    /**
     * Override method - called when surface is changed.
     * @param holder is the surface holder
     * @param format capture format
     * @param w surface width
     * @param h surface height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        refreshCamera();
    }

    /**
     * Override method - called once, only when surface is created.
     * @param holder is the SurfaceView's holder.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // open the camera
            camera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();

        // modify parameter
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        param.setPreviewSize(getPreviewWidth(this.preview_size), getPreviewHeight(this.preview_size));
        param.setPictureSize(getPictureWidth(this.picture_size),getPictureHeight(this.picture_size));
        try {
            camera.setParameters(param);
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        camera.setDisplayOrientation(getAngle(this.camera_angle));
        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            camera.setPreviewDisplay(surfaceHolder);
            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    camera.startPreview();
                }
            });
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
    }

    /**
     * Override method - called when surface is destroyed.
     * @param holder is the SurfaceView's holder.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // stop preview and release camera
        try{
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Get the dialogView.
     * @return The main dialog view.
     */
    public View getDialogView() {
        return dialogView;
    }

    /**
     * Get the Alert Dialog.
     * @return The main Alert Dialog.
     */
    public AlertDialog getDialog() {
        return dialog;
    }

    /**
     * Get the image that was captured.
     * @return The captured image as a bitmap.
     */
    public Bitmap getImageTaken() {
        return imageTaken;
    }

    /**
     * Get the SurfaceView
     * @return The Main SurfaceView.
     */
    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    /**
     * Get the capture button.
     * @return The CameraCapture FloatingActionButton.
     */
    public FloatingActionButton getCaptureButton() {
        return captureButton;
    }

    /**
     * Private method - only used to rotate images that were taken.
     * @param source original bitmap (bitmap to rotate).
     * @param angle the angle to rotate in.
     * @return a rotated bitmap.
     */
    private static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Called from PreviewSurfaceView to set touch focus.
     * @param - Rect - new area for auto focus
     */
    public void doTouchFocus(final Rect tfocusRect) {
        try {
            camera.autoFocus(myAutoFocusCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method checks what is the current orientation of the device in order to setup the camera correctly.
     */
    private void setupOrientation(){
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();
        Log.i("CameraDialog", "setupOrientation: "+orientation);
        switch (orientation){
            case 0:
                //portrait
                this.Orientation = 1;
                camera_angle = CameraParams.CAMERA_ANGLE.A90;
                break;
            case 1:
                //landscape left
                this.Orientation = 2;
                camera_angle = CameraParams.CAMERA_ANGLE.A0;
                picture_size = CameraParams.PICTURE_SIZE.s1280x720;
                preview_size = CameraParams.PREVIEW_SIZE.s1280x720;
                break;
            case 3:
                //landscape right
                this.Orientation = 0;
                camera_angle = CameraParams.CAMERA_ANGLE.A180;
                picture_size = CameraParams.PICTURE_SIZE.s1280x720;
                preview_size = CameraParams.PREVIEW_SIZE.s1280x720;
                break;
        }
    }

    /**
     * Helper method, used by net.crofis.ui.dialog.CameraDialog#onRotate() and by onLayoutChanged in constructors.
     *
     * @param systemOri - The Orientation (int) of the device.
     * @return The equivalent orientation.
     */
    private static int convertSystemOrientation(int systemOri){
        switch (systemOri){
            case ROTATION_0: return ORIENTATION_PORTRAIT;
            case ROTATION_90: return ORIENTATION_LANDSCAPE_LEFT;
            case ROTATION_270: return ORIENTATION_LANDSCAPE_RIGHT;
            default: return 1;
        }
    }

    /**
     * Helper method, only used when net.crofis.ui.dialog.CameraDialog#show() is called.
     * This method checks if the orientation has changed, and then modifies the views accordingly.
     */
    private void onRotate(){
        Display display = ((WindowManager) CameraDialog.this.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();
        if(Orientation!=convertSystemOrientation(orientation)) {
            switch (orientation) {
                case ROTATION_0:
                    //portrait
                    Orientation = 1;
                    camera_angle = CameraParams.CAMERA_ANGLE.A90;
                    break;
                case ROTATION_90:
                    //landscape left
                    Orientation = 2;
                    camera_angle = CameraParams.CAMERA_ANGLE.A0;
                    break;
                case ROTATION_270:
                    //landscape right
                    Orientation = 0;
                    camera_angle = CameraParams.CAMERA_ANGLE.A180;
                    break;
            }
            refreshCamera();
            Log.i("CameraDialog", "new Orientation: " + Orientation);
            postDialogCreated();
        }

    }

    private void setUpCameraParams(Camera.Parameters param){
        List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();

        List<Camera.Size> imageSizes = camera.getParameters().getSupportedPictureSizes();



        List<Camera.Size> widePreviewSizes = new ArrayList<>();
        List<Camera.Size> normalPreviewSizes = new ArrayList<>();

        List<Camera.Size> widePictureSizes = new ArrayList<>();
        List<Camera.Size> normalPictureSizes = new ArrayList<>();

        String previews = "", pictures = "";
        for (int i = 0; i < imageSizes.size(); i++) {
            Camera.Size s = imageSizes.get(i);

        }
        Log.i("CameraDialog", "preview sizes:" + previews + ", picture sizes:" + pictures);



        for (int i = 0; i < previewSizes.size(); i++) {
            Camera.Size s = previewSizes.get(i);
            if(((double) s.width/s.height ) > 1.33333333333 ){
                //16:9
                widePreviewSizes.add(s);
            }
            else{
                //4:3
                normalPreviewSizes.add(s);

            }

        }

        for (int i = 0; i < imageSizes.size(); i++) {
            Camera.Size s = imageSizes.get(i);
            //Log.i("CameraDialog","aspect: "+(double)s.height/s.width) ;
            if(((double) s.width/s.height ) > 1.33333333333 ){
                //16:9
                Log.i("CameraDialog","wide ["+s.height+","+s.width+"]");
                widePictureSizes.add(s);
            }
            else{
                //4:3
                Log.i("CameraDialog","norma; ["+s.height+","+s.width+"]");
                normalPictureSizes.add(s);

            }

        }


        int previewIndex = widePreviewSizes.size()/2;
        int pictureIndex = widePictureSizes.size()/2;
        Camera.Size bestPreview = widePreviewSizes.get(previewIndex);
        Camera.Size bestPicture = widePictureSizes.get(pictureIndex);
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        param.setPreviewSize(bestPreview.width, bestPreview.height);
        param.setPictureSize(bestPicture.width,bestPicture.height);

        Log.i("CameraDialog", "Selected: " + param.getPictureSize().width + "," + param.getPictureSize().height);
    }

    private void postDialogCreated(){
        if(Orientation==1) {
            CameraDialog.this.width = dialogView.findViewById(R.id.dialog_frame).getWidth();
            CameraDialog.this.height = (int) (width * 1.77777777778);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
            surfaceView.setLayoutParams(layoutParams);
            previewIv.setLayoutParams(layoutParams);
            surfaceCover.setLayoutParams(layoutParams);
        }else{
            CameraDialog.this.width = dialogView.findViewById(R.id.dialog_frame).getWidth();
            CameraDialog.this.height = (int) (width * 0.5625);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
            surfaceView.setLayoutParams(layoutParams);
            previewIv.setLayoutParams(layoutParams);
            surfaceCover.setLayoutParams(layoutParams);
        }
    }

    /**
     * Get the value by enum.
     * @param angle is angle.
     * @return the value of angle.
     */
    private static int getAngle(CameraParams.CAMERA_ANGLE angle){
        switch (angle){
            case A0: return 0;
            case A90: return 90;
            case A180: return 180;
            case A270: return 270;
        }
        return 0;
    }

    /**
     * @param size is the picture size.
     * @return get the height value of that size.
     */
    private static int getPictureHeight(CameraParams.PICTURE_SIZE size){
        switch(size){
            case s320x240:   return 240;
            case s640x480:   return 480;
            case s1280x720:  return 720;
            case s1024x768:
            case s1280x768:  return 768;
            case s1280x920:  return 920;
            case s1600x1200: return 1200;
            case s2048x1536: return 1536;
            case s2560x1440: return 1440;
            case s2560x1536: return 1536;
            case s2560x1920: return 1920;
        }
        return 0;
    }

    /**
     * @param size is the picture size.
     * @return get the width value of that size.
     */
    private static int getPictureWidth(CameraParams.PICTURE_SIZE size){
        switch(size){
            case s320x240:   return 320;
            case s640x480:   return 640;
            case s1024x768: return 1024;
            case s1280x720:
            case s1280x768:
            case s1280x920:  return 1280;
            case s1600x1200: return 1600;
            case s2048x1536: return 2048;
            case s2560x1440:
            case s2560x1536:
            case s2560x1920: return 2560;
        }
        return 0;
    }

    /**
     * @param size is the preview size.
     * @return get the height value of that size.
     */
    private static int getPreviewHeight(CameraParams.PREVIEW_SIZE size){
        switch(size){
            case s176x144:  return 144;
            case s320x240:  return 240;
            case s352x288:  return 288;
            case s480x320:  return 320;
            case s480x368:  return 368;
            case s640x480:
            case s800x480:
            case s864x480:  return 480;
            case s800x600:  return 600;
            case s864x576:  return 576;
            case s960x540:  return 540;
            case s1280x720: return 720;
            case s1280x768: return 768;
            case s1280x960: return 960;
        }
        return 0;
    }

    /**
     * @param size is the preview size.
     * @return get the width value of that size.
     */
    private static int getPreviewWidth(CameraParams.PREVIEW_SIZE size){
        switch(size){
            case s176x144:  return 176;
            case s320x240:  return 320;
            case s352x288:  return 352;
            case s480x320:
            case s480x368:  return 480;
            case s640x480:  return 640;
            case s800x480:
            case s800x600:  return 800;
            case s864x480:
            case s864x576:  return 864;
            case s960x540:  return 960;
            case s1280x720:
            case s1280x768:
            case s1280x960: return 1280;
        }
        return 0;
    }


}
