package com.zkteco.android.IDReader;

/**
 * Created by scarx on 2016/2/5.
 */
public class WLTService {
    public static final int IMAGE_WIDTH = 102;
    public static final int IMAGE_HEIGHT = 126;
    public static final int IMAGE_LEN = 3 * 102 * 126;

    static {
        System.loadLibrary("wlt2bmp");
        System.loadLibrary("zkwltdecode");
    }

    public static native int wlt2Bmp(byte[] inbuf, byte[] outbuf);
}
