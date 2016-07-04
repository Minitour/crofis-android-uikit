# crofis-android-uikit

Camera example usages:
------

####Normal Camera:

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



I recommend using the Activity Builder class however if you wish to create your own intent you can do so using these flags:
```java
    public static final String FLAG_SET_CROP_ASPECT_RATIO = "CROP_ASPECT_RATIO";
    public static final String FLAG_SET_CROP_OPTIONAL = "SET_CROP_OPTIONAL";
    public static final String FLAG_DISPLAY_FLASH_TOGGLE = "DISPLAY_FLASH_TOGGLE";
    public static final String FLAG_DISPLAY_SWITCH_CAM = "DISPLAY_SWITCH_CAM";
    public static final String FLAG_SHOW_ZOOM_SEEKBAR = "SHOW_ZOOM_SEEKBAR";
    public static final String FLAG_ALLOW_ZOOM_GESTURE = "ALLOW_ZOOM_GESTURE";
    public static final String FLAG_ALLOW_ROTATION_ANIMATION = "ALLOW_ROTATION_ANIMATION";
    public static final String FLAG_LOAD_CAM = "LOAD_CAMERA";
    public static final String FLAG_SAVE_TO_STORAGE = "SAVE_FILE_TO_STORAGE";
    public static final String FLAG_RETURN_DATA_AS_BYTE_ARRAY = "RETURN_DATA_AS_BYTE_ARRAY";
```
Note that you cannot use custom aspect ratio for cropping without the Activity Builder. however you can pass in one of these parameters:
```java
    public static final int CROP_RATIO_NONE = -1;
    public static final int CROP_RATIO_1_1 = 0;
    public static final int CROP_RATIO_2_1 = 1;
    public static final int CROP_RATIO_4_3 = 2;
    public static final int CROP_RATIO_16_9 = 3;
```


#####Example Usage
```java
Intent intent = new Intent(this, SquareCameraActivity.class);
intent.putExtra(SquareCameraActivity.FLAG_DISPLAY_FLASH_TOGGLE,false);
intent.putExtra(SquareCameraActivity.FLAG_DISPLAY_SWITCH_CAM,false);
intent.putExtra(SquareCameraActivity.FLAG_SAVE_TO_STORAGE,true);
intent.putExtra(SquareCameraActivity.FLAG_SET_CROP_OPTIONAL, true);
intent.putExtra(SquareCameraActivity.FLAG_SET_CROP_ASPECT_RATIO, SquareCameraActivity.CROP_RATIO_1_1);
startActivityForResult(intent, SquareCameraActivity.REQUEST_CODE);
```

####Get Result from Camera
If you wish to get the path of the image you must include these:
- In Activity Builder:
```java
CameraActivity.activity()
              .saveFileToStorage(true)
              .returnDataAsByteArray(false)
              .start(this);
```
- When creating an intent object:
```java
Intent intent = new Intent(this, SquareCameraActivity.class);
intent.putExtra(SquareCameraActivity.FLAG_SAVE_TO_STORAGE,true);
intent.putExtra(SquareCameraActivity.FLAG_RETURN_DATA_AS_BYTE_ARRAY, false);
startActivityForResult(intent, SquareCameraActivity.REQUEST_CODE);
```
And then in the onActivityResult:
```java
String imageUriPath = resultIntent.getStringExtra("uri");
```
Or
```java
String imageUriPath = resultIntent.getStringExtra("data");
```


####onActivityResult
Notice that getting the image as a byte array driectly from the result intent will have an impact of the image quality, since the bitmap is reduced in size to fit in the intent object. If you wish to get the full image take a look at the code above that shows you how to fetch the photo's URI path.
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                }
                break;
        }
    }
```

