package net.crofis.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import net.crofis.ui.R;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Tony Zaitoun on 5/8/16.
 */
public class LoadingDialog extends BaseAlertDialog{

    private static final String TAG = "LoadingDialog.class";

    /**Title of the dialog**/
    private TextView title;

    /**Message of the dialog**/
    private TextView message;

    /**Progress Bar**/
    private SmoothProgressBar progressView;

    /**Positive Button**/
    private FloatingActionButton postiveButton;

    /**In charge of the slide in animation**/
    private boolean allowAnimation = true;

    /**Is dialog cancelable**/
    private boolean cancelable = false;

    /**Is the dialog locked**/
    private boolean isLocked = false;

    /**The delay that takes the button to appear after .complete() is called**/
    private long show_delay = 300;

    /**The delay that is added to dismiss the dialog after .complete() is called, NOTE: this is only used when autoDismiss is true**/
    private long extra_delay = 200;

    /**Just some default text to display TODO: Replace with string from resources**/
    private final String text_completed = "Progress Finished";

    /**Used to execute custom code when .complete() is called. null by default**/
    private PostCompleted postComplete = null;

    /**
     * Default constructor
     * @param context - Application context.
     */
    public LoadingDialog(Context context){
        this.context = context;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        dialogView = inflater.inflate(R.layout.ui_loading_dialog, null);
        this.title = (TextView) dialogView.findViewById(R.id.dialog_title);
        this.title.setText("");
        this.message= (TextView) dialogView.findViewById(R.id.dialog_message);
        this.message.setText("");
        this.message.setMovementMethod(new ScrollingMovementMethod());
        this.progressView = (SmoothProgressBar) dialogView.findViewById(R.id.progress_bar);
        this.postiveButton = (FloatingActionButton) dialogView.findViewById(R.id.pos);
        this.postiveButton.hide(false);
        this.postiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
     * Init with parameters.
     * @param context - Application context.
     * @param title - title of the dialog.
     */
    public LoadingDialog(Context context,String title){
        this.context = context;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        dialogView = inflater.inflate(R.layout.ui_loading_dialog, null);
        this.title = (TextView) dialogView.findViewById(R.id.dialog_title);
        this.title.setText(title == null || title.length()==0 ? "" : title);//validate string
        this.message= (TextView) dialogView.findViewById(R.id.dialog_message);
        this.message.setText("");
        this.message.setMovementMethod(new ScrollingMovementMethod());
        this.progressView = (SmoothProgressBar) dialogView.findViewById(R.id.progress_bar);

        this.postiveButton = (FloatingActionButton) dialogView.findViewById(R.id.pos);
        this.postiveButton.hide(false);
        this.postiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
     * Init with parameters.
     * @param context - Application context.
     * @param title - title of the dialog.
     * @param message - title of the message.
     */
    public LoadingDialog(Context context,String title,String message){
        this.context = context;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        dialogView = inflater.inflate(R.layout.ui_loading_dialog, null);
        this.title = (TextView) dialogView.findViewById(R.id.dialog_title);
        this.title.setText(title == null || title.length()==0 ? "" : title);//validate string
        this.message= (TextView) dialogView.findViewById(R.id.dialog_message);
        this.message.setText(message == null || title.length() == 0 ? "" : message);//validate string
        this.message.setMovementMethod(new ScrollingMovementMethod());
        this.progressView = (SmoothProgressBar) dialogView.findViewById(R.id.progress_bar);

        this.postiveButton = (FloatingActionButton) dialogView.findViewById(R.id.pos);
        this.postiveButton.hide(false);
        this.postiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
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
        this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (allowAnimation)
            dialog.getWindow().getAttributes().windowAnimations = R.style.customDialogAnimation;
    }

    /**
     * Show the dialog.
     */
    @Override
    public void show(){

        dialog.setCancelable(this.cancelable);
        dialog.show();
    }

    /**
     * Dismiss the dialog.
     */
    @Override
    public void dismiss(){
        if(!isLocked) dialog.dismiss();
        else Log.e(TAG, "Dialog is locked.");
    }

    /**
     * .complete() method is used to stop the progress bar (call "finish loading" animation)
     * @param autoDismiss - whether the dialog should dismiss after calling .complete().
     */
    public void complete(final boolean autoDismiss){
        if(!autoDismiss) title.setText("Completed");
        progressView.progressiveStop();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(autoDismiss) dismiss();
                else {
                    postiveButton.show(allowAnimation);
                    message.setText(text_completed);
                }
            }
        }, show_delay + (autoDismiss ? extra_delay : 0));

