package net.crofis.ui.camera;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;

import net.crofis.ui.R;
import net.crofis.ui.custom.PreviewSurfaceView;
import net.crofis.ui.custom.actionitem.ActionItem;
import net.crofis.ui.custom.actionitem.ActionItemClickListener;
import net.crofis.ui.custom.actionitem.UIAlertAction;
import net.crofis.ui.custom.cropper.CropImageView;
import net.crofis.ui.dialog.ActionDialog;
import net.crofis.ui.dialog.DialogManager;
import net.crofis.ui.dialog.InfoDialog;
import net.crofis.ui.dialog.LoadingDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Tony Zaitoun on 5/9/2016.
 */
public class SquareCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback,SensorEventListener {

    /**Class TAG - used for logging**/
    private final static String TAG = "CameraActivity";

    /**The Surface View**/
    private PreviewSurfaceView surfaceView;

    /**The Surface Holder**/
    private SurfaceHolder surfaceHolder;

    /**The Camera**/
    private Camera camera;

    /**The view that overlays the surfaceview**/
    private LinearLayout hider;

    /**Capture Button**/
    private ImageView captureButton;

    /**The confirm button**/
    private FloatingActionButton confirm;

    /**The cancel/delete button**/
    private FloatingActionButton cancel;

    private ImageView cropImageButton;

    private ImageView flashToggleButton;

    private ImageView switchCamButton;

    private View surfaceCover;

    /**In charge of the slide in animation**/
    private boolean allowAnimation = true;

    /**The image that was taken**/
    private Bitmap imageTaken= null;

    /**The Image preview**/
    private ImageView previewIv;

    /**The callback that is used when image is taken**/
    Camera.PictureCallback jpegCallback;

    /**The resolution of the picture when taken**/
    private CameraParams.PICTURE_SIZE picture_size = CameraParams.PICTURE_SIZE.s1280x720;

    /**The resolution of the SurfaceView's preview**/
    private CameraParams.PREVIEW_SIZE preview_size = CameraParams.PREVIEW_SIZE.s1280x720;

    /**The camera's angle - 90 degrees by default - for portrait**/
    private CameraParams.CAMERA_ANGLE camera_angle = CameraParams.CAMERA_ANGLE.A90;


    /**The current camera**/
    private  int cam_view = 0;

    /**The flag that is used to check if the flash is enabled by the user**/
    private boolean allow_flash= false;

    /**Request code (used for result)**/
    public static final int REQUEST_CODE = 510;

    /**Result code, 0 = bad result, 1 = good result**/
    private int RESULT_CODE = 0;

    /**Flag that is meant to track if device has flash**/
    private boolean isFlashAvailable = true;

    /**Flag that is used to check if the camera is busy**/
    private boolean isTakingPicture = false;

    /**Sensor manger, is used to mange the sensors.**/
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;

    /**The Current device orientation**/
    private int Orientation = 1;

    /**Flag that is used to track if device has screen rotation enabled**/
    private boolean allow_rotation=true;

    /**Flag that checks if zoom is currently possible**/
    private boolean allow_zoom = true;

    /**The minimum zoom value,CANNOT BE 0**/
    private static final float MIN_ZOOM = 1f;

    /**The max zoom value, CANNOT BE GREATER THAN 100**/
    private static final float MAX_ZOOM = 5f;

    /**scaleFactor is the range of MIN_ZOOM and MAX_ZOOM, DO NOT CHANGE**/
    private float scaleFactor = 1.f;

    /**The gesture detector, used to detect pinch to zoom**/
    private ScaleGestureDetector detector;

    /**The aspect ratio of the surface view**/
    private double messure = 1.77777777778;


    /**
     * The Following are flags that are used to start activity with custom values.
     * All of these, if not initialized, will receive a default value of true.
     **/
    private boolean SET_CROP_OPTIONAL;
    private boolean ALLOW_IMAGE_CROP;
    private boolean DISPLAY_FLASH_TOGGLE;
    private boolean DISPLAY_SWITCH_CAM;
    private boolean ALLOW_ZOOM_GESTURE;
    private boolean ALLOW_ROTATION_ANIMATION;
    private boolean SAVE_FILE_TO_STORAGE;
    private boolean SHOW_PROGRESS_BAR;
    private int CROP_ASPECT_RATIO;
    private boolean RETURN_DATA_AS_BYTE_ARRAY;
    private static final int CROP_RATIO_CUSTOM = -2;
    public static final int CROP_RATIO_1_1 = 0;
    public static final int CROP_RATIO_2_1 = 1;
    public static final int CROP_RATIO_4_3 = 2;
    public static final int CROP_RATIO_16_9 = 3;
    private int cropAspectX = -1;
    private int cropAspectY= - 1;

    /**
     * Public static final Keys that are used to start the activity with custom parameters.
     */
    public static final String FLAG_ALLOW_ORIENTATION_CHANGE = "ALLOW_ORIENTATION_CHANGE";
    public static final String FLAG_SET_CROP_ASPECT_RATIO = "CROP_ASPECT_RATIO";
    public static final String FLAG_SHOW_PROGRESS_BAR = "SHOW_PROGRESS_BAR";
    public static final String FLAG_DISPLAY_FLASH_TOGGLE = "DISPLAY_FLASH_TOGGLE";
    public static final String FLAG_DISPLAY_SWITCH_CAM = "DISPLAY_SWITCH_CAM";
    public static final String FLAG_ALLOW_ZOOM_GESTURE = "ALLOW_ZOOM_GESTURE";
    public static final String FLAG_ALLOW_ROTATION_ANIMATION = "ALLOW_ROTATION_ANIMATION";
    public static final String FLAG_LOAD_CAM = "LOAD_CAMERA";
    public static final String FLAG_SAVE_TO_STORAGE = "SAVE_FILE_TO_STORAGE";
    public static final String FLAG_SET_CROP_OPTIONAL = "SET_CROP_OPTIONAL";
    public static final String FLAG_RETURN_DATA_AS_BYTE_ARRAY = "RETURN_DATA_AS_BYTE_ARRAY";

    public static final String DIR_NAME = "Crofis";

    /**The id of the back camera (The high-res cam)**/
    private static final int BACK_CAM_ID = 0;

    /**The id of the front camera (Selfie camera)**/
    private static final int FRONT_CAM_ID = 1;

    /**Right side landscape orientation id**/
    private static final int ORIENTATION_LANDSCAPE_RIGHT = 0;

    /**Normal portrait orientation id**/
    private static final int ORIENTATION_PORTRAIT = 1;

    /**Left side landscape orientation id**/
    private static final int ORIENTATION_LANDSCAPE_LEFT = 2;

    private int SurfaceHeight = 0;
    private int SurfaceWidth = 0;


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

