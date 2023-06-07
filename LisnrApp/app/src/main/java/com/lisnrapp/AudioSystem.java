package com.lisnrapp;

import android.content.Context;
import android.util.Log;

import com.lisnr.radius.StreamBuilder;

public class AudioSystem {

    static {
        System.loadLibrary("audiosystem-lib");
    }

    public enum AudioSystemMode {
        /**
         * This state disables listening/recording audio.
         */
        IDLE,
        /**
         * This state enables listening/recording audio.
         */
        INPUT,
        /**
         * Default startup state, no audio is being recorded yet.
         */
        DISABLED
    }

    private final Context mContext;

    public AudioSystem(Context context) {
        mContext = context;
    }

    public int setMode(AudioSystemMode mode) {
        StreamBuilder inputParams = new StreamBuilder(mContext, StreamBuilder.Direction.INPUT);
        inputParams.setSharingMode(StreamBuilder.SharingMode.SHARED);
        return nativeSetMode(mode.ordinal(), inputParams);
    }

    public void audioError(String error) {
        Log.e("RadiusSampleAudioSystem", error);
    }

    private native int nativeSetMode(int mode, Object inputStreamParameters);

    public String getFilePath() {
        return nativeGetFilepath();
    }

    private native String nativeGetFilepath();
}