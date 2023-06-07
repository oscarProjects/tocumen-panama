#include "AudioReceiver.h"
#include "jni_interface.h"
#include <oboe/Definitions.h>
#include "AndroidAudioSystem.h"
#include <thread>

#define BUFFER_SIZE 4800
#define CIRCULAR_BUFFER_SECONDS 3

radiussample::AudioReceiver::AudioReceiver(void *inputParams) {
    m_streamParameters = inputParams;
    m_numChannels = ((StreamParameters *)inputParams)->channelCount;
    m_framesPerBuffer = ((StreamParameters *)inputParams)->framesPerBuffer;
    m_sampleRate = ((StreamParameters *)inputParams)->samplingRate;
    m_stream = nullptr;
    m_inputHasBeenSetup = false;

    m_inputBuffer = std::vector<radiussample::CircularBuffer<int16_t>*>(m_numChannels);
    m_tmpInput.resize(m_numChannels);
    m_conversionBuffer.resize(m_numChannels);

    for (int channel = 0; channel <  m_numChannels; channel++) {
        m_inputBuffer[channel] = new radiussample::CircularBuffer<int16_t>(m_sampleRate * CIRCULAR_BUFFER_SECONDS);
        m_tmpInput[channel].resize(BUFFER_SIZE, 0);
        m_conversionBuffer[channel].resize(BUFFER_SIZE, 0);
    }

    m_audioFileWriter = std::make_shared<radiussample::AudioFileWriter>(m_numChannels);
}

radiussample::AudioReceiver::~AudioReceiver() {
    stop();

    if (m_stream != nullptr) {
        oboe::Result result = m_stream->close();

        if (result != oboe::Result::OK) {
            std::stringstream error;
            error << "Error closing output stream: " << oboe::convertToText(result);
            __android_log_print(ANDROID_LOG_ERROR, LOG_TAG_AUDIO_RECEIVER, "%s", error.str().c_str());
            notifyAudioSystemError(error.str());
        }

        delete m_stream;
        m_stream = nullptr;
    }

    m_tmpInput.clear();
    m_conversionBuffer.clear();

    for (int i = 0; i < m_inputBuffer.size(); i++) {
        delete m_inputBuffer[i];
    }

    m_inputBuffer.clear();
    m_audioFileWriter = nullptr;
    free(m_streamParameters);
}

oboe::Result radiussample::AudioReceiver::setupInput() {
    oboe::Result result = oboe::Result::OK;

    if (m_stream == nullptr) {
        m_streamBuilder.setCallback(this);
        m_streamBuilder.setFormat(oboe::AudioFormat::I16);
        m_streamBuilder.setFramesPerCallback(m_framesPerBuffer);

        StreamParameters *tParams = (StreamParameters *)m_streamParameters;
        m_streamBuilder.setDirection((oboe::Direction)tParams->direction);
        m_streamBuilder.setAudioApi((oboe::AudioApi)tParams->api);
        m_streamBuilder.setPerformanceMode((oboe::PerformanceMode)tParams->performanceMode);
        m_streamBuilder.setUsage((oboe::Usage)tParams->usage);
        m_streamBuilder.setContentType((oboe::ContentType)tParams->contentType);
        m_streamBuilder.setInputPreset((oboe::InputPreset)tParams->inputPreset);
        m_streamBuilder.setSharingMode((oboe::SharingMode)tParams->sharingMode);
        m_streamBuilder.setDeviceId(tParams->deviceId);
        m_streamBuilder.setChannelCount(tParams->channelCount);
        m_streamBuilder.setSampleRate(tParams->samplingRate);
        m_terminate = false;
        result = m_streamBuilder.openStream(&m_stream);

        if (result != oboe::Result::OK || !m_stream) {
            m_terminate = true;
            std::stringstream error;
            error << "Error opening stream: " << oboe::convertToText(result);
            __android_log_print(ANDROID_LOG_ERROR, LOG_TAG_AUDIO_RECEIVER, "%s", error.str().c_str());
            notifyAudioSystemError(error.str());
            return result;
        }

        if (m_stream->getPerformanceMode() != oboe::PerformanceMode::LowLatency) {
            __android_log_print(ANDROID_LOG_WARN, LOG_TAG_AUDIO_RECEIVER,
                                "Stream is NOT low latency.");
        }

        if (m_stream->getChannelCount() != this->m_numChannels ||
            m_stream->getSampleRate() != this->m_sampleRate ||
            m_stream->getFormat() != oboe::AudioFormat::I16) {
            result = oboe::Result::ErrorInternal;
            std::stringstream error;
            error << (int)result << "Channel count, sample rate, or audio format is incorrect: " << oboe::convertToText(result);
            notifyAudioSystemError(error.str());
            return result;
        }

        m_inputHasBeenSetup = true;
    }

    return result;
}

