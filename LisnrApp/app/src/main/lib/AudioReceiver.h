#ifndef LISNR_AUDIORECEIVER_H
#define LISNR_AUDIORECEIVER_H

extern "C++" {
#include <mutex>
#include <vector>
#include "CircularBuffer.h"
}
#include <android/log.h>
#include "AudioFileWriter.h"

#include <oboe/Oboe.h>
#include <thread>

#define LOG_TAG_AUDIO_RECEIVER "AudioReceiver"

namespace radiussample {
    class AudioReceiver : public oboe::AudioStreamCallback {
      public:
        AudioReceiver(void *inputParams);

        ~AudioReceiver();

        oboe::Result start();

        oboe::Result stop();

        oboe::Result pause();

        bool isReceiving();

        // oboe::AudioStreamCallback interface
        oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream,
                                              void *audioData, int32_t numFrames);

        void onErrorBeforeClose(oboe::AudioStream *oboeStream, oboe::Result error);

        void onErrorAfterClose(oboe::AudioStream *oboeStream, oboe::Result error);

        std::string getFilePath();

      private:
        oboe::Result setupInput();
        void inputRunLoop();

        oboe::AudioStreamBuilder m_streamBuilder;
        oboe::AudioStream *m_stream;
        uint32_t m_framesPerBuffer;
        int m_sampleRate;
        uint32_t m_numChannels;
        bool m_receiving = false;
        bool m_terminate = false;
        std::vector<std::vector<float>> m_conversionBuffer;
        std::vector<std::vector<int16_t>> m_tmpInput;
        std::thread m_inputThread;
        std::mutex m_bufferMutex;
        std::vector<radiussample::CircularBuffer<int16_t>*> m_inputBuffer;

        std::shared_ptr<radiussample::AudioFileWriter> m_audioFileWriter;
        bool m_inputHasBeenSetup;
        void *m_streamParameters;
    };
}

#endif //LISNR_AUDIORECEIVER_H
