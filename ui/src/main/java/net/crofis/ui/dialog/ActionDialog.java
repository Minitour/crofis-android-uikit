package net.crofis.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.crofis.ui.R;
import net.crofis.ui.custom.actionitem.ActionItem;

import java.util.ArrayList;

/**
 * Created by Tony Zaitoun on 5/14/2016.
 */
public class ActionDialog extends BaseAlertDialog{
    private Context context;
    private TextView title;
    private TextView description;
    private ListView listView;
    private ArrayList<ActionItem> items;

    /**The View of the dialog**/
    private View dialogView;

    /**The Root AlertDialog**/
    private AlertDialog dialog;
    private boolean allowAnimation = true;
    private boolean cancelable = true;
    private boolean isLocked = false;
    private boolean showActionIcons = true;
    private Handler MainHandler;

    /**
     * Default ActionDialog constructor.
     * @param context - Application Context.
     * @param items - The action items.
     */

    public ActionDialog(Context context, final ArrayList<ActionItem> items){
        this.context = context;
        this.items = items;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        dialogView = inflater.inflate(R.layout.ui_action_dialog, null);
        title = (TextView) dialogView.findViewById(R.id.dialog_title);
        description = (TextView) dialogView.findViewById(R.id.dialog_description);
        listView = (ListView) dialogView.findViewById(R.id.listView);
        ActionDialogAdapter adapter = new ActionDialogAdapter(context,items);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        MainHandler = new Handler();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    items.get(position).getClickListener().onActionSelected();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
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
        if (allowAnimation) dialog.getWindow().getAttributes().windowAnimations = R.style.customDialogAnimation;

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
    }


    public void setDialogTitle(String title){
        try {
            setTitleVisable(true);
            this.title.setText(title);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void setDialogDescription(String description){
        try{
            setDescriptionVisable(true);
            this.description.setText(description);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void setTitleVisable(boolean show){
        try{
            if(show)this.title.setVisibility(View.VISIBLE);
            else this.title.setVisibility(View.GONE);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void setDescriptionVisable(boolean show){
        try{
            if(show)this.description.setVisibility(View.VISIBLE);
            else this.description.setVisibility(View.GONE);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    /**
     *
     * Getters and Setters.
     *
     */


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public TextView getTitle() {
        return title;
    }

    public ListView getListView() {
        return listView;
    }

    public ArrayList<ActionItem> getItems() {
        return items;
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

    public boolean isShowActionIcons() {
        return showActionIcons;
    }

    public void setShowActionIcons(boolean showActionIcons) {
        this.showActionIcons = showActionIcons;
    }

    public TextView getDescription() {
        return description;
    }

    /**
     * ListView Adapter Class.
     */
    public class ActionDialogAdapter extends BaseAdapter{

        Context context;
        ArrayList<ActionItem> items;

        public ActionDialogAdapter(Context context,ArrayList<ActionItem> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public ActionItem getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = items.get(position).getView(context,showActionIcons);
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.ui_general_action, null, false);
                ((TextView) convertView.findViewById(R.id.action_title)).setText(items.get(position).getTitle());
                if (showActionIcons)
                    ((ImageView) convertView.findViewById(R.id.action_icon)).setImageDrawable(items.get(position).getIcon());
                else convertView.findViewById(R.id.action_icon).setVisibility(View.GONE);
                return convertView;
            }
            return convertView;
        }
    }
}