void radiussample::AudioReceiver::onErrorBeforeClose(oboe::AudioStream *oboeStream, oboe::Result error) {
    // This is here for debugging purposes if there is an audio stream error
    std::stringstream errorString;
    errorString << oboe::convertToText(oboeStream->getDirection()) << " stream Error before close: " << oboe::convertToText(error);
    __android_log_print(ANDROID_LOG_ERROR, LOG_TAG_AUDIO_RECEIVER, "%s", errorString.str().c_str());
    notifyAudioSystemError(errorString.str());
}

void radiussample::AudioReceiver::onErrorAfterClose(oboe::AudioStream *oboeStream, oboe::Result error) {
    // This is here for debugging purposes if there is an audio stream error
    std::stringstream errorString;
    errorString << oboe::convertToText(oboeStream->getDirection()) << " stream Error after close: " << oboe::convertToText(error);
    __android_log_print(ANDROID_LOG_ERROR, LOG_TAG_AUDIO_RECEIVER, "%s", errorString.str().c_str());
    notifyAudioSystemError(errorString.str());
}

oboe::DataCallbackResult radiussample::AudioReceiver::onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) {
    if (!m_terminate && oboeStream == m_stream) {
        const short *data = (const short *) audioData;

        for (int frame = 0; frame < numFrames; frame++) {
            for (int channel = 0; channel < m_numChannels; channel++) {
                m_inputBuffer[channel]->append((const short *) data, 1);
                data++;
            }
        }

        memset(audioData, 0, m_numChannels * numFrames * sizeof(short));
        return oboe::DataCallbackResult::Continue;
    } else {
        memset(audioData, 0, m_numChannels * numFrames * sizeof(short));
        return oboe::DataCallbackResult::Stop;
    }
}

bool radiussample::AudioReceiver::isReceiving() {
    return m_receiving;
}

oboe::Result radiussample::AudioReceiver::start() {
    oboe::Result result = oboe::Result::OK;

    if (m_receiving) {
        return oboe::Result::OK;
    }

    // Setup the listener
    if (!m_inputHasBeenSetup) {
        result = setupInput();
    }

    if (result != oboe::Result::OK) {
        std::stringstream error;
        error << "setupInput error: " << oboe::convertToText(result);
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG_AUDIO_RECEIVER, "%s", error.str().c_str());
        notifyAudioSystemError(error.str());
        return result;
    }

    m_inputThread = std::thread(&AudioReceiver::inputRunLoop, this);
    m_terminate = false;
    m_receiving = true;
    result = m_stream->requestStart();

    if (result != oboe::Result::OK) {
        m_receiving = false;
        m_terminate = true;
        std::stringstream error;
        error << "Error starting stream: " << oboe::convertToText(result);
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG_AUDIO_RECEIVER, "%s", error.str().c_str());
        notifyAudioSystemError(error.str());
    } else {
        m_audioFileWriter->generateFilePath();
        m_audioFileWriter->open(m_sampleRate);
    }

    return result;
}

oboe::Result radiussample::AudioReceiver::stop() {
    oboe::Result result;
    m_terminate = true;
    m_receiving = false;

    if (m_inputThread.joinable()) {
        m_inputThread.join();
    }

    if (m_stream != nullptr && (result = m_stream->requestStop()) != oboe::Result::OK) {
        std::stringstream error;
        error << "Error stopping stream: " << oboe::convertToText(result);
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG_AUDIO_RECEIVER, "%s", error.str().c_str());
        notifyAudioSystemError(error.str());
    }

    m_audioFileWriter->close();
    return result;
}

oboe::Result radiussample::AudioReceiver::pause() {
    // pause isn't implemented for oboe/aaudio, so just call stop
    return stop();
}

void radiussample::AudioReceiver::inputRunLoop() {
    while (!m_terminate) {
        // OK to just use single buffer even if multi-channel, oboe will give us equal buffers
        size_t rxBufferSize = m_inputBuffer[0]->size();

        while (m_receiving && rxBufferSize >= BUFFER_SIZE) {
            for (int channel = 0; channel < m_numChannels; channel++) {
                m_bufferMutex.lock();
                m_inputBuffer[channel]->read(0, m_tmpInput[channel].data(), BUFFER_SIZE);
                m_inputBuffer[channel]->remove(BUFFER_SIZE);
                m_bufferMutex.unlock();
            }

            for (int channel = 0; channel < m_numChannels; channel++) {
                for (int index = 0; index < BUFFER_SIZE; index++) {
                    this->m_conversionBuffer[channel][index] = m_tmpInput[channel][index] / (float) (INT16_MAX + 1);
                }
            }

            m_audioFileWriter->put(m_conversionBuffer, BUFFER_SIZE);
            rxBufferSize -= BUFFER_SIZE;

            for (int channel = 0; channel < m_numChannels; channel++) {
                memset(m_tmpInput[channel].data(), 0, BUFFER_SIZE);
            }
        }

        std::this_thread::sleep_for(std::chrono::milliseconds(1));
    }
}

std::string radiussample::AudioReceiver::getFilePath() {
    return m_audioFileWriter->getFilePath();
}