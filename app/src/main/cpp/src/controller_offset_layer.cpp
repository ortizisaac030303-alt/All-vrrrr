#include "controller_offset_layer.h"
#include <android/log.h>
#include <cmath>
#include <mutex>

#define LOG_TAG "VROffsetLayer"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Global offset configuration
OffsetConfig g_offset_config = {
    {0.0f, 0.0f, 0.0f},
    {0.0f, 0.0f, 0.0f},
    0
};

static std::mutex g_config_mutex;

extern "C" {

int32_t layer_init() {
    LOGI("VR Offset Layer initialized");
    return 0; // success
}

int32_t layer_deinit() {
    LOGI("VR Offset Layer deinitialized");
    return 0; // success
}

void apply_controller_offset(float* pose_x, float* pose_y, float* pose_z, const ControllerOffset* offset) {
    if (!pose_x || !pose_y || !pose_z || !offset || !g_offset_config.enabled) {
        return;
    }

    std::lock_guard<std::mutex> lock(g_config_mutex);

    // Apply linear offset to position
    *pose_x += offset->x;
    *pose_y += offset->y;
    *pose_z += offset->z;

    LOGI("Applied offset: X=%.2f, Y=%.2f, Z=%.2f to pose at (%.2f, %.2f, %.2f)",
         offset->x, offset->y, offset->z, *pose_x, *pose_y, *pose_z);
}

void update_controller_offsets(float left_x, float left_y, float left_z,
                               float right_x, float right_y, float right_z,
                               uint32_t enabled) {
    std::lock_guard<std::mutex> lock(g_config_mutex);
    g_offset_config.left_offset = {left_x, left_y, left_z};
    g_offset_config.right_offset = {right_x, right_y, right_z};
    g_offset_config.enabled = enabled;
    LOGI("Updated offsets: L(%.2f,%.2f,%.2f) R(%.2f,%.2f,%.2f) Enabled=%u",
         left_x, left_y, left_z, right_x, right_y, right_z, enabled);
}

OffsetConfig get_offset_config() {
    std::lock_guard<std::mutex> lock(g_config_mutex);
    return g_offset_config;
}

} // extern "C"
