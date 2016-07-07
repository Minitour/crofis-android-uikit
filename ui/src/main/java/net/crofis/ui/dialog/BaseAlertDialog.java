package net.crofis.ui.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

/**
 * Created by Tony Zaitoun on 7/3/2016.
 */
public abstract class BaseAlertDialog {
     public final static String TAG = "CrofisAlertDialog";

    /**Application Context**/
    Context context;

    /**The View of the dialog**/
    View dialogView;

    /**The Root AlertDialog**/
    AlertDialog dialog;

    abstract void show();

    abstract void dismiss();

    abstract AlertDialog getDialog();

    abstract View getDialogView();

    abstract Context getContext();

}
