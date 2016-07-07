package net.crofis.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import net.crofis.ui.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Tony Zaitoun on 4/6/2016.
 */
public class InfoDialog extends BaseAlertDialog{

    private static final String TAG = "InfoDialog.class";

    /**Title of the dialog**/
    private TextView title;

    /**Message of the dialog**/
    private TextView message;

    /**Profile Image**/
    private CircleImageView icon;

    /**Positive Button**/
    private FloatingActionButton postiveButton;

    /**Natural Button**/
    private FloatingActionButton naturalButton;

    /**Negative Button**/
    private FloatingActionButton negativeButton;

    private View.OnClickListener PositiveListener;

    private View.OnClickListener NegativeListener;

    private View.OnClickListener NaturalListener;

    /**An image that is set on the image**/
    private Bitmap imageBitmap;
    private Drawable imageDrawable;

    /**In charge of the slide in animation**/
    private boolean allowAnimation = true;

    /**Is dialog cancelable**/
    private boolean cancelable = true;

    private boolean isLocked = false;

    /**Delay of button animation to start**/
    private static final int startControlAnimationDelay = 300;

    /**Delay between each button**/
    private static final int gapControlAnimationDelay = 150;


    public InfoDialog(Context context){
        this.context = context;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        dialogView = inflater.inflate(R.layout.ui_simple_dialog, null);
        this.title = (TextView) dialogView.findViewById(R.id.dialog_title);
        this.title.setText("");
        this.message= (TextView) dialogView.findViewById(R.id.dialog_message);
        this.message.setMovementMethod(new ScrollingMovementMethod());
        this.icon = (CircleImageView) dialogView.findViewById(R.id.dialog_imageView);
        this.postiveButton = (FloatingActionButton) dialogView.findViewById(R.id.pos);
        this.negativeButton = (FloatingActionButton) dialogView.findViewById(R.id.neg);
        this.naturalButton = (FloatingActionButton) dialogView.findViewById(R.id.natural);
        dialogView.findViewById(R.id.background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialog = dialogBuilder.create();
        this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (allowAnimation)
            dialog.getWindow().getAttributes().windowAnimations = R.style.customDialogAnimation;
    }

    /**
     * Show the dialog.
     */
    @Override
    public void show(){
        if(!isLocked) {


            View.OnClickListener dismissDialog = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            };
            if (title.getText().length() == 0) title.setVisibility(View.GONE);
            if (this.imageBitmap == null && this.imageDrawable == null)
                icon.setVisibility(View.GONE);
            if (NaturalListener == null) this.naturalButton.setVisibility(View.GONE);
            if (this.cancelable) {
                if (NegativeListener == null && PositiveListener == null) {
                    PositiveListener = dismissDialog;
                    postiveButton.setOnClickListener(dismissDialog);


                    negativeButton.setVisibility(View.GONE);
                } else {
                    if (PositiveListener != null && NegativeListener == null) {
                        negativeButton.setOnClickListener(dismissDialog);
                    }
                }
            } else {
                if (NegativeListener == null && PositiveListener == null) {
                    negativeButton.setVisibility(View.GONE);
                    postiveButton.setVisibility(View.GONE);
                }
            }

            if(allowAnimation) {
                postiveButton.hide(false);
                negativeButton.hide(false);
                naturalButton.hide(false);
                int delay = startControlAnimationDelay;
                Handler handler = new Handler();
                for (int i = 0; i < 3; i++) {
                    final int finalI = i;

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switch (finalI) {
                                case 0:
                                    if (PositiveListener != null) postiveButton.show(true);
                                    break;
                                case 1:
                                    if (NegativeListener != null) negativeButton.show(true);
                                    break;
                                case 2:
                                    if (NaturalListener != null) naturalButton.show(true);
                                    break;
                            }


                        }
                    }, delay);
                    delay += gapControlAnimationDelay;
                    Log.i(TAG, delay + " " + i);
                }
            }


            dialog.setCancelable(this.cancelable);
            dialog.show();
        }
        else Log.e(TAG,"Dialog is locked.");
    }

    /**
     * Dismiss the dialog.
     */
    @Override
    public void dismiss(){
        if(!isLocked) dialog.dismiss();
        else Log.e(TAG,"Dialog is locked.");
    }

    @Override
    public AlertDialog getDialog() {
        return dialog;
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
        this.NegativeListener = listener;
        this.negativeButton.setOnClickListener(listener);
    }

    public void setNaturalButtonOnClickListener(View.OnClickListener listener){
        this.NaturalListener = listener;
        this.naturalButton.setOnClickListener(listener);
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
     * Set the message of the dialog.
     * @param message will be shown on the Message.
     */
    public void setMessage(String message){
        this.message.setText(message);
    }

    /**
     * Set circular image in the corner of the dialog.
     * @param bitmap is the image that will be shown.
     */
    public void setBitmapIcon(Bitmap bitmap){
        this.imageBitmap = bitmap;
        this.icon.setImageBitmap(bitmap);
    }

    /**
     * Set circular image in the corner of the dialog.
     * @param drawable is the image that will be shown.
     */

    public void setDrawableIcon(Drawable drawable){
        this.imageDrawable = drawable;
        this.icon.setImageDrawable(drawable);
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

    public TextView getTitle() {
        return title;
    }

    public TextView getMessage() {
        return message;
    }

    public CircleImageView getIcon() {
        return icon;
    }

    public FloatingActionButton getPostiveButton() {
        return postiveButton;
    }

    public FloatingActionButton getNegativeButton() {
        return negativeButton;
    }

    public FloatingActionButton getNaturalButton() {
        return naturalButton;
    }

    @Override
    public View getDialogView() {
        return dialogView;
    }

    @Override
    public Context getContext() {
        return context;
    }
}
