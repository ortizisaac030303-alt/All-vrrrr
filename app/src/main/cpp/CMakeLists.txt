cmake_minimum_required(VERSION 3.22.1)

project(vrcontrolleroffset VERSION 1.0 LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add VR offset layer library (simplified, no external dependencies)
add_library(vroffsetlayer SHARED
    src/controller_offset_layer.cpp
    src/jni_controller_offset.cpp
)

target_include_directories(vroffsetlayer PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${ANDROID_NDK}/sources/android/native_app_glue
)

target_link_libraries(vroffsetlayer
    log
    android
)

# Enable optimization
if(CMAKE_BUILD_TYPE MATCHES Release)
    target_compile_options(vroffsetlayer PRIVATE -O3 -flto)
    target_link_options(vroffsetlayer PRIVATE -flto)
else()
    target_compile_options(vroffsetlayer PRIVATE -O2)
endif()
