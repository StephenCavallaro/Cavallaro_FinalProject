package csc4360.finalproject.Reciever;

import android.content.Intent;
import csc4360.finalproject.AlarmActivity;

// Alarm service

public class AlarmService extends WakeIntentService {
    public static final String TAG="AlarmService";
    public AlarmService() {
        super("AlarmService");
    }

    @Override
    void doReminderWork(Intent intent) {
        Intent inte = new Intent(getApplicationContext(),AlarmActivity.class);
        inte.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(inte);
    }
}