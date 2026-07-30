package com.example.vrcontrolleroffset;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class ControllerOffsetStateTest {
    @Test
    public void appliesStandaloneOffsetsToControllerPose() {
        ControllerOffsetState state = new ControllerOffsetState();
        state.setOffsets(0.25f, -0.15f, 0.4f);
        state.setStandaloneMode(true);

        float[] pose = state.applyPoseOffset(1.0f, 2.0f, 3.0f, true);

        assertArrayEquals(new float[]{1.25f, 1.85f, 3.4f}, pose, 0.0001f);
    }

    @Test
    public void leavesPoseUnchangedWhenStandaloneModeIsDisabled() {
        ControllerOffsetState state = new ControllerOffsetState();
        state.setOffsets(0.25f, -0.15f, 0.4f);
        state.setStandaloneMode(false);

        float[] pose = state.applyPoseOffset(1.0f, 2.0f, 3.0f, false);

        assertArrayEquals(new float[]{1.0f, 2.0f, 3.0f}, pose, 0.0001f);
    }
}
