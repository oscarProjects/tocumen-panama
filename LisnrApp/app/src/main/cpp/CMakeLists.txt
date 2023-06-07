cmake_minimum_required(VERSION 3.6)

if (ANDROID_ABI STREQUAL armeabi-v7a)
    message(android abi armeabi-v7a)
    set(OBOE_DIR ${CMAKE_SOURCE_DIR}/external/oboe/armeabi-v7a)
    set(ANDROID_PLATFORM android-19)
    set(ANDROID_API_LEVEL 19)
elseif(ANDROID_ABI STREQUAL arm64-v8a)
    message(android abi arm64-v8a)
    set(OBOE_DIR ${CMAKE_SOURCE_DIR}/external/oboe/arm64-v8a)
    set(ANDROID_PLATFORM android-21)
    set(ANDROID_API_LEVEL 21)
elseif(ANDROID_ABI STREQUAL x86_64)
    message(android abi x86_64)
    set(OBOE_DIR ${CMAKE_SOURCE_DIR}/external/oboe/x86_64)
    set(ANDROID_API_LEVEL 21)
    set(ANDROID_PLATFORM android-21)
elseif(ANDROID_ABI STREQUAL x86)
    message(android abi x86)
    set(OBOE_DIR ${CMAKE_SOURCE_DIR}/external/oboe/x86)
    set(ANDROID_PLATFORM android-19)
    set(ANDROID_API_LEVEL 19)
else()
    message(FATAL_ERROR 'Android ABI ${ANDROID_ABI} unsupported')
endif()

set(SDK_INCLUDE_DIRS ${OBOE_DIR}/include/)
set(SDK_INCLUDE_LIB_DIR ${OBOE_DIR}/lib)

include_directories(${SDK_INCLUDE_DIRS} lib/)
link_directories(${SDK_INCLUDE_LIB_DIR})

add_library( # Specifies the name of the library.
        audiosystem-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        lib/jni_interface.cpp
        lib/AndroidAudioSystem.cpp
        lib/AudioFileWriter.cpp
        lib/AudioReceiver.cpp
        )

#find_package(oboe REQUIRED CONFIG)
target_link_libraries(audiosystem-lib
        OpenSLES
        oboe
        log)

set_target_properties(audiosystem-lib PROPERTIES COMPILE_FLAGS "-Os")
set_target_properties(audiosystem-lib PROPERTIES BUILD_WITH_INSTALL_RPATH ON INSTALL_NAME_DIR @loader_path)
install(TARGETS audiosystem-lib LIBRARY DESTINATION ${ANDROID_ABI}
        ARCHIVE DESTINATION ${ANDROID_ABI}
        RUNTIME DESTINATION ${ANDROID_ABI})
