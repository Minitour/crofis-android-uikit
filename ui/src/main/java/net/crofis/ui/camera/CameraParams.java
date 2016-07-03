package net.crofis.ui.camera;

/**
 * Created by Tony Zaitoun on 4/14/2016.
 */
public final class CameraParams{
    /**The camera's angle**/
    private CAMERA_ANGLE angle;

    /**The resolution of the picture when taken**/
    private PICTURE_SIZE picture_size;

    /**The resolution of the SurfaceView's preview**/
    private PREVIEW_SIZE preview_size;


    /**
     * Enum CAMERA_ANGLE:
     * Is used to set the camera's orientation.
     */
    public enum CAMERA_ANGLE{
        A0,//landscape mode
        A90,//portrait mode
        A180,//upside down landscape
        A270,//upside down portrait
    }

    /**
     * Enum PICTURE_SIZE:
     * Is used to set the picture's resolution.
     */
    public enum PICTURE_SIZE{
        s320x240,
        s640x480,
        s1024x768,
        s1280x720,
        s1280x768,
        s1280x920,
        s1600x1200,
        s2048x1536,
        s2560x1440,
        s2560x1536,
        s2560x1920,
    }

    /**
     * Enum PREVIEW_SIZE:
     * Is used to set the SurfaceView's preview.
     */
    public enum PREVIEW_SIZE{
        s176x144,
        s320x240,
        s352x288,
        s480x320,
        s480x368,
        s640x480,
        s800x480,
        s800x600,
        s864x480,
        s864x576,
        s960x540,
        s1280x720,
        s1280x768,
        s1280x960,
    }


    /**
     * Default Constructor.
     * @param angle The camera's angle.
     * @param picture_size The resolution of the picture when taken.
     * @param preview_size The resolution of the SurfaceView's preview.
     */
    public CameraParams(CAMERA_ANGLE angle,PICTURE_SIZE picture_size,PREVIEW_SIZE preview_size){
        this.angle = angle;
        this.picture_size = picture_size;
        this.preview_size = preview_size;
    }

    /**
     * @return The camera's angle.
     */
    public CAMERA_ANGLE getAngle() {
        return angle;
    }

    /**
     * @return The resolution of the picture when taken.
     */
    public PICTURE_SIZE getPicture_size() {
        return picture_size;
    }

    /**
     * @return The resolution of the SurfaceView's preview.
     */
    public PREVIEW_SIZE getPreview_size() {
        return preview_size;
    }
}
