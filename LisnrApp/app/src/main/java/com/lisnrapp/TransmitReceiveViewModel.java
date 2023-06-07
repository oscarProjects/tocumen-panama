package com.lisnrapp;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModel;

import com.lisnr.radius.Radius;
import com.lisnr.radius.StreamBuilder;
import com.lisnr.radius.exceptions.AudioSystemException;
import com.lisnr.radius.exceptions.AuthorizationDeniedException;
import com.lisnr.radius.exceptions.InvalidTokenException;

public class TransmitReceiveViewModel extends ViewModel {
    private Radius mRadius;
    private StreamBuilder mInputStreamBuilder;
    private StreamBuilder mOutputStreamBuilder;
    private String mTransmitPayload;
    private int mProfileSelected;
    private int mTransceiverTypeSelected;
    private int mNumBeaconsSent;
    private int mNumPayloadsReceived;

    enum SDKState {
        IDLE,
        RECEIVING,
        TRANSMITTING,
        FINISHING_TRANSMISSION,
        BEACONING,
        FINISHING_BEACON,
        DISABLED
    }

    public Radius getRadius() {
        return mRadius;
    }

    public void createRadius(Lifecycle lifecycle, Context context, String token,
                             Radius.ErrorEventCallback callback) throws InvalidTokenException, AudioSystemException,
            AuthorizationDeniedException {
        mRadius = null;
        mInputStreamBuilder = new StreamBuilder(context, StreamBuilder.Direction.INPUT);
        mOutputStreamBuilder = new StreamBuilder(context, StreamBuilder.Direction.OUTPUT);
        mInputStreamBuilder.setSharingMode(StreamBuilder.SharingMode.SHARED);
        mRadius = new Radius(lifecycle, context, token, mOutputStreamBuilder, mInputStreamBuilder, callback);
    }

    String getTransmitPayload() {
        return mTransmitPayload;
    }

    void setTransmitPayload(String payload) {
        mTransmitPayload = payload;
    }

    int getProfileSelected() {
        return mProfileSelected;
    }

    void setProfileSelected(int index) {
        mProfileSelected = index;
    }

    int getTransceiverTypeSelected() {
        return mTransceiverTypeSelected;
    }

    void setTransceiverTypeSelected(int index) {
        mTransceiverTypeSelected = index;
    }

    void setNumBeaconsSent(int numBeaconsSent) {
        mNumBeaconsSent = numBeaconsSent;
    }

    int getNumBeaconsSent() {
        return mNumBeaconsSent;
    }

    void setNumPayloadsReceived(int numPayloadsReceived) {
        mNumPayloadsReceived = numPayloadsReceived;
    }

    int getNumPayloadsReceived() {
        return mNumPayloadsReceived;
    }
}