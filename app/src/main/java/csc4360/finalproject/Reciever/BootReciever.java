package csc4360.finalproject.Reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// start the alarm service after the system restart
public class BootReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, csc4360.finalproject.AlarmService.class));
    }
}
