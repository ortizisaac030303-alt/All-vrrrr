#ifndef VR_CONTROLLER_OFFSET_LAYER_H
#define VR_CONTROLLER_OFFSET_LAYER_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    float x;
    float y;
    float z;
} ControllerOffset;

typedef struct {
    ControllerOffset left_offset;
    ControllerOffset right_offset;
    uint32_t enabled;
} OffsetConfig;

// Shared offset state
extern OffsetConfig g_offset_config;

// Layer initialization
int32_t layer_init();
int32_t layer_deinit();

// Offset modification
void apply_controller_offset(float* pose_x, float* pose_y, float* pose_z, const ControllerOffset* offset);

// Offset management
void update_controller_offsets(float left_x, float left_y, float left_z,
                              float right_x, float right_y, float right_z,
                              uint32_t enabled);
OffsetConfig get_offset_config();

#ifdef __cplusplus
}
#endif

#endif // VR_CONTROLLER_OFFSET_LAYER_H
