package net.crofis.ui.custom.actionitem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.crofis.ui.R;

/**
 * Created by Tony Zaitoun on 7/3/2016.
 */
public class UIAlertAction extends ActionItem {

    @Override
    public String getTitle() {
        return super.getTitle();
    }

    /**
     * Default constructor.
     *
     * @param icon     - Action Icon
     * @param title    - Action Title
     * @param listener - Action Listener
     */



    public UIAlertAction(Drawable icon, String title, ActionItemClickListener listener) {
        super(icon, title, listener);
    }

    @Override
    public View getView(Context context, boolean showActionIcons) {
        View convertView =  LayoutInflater.from(context).inflate(R.layout.ui_general_action, null, false);
        TextView title = (TextView) convertView.findViewById(R.id.action_title);
        title.setText(getTitle());
        title.setGravity(Gravity.CENTER);
        title.setTextColor(context.getResources().getColor(R.color.blue));
        if (false)((ImageView) convertView.findViewById(R.id.action_icon)).setImageDrawable(getIcon());
        else convertView.findViewById(R.id.action_icon).setVisibility(View.GONE);
        return convertView;
    }
}
