#include <sys/stat.h>
#include <errno.h>
#include "AudioFileWriter.h"

std::string getApplicationId() {
    std::string applicationId;
    FILE *cmdline = fopen("/proc/self/cmdline", "r");

    if (cmdline) {
        char applicationIdBytes[64] = { 0 };
        fread(applicationIdBytes, sizeof(applicationIdBytes), 1, cmdline);
        __android_log_print(ANDROID_LOG_DEBUG, "JNI", "application id: %s", applicationIdBytes);
        applicationId = applicationIdBytes;
    }

    return applicationId;
}

radiussample::AudioFileWriter::AudioFileWriter(int numChannels) {
    m_file = nullptr;
    m_audioBytesWritten = 0;
    m_filePath = "";
    m_numChannels = numChannels;
}

void radiussample::AudioFileWriter::generateFilePath() {
    time_t now = time(nullptr);
    tm *tmLocaltime = localtime(&now);
    std::string appId = getApplicationId();
    m_filePath = "/data/user/0/" + appId + "/files/";
    struct stat sb;

    if (stat(m_filePath.c_str(), &sb) != 0) {
        mkdir(m_filePath.c_str(), 0755);
    }

    m_filePath = m_filePath + "radius_sample_recording_" +
                 std::to_string(1900 + tmLocaltime->tm_year) + "-" +
                 std::to_string(1 + tmLocaltime->tm_mon) + "-" +
                 std::to_string(tmLocaltime->tm_mday) + "T" +
                 std::to_string(tmLocaltime->tm_hour) + ":" +
                 std::to_string(tmLocaltime->tm_min) + ":" +
                 std::to_string(tmLocaltime->tm_sec) + ".wav";
}

void radiussample::AudioFileWriter::open(int sampleRate) {
    m_file = fopen(m_filePath.c_str(), "wb");
    __android_log_print(ANDROID_LOG_ERROR, LOG_TAG_AUDIO_WRITER, "Opening recording file at %s", m_filePath.c_str());

    if (m_file != nullptr) {
        writeWavHeader(sampleRate);
    } else {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG_AUDIO_WRITER, "Failed to open recording file - errno: %d: %s", errno, strerror(errno));
    }
}

void radiussample::AudioFileWriter::close() {
    __android_log_print(ANDROID_LOG_ERROR, LOG_TAG_AUDIO_WRITER, "Closing recording file at %s", m_filePath.c_str());

    if (m_file != nullptr) {
        fclose(m_file);
        m_file = fopen(m_filePath.c_str(), "rb+");

        if (m_file != nullptr) {

            // Write ChunkSize and Subchunk2Size
            // See https://web.archive.org/web/20140327141505/https://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            size_t chunkSize = 36 + m_audioBytesWritten;

            int err = fseek(m_file, 4, SEEK_SET);
            fputc((uint8_t) ((chunkSize >>  0) & 0xFF), m_file);
            fputc((uint8_t) ((chunkSize >>  8) & 0xFF), m_file);
            fputc((uint8_t) ((chunkSize >> 16) & 0xFF), m_file);
            fputc((uint8_t) ((chunkSize >> 24) & 0xFF), m_file);

            err = fseek(m_file, 40, SEEK_SET);
            fputc((uint8_t) ((m_audioBytesWritten >>  0) & 0xFF), m_file);
            fputc((uint8_t) ((m_audioBytesWritten >>  8) & 0xFF), m_file);
            fputc((uint8_t) ((m_audioBytesWritten >> 16) & 0xFF), m_file);
            fputc((uint8_t) ((m_audioBytesWritten >> 24) & 0xFF), m_file);
            fclose(m_file);
        } else {
            __android_log_print(ANDROID_LOG_ERROR, LOG_TAG_AUDIO_WRITER, "Failed to update length of audio file at %s", m_filePath.c_str());
        }

        m_file = nullptr;
    }
}

void radiussample::AudioFileWriter::put(std::vector<std::vector<float>> &samplesByChannel, size_t samplesPerChannel) {
    if (m_file != nullptr) {
        for (size_t channel = 0; channel < m_numChannels; ++channel) {
            for (size_t sample = 0; sample < samplesPerChannel; ++sample) {
                fwrite(samplesByChannel[channel].data() + sample, sizeof(float), 1, m_file);
            }
        }

        m_audioBytesWritten += ((samplesPerChannel * m_numChannels) * sizeof(float));
    }
}

void radiussample::AudioFileWriter::writeWavHeader(int _sampleRate) {
    uint32_t sampleRate = _sampleRate;
    uint16_t bitsPerSample = 32;
    uint16_t numChannels = m_numChannels;
    uint32_t byteRate = sampleRate * numChannels * bitsPerSample / 8;
    uint16_t blockAlign = numChannels * bitsPerSample / 8;

    const int headerLength = 44;
    uint8_t wavHeaderBytes[headerLength] = {
        // ChunkID
        *"R", *"I", *"F", *"F",
        // ChunkSize
        0x00, 0x00, 0x00, 0x00,
        // Format
        *"W", *"A", *"V", *"E",
        // Subchunk1ID
        *"f", *"m", *"t", *" ",
        // Subchunk1Size (16 for current AudioFormat)
        0x10, 0x00, 0x00, 0x00,
        // AudioFormat (3 for Float PCM)
        0x03, 0x00,
        // NumChannels
        (uint8_t) ((numChannels >> 0) & 0xFF), (uint8_t) ((numChannels >> 8) & 0xFF),
        // SampleRate
        (uint8_t) ((sampleRate >> 0) & 0xFF), (uint8_t) ((sampleRate >> 8) & 0xFF), (uint8_t) ((sampleRate >> 16) & 0xFF), (uint8_t) ((sampleRate >> 24) & 0xFF),
        // ByteRate // todo set
        (uint8_t) ((byteRate >> 0) & 0xFF), (uint8_t) ((byteRate >> 8) & 0xFF), (uint8_t) ((byteRate >> 16) & 0xFF), (uint8_t) ((byteRate >> 24) & 0xFF),
        // BlockAlign // todo set
        (uint8_t) ((blockAlign >> 0) & 0xFF), (uint8_t) ((blockAlign >> 8) & 0xFF),
        // BitsPerSample // todo set
        (uint8_t) ((bitsPerSample >> 0) & 0xFF), (uint8_t) ((bitsPerSample >> 8) & 0xFF),
        // Subchunk2ID
        *"d", *"a", *"t", *"a",
        // Subchunk2Size
        0x00, 0x00, 0x00, 0x00,
    };
    fwrite(wavHeaderBytes, sizeof(uint8_t), headerLength, m_file);
}

std::string radiussample::AudioFileWriter::getFilePath() {
    return "/data/data/" + m_filePath.substr(13, m_filePath.length());
}