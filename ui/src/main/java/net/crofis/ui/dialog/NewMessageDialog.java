package net.crofis.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import net.crofis.ui.camera.CameraParams;
import net.crofis.ui.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Tony Zaitoun on 4/6/2016.
 */
public class NewMessageDialog extends BaseAlertDialog{

    private static final String TAG = "NewMessageDialog.class";

    /**Application context**/
    private Context context;

    /**Title of the dialog**/
    private TextView title;

    /**Input Title (Car Plate)**/
    private EditText inputTitle;

    /**Input Message EditText**/
    private EditText inputMessage;

    /**Profile Image**/
    private CircleImageView image;

    /**Positive Button**/
    private FloatingActionButton postiveButton;

    /**Negative Button**/
    private FloatingActionButton negativeButton;

    /**Camera Button**/
    private FloatingActionButton cameraButton;

    /**Image taken by the user**/
    private Bitmap imageTaken=null;

    /**The View of the dialog**/
    private View dialogView;

    /**The Root AlertDialog**/
    private AlertDialog dialog;

    /**An image that is set on the image**/
    private Bitmap imageBitmap;

    /**In charge of the slide in animation**/
    private boolean allowAnimation = true;

    /**Is dialog cancelable**/
    private boolean cancelable = true;

    /**Is the dialog locked, if true can not use .show() and .dismiss(), can be overrided by calling .getRootDialog().dismiss() and .getRootDialog().show()**/
    private boolean isLocked = false;

    private boolean allowCameraButton = true;

    /**Delay of button animation to start**/
    private static final int startControlAnimationDelay = 400;

    /**Delay between each button**/
    private static final int gapControlAnimationDelay = 200;

    private View.OnClickListener PositiveListener;

    private View.OnClickListener NegativeListener;

    private CircleImageView imagePreviewButton;

    private CameraDialog cameraDialog;

    private CameraParams cameraParams;

