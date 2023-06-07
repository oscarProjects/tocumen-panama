#include "AndroidAudioSystem.h"
#include "jni_interface.h"

radiussample::AndroidAudioSystem::AndroidAudioSystem(StreamParameters *inputParams) {
    mReceiver = std::make_shared<radiussample::AudioReceiver>((void *)inputParams);
}

radiussample::AndroidAudioSystem::~AndroidAudioSystem() {
    mReceiver.reset();
}

int radiussample::AndroidAudioSystem::startReceiver() {
    int result = (int)mReceiver->start();

    if (result != (int)oboe::Result::OK) {
        mReceiver->stop();
    }

    return result;
}

int radiussample::AndroidAudioSystem::stopReceiver() {
    int result = (int)oboe::Result::OK;

    if (mReceiver->isReceiving()) {
        result = (int)mReceiver->pause();
    }

    return result;
}

int radiussample::AndroidAudioSystem::setMode(AndroidAudioSystem::Mode mode) {
    int result = (int)oboe::Result::OK;

    switch (mode) {
    case NONE: {
        result = stopReceiver();

        if (result != (int)oboe::Result::OK) {
            return result;
        }

        break;
    }

    case INPUT: {
        result = startReceiver();

        if (result != (int)oboe::Result::OK) {
            mReceiver->stop();
            return result;
        }

        break;
    }

    default: {
        return result; // Fake it til you make it.
    }
    }

    return result;
}

std::string radiussample::AndroidAudioSystem::getFilePath() {
    return mReceiver->getFilePath();
}