// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package net.crofis.ui.custom.cropper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import net.crofis.ui.R;
import net.crofis.ui.custom.actionitem.ActionItem;
import net.crofis.ui.custom.actionitem.ActionItemClickListener;
import net.crofis.ui.dialog.ActionDialog;
import net.crofis.ui.dialog.DialogManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Built-in activity for image cropping.<br>
 * Use {@link CropImage#activity(Uri)} to create a builder to start this activity.
 */
public class CropImageActivity extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnSaveCroppedImageCompleteListener {

    /**
     * The crop image view library widget used in the activity
     */
    private CropImageView mCropImageView;

    /**
     * the options that were set for the crop image
     */
    private CropImageOptions mOptions;

    private final String [] ratios = {"Original","1:1","2:3","3:5","3:4","4:5","5:7","9:16","Cancel"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image_activity);

        mCropImageView = (CropImageView) findViewById(R.id.cropImageView);

        Intent intent = getIntent();
        Uri source = intent.getParcelableExtra(CropImage.CROP_IMAGE_EXTRA_SOURCE);
        mOptions = intent.getParcelableExtra(CropImage.CROP_IMAGE_EXTRA_OPTIONS);

        if (savedInstanceState == null) {
            mCropImageView.setImageUriAsync(source);
        }

        //ActionBar actionBar = getSupportActionBar();
        findViewById(R.id.action_rotate_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage(-90);
            }
        });

        findViewById(R.id.action_rotate_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage(90);
            }
        });

        findViewById(R.id.action_restore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCropImageView.resetCropRect();
            }
        });

        findViewById(R.id.action_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

        findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultCancel();
            }
        });
        findViewById(R.id.action_change_ratio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mCropImageView.setAspectRatio();
                ArrayList<ActionItem> items = new ArrayList<ActionItem>();
                for(int i = 0;i<ratios.length;i++) {
                    final int finalI = i;
                    items.add(new UIAlertAction(null, ratios[i], new ActionItemClickListener() {
                        @Override
                        public void onActionSelected() {
                            switch (ratios[finalI]){
                                case "Original":
                                    mCropImageView.setFixedAspectRatio(false);
                                    break;
                                case "Cancel":
                                    break;
                                default:
                                    mCropImageView.setFixedAspectRatio(true);
                                    mCropImageView.setAspectRatio(Integer.parseInt(ratios[finalI].split(":")[0]),Integer.parseInt(ratios[finalI].split(":")[1]));
                                    break;


                            }
                        }
                    }));
                }


                final ActionDialog dialog = new ActionDialog(CropImageActivity.this,items);
                DialogManager.setDialogPosition(dialog,dialog.getDialog(),Gravity.BOTTOM);
                dialog.show();
            }
        });
//        if (actionBar != null) {
//            String title = mOptions.activityTitle != null && !mOptions.activityTitle.isEmpty()
//                    ? mOptions.activityTitle
//                    : getResources().getString(R.string.crop_image_activity_title);
//            actionBar.setTitle(title);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
        hide();
    }

    private void hide() {
        if (Build.VERSION.SDK_INT >= 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        WindowManager.LayoutParams attrs =this.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        this.getWindow().setAttributes(attrs);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnSaveCroppedImageCompleteListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCropImageView.setOnSetImageUriCompleteListener(null);
        mCropImageView.setOnSaveCroppedImageCompleteListener(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crop_image_menu, menu);

        if (!mOptions.allowRotation) {
            menu.removeItem(R.id.crop_image_menu_rotate);
        }

        Drawable cropIcon = null;
        try {
            cropIcon = ContextCompat.getDrawable(this, R.drawable.crop_image_menu_crop);
            if (cropIcon != null) {
                menu.findItem(R.id.crop_image_menu_crop).setIcon(cropIcon);
            }
        } catch (Exception e) {
        }

        if (mOptions.activityMenuIconColor != 0) {
            updateMenuItemIconColor(menu, R.id.crop_image_menu_rotate, mOptions.activityMenuIconColor);
            if (cropIcon != null) {
                updateMenuItemIconColor(menu, R.id.crop_image_menu_crop, mOptions.activityMenuIconColor);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.crop_image_menu_crop) {
            cropImage();
            return true;
        }
        if (item.getItemId() == R.id.crop_image_menu_rotate) {
            rotateImage();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            setResultCancel();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResultCancel();
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            if (mOptions.initialCropWindowRectangle != null) {
                mCropImageView.setCropRect(mOptions.initialCropWindowRectangle);
            }
            if (mOptions.initialRotation > -1) {
                mCropImageView.setRotatedDegrees(mOptions.initialRotation);
            }
        } else {
            setResult(null, error);
        }
    }

    @Override
    public void onSaveCroppedImageComplete(CropImageView view, Uri uri, Exception error) {
        setResult(uri, error);
    }

    //region: Private methods

    /**
     * Execute crop image and save the result tou output uri.
     */
    protected void cropImage() {
        if (mOptions.noOutputImage) {
            setResult(null, null);
        } else {
            Uri outputUri = getOutputUri();
            mCropImageView.saveCroppedImageAsync(outputUri,
                    mOptions.outputCompressFormat,
                    mOptions.outputCompressQuality,
                    mOptions.outputRequestWidth,
                    mOptions.outputRequestHeight);
        }
    }

    /**
     * Rotate the image in the crop image view.
     */
    protected void rotateImage() {
        mCropImageView.rotateImage(90);
    }

    private void rotateImage(int degrees){
        mCropImageView.rotateImage(degrees);
    }

    /**
     * Get Android uri to save the cropped image into.<br>
     * Use the given in options or create a temp file.
     */
    protected Uri getOutputUri() {
        Uri outputUri = mOptions.outputUri;
        if (outputUri.equals(Uri.EMPTY)) {
            try {
                String ext = mOptions.outputCompressFormat == Bitmap.CompressFormat.JPEG ? ".jpg" :
                        mOptions.outputCompressFormat == Bitmap.CompressFormat.PNG ? ".png" : ".wepb";
                outputUri = Uri.fromFile(File.createTempFile("cropped", ext, getCacheDir()));
            } catch (IOException e) {
                throw new RuntimeException("Failed to create temp file for output image", e);
            }
        }
        return outputUri;
    }

    /**
     * Result with cropped image data or error if failed.
     */
    protected void setResult(Uri uri, Exception error) {
        int resultCode = error == null ? RESULT_OK : CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE;
        setResult(resultCode, getResultIntent(uri, error));
        finish();
    }

    /**
     * Cancel of cropping activity.
     */
    protected void setResultCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * Get intent instance to be used for the result of this activity.
     */
    protected Intent getResultIntent(Uri uri, Exception error) {
        CropImage.ActivityResult result = new CropImage.ActivityResult(uri,
                error,
                mCropImageView.getCropPoints(),
                mCropImageView.getCropRect(),
                mCropImageView.getRotatedDegrees());
        Intent intent = new Intent();
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, result);
        return intent;
    }

    /**
     * Update the color of a specific menu item to the given color.
     */
    private void updateMenuItemIconColor(Menu menu, int itemId, int color) {
        MenuItem menuItem = menu.findItem(itemId);
        if (menuItem != null) {
            Drawable menuItemIcon = menuItem.getIcon();
            if (menuItemIcon != null) {
                try {
                    menuItemIcon.mutate();
                    menuItemIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                    menuItem.setIcon(menuItemIcon);
                } catch (Exception e) {
                }
            }
        }
    }
    //endregion
}

class UIAlertAction extends ActionItem {

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

