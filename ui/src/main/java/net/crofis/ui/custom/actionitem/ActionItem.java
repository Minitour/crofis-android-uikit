package net.crofis.ui.custom.actionitem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Tony Zaitoun on 5/14/2016.
 */
public class ActionItem {
    private Drawable icon;
    private String title;
    private ActionItemClickListener clickListener;

    public ActionItem(Drawable icon,String title, ActionItemClickListener listener){
        this.icon = icon;
        this.title = title;
        this.clickListener = listener;
    }

    public ActionItemClickListener getClickListener() {
        return clickListener;
    }

    public void setOnClickListener(ActionItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * Get the icon of the action.
     * @return
     */
    public Drawable getIcon() {
        return icon;
    }

    /**
     * Set the icon of the action
     * @param icon
     */
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    /**
     * Get the title.
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the action title.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * getView() is a method that is used only by sub-classes. It will always return null by the super class.
     * Example on how to override:
     *
     *   //The following code snippet, will use the default item but it will be designed like iOS.
     *   View convertView =  LayoutInflater.from(context).inflate(R.layout.ui_general_action, null, false);
     *   TextView title = (TextView) convertView.findViewById(R.id.action_title);
     *   title.setText(getTitle());
     *   title.setGravity(Gravity.CENTER);
     *   title.setTextColor(context.getResources().getColor(R.color.blue));
     *   if (false)((ImageView) convertView.findViewById(R.id.action_icon)).setImageDrawable(getIcon());
     *   else convertView.findViewById(R.id.action_icon).setVisibility(View.GONE);
     *   return convertView;
     *
     *
     * @param context - The context that will be given when the method is called within the ListView Adapter.
     * @param showActionIcons - The setting that was set in the ActionDialog class.
     * @return The custom view you wish to use,return null if you want the default item.
     */
    public View getView(Context context,boolean showActionIcons){
        return null;
    }
}
