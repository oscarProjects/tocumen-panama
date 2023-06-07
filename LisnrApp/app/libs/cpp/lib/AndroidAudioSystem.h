#ifndef LISNR_ANDROIDAUDIOSYSTEM_H
#define LISNR_ANDROIDAUDIOSYSTEM_H

#include <stdlib.h>
#include <assert.h>
#include <jni.h>
#include <string.h>
#include <pthread.h>

extern "C" {
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
};

#include "AudioReceiver.h"

#include <android/log.h>

typedef struct _StreamParameters {
    int direction;
    int api;
    int performanceMode;
    int usage;
    int contentType;
    int inputPreset;
    int sharingMode;
    int deviceId;
    int channelCount;
    int samplingRate;
    int framesPerBuffer;
} StreamParameters;


namespace radiussample {
    class AndroidAudioSystem {
      public:
        enum Mode {
            NONE, INPUT
        };

        AndroidAudioSystem(StreamParameters *inputParams);

        virtual ~AndroidAudioSystem();

        int startReceiver();

        int stopReceiver();

        int setMode(Mode mode);

        std::string getFilePath();

      private:
        std::shared_ptr<radiussample::AudioReceiver> mReceiver = nullptr;
    };
}

#endif //LISNR_ANDROIDAUDIOSYSTEM_H
