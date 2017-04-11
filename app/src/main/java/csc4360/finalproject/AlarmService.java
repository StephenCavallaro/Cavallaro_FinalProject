package csc4360.finalproject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import csc4360.finalproject.data.DatabaseHelper;
import csc4360.finalproject.model.Note;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmService extends Service {
    public static final String TAG = "AlarmService";
    Handler h;
    Runnable runnable;
    int delay;
    ArrayList<Note> notesArrayList;
    private DatabaseHelper databaseHelper;

    public static int checkTime(long datetime) {

        Date now = Calendar.getInstance().getTime(); // Get time now
        long differenceInMillis = now.getTime() - datetime;
        long differenceInSeconds = (differenceInMillis) / 1000L; // Divide by millis/sec, secs/min, mins/hr
        return (int) differenceInSeconds;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        notesArrayList = new ArrayList<>();
        populateList();
        h = new Handler();
        delay = 500;
        runnable = new Runnable() {
            public void run() {
                checkDateTime();
                h.postDelayed(this, delay);
            }
        };

        scheduleMethod();
        return START_STICKY;
    }

    // compare note remider date time with the current date time
    private void checkDateTime() {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = df.format(c.getTime());

        for (int i = 0; i < notesArrayList.size(); i++) {
            if (notesArrayList.get(i).getReminder() == 1) {
                String savedD = notesArrayList.get(i).getReminderTime();
                String[] savedDateTime = savedD.split("\\s+");

                try {
                    // format the date in the given format "dd/MM/yyyy"
                    Date savedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                            .parse(savedDateTime[0]);
                    // format the date in the given format "dd/MM/yyyy"
                    Date today = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                            .parse(todayDate);
                    // compare the note date to the current date if they are equal
                    // 0 means they are equal
                    if (savedDate.compareTo(today) == 0) {

                        String savedTime = savedDateTime[1];
                        Calendar currentCal = Calendar.getInstance();

                        int Hr24 = currentCal.get(Calendar.HOUR_OF_DAY);
                        int Min = currentCal.get(Calendar.MINUTE);
                        String curretnTime24 = Hr24 + ":" + Min;
                        // compare note time with current time
                        if (savedTime.equals(curretnTime24)) {
                            Note updatedNOte = notesArrayList.get(i);
                            updatedNOte.setReminder(0);
                            databaseHelper.updateNote(updatedNOte);
                            Intent alarmIntent = new Intent(this, AlarmActivity.class);
                            // must set the intent flag if we are calling it from a non activity class
                            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(alarmIntent);
                        }


                    }
                } catch (ParseException e) {
                    Log.e(TAG, "checkDateTime: ", e);
                }
            }
        }
    }

    private void scheduleMethod() {
        //milliseconds
        h.postDelayed(runnable, delay);
    }

    public void populateList() {
        notesArrayList.clear();
        databaseHelper = new DatabaseHelper(this);
        ArrayList<Note> notes = databaseHelper.getAllNotes();

        for (int i = 0; i < notes.size(); i++) {

            int id = notes.get(i).getId();
            String content = notes.get(i).getContent();
            int reminder = notes.get(i).getReminder();
            String reminderTime = notes.get(i).getReminderTime();
            Long date = notes.get(i).getDateTime();
            Log.d(TAG, "populateList: reminder==>" + reminder + " reminderTime" + reminderTime + "  date==" + date);

            Note note = new Note();

            note.setContent(content);
            note.setReminder(reminder);
            note.setReminderTime(reminderTime);
            note.setDateTime(date);
            note.setId(id);

            notesArrayList.add(note);
        }

    }

}
