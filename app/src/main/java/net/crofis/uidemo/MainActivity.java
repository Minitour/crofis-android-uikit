package net.crofis.uidemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import net.crofis.ui.dialog.BaseAlertDialog;
import net.crofis.ui.dialog.CameraDialog;
import net.crofis.ui.dialog.DialogManager;
import net.crofis.ui.dialog.LoadingDialog;
import net.crofis.ui.dialog.NewMessageDialog;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    CameraDialog camDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogManager.makeDialog(MainActivity.this,"Dialog Title","some informative text should be displayed here.").show();
//                Intent intent = new Intent(MainActivity.this, AlbumSelectActivity.class);
////set limit on number of images that can be selected, default is 10
//                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 1);
//                startActivityForResult(intent, Constants.REQUEST_CODE);

                AlbumSelectActivity.activity().setPhotoLimit(21).start(MainActivity.this);

            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMessageDialog messageDialog = DialogManager.makeMessageDialog(MainActivity.this, "New Message", true);//new NewMessageDialog(MainActivity.this);
                messageDialog.setTitle("New Message");
                messageDialog.getInputTitle().setText("TO: Minitour");
                messageDialog.getInputTitle().setEnabled(false);
                messageDialog.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                        R.drawable.demo_pic));
                messageDialog.setPostiveButtonOnClickListener(new BaseAlertDialog.OnClickListener() {
                    @Override
                    public void onClick(View v, BaseAlertDialog dialog) {
                        dialog.getDialog().dismiss();
                        NewMessageDialog d = (NewMessageDialog) dialog;
                        String message = d.getInputMessage().getText().toString();
                        Toast.makeText(MainActivity.this, "["+message+"] Message Sent!", Toast.LENGTH_SHORT).show();
                    }
                });
                messageDialog.setNegativeButtonOnClickListener(new BaseAlertDialog.OnClickListener() {
                    @Override
                    public void onClick(View v, BaseAlertDialog dialog) {

                        dialog.dismiss();
                    }
                });
                messageDialog.show();
            }
        });
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoadingDialog dialog = new LoadingDialog(MainActivity.this,"Loading...");
                dialog.show();



                dialog.setPostComplete(new LoadingDialog.PostCompleted() {
                    @Override
                    public void onComplete(LoadingDialog dialog) {
                        //To something when finished.
                    }
                });


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.complete(false,false,"new title","new message");
                    }
                },5000);



            }
        });

        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 camDialog = new CameraDialog(MainActivity.this);
                camDialog.show();
                camDialog.setPostImageTaken(new CameraDialog.PostPictureTaken() {
                    @Override
                    public void onConfirmPictureTaken(Bitmap imageTaken, CameraDialog dialog) {
                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(imageTaken);
                    }
                });

            }
        });

        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CameraActivity.activity()
                        .setCropOptional(true)
                        .saveFileToStorage(true)
                        .start(MainActivity.this);
            }
        });

        findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                dialog.setDialogDescription("Select an action to close the camDialog.");
                //camDialog.setShowActionIcons(false);
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
                //DialogManager.setDialogPosition(camDialog,camDialog.getDialog(), Gravity.BOTTOM);
                dialog.getTitle().setGravity(Gravity.CENTER);
                dialog.getDescription().setGravity(Gravity.CENTER);
                dialog.setDialogTitle("Select Action");
                dialog.setDialogDescription("Select an action to close the camDialog.");

                //camDialog.setShowActionIcons(false);
                dialog.show();


            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        camDialog.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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