    /**Tracker - used to track the preview's status (UI preview not camera preview) **/
    private int CURRENT_PREVIEW_STATUS = 0;
   // private CropImageView imageCropper;


    /**
     * Used to show the FlashToggle button.
     */
    private void showFlashToggle(){
        if(DISPLAY_FLASH_TOGGLE) flashToggleButton.setVisibility(View.VISIBLE);
    }

    /**
     * Used to show the CameraToggle button.
     */
    private void showCamToggle(){
        if(DISPLAY_SWITCH_CAM) switchCamButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //Here you can get the size!
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
        params.height =(int) (surfaceView.getWidth()*messure);
        surfaceView.setLayoutParams(params);

        SurfaceHeight = surfaceView.getHeight();
        SurfaceWidth = surfaceView.getWidth();

        int gap = this.findViewById(android.R.id.content).getRootView().getHeight() - SurfaceWidth;
        params = (FrameLayout.LayoutParams) hider.getLayoutParams();
        //params.height = (SurfaceHeight - SurfaceWidth);
        params.height = gap/2;
        hider.setLayoutParams(params);

        View toolbar =  findViewById(R.id.top_bar);
        Log.i(TAG,"class type "+toolbar.getLayoutParams().getClass());
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
        params1.height = gap/2;
        toolbar.setLayoutParams(params1);


        params = (FrameLayout.LayoutParams) previewIv.getLayoutParams();
        params.height = SurfaceWidth;
        previewIv.setLayoutParams(params);
        setSurfaceCover(false);


    }