        this.dialog.setCancelable(true);
    }

    /**
     * .complete() method is used to stop the progress bar (call "finish loading" animation)
     *
     * @param autoDismiss - whether the dialog should dismiss after calling .complete().
     * @param isPositive - if the button that will be dismissed will look postive (Green) or negative (red).
     * @param newTitle - the new title that will be shown after .complete() is called, will be ignored if autoDismiss is true.
     * @param newMsg - the new message that will be shown after .complete() is called, will be ignored if autoDismiss is true.
     */
    public void complete(final boolean autoDismiss,final boolean isPositive,final String newTitle,final String newMsg){
        if(!autoDismiss) if(newTitle!=null)title.setText(newTitle);
        progressView.progressiveStop();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (autoDismiss) dismiss();
                else {
                    if (isPositive) postiveButton.show(allowAnimation);
                    else {
                        //display negative button
                        //TODO: change the look of the postive button
                        postiveButton.setColorNormalResId(R.color.red);
                        postiveButton.setColorPressedResId(R.color.red_dark);
                        postiveButton.setColorRippleResId(R.color.red_light);
                        postiveButton.setImageResource(R.drawable.ic_clear_white_24dp);
                        postiveButton.show(allowAnimation);
                    }
                    if(newMsg!=null) message.setText(newMsg);
                    onCompleted(postComplete);
                }
            }
        }, show_delay + (autoDismiss ? extra_delay : 0));
        this.dialog.setCancelable(true);
    }

    /**
     * This automatically executes the code that inside postComplete runnable.
     * is called when .complete() is called. will be ignored if postComplete is null.
     *
     * @param toExecute - is the runnable that will be executed.
     */
    private void onCompleted(PostCompleted toExecute){
        if(toExecute==null) return;
        toExecute.onComplete(this);
    }

    public void setProgressBarColor(String color){
        try {
            this.progressView.setSmoothProgressDrawableColor(Color.parseColor(color));
        } catch (Exception g){
            g.printStackTrace();
        }
    }


    /**
     *
     * Getters and Setters.
     *
     */

    public boolean isAllowAnimation() {
        return allowAnimation;
    }

    public void setAllowAnimation(boolean allowAnimation) {
        this.allowAnimation = allowAnimation;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }
    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    @Override
    public AlertDialog getDialog() {
        return dialog;
    }
    @Override
    public View getDialogView() {
        return dialogView;
    }


    public long getExtra_delay() {
        return extra_delay;
    }

    public void setExtra_delay(long extra_delay) {
        this.extra_delay = extra_delay;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public TextView getMessage() {
        return message;
    }

    @Deprecated
    public void setPostComplete(Runnable postComplete) {
        //this.postComplete = postComplete;
    }

    public void setPostComplete(PostCompleted todo){
        this.postComplete = todo;
    }

    public FloatingActionButton getPostiveButton() {
        return postiveButton;
    }

    public SmoothProgressBar getProgressView() {
        return progressView;
    }

    public long getShow_delay() {
        return show_delay;
    }

    public void setShow_delay(long show_delay) {
        this.show_delay = show_delay;
    }

    public TextView getTitle() {
        return title;
    }

    public interface PostCompleted{
        void onComplete(LoadingDialog dialog);
    }

}
