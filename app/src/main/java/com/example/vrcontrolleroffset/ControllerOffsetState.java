package com.example.vrcontrolleroffset;

public class ControllerOffsetState {
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private boolean standaloneMode;

    public void setOffsets(float x, float y, float z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
    }

    public void setStandaloneMode(boolean enabled) {
        this.standaloneMode = enabled;
    }

    public boolean isStandaloneMode() {
        return standaloneMode;
    }

    public float[] applyPoseOffset(float x, float y, float z, boolean isControllerPose) {
        if (!standaloneMode || !isControllerPose) {
            return new float[]{x, y, z};
        }
        return new float[]{x + offsetX, y + offsetY, z + offsetZ};
    }
}
