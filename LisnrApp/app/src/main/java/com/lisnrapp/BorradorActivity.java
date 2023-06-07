package com.lisnrapp;

import static com.lisnr.radius.StreamBuilder.Direction.INPUT;
import static com.lisnr.radius.StreamBuilder.Direction.OUTPUT;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lisnr.radius.Radius;
import com.lisnr.radius.Receiver;
import com.lisnr.radius.StreamBuilder;
import com.lisnr.radius.Tone;
import com.lisnr.radius.Transmitter;
import com.lisnr.radius.exceptions.InvalidArgumentException;
import com.lisnr.radius.exceptions.InvalidProfileException;
import com.lisnr.radius.exceptions.InvalidTokenException;
import com.lisnr.radius.exceptions.RadiusDestroyedException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BorradorActivity extends AppCompatActivity implements LifecycleObserver {
    private static final int PERMISSIONS_REQUEST_MICROPHONE_ACCESS = 0;

    private static final String LOG_TAG = "RadiusActivity"
            ;
    private String mSharedPrefLocation = "";

    private TransmitReceiveViewModel mViewModel;
    private Spinner mToneProfileSpinner;
    private Receiver.ReceiverCallback mCallback;
    private Receiver mReceiver;
    private int mNumPayloadsReceived;
    private PayloadAdapter mPayloadAdapter;
    private TransmitReceiveViewModel.SDKState mState;
    private HandlerThread mHandlerThread;
    private AudioSystem mAudioSystem;
    private RecyclerView mPayloads;
    private GradientDrawable mDrawableSignalQuality;

    private Transmitter mTransmitter = null;
    private byte[] mPayloadBytes = "test".getBytes();
    private static boolean botonPresionado = false;
    private String mToken;
    private Animation currentAnimation;


    private ImageView bg_img_animated;
    private Button btnListen;
    private TextView txtDetailPushBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToken = getResources().getString(R.string.lisnr_sdk_token);

        StreamBuilder inputStreamBuilder = new StreamBuilder(getApplicationContext(), INPUT);
        StreamBuilder outputStreamBuilder = new StreamBuilder(getApplicationContext(), OUTPUT);
        outputStreamBuilder.setSharingMode(StreamBuilder.SharingMode.SHARED);

        // Inicializar Radius


        txtDetailPushBtn = findViewById(R.id.txtDetailPushBtn);
        bg_img_animated = findViewById(R.id.bg_img_animated);
        btnListen = findViewById(R.id.btnListen);
        mToneProfileSpinner = findViewById(R.id.spnToneProfile);


        btnListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!botonPresionado) {
                    txtDetailPushBtn.setText(R.string.text_touch_Despress);
                    ejecutarAnimacion();
                    startReceiving();
                    botonPresionado = true;
                } else {
                    txtDetailPushBtn.setText(R.string.text_touch_press);
                    stopReceiving();
                    detenerAnimacion();

                    botonPresionado = false;
                }
            }
        });

//        mToneProfileSpinner.setOnItemSelectedListener(this);

        mPayloads = findViewById(R.id.recycler_payloads);
        RecyclerView.LayoutManager payloadLayout = new LinearLayoutManager(getApplicationContext());
        mPayloads.setLayoutManager(payloadLayout);

        mPayloadAdapter = new PayloadAdapter(getApplicationContext(), new ArrayList<Payload>(), mPayloads);
        mPayloads.setAdapter(mPayloadAdapter);

        // Obtener referencia al ViewModel
        mViewModel = new ViewModelProvider(BorradorActivity.this).get(TransmitReceiveViewModel.class);
        mViewModel.setNumPayloadsReceived(mNumPayloadsReceived);

        // Inicializar el SDK de LISNR
        validateToken();
