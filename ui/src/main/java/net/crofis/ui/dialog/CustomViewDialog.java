package net.crofis.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import net.crofis.ui.R;

/**
 * Created by Tony Zaitoun on 6/8/2016.
 */
public class CustomViewDialog {
    private static final String TAG = "CustomViewDialog";

    /**Application context**/
    private Context context;

    /**Title of the dialog**/
    private TextView title;

    /**Positive Button**/
    private FloatingActionButton postiveButton;

    /**Negative Button**/
    private FloatingActionButton negativeButton;

    private View.OnClickListener PositiveListener;

    private View.OnClickListener NegativeListener;

    private LinearLayout customViewLayout;

    private View customView;

    /**The View of the dialog**/
    private View dialogView;

    /**The Root AlertDialog**/
    private AlertDialog dialog;

    /**In charge of the slide in animation**/
    private boolean allowAnimation = true;

    /**Is dialog cancelable**/
    private boolean cancelable = true;

    private boolean isLocked = false;

    /**Delay of button animation to start**/
    private static final int startControlAnimationDelay = 300;

    /**Delay between each button**/
    private static final int gapControlAnimationDelay = 150;



    public CustomViewDialog(Context context){
        this.context = context;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        dialogView = inflater.inflate(R.layout.ui_custom_view_dialog, null);
        title = (TextView) dialogView.findViewById(R.id.dialog_title);
        customViewLayout = (LinearLayout) dialogView.findViewById(R.id.customView);
        postiveButton = (FloatingActionButton) dialogView.findViewById(R.id.pos);
        negativeButton = (FloatingActionButton) dialogView.findViewById(R.id.neg);
        dialogView.findViewById(R.id.background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialog = dialogBuilder.create();
    }

    public void show(){
        if(getTitle()==null) this.title.setVisibility(View.GONE);
        if(NegativeListener == null) this.negativeButton.setVisibility(View.GONE);
        if(PositiveListener == null) this.postiveButton.setVisibility(View.GONE);
        this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (allowAnimation)
            dialog.getWindow().getAttributes().windowAnimations = R.style.customDialogAnimation;
        dialog.setCancelable(this.cancelable);
        dialog.show();
    }

    public void dismiss(){
        if(!isLocked) dialog.dismiss();
        else Log.e(TAG,"Dialog is locked.");
    }

    public void setCustomView(View v){
        try {
            this.customViewLayout.removeAllViews();
            this.customViewLayout.addView(v);
        }catch (NullPointerException e){
            //Handle Exception
            e.printStackTrace();
        }
        this.customView = v;

    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getTitle() {
        try {
            return title.getText().toString();
        }catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }

    public void setTitle(String title) {
        try{
            this.title.setText(title);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public FloatingActionButton getPostiveButton() {
        return postiveButton;
    }

    public void setPostiveButton(FloatingActionButton postiveButton) {
        this.postiveButton = postiveButton;
    }

    public FloatingActionButton getNegativeButton() {
        return negativeButton;
    }

    public void setNegativeButton(FloatingActionButton negativeButton) {
        this.negativeButton = negativeButton;
    }

    public View.OnClickListener getPositiveListener() {
        return PositiveListener;
    }

    public void setPositiveListener(View.OnClickListener positiveListener) {
        PositiveListener = positiveListener;
    }

    public View.OnClickListener getNegativeListener() {
        return NegativeListener;
    }

    public void setNegativeListener(View.OnClickListener negativeListener) {
        NegativeListener = negativeListener;
    }

    public View getDialogView() {
        return dialogView;
    }

    public void setDialogView(View dialogView) {
        this.dialogView = dialogView;
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }

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

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
