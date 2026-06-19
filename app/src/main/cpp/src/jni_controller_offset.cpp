#include <jni.h>
#include "controller_offset_layer.h"
#include <android/log.h>

#define LOG_TAG "VROffsetJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" {

JNIEXPORT jint JNICALL
Java_com_example_vrcontrolleroffset_NativeVRLayer_initLayer(JNIEnv* env, jclass clazz) {
    LOGI("JNI: Initializing VR offset layer");
    return layer_init();
}

JNIEXPORT jint JNICALL
Java_com_example_vrcontrolleroffset_NativeVRLayer_deinitLayer(JNIEnv* env, jclass clazz) {
    LOGI("JNI: Deinitializing VR offset layer");
    return layer_deinit();
}

JNIEXPORT void JNICALL
Java_com_example_vrcontrolleroffset_NativeVRLayer_updateOffsets(
        JNIEnv* env, jclass clazz,
        jfloat left_x, jfloat left_y, jfloat left_z,
        jfloat right_x, jfloat right_y, jfloat right_z,
        jboolean enabled) {
    LOGI("JNI: Update offsets L(%.2f,%.2f,%.2f) R(%.2f,%.2f,%.2f) enabled=%d",
         left_x, left_y, left_z, right_x, right_y, right_z, enabled);
    update_controller_offsets(left_x, left_y, left_z,
                              right_x, right_y, right_z,
                              enabled ? 1u : 0u);
}

JNIEXPORT jfloatArray JNICALL
Java_com_example_vrcontrolleroffset_NativeVRLayer_getOffsets(JNIEnv* env, jclass clazz) {
    OffsetConfig config = get_offset_config();
    jfloatArray result = env->NewFloatArray(7);
    jfloat values[7] = {
        config.left_offset.x,
        config.left_offset.y,
        config.left_offset.z,
        config.right_offset.x,
        config.right_offset.y,
        config.right_offset.z,
        config.enabled ? 1.0f : 0.0f
    };
    env->SetFloatArrayRegion(result, 0, 7, values);
    return result;
}

} // extern "C"
