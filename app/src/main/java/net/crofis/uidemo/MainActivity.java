package net.crofis.uidemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import net.crofis.ui.camera.CameraActivity;
import net.crofis.ui.camera.SquareCameraActivity;
import net.crofis.ui.custom.actionitem.ActionItem;
import net.crofis.ui.custom.actionitem.ActionItemClickListener;
import net.crofis.ui.custom.actionitem.UIAlertAction;
import net.crofis.ui.custom.cropper.CropImage;
import net.crofis.ui.custom.cropper.CropImageView;
import net.crofis.ui.custom.imagepicker.activities.AlbumSelectActivity;
import net.crofis.ui.custom.imagepicker.helpers.Constants;
import net.crofis.ui.custom.imagepicker.models.Image;
import net.crofis.ui.dialog.ActionDialog;
import net.crofis.ui.dialog.CameraDialog;
import net.crofis.ui.dialog.DialogManager;
import net.crofis.ui.dialog.LoadingDialog;
import net.crofis.ui.dialog.NewMessageDialog;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogManager.makeDialog(MainActivity.this,"Dialog Title","some informative text should be displayed here.").show();
                Intent intent = new Intent(MainActivity.this, AlbumSelectActivity.class);
//set limit on number of images that can be selected, default is 10
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 1);
                startActivityForResult(intent, Constants.REQUEST_CODE);
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NewMessageDialog messageDialog = DialogManager.makeMessageDialog(MainActivity.this, "New Message", false);//new NewMessageDialog(MainActivity.this);
//                messageDialog.setTitle("New Message");
//                messageDialog.setPostiveButtonOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        messageDialog.getRootDialog().cancel();
//                        Toast.makeText(MainActivity.this, "Message Sent!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                messageDialog.setNegativeButtonOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        messageDialog.dismiss();
//                    }
//                });
                messageDialog.show();
            }
        });
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoadingDialog dialog = new LoadingDialog(MainActivity.this,"Loading...");
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.complete(false,false,"new title","new message");
                    }
                },5000);

//                final CustomViewDialog dialog = new CustomViewDialog(MainActivity.this);
//                LinearLayout layout = new LinearLayout(MainActivity.this);
//                layout.setOrientation(LinearLayout.VERTICAL);
//                for (int i = 0; i < 50; i++) {
//                    TextView tv = new TextView(MainActivity.this);
//                    tv.setText("hello world "+(i+1));
//                    layout.addView(tv);
//                }
//                ScrollView view = new ScrollView(MainActivity.this);
//                view.addView(layout);
//                dialog.setCustomView(view);
//                dialog.show();


            }
        });

        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CameraDialog cameraDialog = new CameraDialog(MainActivity.this);
                cameraDialog.show();
                cameraDialog.setPostImageTaken(new Runnable() {
                    @Override
                    public void run() {
                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(cameraDialog.getImageTaken());
                    }
                });
            }
        });

        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
//                intent.putExtra(CameraActivity.FLAG_SET_CROP_OPTIONAL,true);
//                intent.putExtra(CameraActivity.FLAG_SAVE_TO_STORAGE,true);
//                intent.putExtra(CameraActivity.FLAG_DISPLAY_SWITCH_CAM, false);
//                intent.putExtra(CameraActivity.FLAG_ALLOW_ROTATION_ANIMATION,false);
//                startActivityForResult(intent, CameraActivity.REQUEST_CODE);


                CameraActivity.activity()
                        .setCropOptional(true)
                        .start(MainActivity.this);
            }
        });

        findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SquareCameraActivity.class);
//                intent.putExtra(SquareCameraActivity.FLAG_DISPLAY_FLASH_TOGGLE,false);
//                intent.putExtra(SquareCameraActivity.FLAG_DISPLAY_SWITCH_CAM,false);
//                intent.putExtra(SquareCameraActivity.FLAG_SAVE_TO_STORAGE,true);
//                intent.putExtra(SquareCameraActivity.FLAG_RETURN_DATA_AS_BYTE_ARRAY, true);
//                intent.putExtra(SquareCameraActivity.FLAG_SET_CROP_ASPECT_RATIO, SquareCameraActivity.CROP_RATIO_1_1);
//                startActivityForResult(intent, SquareCameraActivity.REQUEST_CODE);

                SquareCameraActivity.activity()
                        .displayFlashToggle(true)
                        .displayCameraSwitchToggle(true)
                        .setCropOptional(true)
                        .setCropAspectRatio(1,1)
                        .start(MainActivity.this);



            }
        });

        findViewById(R.id.btn7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ActionItem> items = new ArrayList<>();
                Drawable icon = MainActivity.this.getResources().getDrawable(R.drawable.common_ic_googleplayservices);
                for (int i = 0; i < 4; i++) {
                    final int finalI = i;
                    ActionItem item = new ActionItem(icon, "item " + (i + 1), new ActionItemClickListener() {
                        @Override
                        public void onActionSelected() {
                            Toast.makeText(MainActivity.this,"item "+(finalI +1)+" selected.",Toast.LENGTH_SHORT).show();
                        }
                    });
                    items.add(item);
                }

                final ActionDialog dialog = new ActionDialog(MainActivity.this, items);
                DialogManager.setDialogPosition(dialog,dialog.getDialog(), Gravity.BOTTOM);

                dialog.setDialogTitle("Select Action");
                dialog.setDialogDescription("Select an action to close the dialog.");
                //dialog.setShowActionIcons(false);
                dialog.show();
            }
        });

        findViewById(R.id.btn8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ActionItem> items = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    final int finalI = i;
                    ActionItem item = new UIAlertAction(null, "item " + (i + 1), new ActionItemClickListener() {
                        @Override
                        public void onActionSelected() {
                            Toast.makeText(MainActivity.this,"item "+(finalI +1)+" selected.",Toast.LENGTH_SHORT).show();
                        }
                    });
                    items.add(item);
                }

                final ActionDialog dialog = new ActionDialog(MainActivity.this, items);
                //DialogManager.setDialogPosition(dialog,dialog.getDialog(), Gravity.BOTTOM);
                dialog.getTitle().setGravity(Gravity.CENTER);
                dialog.getDescription().setGravity(Gravity.CENTER);
                dialog.setDialogTitle("Select Action");
                dialog.setDialogDescription("Select an action to close the dialog.");

                //dialog.setShowActionIcons(false);
                dialog.show();


            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("MainActivity", "Invoked with params: reqC:" + requestCode + " resC:" + resultCode);
        switch (requestCode) {
            case SquareCameraActivity.REQUEST_CODE:
                if (resultCode == 1) {
                    byte[] arr = data.getByteArrayExtra("data");
                    Bitmap bmp = BitmapFactory.decodeByteArray(arr, 0, arr.length);
                    ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bmp);
                }

                break;
            case CameraActivity.REQUEST_CODE:
                if (resultCode == 1) {
                    byte[] arr = data.getByteArrayExtra("data");
                    Bitmap bmp = BitmapFactory.decodeByteArray(arr, 0, arr.length);
                    ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bmp);
                    Log.i("", "");
                }
                break;
            case Constants.REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                    //((ImageView) findViewById(R.id.imageView)).setImageBitmap(images.get(0).getBitmap());
                    CropImage.activity(Uri.fromFile(new File(images.get(0).path)))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);

                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    ((ImageView) findViewById(R.id.imageView)).setImageBitmap(getBitmapByPath(resultUri.getPath()));
                }
                break;
        }
    }

    public Bitmap getBitmapByPath(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        return bm;
    }
}


