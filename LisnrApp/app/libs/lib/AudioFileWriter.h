#ifndef LISNR_SDK_ANDROID_AUDIOFILEWRITER_H
#define LISNR_SDK_ANDROID_AUDIOFILEWRITER_H

#include <string>
#include <ctime>
#include <vector>
#include <stdlib.h>
#include <stdio.h>
#include <android/log.h>
#define LOG_TAG_AUDIO_WRITER "SampleAudioFileWriter"

namespace radiussample {
    /*
     * This class is designed for internal use only, not to be distributed
     */
    class AudioFileWriter {
      public:
        AudioFileWriter(int numChannels = 1);

        ~AudioFileWriter() {};

        void generateFilePath();

        void open(int sampleRate);

        void put(std::vector<std::vector<float>> &samplesByChannel, size_t floatSamplesPerChannel);

        void close();

        void writeWavHeader(int sampleRate);

        std::string getFilePath();

      private:
        int m_numChannels;
        size_t m_audioBytesWritten;
        std::string m_filePath;
        FILE *m_file;
    };
}

#endif //LISNR_SDK_ANDROID_AUDIOFILEWRITER_H
