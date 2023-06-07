#ifndef JNI_INTERFACE_H
#define JNI_INTERFACE_H

#include <jni.h>
#include <memory>

#include <android/log.h>

extern "C" {
    JNIEXPORT jint JNICALL Java_com_lisnr_radiussampleapp_AudioSystem_nativeSetMode(JNIEnv *env, jobject thiz, jint mode,
            jobject inputStreamParametersObj);

    JNIEXPORT jstring JNICALL Java_com_lisnr_radiussampleapp_AudioSystem_nativeGetFilepath(JNIEnv *env, jobject thiz);
}

JNIEnv *attachToJvm(JavaVM *vm, bool *attachedPtr);

void detachFromJvm(JavaVM *vm, bool attached);

void notifyAudioSystemError(std::string error);

#endif // JNI_INTERFACE_H
