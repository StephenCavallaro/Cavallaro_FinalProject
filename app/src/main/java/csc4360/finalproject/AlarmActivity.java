package csc4360.finalproject;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AlarmActivityAlarm";

    Ringtone ringtone;

    ImageView iv_alarm;
    Button btn_stop;

    Handler h;
    Runnable runnable;
    // Wake the screen to display the alarm notification
    PowerManager.WakeLock screenLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        iv_alarm = (ImageView) findViewById(R.id.iv_alarm);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);
        // initialize the wake lock
        screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        KeyguardManager manager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = manager.newKeyguardLock("abc");
        lock.disableKeyguard();
        screenLock.acquire();
        h = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                ringtone.play();
            }
        };
    }

    @Override
    protected void onResume() {
        startAlarm();

        super.onResume();
    }

    // stop alarm
    public void stopAlarm() {
        h.removeCallbacks(runnable);
        // check if alarm tone is playing
        // then stop it
        if (ringtone.isPlaying()) {
            ringtone.stop();
        }
        this.finish();
    }


    // start alarm
    private void startAlarm() {
        // get ring tone type : Alarm in this case
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {  // I can't see this ever being null (as always have a default notification) but just incase
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alert);
        if (ringtone != null) {
            ringtone.play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ringtone.isPlaying()) {
            ringtone.stop();
        }
        h.removeCallbacks(runnable);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_stop:

                stopAlarm();
                break;
        }
    }
}
