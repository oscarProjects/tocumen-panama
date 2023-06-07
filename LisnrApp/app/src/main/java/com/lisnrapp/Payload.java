package com.lisnrapp;

import com.lisnr.radius.Radius;
import com.lisnr.radius.Receiver;
import com.lisnr.radius.Tone;
import com.lisnr.radius.Transceiver;

public class Payload {

   private final String mTime;
   private final String mPayload;
   private final int mPayloadLength;
   private final String mProfileType;
   private final double mSnrDb;
   private final double mHeaderEvmDb;
   private final double mPayloadEvmDb;

   private boolean mIsExpanded = false;
   private final boolean mFailed;

   // Constructor for good payloads
   public Payload(String time, Receiver receiver, Tone tone, double signalStrength, double headerQuality, double payloadQuality) {
      mFailed = false;

      mTime = time;

      byte[] payloadData = tone.getData();
      mPayload = HelperFunctions.bytesToPayloadString(payloadData);
      mPayloadLength = payloadData.length;


      mProfileType = receiver.getProfile();

      mSnrDb = signalStrength;
      mHeaderEvmDb = headerQuality;
      mPayloadEvmDb = payloadQuality;
   }

   public Payload(String time, Transceiver transceiver, Tone tone, double signalStrength, double headerQuality, double payloadQuality) {
      mFailed = false;

      mTime = time;

      byte[] payloadData = tone.getData();
      mPayload = HelperFunctions.bytesToPayloadString(payloadData);
      mPayloadLength = payloadData.length;


      switch (transceiver.getType()) {
         case PKAB_SEND_STANDARD_WIDEBAND_RECEIVE:
            mProfileType = Radius.PROFILE_STANDARD2_WIDEBAND;
            break;

         case PKAB_HIGHER_SEND_PKAB_LOWER_RECEIVE:
            mProfileType = "pkab2_lower";
            break;

         case PKAB_LOWER_SEND_PKAB_HIGHER_RECEIVE:
            mProfileType = "pkab2_higher";
            break;

         case STANDARD_WIDEBAND_SEND_PKAB_RECEIVE:
            mProfileType = Radius.PROFILE_PKAB2;
            break;

         default:
            mProfileType = "unknown";
            break;
      }

      mSnrDb = signalStrength;
      mHeaderEvmDb = headerQuality;
      mPayloadEvmDb = payloadQuality;
   }

   String getTime() {
      return mTime;
   }

   String getPayload() {
      return mPayload;
   }

   int getPayLoadLength() {
      return mPayloadLength;
   }

   String getProfileType() {
      return mProfileType;
   }

   double getSignalStrength() {
      return mSnrDb;
   }

   double getHeaderQuality() {
      return mHeaderEvmDb;
   }

   double getPayloadQuality() {
      return mPayloadEvmDb;
   }

   boolean isExpanded() {
      return mIsExpanded;
   }

   void setExpanded(boolean expanded) {
      mIsExpanded = expanded;
   }

   boolean isValid() {
      return !mFailed;
   }
}