//        try {
//            Radius radius = new Radius(getLifecycle(), getApplicationContext(), "", outputStreamBuilder, inputStreamBuilder);
//        } catch (InvalidTokenException e) {
//            e.printStackTrace();
//        }
//        mAudioSystem = new AudioSystem(getApplicationContext());


    }

    private void requestMicPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.i(this.getClass().getSimpleName(), getString(R.string.permissions_not_granted));

            // Code to handle getting microphone permissions on Android 6.0+
            if (Build.VERSION.SDK_INT >= 23) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.requesting_mic_permissions)
                            .setMessage(getString(R.string.in_a_moment) + getApplication().getString(
                                    getApplication().getApplicationInfo().labelRes)  +
                                    getString(R.string.permission_request_details))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(BorradorActivity.this,
                                            new String[]{Manifest.permission.RECORD_AUDIO},
                                            PERMISSIONS_REQUEST_MICROPHONE_ACCESS);
                                }
                            }).show();
                } else {
                    if (!prefs.getBoolean(getString(R.string.permissions_requested), false)) {
                        ActivityCompat.requestPermissions(BorradorActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                PERMISSIONS_REQUEST_MICROPHONE_ACCESS);
                    } else {
                        // Show error message
                        mPayloads.setVisibility(View.INVISIBLE);
//                        disableAllButtons();
                    }
                }
            }
        } else {
            mPayloads.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSdkState(TransmitReceiveViewModel.SDKState.IDLE);
        requestMicPermission();
        mCallback = new Receiver.ReceiverCallback() {
            @Override
            public void onToneReceived(Receiver receiver, Tone tone) {
                mNumPayloadsReceived++;
                mViewModel.setNumPayloadsReceived(mNumPayloadsReceived);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        mPayloadsReceived.setText(String.valueOf(mNumPayloadsReceived));
                    }
                });

                double signalToNoiseRatio = tone.getSnrDb();
                double headerEvmDb = tone.getHeaderEvmDb();
                double payloadEvmDb = tone.getPayloadEvmDb();

                String time = new SimpleDateFormat(getString(R.string.time_format), Locale.US).format(new Date());
                Payload payload = new Payload(time, receiver, tone, signalToNoiseRatio, headerEvmDb, payloadEvmDb);

                if (receiver.getProfile().equals(Radius.PROFILE_PKAB2) || receiver.getProfile().equals(Radius.PROFILE_PKAB2_WIDEBAND)) {
                    float green;
                    float size;

                    if (signalToNoiseRatio > 40.0) {
                        size = 1.0F;
                    } else if (signalToNoiseRatio < 0.0) {
                        size = 0.0F;
                    } else {
                        size = (float) (signalToNoiseRatio / 40.0);
                    }

                    if (payloadEvmDb < -24.0) {
                        green = 1.0F;
                    } else if (payloadEvmDb > 0.0) {
                        green = 0.0F;
                    } else {
                        green = (float) (payloadEvmDb / -24.0);
                    }

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mDrawableSignalQuality.setColor(HelperFunctions.colorGradient((int) (green * 100)));
//                            mImgSnr.setScaleX(size);
//                            mImgSnr.setScaleY(size);
//                            mImgSnr.setImageDrawable(mDrawableSignalQuality);
//                        }
//                    });
                }

                System.out.println("Lo que metio al adaptador: " + payload.getPayload().toString());
                mPayloadAdapter.add(payload);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPayloadAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        getToneProfiles();

        mNumPayloadsReceived = mViewModel.getNumPayloadsReceived();
