package com.example.domaaudioapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;

public class AudioHelper {
    private final Context context;
    private final AudioManager audioManager;

    public AudioHelper(Context context){
        this.context = context;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    // verificar se determinada saida de audio esta disponivel
    public boolean isAudioOutputAvailable(int type){
        // verifica hardware suporte saida de audio
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)){
            return false;
        }

        // obter lista de dispositivos de saida de audio conectado
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        for (AudioDeviceInfo device : devices) {
            if (device.getType() == type) {
                return true;
            }
        }

        return false;
    }

    public AudioManager getAudioManager(){
        return audioManager;
    }
}
