package com.example.vrcontrolleroffset;

public class NativeVRLayer {
    static {
        try {
            System.loadLibrary("vroffsetlayer");
        } catch (UnsatisfiedLinkError e) {
            android.util.Log.e("NativeVRLayer", "Failed to load vroffsetlayer: " + e.getMessage());
        }
    }

    public native static int initLayer();
    public native static int deinitLayer();
    public native static void updateOffsets(float left_x, float left_y, float left_z,
                                            float right_x, float right_y, float right_z,
                                            boolean enabled);
    public native static float[] getOffsets();

    public static void setSynchronizedOffsets(float x, float y, float z, boolean fixed) {
        try {
            updateOffsets(x, y, z, x, y, z, fixed);
            LogHelper.append(String.format("Native layer updated: X=%.2f Y=%.2f Z=%.2f Fixed=%s", x, y, z, fixed ? "On" : "Off"));
        } catch (UnsatisfiedLinkError e) {
            LogHelper.append("Native layer not available: " + e.getMessage());
        }
    }

    public static float[] getAndLogCurrentOffsets() {
        try {
            float[] offsets = getOffsets();
            if (offsets != null && offsets.length == 7) {
                LogHelper.append(String.format("Native layer offsets: L(%.2f,%.2f,%.2f) R(%.2f,%.2f,%.2f) Enabled=%s",
                        offsets[0], offsets[1], offsets[2],
                        offsets[3], offsets[4], offsets[5],
                        offsets[6] > 0 ? "On" : "Off"));
                return offsets;
            }
        } catch (UnsatisfiedLinkError e) {
            LogHelper.append("Failed to read native offsets: " + e.getMessage());
        }
        return null;
    }
}
