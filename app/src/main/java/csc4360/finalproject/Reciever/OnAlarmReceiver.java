package csc4360.finalproject.Reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

// call the wake service when the broadcast receiver, receive an alarm
public class OnAlarmReceiver extends BroadcastReceiver {
    public static final String TAG="OnAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: alarm called");
        // request the wake up call
        // means unlock the system for the alarm to play
        WakeIntentService.acquireStaticLock(context);
        Intent inte = new Intent(context,AlarmService.class);

        context.startService(inte);
    }}