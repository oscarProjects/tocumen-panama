package com.lisnrapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleObserver;

import com.google.android.material.navigation.NavigationView;
import com.lisnr.radius.Radius;
import com.lisnr.radius.Receiver;
import com.lisnr.radius.Tone;
import com.lisnr.radius.Transmitter;
import com.lisnr.radius.exceptions.AudioSystemException;
import com.lisnr.radius.exceptions.AuthorizationDeniedException;
import com.lisnr.radius.exceptions.InvalidArgumentException;
import com.lisnr.radius.exceptions.InvalidProfileException;
import com.lisnr.radius.exceptions.InvalidTokenException;
import com.lisnr.radius.exceptions.InvalidTonePayloadException;
import com.lisnr.radius.exceptions.RadiusDestroyedException;
import com.lisnr.radius.exceptions.TransmitterNotRegisteredException;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {
    private static final String LOG_TAG = "RadiusActivity";
    private Radius mRadius = null;
    private Transmitter mTransmitter = null;
    private Receiver mReceiver = null;
    private byte[] mPayloadBytes = "test".getBytes();
    private AudioRecord mAudioRecord;
    private Receiver.ReceiverCallback mCallback;
    private boolean audioCapturado = false;

    private static boolean botonPresionado = false;
    private String mToken;
    private Animation currentAnimation;

    private ImageView bg_img_animated;
    private Button btnListen;
    private TextView txtDetailPushBtn;
    private ByteBuffer byteBuffer = null;

    private String bytesToPayloadString(byte[] data) {
        StringBuilder dataString = new StringBuilder();
        for (int byteOffset = 0; byteOffset < data.length; byteOffset++) {
            byte dataNybbleLow = (byte) (data[byteOffset] & 0x0F);
            byte dataNybbleHigh = (byte) ((data[byteOffset] & 0xF0) >> 4);
            dataString.append(nybbleToChar(dataNybbleHigh));
            dataString.append(nybbleToChar(dataNybbleLow));
            // Spaces for readability
            dataString.append(" ");
        }
        final String[] strSplit = dataString.toString().split(" ");
        dataString = new StringBuilder();
        for (final String str : strSplit) {
            final char chr = (char) Integer.parseInt(str, 16);
            if (chr < 0x20 || chr > 0x7E) {
                // Display non-printable ASCII as a unicode ?
                dataString.append('\uFFFD');
            } else {
                dataString.append(chr);
            }
        }
        return dataString.toString();
    }

    private char nybbleToChar(byte data) {
        char dataChar;
        if (data <= 9) {
            dataChar = (char) ('0' + data);
        } else {
            dataChar = (char) ('A' + data - 0x0A);
        }
        return dataChar;
    }

    class RxCallback implements Receiver.ReceiverCallback {
        @Override
        public void onToneReceived(Receiver receiver, Tone tone) {
            System.out.println("LSNR llego a RxCallback");

            double signalToNoiseRatio = tone.getSnrDb();
            double headerEvmDb = tone.getHeaderEvmDb();
            double payloadEvmDb = tone.getPayloadEvmDb();
            float green;
            float size;
            String time = new SimpleDateFormat(getString(R.string.time_format), Locale.US).format(new Date());
            Payload payload = new Payload(time, receiver, tone, signalToNoiseRatio, headerEvmDb, payloadEvmDb);
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
            String payloadString = bytesToPayloadString(tone.getData());
            Log.d(LOG_TAG, "Tone received with ASCII2: " + payload.getPayload().toString());
            Log.d(LOG_TAG, "Tone received with ASCII: " + payloadString);
            if (payloadString.contains("https")) {
                stopReceiving();
                openActivityShowImg(payloadString);
            }
        }
    }

    class TxCallback implements Transmitter.TransmitterCallback {

        @Override
        public void onTransmitComplete(Transmitter transmitter, Tone tone) {
            Log.d(LOG_TAG, "Tone sent with ASCII: " + bytesToPayloadString(tone.getData()));

        }

        @Override
        public void onTransmitterEmpty(Transmitter transmitter) {
            Log.d(LOG_TAG, "Transmitter is now empty");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("LSNR paso por onStop");
        mRadius = null;
        mTransmitter = null;
        mReceiver = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToken = getResources().getString(R.string.lisnr_sdk_token);
        txtDetailPushBtn = findViewById(R.id.txtDetailPushBtn);
        bg_img_animated = findViewById(R.id.bg_img_animated);
        btnListen = findViewById(R.id.btnListen);

        txtDetailPushBtn.setVisibility(View.GONE);

        btnListen.performClick();


        byteBuffer = null;

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        Button hamburgerButton = findViewById(R.id.hamburger_button);
        hamburgerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.white));
        navigationView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.white));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.mainFragment) {
                    drawer.closeDrawer(GravityCompat.START);

                } else if (id == R.id.receivedFragment) {
                    Intent intent = new Intent(MainActivity.this, ReceivedActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    botonPresionado = false;
                    finish();
                }
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;            }
        });


        btnListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!botonPresionado) {
                    chargeReceiving();

                } else {
                    stopReceiving();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.i(this.getClass().getSimpleName(), "Permissions not granted");
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECORD_AUDIO)) {
                    new android.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle("Requesting Permissions")
                            .setMessage("In a moment " + getApplication().getString(getApplication().getApplicationInfo().labelRes) + " will request permission to access your microphone. Microphone access is used only to listen for high-frequency data tones that are used to unlock extra content and improve your experience while using the app. Your data is only processed locally on this device, and never saved on or uploaded to a server.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnListen.performClick();
    }

    private void onProccesLsnr() {
        System.out.println("LSNR Paso por onProccesLsnr");
        try {
            if (audioCapturado) {

            if (mRadius == null) {
                mRadius = new Radius(getLifecycle(), getApplicationContext(), mToken, new Radius.ErrorEventCallback() {
                    @Override
                    public void onUnauthorizedCallback(String s) {
                        Log.e("Radius", "Error en la operación de Radius: " + s);

                    }

                    @Override
                    public void onExceptionCallback(String s) {
                        Log.e("Radius", "Error en la operación de Radius: " + s);

                    }
                });
            }
            if (mTransmitter == null) {
                mTransmitter = new Transmitter(Radius.PROFILE_STANDARD2, new TxCallback());
            }
            if (mReceiver == null) {
                mReceiver = new Receiver(Radius.PROFILE_STANDARD2, new RxCallback());
            }
            mRadius.registerReceiver(mReceiver);
            mRadius.registerTransmitter(mTransmitter);
            mTransmitter.transmit(new Tone(mPayloadBytes));
        }
    }catch (AuthorizationDeniedException | TransmitterNotRegisteredException | InvalidProfileException | InvalidTokenException | InvalidTonePayloadException | AudioSystemException | InvalidArgumentException | RadiusDestroyedException e) {
            e.printStackTrace();
        }
    }

    private void chargeReceiving() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("LSNR Paso por chargeReceiving");
                txtDetailPushBtn.setText(R.string.text_touch_Despress);
                ejecutarAnimacion();
                botonPresionado = true;
                audioCapturado = true;
                startReceivingAudio();
            }
        });
    }

    private ByteBuffer startReceivingAudio()  {

        System.out.println("LSNR Paso por startReceiving");

        // Crear un objeto AudioRecord para capturar audio
        int bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Debe habilitar los permisos para utilizar el micrófono", Toast.LENGTH_LONG).show();
            return null;
        }
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        mAudioRecord.startRecording();
        // Leer los datos de audio capturados en un búfer
        byte[] buffer = new byte[bufferSize];
        mAudioRecord.read(buffer, 0, bufferSize);
        mAudioRecord.stop();
        mAudioRecord.release();
        System.out.println("buffer LSMR: " + buffer.toString());

        // Convertir el array de bytes en un buffer
        byteBuffer = ByteBuffer.wrap(buffer);

        onProccesLsnr();
        return byteBuffer;
    }

    private void stopReceiving() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("LSNR Paso por stopReceiving");
                txtDetailPushBtn.setText(R.string.text_touch_press);
                detenerAnimacion();
                botonPresionado = false;
                byteBuffer = null;
                audioCapturado = false;
                if (mAudioRecord != null) {
                    mRadius = null;
                    mTransmitter = null;
                    mReceiver = null;
                    mAudioRecord = null;
                }
            }
        });
    }

    private void ejecutarAnimacion() {
        if (bg_img_animated != null) {
            System.out.println("LSNR Paso por ejecutar animacion");

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
            System.out.println("LSNR Paso por detener Animacion");

            bg_img_animated.getAnimation().setAnimationListener(null);
            bg_img_animated.clearAnimation();
        }
        currentAnimation = null;
    }

    private void openActivityShowImg(String urlImg) {
        Intent i = new Intent(MainActivity.this, GalleryActivity.class);
        i.putExtra("urlImg", urlImg);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}