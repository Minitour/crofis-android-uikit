Camera Kit
------

####Normal Camera:
<img src="screenshots/device-2016-07-04-224046.png" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png"  height="400" />
```java
import net.crofis.ui.camera.CameraActivity;
```
```java
    CameraActivity.activity()
                  .setCropOptional(true)
                  .showZoomBar(false)
                  .saveFileToStorage(true)
                  .setCropAspectRatio(4,3)
                  .openWithCamera(1)
                  .start(this);
```

####Square Camera:
<img src="screenshots/device-2016-07-04-224311.png" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png"  height="400" />
```java
import net.crofis.ui.camera.SquareCameraActivity;
```
```java
    SquareCameraActivity.activity()
                        .displayFlashToggle(true)
                        .displayCameraSwitchToggle(true)
                        .setCropOptional(true)
                        .setCropAspectRatio(1,1)
                        .start(this);
```

Dialog Kit
------

####Loading Dialog
This dialog makes use of the [SmoothProgressBar](https://github.com/castorflex/SmoothProgressBar) library by [castorflex](https://github.com/castorflex).

<img src="screenshots/loading_dialog.gif" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png"  height="400" />

**Usage:**
```java
    final LoadingDialog dialog = new LoadingDialog(MainActivity.this,"Loading...");
    dialog.show();
```

And when ready to dismiss you can use one of the following:
```java
//Dismiss with animation.
dialog.complete(true);
```
Or
```java
    //Dismiss without waiting for the progress bar to finish the animation.
    dialog.dismiss();
```
And to display a message on the dialog use the following method:
```java
    /**
     * .complete() method is used to stop the progress bar (call "finish loading" animation)
     *
     * @param autoDismiss - whether the dialog should dismiss after calling .complete().
     * @param isPositive - if the button that will be dismissed will look postive (Green) or negative (red).
     * @param newTitle - the new title that will be shown after .complete() is called, will be ignored if autoDismiss is true.
     * @param newMsg - the new message that will be shown after .complete() is called, will be ignored if autoDismiss is true.
     */
    public void complete(final boolean autoDismiss,final boolean isPositive,final String newTitle,final String newMsg);
```
####Action Dialog
This dialog was inspired by the iOS [UIAlertController](https://developer.apple.com/library/ios/documentation/UIKit/Reference/UIAlertController_class/) class. And its main usage is to present a ListView with custom actions inside a dialog. I made this dialog because I simply needed a dialog that can hold more than 3 buttons, So now I can have as many as I want. Note that even if the list view exceeds the screen's limit, there is the built in ScrollView that comes with the ListView. Another important thing to note is that I made this as dynamic as possible, so other developers can also create their own custom action items.

<img src="screenshots/action_dialogs.gif" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png"  height="400" />

**Usage:**
```java
    import net.crofis.ui.custom.actionitem.ActionItem;
    import net.crofis.ui.custom.actionitem.ActionItemClickListener;
    import net.crofis.ui.dialog.ActionDialog;
```
```java
    //First we have to create the list that will contain the action items
    List <ActionItem> items = new ArrayList<>();
    
    //Fill the list with our desired actions
    Drawable icon = getResources().getDrawable(R.drawable.common_ic_googleplayservices);
    for (int i = 0; i < 4; i++) {
        final int finalI = i;
        
        //Create the action item with constructor ActionItem(Drawable,String, ActionItemClickListner);
        ActionItem item = new ActionItem(icon, "item " + (i + 1), new ActionItemClickListener() {
            @Override
            public void onActionSelected() {
                //This is what will be triggered when the action is selected.
                Toast.makeText(MainActivity.this,"item "+(finalI +1)+" selected.",Toast.LENGTH_SHORT).show();
            }
        });
      items.add(item);
    }
    
    //Create the ActionDialog object with constuctor ActionDialog(Context,List<ActionItem>)
    final ActionDialog dialog = new ActionDialog(this, items);
    
    //Optionl customization - set the gravity of the dialog (TOP or BOTTOM)
    //Must import net.crofis.ui.dialog.DialogManager;
    DialogManager.setDialogPosition(dialog,dialog.getDialog(), Gravity.BOTTOM);
    
    //Set the text - if not set, the TextViews will disappear.
    dialog.setDialogTitle("Select Action");
    dialog.setDialogDescription("Select an action to close the dialog.");
    
    //Finall, show the dialog
    dialog.show();
```
**Create your own ActionItem class:**

Here is an example of a class that extends ActionItem:
```java
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
        
        /**
          * getView() is a method that is used only by sub-classes. It will always return null by the super class.
          * Example on how to override:
          *
          * The following code snippet, will use the default item but it will be designed like iOS.
          */
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
```

ui_general_action.xml
```xml
  <?xml version="1.0" encoding="utf-8"?>
  <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="horizontal" android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:gravity="center_vertical">

    <ImageView
        android:layout_margin="8dp"
        android:layout_width="32dp"
        android:layout_height="32dp"
        android:id="@+id/action_icon" />

    <TextView
        android:layout_weight="1"
        android:layout_margin="8dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textAppearance="?android:attr/textAppearanceMedium"
        android:text="Medium Text"
        android:id="@+id/action_title" />
</LinearLayout>
```
And then simply do:
```java
    ArrayList<ActionItem> items = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
        final int finalI = i;
        ActionItem item = new UIAlertAction(null, "item " + (i + 1), new ActionItemClickListener() {
            @Override
            public void onActionSelected() {
                Toast.makeText(this,"item "+(finalI +1)+" selected.",Toast.LENGTH_SHORT).show();
            }
        });
        items.add(item);
    }

    final ActionDialog dialog = new ActionDialog(this, items);
    dialog.getTitle().setGravity(Gravity.CENTER);
    dialog.getDescription().setGravity(Gravity.CENTER);
    dialog.setDialogTitle("Select Action");
    dialog.setDialogDescription("Select an action to close the dialog.");

    dialog.show();
```
