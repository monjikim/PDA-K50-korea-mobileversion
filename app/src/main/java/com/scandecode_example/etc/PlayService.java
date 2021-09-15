package com.scandecode_example.etc;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class PlayService extends Service {

    String msgs;


    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            msgs=intent.getStringExtra("msgs");
            Log.e("PlayService","BroadcastReceiver");
            Log.e("PlayService",msgs);
            if(msgs != null){

                Intent aIntent=new Intent();
                //Intent aIntent=new Intent("com.example.PLAY_TO_ACTIVITY");
                aIntent.putExtra("msgs",msgs);
                sendBroadcast(aIntent);
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("PlayService","Oncreate");
        Log.e("PlayService",msgs+"");
        //registerReceiver(receiver, new IntentFilter("com.example.PLAY_TO_SERVICE"));
        registerReceiver(receiver, new IntentFilter());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("PlayService","onDestroy");
        Log.e("PlayService",msgs);
        unregisterReceiver(receiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("PlayService","onStartCommand");
        Log.e("PlayService",msgs+"");
        if(msgs != null){
            Intent aIntent=new Intent();
            //Intent aIntent=new Intent("com.example.PLAY_TO_ACTIVITY");
            aIntent.putExtra("msgs", msgs);
            sendBroadcast(aIntent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
