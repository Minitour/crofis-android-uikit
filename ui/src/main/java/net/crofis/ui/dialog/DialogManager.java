package net.crofis.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import net.crofis.ui.R;
import net.crofis.ui.custom.actionitem.ActionItem;

import java.util.ArrayList;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.TOP;

/**
 * Created by Tony Zaitoun on 4/6/2016.
 */
public final class DialogManager {

    private static final String TAG = "DialogManager";


    /**
     *
     * @param context context of the app.
     * @return returns a NewMessageDialog.
     */
    public static NewMessageDialog makeMessageDialog(Context context)
    {
        NewMessageDialog dialog = new NewMessageDialog(context);
        return dialog;
    }

    /**
     *
     * @param context context of the app.
     * @param title  title of the dialog.
     * @return returns a NewMessageDialog.
     */
    public static NewMessageDialog makeMessageDialog(Context context, String title){
        NewMessageDialog dialog = makeMessageDialog(context);
        dialog.setTitle(title);
        return dialog;
    }

    /**
     *
     * @param context context of the app.
     * @param title  title of the dialog.
     * @param image image of the dialog.
     * @return returns a NewMessageDialog.
     */

    public static NewMessageDialog makeMessageDialog(Context context, String title, Bitmap image){
        NewMessageDialog dialog = makeMessageDialog(context,title);
        dialog.setImageBitmap(image);
        return dialog;
    }

    public static NewMessageDialog makeMessageDialog(Context context, String title, boolean UseCamera){
        NewMessageDialog dialog = makeMessageDialog(context,title);
        dialog.setAllowCameraButton(UseCamera);
        return dialog;
    }



