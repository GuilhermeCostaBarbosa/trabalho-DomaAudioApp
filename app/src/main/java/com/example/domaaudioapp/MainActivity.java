package com.example.domaaudioapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private AudioHelper audioHelper;
    private AudioManager audioManager;
    private AudioDeviceCallback audioDeviceCallback;

    private TextView tvStatusAudio;
    private Button btnBluetoothSettings;
    private Button btnPlayAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatusAudio = findViewById(R.id.tvStatusAudio);
        btnBluetoothSettings = findViewById(R.id.btnBluetoothSettings);
        btnPlayAlert = findViewById(R.id.btnPlayAlert);

        audioHelper = new AudioHelper(this);
        audioManager = audioHelper.getAudioManager();

        atualizarStatusAudio();

        audioDeviceCallback = new AudioDeviceCallback() {
            @Override
            public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
                super.onAudioDevicesAdded(addedDevices);
                if (audioHelper.isAudioOutputAvailable(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP)) {
                    Toast.makeText(MainActivity.this, "Fone Bluetooth Conectado!", Toast.LENGTH_SHORT).show();
                    atualizarStatusAudio();
                }
            }

            @Override
            public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
                super.onAudioDevicesRemoved(removedDevices);
                if (!audioHelper.isAudioOutputAvailable(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP)) {
                    Toast.makeText(MainActivity.this, "Fone Bluetooth Desconectado!", Toast.LENGTH_SHORT).show();
                    atualizarStatusAudio();
                }
            }
        };

        btnBluetoothSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirConfiguracoesBluetooth();
            }
        });

        btnPlayAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tocarAlertaDoma();
            }
        });
    }


    private void atualizarStatusAudio() {
        if (audioHelper.isAudioOutputAvailable(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP)) {
            tvStatusAudio.setText("Status: Fone Bluetooth Conectado");
        } else if (audioHelper.isAudioOutputAvailable(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER)) {
            tvStatusAudio.setText("Status: Alto-falante Interno Ativo");
        } else {
            tvStatusAudio.setText("Status: Nenhuma Saída Detectada");
        }
    }


    private void abrirConfiguracoesBluetooth() {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("EXTRA_CONNECTION_ONLY", true);
        intent.putExtra("EXTRA_CLOSE_ON_CONNECT", true);
        intent.putExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 1);
        startActivity(intent);
    }

    private void tocarAlertaDoma() {
        String msg;
        if (audioHelper.isAudioOutputAvailable(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP)) {
            msg = "Emitindo alerta de segurança no fone Bluetooth...";
        } else if (audioHelper.isAudioOutputAvailable(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER)) {
            msg = "Emitindo alerta sonoro no alto-falante do relógio...";
        } else {
            msg = "Conecte um fone Bluetooth para ouvir as notificações de áudio!";
        }

        new AlertDialog.Builder(this)
                .setTitle("Alerta Doma (Assistência)")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (audioManager != null && audioDeviceCallback != null) {
            audioManager.registerAudioDeviceCallback(audioDeviceCallback, new Handler(Looper.getMainLooper()));
        }
        atualizarStatusAudio();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (audioManager != null && audioDeviceCallback != null) {
            audioManager.unregisterAudioDeviceCallback(audioDeviceCallback);
        }
    }
}