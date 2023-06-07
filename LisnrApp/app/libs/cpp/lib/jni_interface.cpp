#include "jni_interface.h"
#include <tuple>

#include "AndroidAudioSystem.h"

std::shared_ptr<radiussample::AndroidAudioSystem> audioSystem;

JavaVM *g_vm;
jobject g_audioManager = nullptr;

// Cached jclass references as per https://developer.android.com/training/articles/perf-jni.html#faq_FindClass
jclass jclass_com_lisnr_radiussample_AudioSystem;

JNIEnv *attachToJvm(JavaVM *vm, bool *attached_ptr) {
    JNIEnv *env = nullptr;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) == JNI_EDETACHED) {
        vm->AttachCurrentThread(&env, NULL);
        *attached_ptr = true;
    } else {
        *attached_ptr = false;
    }

    return env;
}

void detachFromJvm(JavaVM *vm, bool attached) {
    if (attached) {
        vm->DetachCurrentThread();
    }
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;

    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    // Get jclass with env->FindClass
    jclass_com_lisnr_radiussample_AudioSystem = (jclass) env->NewGlobalRef(env->FindClass("com/lisnr/radiussampleapp/AudioSystem")); //common

    return JNI_VERSION_1_6;
}

// end jni required functions

// NOTE the responsibility for freeing this pointer is being passed along to the audio system
StreamParameters *parseJavaStreamBuilder(JNIEnv *env, jobject builderObj) {
    env->GetJavaVM(&g_vm);
    jclass builderObjClass = env->GetObjectClass(builderObj);
    jfieldID direction = env->GetFieldID(builderObjClass, "mDirection", "I");
    jfieldID audioApi = env->GetFieldID(builderObjClass, "mAudioApi", "I");
    jfieldID performanceMode = env->GetFieldID(builderObjClass, "mPerformanceMode", "I");
    jfieldID usage = env->GetFieldID(builderObjClass, "mUsage", "I");
    jfieldID contentType = env->GetFieldID(builderObjClass, "mContentType", "I");
    jfieldID inputPreset = env->GetFieldID(builderObjClass, "mInputPreset", "I");
    jfieldID sharingMode = env->GetFieldID(builderObjClass, "mSharingMode", "I");
    jfieldID channelCount = env->GetFieldID(builderObjClass, "mChannelCount", "I");
    jfieldID deviceId = env->GetFieldID(builderObjClass, "mDeviceId", "I");
    jfieldID samplingRate = env->GetFieldID(builderObjClass, "mSamplingRate", "I");
    jfieldID framesPerBuffer = env->GetFieldID(builderObjClass, "mFramesPerBuffer", "I");

    StreamParameters *params = (StreamParameters *)malloc(sizeof(StreamParameters));
    memset(params, 0, sizeof(StreamParameters));
    params->direction = env->GetIntField(builderObj, direction);
    params->api = env->GetIntField(builderObj, audioApi);
    // PerformanceMode enum in Definitions.h starts at 10
    params->performanceMode = env->GetIntField(builderObj, performanceMode) + 10;

    // Fixing up usage mapping because their enum is wack
    switch (params->usage) {
    case 1: // Media
    case 2: // VoiceCommunication
    case 3: // VoiceCommunicationSignalling
    case 4: // Alarm
    case 5: // Notification
    case 6: // NotificationRingtone
        params->usage = params->usage + 1;
        break;

    case 7: // NotificationEvent
    case 8: // AssistanceAccessibility
    case 9: // AssistanceNavigationGuidance
    case 10: // AssistanceSonification
    case 11: // Game
        params->usage = params->usage + 3;
        break;

    case 12: // Assistant
        params->usage = 16;
        break;

    default:
        params->usage = 1; // Alarm
    }

    params->contentType = env->GetIntField(builderObj, contentType) + 1;
    params->inputPreset = env->GetIntField(builderObj, inputPreset);

    // Fixing up input preset mapping because their enum is wack
    switch (params->inputPreset) {
    // Generic
    case 0:
        params->inputPreset = 1;
        break;

    // Camcorder
    case 1:
        params->inputPreset = 5;
        break;

    // VoiceRecognition
    case 2:
        params->inputPreset = 6;
        break;

    // VoiceCommunication
    case 3:
        params->inputPreset = 7;
        break;

    // Unprocessed
    case 4:
        params->inputPreset = 9;
        break;

    // VoicePerformance
    case 5:
        params->inputPreset = 10;
        break;

    default:
        params->inputPreset = 9; // Defaulting to unprocessed for safety
    }

    params->sharingMode = env->GetIntField(builderObj, sharingMode);
    params->deviceId = env->GetIntField(builderObj, deviceId);
    params->channelCount = env->GetIntField(builderObj, channelCount);
    params->samplingRate = env->GetIntField(builderObj, samplingRate);
    params->framesPerBuffer = env->GetIntField(builderObj, framesPerBuffer);

    return params;
}

// Audio system interfaces
jint audioSystem_setMode(JNIEnv *env, jint mode, jobject inputStreamParameters) {
    if (audioSystem == nullptr) {
        StreamParameters *inputParams = parseJavaStreamBuilder(env, inputStreamParameters);
        audioSystem = std::make_shared<radiussample::AndroidAudioSystem>(inputParams);

        if (audioSystem == nullptr) {
            return -1;
        }
    }

    return audioSystem->setMode((radiussample::AndroidAudioSystem::Mode)mode);
}

void notifyAudioSystemError(std::string error) {
    bool attached;
    JNIEnv *env = attachToJvm(g_vm, &attached);
    jmethodID audioErrorMethod = env->GetMethodID(jclass_com_lisnr_radiussample_AudioSystem, "audioError", "(Ljava/lang/String;)V");

    if (audioErrorMethod != 0 && g_audioManager != nullptr) {
        jstring err = env->NewStringUTF(error.c_str());
        env->CallVoidMethod(g_audioManager, audioErrorMethod, err);
    }

    detachFromJvm(g_vm, attached);
}

JNIEXPORT jint JNICALL Java_com_lisnr_radiussampleapp_AudioSystem_nativeSetMode(JNIEnv *env, jobject thiz, jint mode,
        jobject inputStreamParametersObj) {
    return audioSystem_setMode(env, mode, inputStreamParametersObj);
}

JNIEXPORT jstring JNICALL Java_com_lisnr_radiussampleapp_AudioSystem_nativeGetFilepath(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(audioSystem->getFilePath().data());
}