    /**
     * returns a read dialog.
     * @param context context of the app.
     * @return
     */
    public static InfoDialog makeReadDialog(Context context){
        InfoDialog dialog = new InfoDialog(context);
        dialog.getPostiveButton().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_thumb_up_white_24dp));
        dialog.getNegativeButton().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_thumb_down_white_24dp));
        dialog.getNaturalButton().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_message_white_24dp));
        return dialog;
    }

    /**
     *
     * @param context context of the app.
     * @param title title of the dialog.
     * @param message message of the dialog.
     * @return returns a read-dialog.
     */
    public static InfoDialog makeReadDialog(Context context,String title,String message){
        InfoDialog dialog = makeReadDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        return dialog;
    }

    /**
     *
     * @param context context of the app.
     * @param title title of the dialog.
     * @param message message of the dialog.
     * @param icon icon of the dialog (bitmap).
     * @return returns a read-dialog.
     */

    public static InfoDialog makeReadDialog(Context context,String title,String message,Bitmap icon){
        InfoDialog dialog = makeReadDialog(context,title,message);
        dialog.setBitmapIcon(icon);
        return dialog;
    }

    /**
     *
     * @param context context of the app.
     * @param title title of the dialog.
     * @param message message of the dialog.
     * @param icon icon of the app (drawable).
     * @return returns a read-dialog.
     */

    public static InfoDialog makeReadDialog(Context context,String title,String message,Drawable icon){
        InfoDialog dialog = makeReadDialog(context,title,message);
        dialog.setDrawableIcon(icon);
        return dialog;
    }

    /**
     *
     * @param context context of the app.
     * @param title title of the dialog.
     * @param message message of the dialog.
     * @param icon icon of the app (drawable).
     * @param animation fade-in fade-out animation.
     * @return returns a read-dialog.
     */

    public static InfoDialog makeReadDialog(Context context,String title,String message,Drawable icon,boolean animation){
        InfoDialog dialog = makeReadDialog(context,title,message,icon);
        dialog.showAnimation(animation);
        return dialog;
    }

    /**
     *
     * @param context context of the app.
     * @param title title of the dialog.
     * @param message message of the dialog.
     * @param icon icon of the app (bitmap).
     * @param animation fade-in fade-out animation.
     * @return returns a read-dialog.
     */

    public static InfoDialog makeReadDialog(Context context,String title,String message,Bitmap icon,boolean animation){
        InfoDialog dialog = makeReadDialog(context,title,message,icon);
        dialog.showAnimation(animation);
        return dialog;
    }

    /**
     *
     * @param context Context of the app.
     * @param title Title of the dialog.
     * @param message Message of the dialog.
     * @return returns InfoDialog.
     */
    public static InfoDialog makeDialog(Context context,String title,String message){
        InfoDialog infoDialog = new InfoDialog(context);
        infoDialog.setTitle(title);
        infoDialog.setMessage(message);
        return infoDialog;
    }

    /**
     *
     * @param context Context of the app.
     * @param title Title of the dialog.
     * @param message Message of the dialog.
     * @param icon icon of the dialog (Bitmap).
     * @return returns InfoDialog.
     */

    public static InfoDialog makeDialog(Context context,String title,String message,Bitmap icon){
        InfoDialog infoDialog = makeDialog(context, title, message);
        infoDialog.setBitmapIcon(icon);
        return infoDialog;
    }

    /**
     *
     * @param context Context of the app.
     * @param title Title of the dialog.
     * @param message Message of the dialog.
     * @param icon icon of the dialog (Drawable).
     * @return returns InfoDialog.
     */
    public static InfoDialog makeDialog(Context context,String title,String message,Drawable icon){
        InfoDialog infoDialog = makeDialog(context, title, message);
        infoDialog.setDrawableIcon(icon);
        return infoDialog;
    }

    /**
     *
     * @param context Context of the app.
     * @param title Title of the dialog.
     * @param message Message of the dialog.
     * @param icon icon of the dialog (Bitmap).
     * @param animation show with/without animation.
     * @return returns InfoDialog.
     */
    public static InfoDialog makeDialog(Context context,String title,String message,Bitmap icon,boolean animation){
        InfoDialog infoDialog = makeDialog(context,title,message,icon);
        infoDialog.showAnimation(animation);
        return infoDialog;
    }

    /**
     *
     * @param context Context of the app.
     * @param title Title of the dialog.
     * @param message Message of the dialog.
     * @param icon icon of the dialog (Drawable).
     * @param animation show with/without animation.
     * @return returns InfoDialog.
     */
    public static InfoDialog makeDialog(Context context,String title,String message,Drawable icon,boolean animation){
        InfoDialog infoDialog = makeDialog(context, title, message,icon);
        infoDialog.showAnimation(animation);
        return infoDialog;
    }


    /**
     *
     * @param context - Application context.
     * @param actionList - An Array List that contains the names of the actions, the icons, and the runnables
     * @param title - The title of the dialog.
     * @return ActionDialog
     */
    public static ActionDialog makeActionDialog(Context context, ArrayList<ActionItem> actionList, String title){
        ActionDialog dialog = new ActionDialog(context,actionList);
        dialog.getTitle().setText(title);
        return dialog;
    }

    /**
     *
     * @param context - Application context.
     * @param actionList - An Array List that contains the names of the actions, the icons, and the runnables
     * @param title - The title of the dialog.
     * @param showTitle - true to display the title, false to hide it.
     * @return ActionDialog
     */
    public static ActionDialog makeActionDialog(Context context, ArrayList<ActionItem> actionList,String title,boolean showTitle){
        ActionDialog dialog = new ActionDialog(context,actionList);
        if(showTitle) dialog.getTitle().setVisibility(View.VISIBLE);
        else dialog.getTitle().setVisibility(View.GONE);
        return dialog;
    }

    /**
     *
     * @param context - Application context.
     * @param actionList - An Array List that contains the names of the actions, the icons, and the runnables
     * @param title - The title of the dialog.
     * @param showTitle - true to display the title, false to hide it.
     * @param showIcons - true to show icons, false to hide them.
     * @return ActionDialog
     */
    public static ActionDialog makeActionDialog(Context context, ArrayList<ActionItem> actionList,String title,boolean showTitle,boolean showIcons){
        ActionDialog dialog = makeActionDialog(context, actionList,title, showTitle);
        dialog.setShowActionIcons(showIcons);
        return dialog;
    }


    /**
     * Set the position of the dialog. only supports ActionDialog, InfoDialog,LoadingDialog.
     *
     * @param parent The Dialog Object.
     * @param dialog use dialog.getAlertDialog()
     * @param gravity The requested gravity (only top or bottom or center)
     */
    @Deprecated
    public static void setDialogPosition(BaseAlertDialog parent,Dialog dialog,int gravity){

        if(parent instanceof ActionDialog||parent instanceof InfoDialog||parent instanceof LoadingDialog){
            WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();

            if(gravity == TOP ||gravity == BOTTOM||gravity == CENTER) wlp.gravity = gravity;
            else {
                Log.e(TAG, "setDialogPosition: Gravity type not supported! only Gravity.TOP or Gravity.BOTTOM.");
                return;
            }

            //wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            wlp.windowAnimations = R.style.bottomDialogAnimation;
            switch (gravity){
                case BOTTOM:
                    wlp.windowAnimations = R.style.bottomDialogAnimation;
                    break;
                case TOP:
                    wlp.windowAnimations = R.style.topDialogAnimation;
                    break;
                default:
                    wlp.windowAnimations = R.style.customDialogAnimation;
                    break;
            }
            dialog.getWindow().setAttributes(wlp);

            if(parent instanceof ActionDialog){
                ActionDialog dialog1 = (ActionDialog)parent;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)dialog1.getDialogView().findViewById(R.id.dialogFrame).getLayoutParams();

                int topMargin = params.topMargin;
                int leftMargin = params.leftMargin;
                int rightMargin = params.rightMargin;
                int bottomMargin = params.bottomMargin;
                switch (gravity){
                    case BOTTOM:
                        bottomMargin/=3;
                        break;
                    case TOP:
                        topMargin/=3;
                        break;
                }

                params.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
                dialog1.getDialogView().setLayoutParams(params);
                return;
            }
            if(parent instanceof InfoDialog){
                InfoDialog dialog1 = (InfoDialog)parent;
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)dialog1.getDialogView().getLayoutParams();
                int topMargin = params.topMargin;
                int leftMargin = params.leftMargin;
                int rightMargin = params.rightMargin;
                int bottomMargin = params.bottomMargin;
                switch (gravity){
                    case BOTTOM:
                        bottomMargin/=3;
                        break;
                    case TOP:
                        topMargin/=3;
                        break;
                }

                params.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
                dialog1.getDialogView().setLayoutParams(params);
                return;
            }
            if(parent instanceof LoadingDialog){
                InfoDialog dialog1 = (InfoDialog)parent;
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)dialog1.getDialogView().getLayoutParams();
                int topMargin = params.topMargin;
                int leftMargin = params.leftMargin;
                int rightMargin = params.rightMargin;
                int bottomMargin = params.bottomMargin;
                switch (gravity){
                    case BOTTOM:
                        bottomMargin/=3;
                        break;
                    case TOP:
                        topMargin/=3;
                        break;
                }

                params.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
                dialog1.getDialogView().setLayoutParams(params);
                return;
            }
        }
        else {
            Log.e(TAG, "setDialogPosition: Dialog type not supported! Supported dialogs: ActionDialog, InfoDialog, LoadingDialog.");
            return;
        }
    }

    /**
     * Set the position of the dialog. Support every sub class of BaseAlertDialog.
     *
     * @param parent Sub class of BaseAlertDialog.
     * @param gravity The requested position. Gravity.TOP, Gravity.BOTTOM
     */
    public static void setDialogPostion(BaseAlertDialog parent,int gravity){
        AlertDialog dialog = parent.getDialog();
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();

        if(gravity == TOP ||gravity == BOTTOM||gravity == CENTER) wlp.gravity = gravity;
        else {
            Log.e(TAG, "setDialogPosition: Gravity type not supported! only Gravity.TOP or Gravity.BOTTOM.");
            return;
        }

        //wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.windowAnimations = R.style.bottomDialogAnimation;
        switch (gravity){
            case BOTTOM:
                wlp.windowAnimations = R.style.bottomDialogAnimation;
                break;
            case TOP:
                wlp.windowAnimations = R.style.topDialogAnimation;
                break;
            default:
                wlp.windowAnimations = R.style.customDialogAnimation;
                break;
        }
        dialog.getWindow().setAttributes(wlp);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)parent.getDialogView().getLayoutParams();
        int topMargin = params.topMargin;
        int leftMargin = params.leftMargin;
        int rightMargin = params.rightMargin;
        int bottomMargin = params.bottomMargin;
        switch (gravity){
            case BOTTOM:
                bottomMargin/=3;
                break;
            case TOP:
                topMargin/=3;
                break;
        }
        params.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
        parent.getDialogView().setLayoutParams(params);
    }








}