//        mPayloadsReceived.setText(String.valueOf(mNumPayloadsReceived));
    }

    private void getToneProfiles() {
        ArrayList<String> toneProfiles = new ArrayList<>();
        toneProfiles.add(Radius.PROFILE_STANDARD2);
        toneProfiles.add(Radius.PROFILE_STANDARD2_WIDEBAND);
        toneProfiles.add(Radius.PROFILE_PKAB2);
        toneProfiles.add(Radius.PROFILE_PKAB2_WIDEBAND);

        ArrayAdapter<String> adapterToneProfiles = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,
                toneProfiles);
        adapterToneProfiles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mToneProfileSpinner.setAdapter(adapterToneProfiles);
        mToneProfileSpinner.setSelection(mViewModel.getProfileSelected());

        System.out.println("mToneProfileSpinner: " + mToneProfileSpinner);

        try {
            mReceiver = null;
            mReceiver = new Receiver(mToneProfileSpinner.getSelectedItem().toString(), mCallback);
        } catch (InvalidProfileException e) {
            e.printStackTrace();
        }
    }

    private void ejecutarAnimacion() {
        if (bg_img_animated != null) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.zoom);
            anim.setRepeatCount(Animation.INFINITE);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // No se usa
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    bg_img_animated.startAnimation(animation);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // No se usa
                }
            });
            currentAnimation = anim;
            bg_img_animated.startAnimation(currentAnimation);
        }
    }


    private void detenerAnimacion() {
        if (bg_img_animated != null && bg_img_animated.getAnimation() != null) {
            bg_img_animated.getAnimation().setAnimationListener(null);
            bg_img_animated.clearAnimation();
        }
        currentAnimation = null;
    }


    private void validateToken() {
        SharedPreferences prefs = BorradorActivity.this.getSharedPreferences(mSharedPrefLocation, Context.MODE_PRIVATE);
        prefs.edit().putString(getString(R.string.sdk_token_storage), mToken).apply();
        try {
            Radius.validateToken(mToken);
        } catch (InvalidTokenException e) {
            e.printStackTrace();
        }
    }

    private void startThreadProgressBar() {
        new Handler().postDelayed(new Runnable(){
            public void run(){

                //----------------------------
                openActivityShowImg();
                //----------------------------

            }
        }, 5000); //5000 millisegundos = 5 segundos.


    }

    private void openActivityShowImg() {
        Intent i = new Intent(BorradorActivity.this, GalleryActivity.class);
        startActivity(i);
    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////        try {
////            mViewModel.getRadius().unregisterReceiver(mReceiver);
////        } catch (InvalidArgumentException | RadiusDestroyedException e) {
////            e.printStackTrace();
////        }
//
//        mReceiver = null;
//
//        try {
//            mReceiver = new Receiver(mToneProfileSpinner.getSelectedItem().toString(), mCallback);
//            mViewModel.setProfileSelected(position);
//        } catch (InvalidProfileException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }

    private void setSdkState(TransmitReceiveViewModel.SDKState newSdkState) {
        mState = newSdkState;
        TextView sdkStateView = findViewById(R.id.txtSdkState);
        String sdkStateString = getString(R.string.empty_string);

        switch (newSdkState) {
            case IDLE:
                sdkStateString = getString(R.string.idle);
                break;

            case RECEIVING:
                sdkStateString = getString(R.string.receiving);
                break;

            case DISABLED:
                sdkStateString = getString(R.string.Disabled);
                break;

            default:
                break;
        }

        if (newSdkState == TransmitReceiveViewModel.SDKState.RECEIVING) {
            mToneProfileSpinner.setEnabled(false);
        } else if (newSdkState == TransmitReceiveViewModel.SDKState.IDLE) {
        }

        sdkStateView.setText(sdkStateString);
    }

    @Override
    protected void onPause() {
        setSdkState(TransmitReceiveViewModel.SDKState.IDLE);
//        mAudioSystem.setMode(AudioSystem.AudioSystemMode.IDLE);
//        mEnableRecordingSwitch.setChecked(false);
        new Thread() {
            @Override
            public void run() {
                try {
                    if (mViewModel.getRadius() != null) {
                        mViewModel.getRadius().unregisterAll();
                    }
                } catch (RadiusDestroyedException e) {
                    e.printStackTrace();
                }
            }
        } .start();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void startReceiving() {
        try {
            mViewModel.getRadius().registerReceiver(mReceiver);
            setSdkState(TransmitReceiveViewModel.SDKState.RECEIVING);
//            startThreadProgressBar();
        } catch (InvalidArgumentException | RadiusDestroyedException e) {
            e.printStackTrace();
        }
    }

    private void stopReceiving() {
        try {
            mViewModel.getRadius().unregisterReceiver(mReceiver);
            setSdkState(TransmitReceiveViewModel.SDKState.IDLE);
//            startThreadProgressBar();
        } catch (InvalidArgumentException | RadiusDestroyedException e) {
            e.printStackTrace();
        }
    }

    public void radiusErrorEventOccurred(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setSdkState(TransmitReceiveViewModel.SDKState.DISABLED);
                mToneProfileSpinner.setEnabled(false);
                mPayloads.setVisibility(View.INVISIBLE);
            }
        });
    }


}
