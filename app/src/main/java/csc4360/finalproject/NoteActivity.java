package csc4360.finalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import csc4360.finalproject.data.DatabaseHelper;
import csc4360.finalproject.model.Note;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteActivity extends AppCompatActivity {

    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.reminder)
    SwitchCompat reminder;
    @BindView(R.id.date_picker)
    TextView datePicker;
    @BindView(R.id.time_picker)
    TextView timePicker;
    private DatabaseHelper databaseHelper;
    private Note note = null;
    private String action = "";
    private String time = "", date = "";

    // Return datepicker result : selected date
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            String year = String.valueOf(selectedYear);
            String month = String.valueOf(selectedMonth + 1);
            String day = String.valueOf(selectedDay);
            date = day + "/" + month + "/" + year;
            datePicker.setText(date);
        }
    };
    // Return time picker result : selected time
    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            time = hourOfDay + ":" + minute;
            timePicker.setText(time);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle extras = getIntent().getExtras();
        action = extras.getString("action");
        String activityTitle = null;
        // Set the activity title depending upon the action: (update or create)
        if (action != null) {
            activityTitle = (action.equals("add")) ? "Create new Note/Reminder" : "Edit this Note/Reminder";
        }
        setTitle(activityTitle);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.bind(this);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize database helper class
        databaseHelper = new DatabaseHelper(NoteActivity.this);


        content = (EditText) findViewById(R.id.content);


        if (action.equals("update")) {
            note = new Note();
            note.setId(extras.getInt("Id"));
            note.setContent(extras.getString("Content"));
            note.setDateTime(extras.getLong("Date"));
            note.setReminder(extras.getInt("Reminder"));
            note.setReminderTime(extras.getString("ReminderTime"));

            content.setText(note.getContent());
            //Put cursor at the end of the text
            content.setSelection(content.getText().length());
            if (note.getReminder() == 1) {
                reminder.setChecked(true);
                // Split date time on the base of space "2017/02/29 12:23"
                String dateTime[] = note.getReminderTime().split(" ");
                datePicker.setText(dateTime[0]);
                timePicker.setText(dateTime[1]);
            }
            // Hide softkeyboard in the update/edit mode
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } else {
            //Always show keyboard while adding new note
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggle(reminder.isChecked());
            }
        });
        toggle(reminder.isChecked());

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set current date in the datepicker dialog and display it to the user
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                DatePickerDialog datePickerDialog = new DatePickerDialog(NoteActivity.this,
                        R.style.PickerDialog,
                        datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setCancelable(false);
                datePickerDialog.show();
            }
        });

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set current time in the timepicker dialog and display it to the user
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                TimePickerDialog timePickerDialog = new TimePickerDialog(NoteActivity.this,
                        R.style.PickerDialog,
                        timePickerListener,
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        false);
                timePickerDialog.setCancelable(false);
                timePickerDialog.show();
            }
        });

    }

    private void toggle(boolean isChecked) {
        if (isChecked) {
            datePicker.setEnabled(true);
            timePicker.setEnabled(true);
        } else {
            datePicker.setEnabled(false);
            timePicker.setEnabled(false);
        }
    }

    //Save new note to the database
    // New note data is accessed from the interface of New Note Activity with  "Add Note" title
    private void saveNote() {
        // Create new note object
        Note note = new Note();

        // Set data into the object
        // trim any white space: trim()
        note.setContent(content.getText().toString().trim());
        int val = reminder.isChecked() ? 1 : 0;
        note.setReminder(val);
        note.setReminderTime(date + " " + time);
        // Save note object to the database
        Boolean result = databaseHelper.addNote(note);
        databaseHelper.close();
        if (result) {
            // close database connection
            databaseHelper.close();
            // Clear fields
            content.setText("");

            Toast.makeText(NoteActivity.this, getResources().getString(R.string.alert_add), Toast.LENGTH_LONG).show();
            // Stop alarm service
            stopService(new Intent(this,AlarmService.class));
            // start alarm service
            startService(new Intent(this,AlarmService.class));
            // Close activity
            finish();
        }
    }

    /**
     * Update note in the database
     */
    private void updateNote() {
        // Update data into the note object
        // Trim any white space : trim()
        note.setContent(content.getText().toString().trim());
        int val = reminder.isChecked() ? 1 : 0;
        note.setReminder(val);
        note.setReminderTime(date + " " + time);
        // Update note into the database : return true if successful
        // false if updation failed
        Boolean result = databaseHelper.updateNote(note);
        databaseHelper.close();
        if (result) {
            // Clear fields
            content.setText("");
            Toast.makeText(NoteActivity.this, getResources().getString(R.string.alert_update), Toast.LENGTH_LONG).show();
            // Stop alarm service
            stopService(new Intent(this,AlarmService.class));
            // start alarm service
            startService(new Intent(this,AlarmService.class));
            // Close activity
            finish();
        }
    }

    // Delete not from the database
    private void deleteNote() {
        Boolean result = databaseHelper.deleteNote(note);
        databaseHelper.close();
        if (result) {
            Toast.makeText(NoteActivity.this, getResources().getString(R.string.alert_delete), Toast.LENGTH_LONG).show();
            // Stop alarm service
            stopService(new Intent(this,AlarmService.class));
            // start alarm service
            startService(new Intent(this,AlarmService.class));
            // Close activity
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        // If activity in the create mode then we have no not to delete
        // So hide the delete menu
        if (action.equals("add")) {
            menu.findItem(R.id.btn_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.btn_delete:
                //Confirmation Dialog
                final TextView alertContainer = new TextView(getApplicationContext());
                int sizeInDp = 15;
                // These line manage the 15 according to the different screen densities (screen resolution)
                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (sizeInDp * scale + 0.5f);
                alertContainer.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, 0);
                alertContainer.setTextColor(Color.parseColor("#FFFFFF"));
                alertContainer.setText(getResources().getString(R.string.dialog_text));
                alertContainer.setTextSize(16);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(NoteActivity.this);
                alertBuilder.setView(alertContainer);
                alertBuilder.setCancelable(false);
                alertBuilder.setPositiveButton(getResources().getString(R.string.btn_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNote();
                    }
                });
                alertBuilder.setNegativeButton(getResources().getString(R.string.btn_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
                break;
            case R.id.btn_save:
                if (content.getText().toString().trim().equals("")) {
                    Toast.makeText(NoteActivity.this, getResources().getString(R.string.empty), Toast.LENGTH_SHORT).show();
                } else {
                    if (reminder.isChecked() && !time.equals("") && !date.equals("")) {
                        if (action.equals("add")) {
                            saveNote();
                        } else {
                            updateNote();
                        }
                    } else if (!reminder.isChecked()) {
                        if (action.equals("add")) {
                            saveNote();
                        } else {
                            updateNote();
                        }
                    } else {
                        Toast.makeText(NoteActivity.this, "Must include date and time for reminder!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