    @Override
    public void onBackPressed() {
        switch(CURRENT_PREVIEW_STATUS){
            case 0:
                super.onBackPressed();
                break;
            case 1:
                hidePreview(true);
                imageTaken = null;
                RESULT_CODE = 0;
                break;
            case 2:
                findViewById(R.id.camera_view).setVisibility(View.VISIBLE);
                findViewById(R.id.crop_view_layout).setVisibility(View.GONE);
                if(!SET_CROP_OPTIONAL) {
                    hidePreview(true);
                    imageTaken = null;
                    RESULT_CODE = 0;
                    CURRENT_PREVIEW_STATUS = 0;
                }
                else CURRENT_PREVIEW_STATUS = 1;
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_camera);

        /**
         * Initialize camera with custom parameters.
         */
        Intent intent = getIntent();
        allow_rotation = intent.getBooleanExtra(FLAG_ALLOW_ORIENTATION_CHANGE,true);
        DISPLAY_FLASH_TOGGLE = intent.getBooleanExtra(FLAG_DISPLAY_FLASH_TOGGLE,true);
        DISPLAY_SWITCH_CAM = intent.getBooleanExtra(FLAG_DISPLAY_SWITCH_CAM,true);
        ALLOW_ZOOM_GESTURE = intent.getBooleanExtra(FLAG_ALLOW_ZOOM_GESTURE, true);
        ALLOW_ROTATION_ANIMATION = intent.getBooleanExtra(FLAG_ALLOW_ROTATION_ANIMATION,true);
        //ALLOW_IMAGE_CROP = intent.getBooleanExtra(FLAG_ALLOW_IMAGE_CROP,false);
        SET_CROP_OPTIONAL = intent.getBooleanExtra(FLAG_SET_CROP_OPTIONAL,false);
       //if(!SET_CROP_OPTIONAL) ALLOW_IMAGE_CROP = intent.getBooleanExtra(FLAG_ALLOW_CROP,false);
        SAVE_FILE_TO_STORAGE = intent.getBooleanExtra(FLAG_SAVE_TO_STORAGE,false);
        if(SAVE_FILE_TO_STORAGE) RETURN_DATA_AS_BYTE_ARRAY = intent.getBooleanExtra(FLAG_RETURN_DATA_AS_BYTE_ARRAY,true);
        SHOW_PROGRESS_BAR = intent.getBooleanExtra(FLAG_SHOW_PROGRESS_BAR,true);
        CROP_ASPECT_RATIO = intent.getIntExtra(FLAG_SET_CROP_ASPECT_RATIO,-1);
        if(CROP_ASPECT_RATIO == -2) {
            cropAspectX = intent.getIntExtra("cropAspectX",-1);
            cropAspectY = intent.getIntExtra("cropAspectY",-1);
        }
        cam_view = intent.getIntExtra(FLAG_LOAD_CAM,BACK_CAM_ID);

        if(cam_view!=BACK_CAM_ID||cam_view!=FRONT_CAM_ID) cam_view =BACK_CAM_ID; //make sure cam_view is either 0 or 1

        /**
         * Initialize sensors.
         */
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        hider = (LinearLayout)findViewById(R.id.hider);
        surfaceView = (PreviewSurfaceView) findViewById(R.id.surfaceView);
        detector = new ScaleGestureDetector(this, new ScaleListener());
        surfaceHolder = surfaceView.getHolder();

        /**
         * Initialize main intractable views.
         */
        captureButton = (ImageView) findViewById(R.id.camera);
        confirm = (FloatingActionButton) findViewById(R.id.pos);
        cancel = (FloatingActionButton) findViewById(R.id.neg);
        flashToggleButton = (ImageView) findViewById(R.id.toggleFlash);
        switchCamButton= (ImageView) findViewById(R.id.toggleCam);
        previewIv = (ImageView) findViewById(R.id.preview);
        ((SmoothProgressBar)findViewById(R.id.progress_bar)).progressiveStop();
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        cropImageButton = (ImageView) findViewById(R.id.cropImage);


        cropImageButton.setOnClickListener(new View.OnClickListener() {

            Bitmap output;
            @Override
            public void onClick(View v) {
                CURRENT_PREVIEW_STATUS = 2;
                if(cam_view == 1) output = codec(imageTaken, Bitmap.CompressFormat.JPEG,100);
                else output = imageTaken;
                final CropImageView imageCropper = new CropImageView(SquareCameraActivity.this);
                imageCropper.setAutoZoomEnabled(true);
                imageCropper.setScaleType(CropImageView.ScaleType.FIT_CENTER);
                ((FrameLayout)findViewById(R.id.crop_view_parent)).addView(imageCropper);
                imageCropper.setImageBitmap(output);
                imageCropper.setCropRect(new Rect(0, 0, output.getWidth(), output.getHeight()));
                imageCropper.setAutoZoomEnabled(true);
                imageCropper.setScaleType(CropImageView.ScaleType.FIT_CENTER);
                if(CROP_ASPECT_RATIO!=-1) imageCropper.setFixedAspectRatio(true);
                findViewById(R.id.action_change_ratio).setVisibility(View.GONE);
                switch(CROP_ASPECT_RATIO){
                    case CROP_RATIO_1_1:
                        imageCropper.setAspectRatio(1,1);
                        break;
                    case CROP_RATIO_2_1:
                        imageCropper.setAspectRatio(2,1);
                        break;
                    case CROP_RATIO_4_3:
                        imageCropper.setAspectRatio(4,3);
                        break;
                    case CROP_RATIO_16_9:
                        imageCropper.setAspectRatio(16,9);
                        break;
                    case CROP_RATIO_CUSTOM:
                        if(cropAspectX <= 0 || cropAspectY <= 0) break;
                        imageCropper.setAspectRatio(cropAspectX,cropAspectY);
                        break;
                    default:
                        findViewById(R.id.action_change_ratio).setVisibility(View.VISIBLE);
                        break;
                }

                findViewById(R.id.crop_view_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.camera_view).setVisibility(View.GONE);
                final ImageView restore = (ImageView)findViewById(R.id.action_restore);
                final ImageView rotateButtonRight =(ImageView) findViewById(R.id.action_rotate_right);
                final ImageView rotateButtonLeft =(ImageView) findViewById(R.id.action_rotate_left);
                final ImageView confirmButton =(ImageView) findViewById(R.id.action_done);
                final ImageView cancelButton =(ImageView) findViewById(R.id.action_cancel);

                restore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageCropper.resetCropRect();
                    }
                });

                rotateButtonRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageCropper.rotateImage(90);
                    }
                });

                rotateButtonLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageCropper.rotateImage(-90);
                    }
                });

                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        output = imageCropper.getCroppedImage();//RotateBitmap(imageCropper.getBitmap(),imageCropper.getRotatedDegrees());
                        imageTaken = output;
                        confirmImage();
                    }
                });

                final String [] ratios = getResources().getStringArray(R.array.aspect_ratios);
                findViewById(R.id.action_change_ratio).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //mCropImageView.setAspectRatio();
                        ArrayList<ActionItem> items = new ArrayList<ActionItem>();
                        for (int i = 0; i < ratios.length; i++) {
                            final int finalI = i;
                            items.add(new UIAlertAction(null, ratios[i], new ActionItemClickListener() {
                                @Override
                                public void onActionSelected() {
                                    if(finalI==0){
                                        //Original was clicked
                                        imageCropper.setFixedAspectRatio(false);
                                    }else if(finalI == ratios.length-1){
                                        //Cancel was clicked.
                                    }else{
                                        imageCropper.setFixedAspectRatio(true);
                                        int ratio2;
                                        int ratio1;
                                        try {
                                            //Validate aspect ratio format.
                                            if(ratios[finalI].length()-1 != ratios[finalI].replace(":","").length()) throw new NumberFormatException("Invalid Aspect! Correct format is 'x:y'!");

                                            //Parse String to Integers.
                                            ratio1 = Integer.parseInt(ratios[finalI].split(":")[0]);
                                            ratio2 = Integer.parseInt(ratios[finalI].split(":")[1]);

                                            //Check if integers are not 0 to avoid error.
                                            if(ratio1 == 0 || ratio2 == 0) throw new ArithmeticException("Ratio Cannot contain 0 value!");
                                            imageCropper.setAspectRatio(ratio1,ratio2 );
                                        }catch (NumberFormatException e){
                                            //Handle number format exception.
                                            e.printStackTrace();
                                            Log.e(TAG,"Invalid Aspect Format.");
                                            imageCropper.setFixedAspectRatio(false);

                                        }catch (ArithmeticException e){
                                            //Handle zero values.
                                            e.printStackTrace();
                                            Log.e(TAG,"Invalid Aspect given.");
                                            imageCropper.setFixedAspectRatio(false);
                                        }

                                    }
                                }
                            }));
                        }


                        final ActionDialog dialog = new ActionDialog(SquareCameraActivity.this, items);
                        dialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                hide();
                            }
                        });
                        DialogManager.setDialogPosition(dialog, dialog.getDialog(), Gravity.BOTTOM);
                        dialog.show();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageCropper.clearImage();
                        imageCropper.resetCropRect();
                        ((FrameLayout)findViewById(R.id.crop_view_parent)).removeAllViews();
                        findViewById(R.id.camera_view).setVisibility(View.VISIBLE);
                        findViewById(R.id.crop_view_layout).setVisibility(View.GONE);
                        CURRENT_PREVIEW_STATUS = 1;
                        //hidePreview(true);
                        //imageTaken = null;
                        RESULT_CODE = 1;
                    }
                });
            }
        });



        /**
         * Check if flash is available (in the device).
         */
        if(!isFlashAvailable(this)) {
            isFlashAvailable = false;
            flashToggleButton.setVisibility(View.GONE);
        }

        /**
         * Check if device has camera.
         */
        if(!isCameraAvailable(this)) {
            switchCamButton.setVisibility(View.GONE);
            final InfoDialog dialog = DialogManager.makeDialog(this,getString(R.string.camera_error_nocam),getString(R.string.camera_error_nocam_msg));
            dialog.setPostiveButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("data", (byte[]) null);
                    setResult(RESULT_CODE,intent);
                    dialog.dismiss();
                    SquareCameraActivity.this.finish();
                }
            });
            dialog.show();
        }

        switchCamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlipCam();
            }
        });

        confirm.hide(false);
        cancel.hide(false);

        confirm.setOnClickListener(new View.OnClickListener() {
            public Bitmap output;

            @Override
            public void onClick(View v) {

                if(false){
                    findViewById(R.id.camera_view).setVisibility(View.GONE);
                    findViewById(R.id.crop_view_layout).setVisibility(View.VISIBLE);
                    final CropImageView imageCropper = new CropImageView(SquareCameraActivity.this);
                    imageCropper.setAutoZoomEnabled(true);
                    imageCropper.setScaleType(CropImageView.ScaleType.FIT_CENTER);
                    imageCropper.setAspectRatio(1, 1);
                    imageCropper.setFixedAspectRatio(true);
                    ((FrameLayout)findViewById(R.id.crop_view_parent)).addView(imageCropper);
                    CURRENT_PREVIEW_STATUS = 2;
                    if(cam_view == 1) output = codec(imageTaken, Bitmap.CompressFormat.JPEG,100);
                    else output = imageTaken;
                    imageCropper.setImageBitmap(output);
                    imageCropper.setCropRect(new Rect(0, 0, output.getWidth(), output.getHeight()));
                    if(CROP_ASPECT_RATIO!=-1) imageCropper.setFixedAspectRatio(true);
                    switch(CROP_ASPECT_RATIO){
                        case CROP_RATIO_1_1:
                            imageCropper.setAspectRatio(1,1);
                            break;
                        case CROP_RATIO_2_1:
                            imageCropper.setAspectRatio(2,1);
                            break;
                        case CROP_RATIO_4_3:
                            imageCropper.setAspectRatio(4,3);
                            break;
                        case CROP_RATIO_16_9:
                            imageCropper.setAspectRatio(16,9);
                            break;
                    }
                    Log.i(TAG,"width="+output.getWidth()+" height="+output.getHeight());

                    final ImageView restore = (ImageView)findViewById(R.id.action_restore);
                    final ImageView rotateButtonRight =(ImageView) findViewById(R.id.action_rotate_right);
                    final ImageView rotateButtonLeft =(ImageView) findViewById(R.id.action_rotate_left);
                    final ImageView confirmButton =(ImageView) findViewById(R.id.action_done);
                    final ImageView cancelButton =(ImageView) findViewById(R.id.action_cancel);

                    restore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageCropper.resetCropRect();
                        }
                    });

                    rotateButtonRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageCropper.rotateImage(90);
                        }
                    });

                    rotateButtonLeft.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageCropper.rotateImage(-90);
                        }
                    });

                    confirmButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            output = imageCropper.getCroppedImage();//RotateBitmap(imageCropper.getBitmap(),imageCropper.getRotatedDegrees());
                            imageTaken = output;
                            confirmImage();
                        }
                    });

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageCropper.clearImage();
                            imageCropper.resetCropRect();
                            ((FrameLayout)findViewById(R.id.crop_view_parent)).removeAllViews();
                            findViewById(R.id.camera_view).setVisibility(View.VISIBLE);
                            findViewById(R.id.crop_view_layout).setVisibility(View.GONE);
                            hidePreview(true);
                            imageTaken = null;
                            RESULT_CODE = 0;
                        }
                    });
                }
                else{
                    /**
                     * Confirm photo:
                     */
                    confirmImage();
                }



            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePreview(true);
                imageTaken = null;
                RESULT_CODE = 0;
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if(!isTakingPicture) {
                    surfaceView.allow_touch = false;
                    toggleFlashSetting(allow_flash);
                    //hideUI(true);
                    camera.takePicture(null, null, jpegCallback);
                }
                isTakingPicture=true;


            }
        });

        jpegCallback = new Camera.PictureCallback() {
            /**
             * The Capture call back:
             * converts the image to bitmap, and apply it to imageTaken.
             * Rotate the bitmap according to current orientation and change the result code to 1.
             * @param data the image in byte array
             * @param camera the camera object
             */
            public void onPictureTaken(final byte[] data,final Camera camera) {
                new AsyncTask<Void,Void,Void>(){
                    SmoothProgressBar bar;
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if(SAVE_FILE_TO_STORAGE) {
                           bar = (SmoothProgressBar) findViewById(R.id.progress_bar);
                           if(SHOW_PROGRESS_BAR) bar.setVisibility(View.VISIBLE);
                            if(SHOW_PROGRESS_BAR) bar.progressiveStart();
                        }
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        imageTaken = null;
                        imageTaken  = BitmapFactory.decodeByteArray(data, 0, data.length);
                        imageTaken.compress(Bitmap.CompressFormat.JPEG, 50, new ByteArrayOutputStream());
                        if(cam_view==0) imageTaken = RotateBitmap(imageTaken,-270);
                        else{
                            imageTaken = RotateBitmap(imageTaken,-90);
                            imageTaken = flip(imageTaken);
                        }
                        imageTaken = Bitmap.createBitmap(imageTaken, 0, 0, imageTaken.getWidth(), imageTaken.getWidth());

                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        freeMemory();
                        if(SAVE_FILE_TO_STORAGE) if(SHOW_PROGRESS_BAR) bar.progressiveStop();
                        if(Orientation!=1)
                        {
                            if(Orientation==2) imageTaken = RotateBitmap(imageTaken,-90);
                            if(Orientation==0) imageTaken = RotateBitmap(imageTaken,90);
                        }
                        showPreview(imageTaken,true);
                        surfaceView.allow_touch = true;
                        toggleFlashSetting(false);
                        //hideUI(false);
                        RESULT_CODE = 1;

                        captureButton.setEnabled(true);
                    }
                }.execute();
            }
        };

        flashToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allow_flash = !allow_flash;
                if(!allow_flash){
                    flashToggleButton.setImageResource(R.drawable.ic_flash_off_white_24dp);
                }
                else{
                    flashToggleButton.setImageResource(R.drawable.ic_flash_on_white_24dp);
                }
                refreshCamera();
            }
        });
        detector = new ScaleGestureDetector(this,new ScaleListener());
        findViewById(R.id.frame).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(surfaceView.allow_touch){
                    if(allow_zoom) {
                        if(ALLOW_ZOOM_GESTURE) {
                            detector.onTouchEvent(event);
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        if(!DISPLAY_SWITCH_CAM) switchCamButton.setVisibility(View.INVISIBLE);
        if(!DISPLAY_FLASH_TOGGLE) flashToggleButton.setVisibility(View.INVISIBLE);

        surfaceHolder.addCallback(this);
        surfaceView.setListener(this);
        surfaceView.setFrame(findViewById(R.id.frame));
        hide();

        if (Build.VERSION.SDK_INT >= 23){
            String[] perms = {"android.permission.CAMERA"};
            int permsRequestCode = 200;
            if(!((this).checkSelfPermission(perms[0])== PackageManager.PERMISSION_GRANTED)) {
                this.requestPermissions(perms,permsRequestCode);

            }
        }
    }
    private void confirmImage(){
        final LoadingDialog dialog = new LoadingDialog(this,getString(R.string.camera_msg_save));
        dialog.setAllowAnimation(false);

        if(SAVE_FILE_TO_STORAGE){
            new AsyncTask<Void,Void,Void>(){
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    dialog.show();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    String uri = null;
                    try {
                        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
                        String name = s.format(new Date());
                        uri = saveImageToExternal(name,imageTaken);
                        Log.i(TAG,uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent();
                    if(RETURN_DATA_AS_BYTE_ARRAY){
                        //return data as byte array
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        if (cam_view == BACK_CAM_ID)
                            imageTaken.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                        else imageTaken.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                        byte[] byteArray = stream.toByteArray();
                        intent.putExtra("data", byteArray);
                        intent.putExtra("uri",uri);
                        setResult(RESULT_CODE,intent);
                    }

                    //else save image to local files and get file path
                    else{
                        //return file path only
                        intent.putExtra("data", uri);
                        intent.putExtra("uri",uri);
                        setResult(RESULT_CODE,intent);
                    }
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    if(cam_view==BACK_CAM_ID)imageTaken.compress(Bitmap.CompressFormat.JPEG, 80, stream);
//                    else imageTaken.compress(Bitmap.CompressFormat.JPEG,50,stream);
//                    byte[] byteArray = stream.toByteArray();
//                    intent.putExtra("data", byteArray);
//                    intent.putExtra("uri",uri);
                   // setResult(RESULT_CODE,intent);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    dialog.complete(true);
                    SquareCameraActivity.this.finish();
                }
            }.execute();
        }
        else new AsyncTask<Void,Void,Void>(){
            /**
             * Process image in background - to avoid camera spike lags.
             * @param params not used.
             * @return null;
             */
            @Override
            protected Void doInBackground(Void... params) {
                Intent intent = new Intent();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if(cam_view==BACK_CAM_ID)imageTaken.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                else imageTaken.compress(Bitmap.CompressFormat.JPEG,50,stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("data", byteArray);
                setResult(RESULT_CODE,intent);
                return null;
            }

            /**
             * When finished processing the image activity will close and return result.
             * @param aVoid not used.
             */
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                SquareCameraActivity.this.finish();
            }
        }.execute();
    }

    public String saveImageToExternal(String imgName, Bitmap bm) throws IOException {
        //Create Path to save Image
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+"/"+DIR_NAME); //Creates app specific folder
        path.mkdirs();
        File imageFile = new File(path, imgName+".png"); // Imagename.png
        FileOutputStream out = new FileOutputStream(imageFile);
        try{
            bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
            out.flush();
            out.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,new String[] { imageFile.getAbsolutePath() }, null,new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch(Exception e) {
            throw new IOException();
        }
        return path+imgName+".png";
    }

    /**
     * Toggle UI controls
     * @param hide - to hide (true = hide UI, false = show UI)
     */
    @Deprecated
    public void hideUI(boolean hide){
        captureButton.setEnabled(!hide);
        if(hide){
            //flashToggleButton.setVisibility(View.GONE);
            //switchCamButton.setVisibility(View.GONE);
        }else {
            //showFlashToggle();
            showCamToggle();
            //showSeekbar();
        }
    }


    private void setSurfaceCover(boolean toSet){
        //if(toSet) surfaceCover.setVisibility(View.VISIBLE);
        //else surfaceCover.setVisibility(View.GONE);
        FrameLayout cameraFrame = (FrameLayout) findViewById(R.id.cameraFrame);
        if(toSet) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            param.width = cameraFrame.getWidth();
            param.height = cameraFrame.getHeight();
            surfaceCover = new View(this);
            surfaceCover.setBackgroundColor(getResources().getColor(android.R.color.black));
            surfaceCover.setLayoutParams(param);
            cameraFrame.addView(surfaceCover);
        }else{
            cameraFrame.removeView(surfaceCover);
        }

    }

    /**
     * Flip the camera.
     */
    public  void FlipCam(){
        if(cam_view==BACK_CAM_ID) {
            cam_view=FRONT_CAM_ID;
            //TODO:CamControls.control_flipper.setImageResource(R.drawable.camera_happy_turn);
            this.switchCamButton.setImageResource(R.drawable.ic_camera_alt_white_24dp);
            if(isFlashAvailable) this.flashToggleButton.setVisibility(View.GONE);
        }
        else {
            cam_view = BACK_CAM_ID;
            //TODO:CamControls.control_flipper.setImageResource(R.drawable.aa_camera_switch_button);
            this.switchCamButton.setImageResource(R.drawable.ic_camera_alt_white_24dp);
            if(isFlashAvailable) showFlashToggle();
        }
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
            camera.release();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            // open the camera
            camera = Camera.open(cam_view);
            camera.setDisplayOrientation(getAngle(camera_angle));
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();

        // modify parameter
        if(cam_view==BACK_CAM_ID){
            setUpCameraParams(param);
        }
        else{
            //param.setPreviewSize(getPreviewWidth(this.preview_size), getPreviewHeight(this.preview_size));
            param.setPreviewSize(getPreviewWidth(this.preview_size), getPreviewHeight(this.preview_size));
            param.setPictureSize(getPictureWidth(this.picture_size), getPictureHeight(this.picture_size));

        }

        try{
            camera.setParameters(param);
        }catch (RuntimeException e){
            param = camera.getParameters();
            param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            param.setPreviewSize(getPreviewWidth(this.preview_size), getPreviewHeight(this.preview_size));
            param.setPictureSize(getPictureWidth(this.picture_size),getPictureHeight(this.picture_size));
        }
        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
    }

    /**
     * Toggle flash setting
     * @param allow true = flash enabled, false = flash disabled.
     */
    private void toggleFlashSetting(boolean allow){
        if (camera==null||cam_view!=BACK_CAM_ID) return;
        Camera.Parameters param = camera.getParameters();
        if(allow){
            param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        }
        else{
            param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        camera.setParameters(param);
    }

    /**
     * Opens the camera. Called only once, when surface is initialized or when permissions are granted.
     * @return returns true of the camera was succesfully enabled.
     */
    private boolean openCamera(){
        if(camera==null){
            try {
                camera = Camera.open();
                camera.setDisplayOrientation(getAngle(camera_angle));
            }catch(RuntimeException e){
                e.printStackTrace();
                if (Build.VERSION.SDK_INT >= 23){
                    String[] perms = {"android.permission.CAMERA"};
                    if(((this).checkSelfPermission(perms[0])==PackageManager.PERMISSION_GRANTED)) {
                        final InfoDialog dialog = DialogManager.makeDialog(this,getString(R.string.camera_error_general),e.getMessage());
                        dialog.setPostiveButtonOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.putExtra("data", (byte[]) null);
                                setResult(RESULT_CODE,intent);
                                dialog.dismiss();
                                SquareCameraActivity.this.finish();
                            }
                        });
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                }
                return false;


            }
        }
        return true;
    }

    /**
     * Refresh the surface view, private method.
     */
    private void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        if (camera != null){
            try {
                Camera.Parameters param;
                param = camera.getParameters();

                if(cam_view==BACK_CAM_ID){
                    setUpCameraParams(param);
                    //param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

                }
                if(cam_view==FRONT_CAM_ID) {
                    param.setPictureSize(getPictureWidth(this.picture_size), getPictureHeight(this.picture_size));
                    param.setPreviewSize(getPreviewWidth(this.preview_size), getPreviewHeight(this.preview_size));
                }

                camera.setParameters(param);
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                //previewing = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
        openCamera();
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
     * Will preview the image that was taken.
     * @param image is the image that was taken.
     */
    private void showPreview(Bitmap image,final boolean toAnimate){
        CURRENT_PREVIEW_STATUS = 1;
        setSurfaceCover(true);
        captureButton.setVisibility(View.GONE);
        allow_zoom=false;
        confirm.setEnabled(true);
        cancel.setEnabled(true);
        Log.i(TAG,"width ="+SurfaceWidth);

        if(toAnimate) {
            previewIv.setImageBitmap(image);
            Animation animation = getGrowAnimation();
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    previewIv.setVisibility(View.VISIBLE);
                    if (isFlashAvailable) flashToggleButton.setVisibility(View.GONE);
                    switchCamButton.setVisibility(View.GONE);
                    if(SET_CROP_OPTIONAL) cropImageButton.setVisibility(View.VISIBLE);
                    confirm.show(toAnimate);
                    cancel.show(toAnimate);

                    //camera.stopPreview();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            previewIv.startAnimation(animation);
        }
        else{
            previewIv.setImageBitmap(image);
            previewIv.setVisibility(View.VISIBLE);
            if (isFlashAvailable) flashToggleButton.setVisibility(View.GONE);
            switchCamButton.setVisibility(View.GONE);
            if(SET_CROP_OPTIONAL) cropImageButton.setVisibility(View.VISIBLE);
            confirm.show(toAnimate);
            cancel.show(toAnimate);
        }

    }


    /**
     * This method does the complete opposite of showPreview().
     * it hides the Preview ImageView and the cancel/confirm buttons,
     * and then shows the Camera Capture button.
     */
    private void hidePreview(final boolean toAnimate){
        CURRENT_PREVIEW_STATUS = 0;
        confirm.setEnabled(false);
        cancel.setEnabled(false);
        if(toAnimate) {
            Animation animation = getShrinkAnimation();
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // preview_frame.setVisibility(View.GONE);
                    if (cam_view == BACK_CAM_ID) if (isFlashAvailable) showFlashToggle();
                    showCamToggle();
                    previewIv.setVisibility(View.INVISIBLE);
                    captureButton.setVisibility(View.VISIBLE);
                    if(SET_CROP_OPTIONAL) cropImageButton.setVisibility(View.GONE);
                    confirm.hide(toAnimate);
                    cancel.hide(toAnimate);

                    //refreshCamera();
                    isTakingPicture = false;
                    allow_zoom=true;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshCamera();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setSurfaceCover(false);
                                }
                            },50);
                        }
                    }, confirm.getAnimation().getDuration()+50);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            previewIv.startAnimation(animation);
        }
        else{
            if (cam_view == BACK_CAM_ID)
                if (isFlashAvailable) showFlashToggle();
            showCamToggle();
            previewIv.setVisibility(View.GONE);
            captureButton.setVisibility(View.GONE);
            if(SET_CROP_OPTIONAL) cropImageButton.setVisibility(View.GONE);
            confirm.hide(toAnimate);
            cancel.hide(toAnimate);
        }


    }


    /**
     * returns shrink animation, only used by the Preview ImageView.
     * @return Animation object - shrink animation
     */
    private Animation getShrinkAnimation(){
        final float growTo = 0f;
        final long duration = 200;

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

    /**
     * static method - checks if device has camera.
     * @param context is the application context.
     * @return true if device has camera.
     */
    private static boolean isCameraAvailable(Context context) {
        if (android.os.Build.VERSION.SDK_INT < 17){
            // Do something for lollipop and above versions
            return isCameraAvailable(context,1);
        } else{
            // do something for phones running an SDK before lollipop
            return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        }
    }

    /**
     * static method - checks if device has a specific camera.
     * @param context is the application context.
     * @param type is teh camera ID.
     * @return true whether the camera with the given id exists in the device.
     */
    private static boolean isCameraAvailable(Context context,int type) {
        switch (type){
            case 0:
                return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
            case 1:
                return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
            default:
                return false;
        }
    }

    /**
     * static method - checks if device has flash.
     * @param context is the application context.
     * @return true if device has flash.
     */
    private static boolean isFlashAvailable(Context context){
        return  context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    /**
     * onRequestPermissionResult - used only for API 23 and above.
     * Called when user grants camera permissions.
     * When called the camera is initialized and the surface is refreshed.
     * @param requestCode is the request code (always 200).
     * @param permissions array of requested permissions.
     * @param grantResults the results.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case 200:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                    refreshCamera();
                }
                else {
                    //handle error
                    final InfoDialog dialog = DialogManager.makeDialog(this,getString(R.string.camera_error_general),getString(R.string.camera_error_perm));
                    dialog.setPostiveButtonOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("data", (byte[]) null);
                            setResult(RESULT_CODE,intent);
                            dialog.dismiss();
                            SquareCameraActivity.this.finish();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                }
                break;
        }
    }

    /**
     * Private method - only used to rotate images that were taken.
     * @param source original bitmap (bitmap to rotate).
     * @param angle the angle to rotate in.
     * @return a rotated bitmap.
     */
    private static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static Bitmap codec(Bitmap src, Bitmap.CompressFormat format,
                                int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        byte[] array = os.toByteArray();
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    /**
     * Private method - only used for FRONT_CAM in order to mirror the image.
     * @param src is the original bitmap.
     * @return a mirrored bitmap.
     */
    private static Bitmap flip(Bitmap src)
    {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    /**
     * Is used to detect and monitor the device's current orientation.
     * When orientation changes are found, UI views will be rotated accordingly.
     * @param event is the event that the sensor has detected.
     */
    public void onSensorChanged(SensorEvent event) {

        //Check if rotation is enabled in the device
        boolean isOrientationEnabled;
        try {
            isOrientationEnabled = Settings.System.getInt(getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION) == 1;
        } catch (Settings.SettingNotFoundException e) {
            isOrientationEnabled = false;
        }

        //If the device has orientation disabled, set the orientation to portrait and return
        if (!isOrientationEnabled){
            if(this.Orientation!= ORIENTATION_PORTRAIT)rotateViews(ORIENTATION_PORTRAIT);
            this.Orientation = ORIENTATION_PORTRAIT;
            return;
        }

        //Check if rotation is allowed here
        if(allow_rotation) {

            //Get the gravity from the accelerometer
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values;

            //Get the magnetic field (if possible)
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;

            //If accelerometer was able to get the current gravity
            if(mGravity !=null) {

                //if accelerometer was able to get the geo magnetic field (This may not work on all devices, therefor get the pitch and roll using only the mGravity)
                if (mGeomagnetic != null) {

                    float R[] = new float[9];
                    float I[] = new float[9];
                    boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                    if (success) {
                        float orientation[] = new float[3];
                        SensorManager.getOrientation(R, orientation);


                        // orientation contains: azimut, pitch and roll
                        float pitch = (float) Math.toDegrees(orientation[1]);
                        float roll = (float) Math.abs(Math.toDegrees(orientation[2]));

                        if (pitch < -45 && pitch > -135) {
                            // if device is laid flat on a surface, we don't want to change the orientation
                            if (this.Orientation != ORIENTATION_PORTRAIT)
                                rotateViews(ORIENTATION_PORTRAIT);
                            this.Orientation = ORIENTATION_PORTRAIT;
                            return;
                        }

                        if ((roll > 60 && roll < 135)) {
                            // The device is closer to landscape orientation. Enable fullscreen
                            int landscape_mode;//0 = right, 2 = left
                            if (Math.toDegrees(orientation[2]) > 0)
                                landscape_mode = ORIENTATION_LANDSCAPE_RIGHT;
                            else landscape_mode = ORIENTATION_LANDSCAPE_LEFT;


                            if (this.Orientation != landscape_mode) rotateViews(landscape_mode);
                            this.Orientation = landscape_mode;
                        } else if (roll < 45 && roll > 135) {
                            // The device is closer to portrait orientation. Disable fullscreen

                            if (this.Orientation != 1) rotateViews(ORIENTATION_PORTRAIT);
                            this.Orientation = ORIENTATION_PORTRAIT;
                        }

                    }
                }else {
                    final double gravityNorm = Math.sqrt(mGravity[0] * mGravity[0] + mGravity[1] * mGravity[1] + mGravity[2] * mGravity[2]);
                    final float pitch = (float) Math.asin(-mGravity[1] / gravityNorm);
                    final float roll = (float) Math.atan2(-mGravity[0] / gravityNorm, mGravity[2] / gravityNorm);
                    int orientation  = calculate(pitch,roll);

                    if (this.Orientation != orientation) {
                        rotateViews(orientation);
                        this.Orientation = orientation;
                    }
                }
            }
        }
    }


    /**
     * Method calculates device orientation using pitch and roll that were extracted from accelerometer gravity only without using geo magnetic field.
     *
     * @param pitch - the pitch of the device.
     * @param roll - the roll of the device.
     * @return device orientation.
     */
    private int calculate(float pitch,float roll){
        int p = Integer.valueOf((int) (pitch*100));
        int r = Integer.valueOf((int) (roll*100));
        int orientation = 1;
        if ( p < 30 && p > -30){
            if (r < 0){
                orientation = 2;
            }else{
                orientation = 0;
            }
        }
        return orientation;

    }


    /**
     * register sensors.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        try{
            camera.startPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
        hide();
    }

    /**
     * unregister sensors.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        try{
            camera.stopPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        freeMemory();
        super.onDestroy();
    }

    /**
     * Rotate views
     * @param orientation is the current orientation of the device.
     */
    private void rotateViews(int orientation){
        float newAngel = 0;
        switch(orientation){
            case 0:
                newAngel=-90;
                break;
            case 1:
                newAngel = 0;
                break;
            case 2:
                newAngel=90;
                break;
        }

        if(ALLOW_ROTATION_ANIMATION) {
            ObjectAnimator mAnimate;
            ArrayList<View> views = new ArrayList<>();
            views.add(this.captureButton);
            views.add(flashToggleButton);
            views.add(confirm);
            views.add(cancel);
            views.add(switchCamButton);

            for (int i = 0; i < 5; i++) {
                mAnimate = ObjectAnimator.ofFloat(views.get(i), "rotation", views.get(i).getRotation(), newAngel);
                mAnimate.setDuration(250);
                mAnimate.start();
            }
        }
        else{
            this.captureButton.setRotation(newAngel);
            this.flashToggleButton.setRotation(newAngel);
            this.confirm.setRotation(newAngel);
            this.cancel.setRotation(newAngel);
            this.switchCamButton.setRotation(newAngel);
        }
        this.previewIv.setRotation(newAngel);

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
     * Set the Camera zoom.
     * @param precent is the zoom level with range of 0 to 100.
     */
    private void zoom(int precent){
        if(precent>100||precent<0||camera==null) return;
        Camera.Parameters params=camera.getParameters();
        int max = (int) (precent*((float)camera.getParameters().getMaxZoom()/100));
        params.setZoom(max);
        camera.setParameters(params);
    }

    /**
     * Hide the navigation bar. (Setup full screen mode)
     */
    private void hide() {
        if (Build.VERSION.SDK_INT >= 19) {
            View v = SquareCameraActivity.this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        WindowManager.LayoutParams attrs = SquareCameraActivity.this.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        SquareCameraActivity.this.getWindow().setAttributes(attrs);
    }


    private void freeMemory(){
        if(!SAVE_FILE_TO_STORAGE) return;
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    private strictfp void setUpCameraParams(Camera.Parameters param){
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
            if(((double) s.width/s.height ) > 1.70 ){
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
            if(((double) s.width/s.height ) > 1.70 ){
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
        Camera.Size bestPreview;
        Camera.Size bestPicture;
        try {
            bestPreview = widePreviewSizes.get(previewIndex);
            bestPicture = widePictureSizes.get(pictureIndex);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            bestPreview = normalPreviewSizes.get(0);
            bestPicture = normalPictureSizes.get(0);
            messure = 1.33333333333;
        }
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        param.setPreviewSize(bestPreview.width, bestPreview.height);
        param.setPictureSize(bestPicture.width,bestPicture.height);

        Log.i("CameraDialog", "Selected: " + param.getPictureSize().width + "," + param.getPictureSize().height);
    }

    /**
     * ScaleListener custom class, used for the Gesture Detector inorder to zoom.
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        /**
         * is called when scale is changing
         * @param detector is the gesture detector.
         * @return always true.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            float prcentage =  ((scaleFactor-1)*(100/(MAX_ZOOM-MIN_ZOOM)));
            zoom((int)prcentage);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return super.onScaleBegin(detector);

        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }


    public static ActivityBuilder activity(){
        return new ActivityBuilder();
    }

    public static final class ActivityBuilder{

        private boolean ALLOW_ORIENTATION_CHANGE = true;
        private boolean DISPLAY_FLASH_TOGGLE = true;
        private boolean DISPLAY_SWITCH_CAM = true;
        private boolean ALLOW_ZOOM_GESTURE = true;
        private boolean ALLOW_ROTATION_ANIMATION = true;
        private boolean SAVE_FILE_TO_STORAGE = false;
        private boolean SET_CROP_OPTIONAL = false;
        private boolean RETURN_DATA_AS_BYTE_ARRAY = true;
        private int CROP_ASPECT_RATIO = -1;
        private int cropRatioX;
        private int cropRatioY;
        private int LOAD_CAMERA = 0;

        /**
         * DISPLAY_FLASH_TOGGLE is true by default, however if you wish to disable the flash in the camera
         * (Hide the flash button) call this method with @param flag = false
         *
         * @param flag The flag that decides if the flash-toggle button will show in the camera activity.
         * @return The ActivityBuilder that we are building
         */
        public ActivityBuilder displayFlashToggle(boolean flag){
            DISPLAY_FLASH_TOGGLE = flag;
            return this;
        }

        /**
         * DISPLAY_SWITCH_CAM is true by default, however if you want to prevent the user from switching between their cameras
         * set @param flag to false
         *
         * @param flag The flag that decides if the cam-switch button will show in the camera activity.
         * @return The ActivityBuilder that we are building
         */
        public ActivityBuilder displayCameraSwitchToggle(boolean flag){
            DISPLAY_SWITCH_CAM = flag;
            return this;
        }

        /**
         * ALLOW_ZOOM_GESTURE is true by default. This flag , if enabled, will allow the user to pinch in order to zoom in and out.
         *
         * @param flag By setting this flag to false the user will lose capability of zooming in.
         * @return The ActivityBuilder that we are building
         */
        public ActivityBuilder allowZoomGesture(boolean flag){
            ALLOW_ZOOM_GESTURE = flag;
            return this;
        }

        /**
         * ALLOW_ROTATION_ANIMATION is true by default. This will have an effect only if ALLOW_ORIENTATION_CHANGE is also set to true.
         * If this flag was set to true, when rotating the device the buttons will rotate with a smooth rotation animation. To disable this animation
         * set this flag to false. Note that this won't prevent the icons from rotating, they will simply rotate without animation.
         *
         * @param flag Setting this to false will disable the animation that is applied to all views that rotate when the screen rotates.
         * @return The ActivityBuilder that we are building
         */
        public ActivityBuilder allowRotationAnimation(boolean flag){
            ALLOW_ROTATION_ANIMATION = flag;
            return this;
        }

        /**
         * SAVE_FILE_TO_STORAGE is set to false by default. When setting it to true any picture that is taken by the camera will be saved in a photo album.
         *
         * @param flag This allows you to save the images you capture into an external storage.
         * @return The ActivityBuilder that we are building
         */
        public ActivityBuilder saveFileToStorage(boolean flag){
            SAVE_FILE_TO_STORAGE = flag;
            return this;
        }

        /**
         * SET_CROP_OPTIONAL is set to false by default. This option, when enabled, will present the user with an option to crop the image they captured
         * within the camera activity before sending it with the result intent.
         *
         * @param flag When set to true users will have the option to crop the image the captured.
         * @return The ActivityBuilder that we are building
         */
        public ActivityBuilder setCropOptional(boolean flag){
            SET_CROP_OPTIONAL = flag;
            return this;
        }

        /**
         * RETURN_DATA_AS_BYTE_ARRAY is true by default. Setting it to false will return the URI path of the image that was captured. This option may help
         * you in cases where the image is way too big in size to pass within a result intent.
         *
         * When enabled, the result intent will include the path in the keys "data" and "uri".
         *
         * @param flag By setting this to false, the result intent of the camera actvity will return the URI path of the image, resulting in a better performance.
         * @return The ActivityBuilder that we are building
         */
        public ActivityBuilder returnDataAsByteArray(boolean flag){
            RETURN_DATA_AS_BYTE_ARRAY = flag;
            return this;
        }

        /**
         * This Method is deprecated.
         * use setCropAspectRatio(int x,int y) instead.
         *
         * CROP_ASPECT_RATIO is set to -1 by default. This flag modification will only have an impact if SET_CROP_OPTIONAL is set to true.
         *
         * @param ratio is one of the following numbers: {0,1,2,3} where:
         *              0 = 1:1 ratio
         *              1 = 2:1 ratio
         *              3 = 16:9 ratio
         * @return The ActivityBuilder that we are building
         */
        @Deprecated
        public ActivityBuilder setCropAspectRatio(int ratio){
            CROP_ASPECT_RATIO = ratio;
            return this;
        }

        /**
         * CROP_ASPECT_RATIO is set to -1 by default. This flag modification will only have an impact if SET_CROP_OPTIONAL is set to true.
         *
         * Setting this flag will impact the CropView inside the camera activity, which will result with a locked aspect ratio of x:y
         * Note that x and y cannot be equal to or less than zero, However the application can handle an exception and simply will set no specified
         * aspect ratio and let the user choose a ratio.
         *
         * @param x The aspect ratio where x:y
         * @param y The aspect ratio where x:y
         * @return The ActivityBuilder that we are building
         */
        public ActivityBuilder setCropAspectRatio(int x,int y){
            CROP_ASPECT_RATIO = -2;
            cropRatioX = x;
            cropRatioY = y;
            return this;
        }

        /**
         * ALLOW_ORIENTATION_CHANGE is set to true by default. By setting it to false, the gyro sensor will be locked
         * and the activity buttons will not rotate with the screen.
         *
         * @param flag Set this to false if you wish to disable UI rotations.
         * @return The ActivityBuilder that we are building
         */
        public ActivityBuilder setOrientationChangeEnabled(boolean flag){
            ALLOW_ORIENTATION_CHANGE = flag;
            return this;
        }

        /**
         * LOAD_CAMERA flag is set to 0 by default, which is the back camera.
         * This is the camera that will be loaded when the activity starts.
         *
         * @param camId You can set this to either 1 (Face Cam) or 0 (Back Cam)
         * @return
         */
        public ActivityBuilder openWithCamera(int camId){
            LOAD_CAMERA = camId;
            return this;
        }

        /**
         * This method will start the camera activity with the flags that were set using other methods in this class.
         * Note this method uses an Activity Object to create and launh the intent.
         * If you cannot access an activity use the getIntent(Context context) to get the built intent.
         *
         * @param context is the current activity (application context).
         */
        public void start(Activity context){
            context.startActivityForResult(getIntent(context),SquareCameraActivity.REQUEST_CODE);
        }

        public Intent getIntent(Context context){
            Intent intent = new Intent(context, SquareCameraActivity.class);
            intent.putExtra("ALLOW_ORIENTATION_CHANGE",ALLOW_ORIENTATION_CHANGE);
            intent.putExtra("DISPLAY_FLASH_TOGGLE",DISPLAY_FLASH_TOGGLE);
            intent.putExtra("DISPLAY_SWITCH_CAM",DISPLAY_SWITCH_CAM);
            intent.putExtra("ALLOW_ZOOM_GESTURE",ALLOW_ZOOM_GESTURE);
            intent.putExtra("ALLOW_ROTATION_ANIMATION",ALLOW_ROTATION_ANIMATION);
            intent.putExtra("SAVE_FILE_TO_STORAGE",SAVE_FILE_TO_STORAGE);
            intent.putExtra("SET_CROP_OPTIONAL",SET_CROP_OPTIONAL);
            intent.putExtra("RETURN_DATA_AS_BYTE_ARRAY",RETURN_DATA_AS_BYTE_ARRAY);
            intent.putExtra("CROP_ASPECT_RATIO",CROP_ASPECT_RATIO);
            if(CROP_ASPECT_RATIO==-2){
                intent.putExtra("cropAspectX",cropRatioX);
                intent.putExtra("cropAspectY",cropRatioY);
            }
            intent.putExtra("LOAD_CAMERA",LOAD_CAMERA);
            return intent;
        }
    }
}