    /**
     * Public constructor, initialize the dialog and connect all the views.
     * @param context Application Context
     */
    public NewMessageDialog(final Context context){
        this.context = context;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        dialogView = inflater.inflate(R.layout.ui_message_dialog, null);
        this.title = (TextView) dialogView.findViewById(R.id.dialog_title);
        this.title.setText("");
        this.inputTitle = (EditText) dialogView.findViewById(R.id.dialog_car_plate);
        this.inputMessage = (EditText) dialogView.findViewById(R.id.dialog_message);
        this.image = (CircleImageView) dialogView.findViewById(R.id.dialog_imageView);
        this.postiveButton = (FloatingActionButton) dialogView.findViewById(R.id.pos);
        this.negativeButton = (FloatingActionButton) dialogView.findViewById(R.id.neg);
        this.cameraButton = (FloatingActionButton) dialogView.findViewById(R.id.camera);
        this.imagePreviewButton = (CircleImageView) dialogView.findViewById(R.id.image);
        cameraButton.post(new Runnable() {
            @Override
            public void run() {
                int width = dialogView.findViewById(R.id.camera).getWidth()*14/16;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
                imagePreviewButton.setLayoutParams(layoutParams);
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageTaken==null){
                    cameraDialog =  new CameraDialog(context, cameraParams);
                    cameraDialog.show();
                    cameraDialog.setPostImageTaken(new Runnable() {
                        @Override
                        public void run() {
                            cameraButton.setVisibility(View.GONE);
                            imagePreviewButton.setVisibility(View.VISIBLE);
                            imagePreviewButton.setImageBitmap(cameraDialog.getImageTaken());
                            imageTaken = cameraDialog.getImageTaken();
                        }
                    });
                }

            }
        });
        imagePreviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMessageDialog.this.cameraDialog = new CameraDialog(context,imageTaken);
                cameraDialog.show();
                cameraDialog.setPostImageTaken(new Runnable() {
                    @Override
                    public void run() {
                        imagePreviewButton.setImageBitmap(cameraDialog.getImageTaken());
                        imageTaken = cameraDialog.getImageTaken();
                    }
                });
                cameraDialog.setPostImageDeleted(new Runnable() {
                    @Override
                    public void run() {
                        imageTaken=null;
                        cameraButton.setVisibility(View.VISIBLE);
                        imagePreviewButton.setVisibility(View.GONE);
                    }
                });
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
     * Public constructor, initialize the dialog and connect all the views with camera settings.
     * @param context Application context.
     * @param params Camera settings.
     */
    public NewMessageDialog(final Context context,CameraParams params){
        this.cameraParams = params;
        this.context = context;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        dialogView = inflater.inflate(R.layout.ui_message_dialog, null);
        this.title = (TextView) dialogView.findViewById(R.id.dialog_title);
        this.title.setText("");
        this.inputTitle = (EditText) dialogView.findViewById(R.id.dialog_car_plate);
        this.inputMessage = (EditText) dialogView.findViewById(R.id.dialog_message);
        this.image = (CircleImageView) dialogView.findViewById(R.id.dialog_imageView);
        this.postiveButton = (FloatingActionButton) dialogView.findViewById(R.id.pos);
        this.negativeButton = (FloatingActionButton) dialogView.findViewById(R.id.neg);
        this.cameraButton = (FloatingActionButton) dialogView.findViewById(R.id.camera);
        this.imagePreviewButton = (CircleImageView) dialogView.findViewById(R.id.image);
        cameraButton.post(new Runnable() {
            @Override
            public void run() {
                int width = dialogView.findViewById(R.id.camera).getWidth()*14/16;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
                imagePreviewButton.setLayoutParams(layoutParams);
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageTaken==null){
                    cameraDialog =  new CameraDialog(context, cameraParams);
                    cameraDialog.show();
                    cameraDialog.setPostImageTaken(new Runnable() {
                        @Override
                        public void run() {
                            cameraButton.setVisibility(View.GONE);
                            imagePreviewButton.setVisibility(View.VISIBLE);
                            imagePreviewButton.setImageBitmap(cameraDialog.getImageTaken());
                            imageTaken = cameraDialog.getImageTaken();
                        }
                    });
                }

            }
        });
        imagePreviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMessageDialog.this.cameraDialog = new CameraDialog(context,imageTaken);
                cameraDialog.show();
                cameraDialog.setPostImageTaken(new Runnable() {
                    @Override
                    public void run() {
                        imagePreviewButton.setImageBitmap(cameraDialog.getImageTaken());
                        imageTaken = cameraDialog.getImageTaken();
                    }
                });
                cameraDialog.setPostImageDeleted(new Runnable() {
                    @Override
                    public void run() {
                        imageTaken=null;
                        cameraButton.setVisibility(View.VISIBLE);
                        imagePreviewButton.setVisibility(View.GONE);
                    }
                });
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
     * Show the dialog.
     */
    @Override
    public void show(){
        if(!isLocked) {
            if (title.getText().length() == 0) title.setVisibility(View.GONE);
            if (this.imageBitmap == null) image.setVisibility(View.GONE);
            if(allowAnimation) {
                postiveButton.hide(false);
                negativeButton.hide(false);
                cameraButton.hide(false);
                int delay = startControlAnimationDelay;
                Handler handler = new Handler();
                for (int i = -1; i < 2; i++) {
                    final int finalI = i;

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switch (finalI) {
                                case -1:
                                    if(allowCameraButton) cameraButton.show(true);
                                    break;
                                case 0:
                                     postiveButton.show(true);
                                    break;
                                case 1:
                                    negativeButton.show(true);
                                    break;
                            }

                        }
                    }, delay);
                    delay += gapControlAnimationDelay;
                }
            }
            this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (allowAnimation)
                dialog.getWindow().getAttributes().windowAnimations = R.style.customDialogAnimation;
            dialog.setCancelable(this.cancelable);
            dialog.show();
        }
        else Log.e(TAG, "Dialog is locked.");
    }

    /**
     * Dismiss the dialog.
     */
    @Override
    public void dismiss(){
        if(!isLocked) dialog.dismiss();
        else Log.e(TAG,"Dialog is locked.");
    }

    /**
     * Set a on click listener for the positive button.
     * @param listener is the positive listener.
     */

    public void setPostiveButtonOnClickListener(View.OnClickListener listener){
        this.PositiveListener = listener;
        this.postiveButton.setOnClickListener(listener);
    }

    /**
     * Set a on click listener for the negative button.
     * @param listener is the negative listener
     */
    public  void setNegativeButtonOnClickListener(View.OnClickListener listener){
        this.NegativeListener= listener;
        this.negativeButton.setOnClickListener(listener);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener dismissListener){
        this.dialog.setOnDismissListener(dismissListener);
    }

    public AlertDialog getRootDialog(){
        return this.dialog;
    }

    /**
     * Set the title of the dialog.
     * @param title will be shown on the Title.
     */
    public void setTitle(String title){
        this.title.setText(title);
    }

    /**
     * Set circular image in the corner of the dialog.
     * @param bitmap is the image that will be shown.
     */
    public void setImageBitmap(Bitmap bitmap){
        this.imageBitmap = bitmap;
        this.image.setImageBitmap(bitmap);
    }

    /**
     * Set the animation.
     * @param show - true means the dialog will be displayed with a slide in/out animation. If set to false, the default fade will take place.
     */

    public void showAnimation(boolean show){
        this.allowAnimation = show;
    }

    /**
     * Set the dialog dismiss-by-user state.
     * @param isCancelable if true dialog can be dismissed by the user, if false dialog can only be dismissed by calling Dialog.dismiss().
     */
    public void setCancelable(boolean isCancelable){
        this.cancelable = isCancelable;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLock(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public View getDialogView(){
        return this.dialogView;
    }

    public TextView getTitle() {
        return title;
    }

    public EditText getInputTitle() {
        return inputTitle;
    }

    public EditText getInputMessage() {
        return inputMessage;
    }

    public CircleImageView getImage() {
        return image;
    }

    public FloatingActionButton getPostiveButton() {
        return postiveButton;
    }

    public FloatingActionButton getNegativeButton() {
        return negativeButton;
    }

    public CameraParams getCameraParams() {
        return cameraParams;
    }

    public CameraDialog getCameraDialog() {
        return cameraDialog;
    }

    public CircleImageView getImagePreviewButton() {
        return imagePreviewButton;
    }

    public static int getGapControlAnimationDelay() {
        return gapControlAnimationDelay;
    }

    public View.OnClickListener getPositiveListener() {
        return PositiveListener;
    }

    public View.OnClickListener getNegativeListener() {
        return NegativeListener;
    }

    public static int getStartControlAnimationDelay() {
        return startControlAnimationDelay;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public boolean isAllowAnimation() {
        return allowAnimation;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public FloatingActionButton getCameraButton() {
        return cameraButton;
    }

    public void setAllowCameraButton(boolean allowCameraButton) {
        this.allowCameraButton = allowCameraButton;
    }
}
