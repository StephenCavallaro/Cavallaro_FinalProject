package csc4360.finalproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import csc4360.finalproject.model.Note;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";
    private final ArrayList<Note> notesArrayList = new ArrayList<>();

    public DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Schema of the notes table
        String CREATE_NOTES_TABLE = "CREATE TABLE " + Constants.NOTES_TABLE_NAME +
                "(" +
                Constants.NOTES_KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                Constants.NOTES_CONTENT_NAME + " TEXT," +
                Constants.NOTES_REMINDER + " INTEGER," +
                Constants.NOTES_REMINDER_TIME + " TEXT," +
                Constants.NOTES_CREATED_AT_NAME + " LONG" +
                ");";
        // create new table
        db.execSQL(CREATE_NOTES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.NOTES_TABLE_NAME);
        //Create again
        onCreate(db);
    }

    //Add note to the database
    public boolean addNote(Note note) {
        // get database in the writable mode
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // put note data in the content values
        values.put(Constants.NOTES_CONTENT_NAME, note.getContent());
        values.put(Constants.NOTES_REMINDER, note.getReminder());
        values.put(Constants.NOTES_REMINDER_TIME, note.getReminderTime());
        values.put(Constants.NOTES_CREATED_AT_NAME, System.currentTimeMillis());
        Log.d(TAG, "getAllNotes: dateTime set==" + System.currentTimeMillis());
        // save data in the database
        Boolean result = db.insert(Constants.NOTES_TABLE_NAME, null, values) > 0;
        db.close();
        return result;
    }

    //Update note into the database
    public boolean updateNote(Note note) {

        // Get the database in the writable mode
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // put data in the content
        values.put(Constants.NOTES_CONTENT_NAME, note.getContent());
        values.put(Constants.NOTES_REMINDER, note.getReminder());
        values.put(Constants.NOTES_REMINDER_TIME, note.getReminderTime());
        values.put(Constants.NOTES_CREATED_AT_NAME, note.getDateTime());
        // update data in the database
        Boolean result = db.update(Constants.NOTES_TABLE_NAME, values, Constants.NOTES_KEY_ID + " = ?", new String[]{String.valueOf(note.getId())}) > 0;
        db.close();
        return result;
    }

    //Delete note from the database
    public boolean deleteNote(Note note) {
        // get database in the writable mode
        SQLiteDatabase db = this.getWritableDatabase();
        // get the note and delete it
        Boolean result = db.delete(Constants.NOTES_TABLE_NAME, Constants.NOTES_KEY_ID + " = ?", new String[]{String.valueOf(note.getId())}) > 0;
        db.close();
        return result;
    }

    //Get all notes
    public ArrayList<Note> getAllNotes() {

        // Get database in the readable mode
        SQLiteDatabase db = this.getReadableDatabase();

        // query the database for all notes
        Cursor cursor = db.query(Constants.NOTES_TABLE_NAME, new String[]{
                Constants.NOTES_KEY_ID,
                Constants.NOTES_CONTENT_NAME,
                Constants.NOTES_REMINDER,
                Constants.NOTES_REMINDER_TIME,
                Constants.NOTES_CREATED_AT_NAME
        }, null, null, null, null, Constants.NOTES_CREATED_AT_NAME + " DESC");

        // consider cursor as virtual table which holds the result of the executed query
        // in this case result of the above query
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // New note object
                Note note = new Note();
                // put data the note object
                note.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Constants.NOTES_KEY_ID))));
                note.setContent(cursor.getString(cursor.getColumnIndex(Constants.NOTES_CONTENT_NAME)));
                note.setReminder(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Constants.NOTES_REMINDER))));
                note.setReminderTime(cursor.getString(cursor.getColumnIndex(Constants.NOTES_REMINDER_TIME)));
                note.setDateTime(cursor.getLong(cursor.getColumnIndex(Constants.NOTES_CREATED_AT_NAME)));
                Log.d(TAG, "getAllNotes: dateTime got==" + note.getDateTime());
                // save note object in the array list
                notesArrayList.add(note);
            }
        }
        if (cursor != null) {
            // close cursor to free up the resources
            cursor.close();
        }
        // close connection
        db.close();
        // return array list to the request method
        return notesArrayList;
    }

    //Get all notes: (having reminder alert)
    public ArrayList<Note> getReminderNotes() {
        // Get database in the readable mode
        SQLiteDatabase db = this.getReadableDatabase();
        // query the database for all notes
        Cursor cursor = db.query(Constants.NOTES_TABLE_NAME, new String[]{
                Constants.NOTES_KEY_ID,
                Constants.NOTES_CONTENT_NAME,
                Constants.NOTES_REMINDER,
                Constants.NOTES_REMINDER_TIME,
                Constants.NOTES_CREATED_AT_NAME
        }, Constants.NOTES_REMINDER + " = '1'", null, null, null, Constants.NOTES_CREATED_AT_NAME + " DESC");

        // consider cursor as virtual table which holds the result of the executed query
        // in this case result of the above query
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // New note object
                Note note = new Note();
                // put data the note object
                note.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Constants.NOTES_KEY_ID))));
                note.setContent(cursor.getString(cursor.getColumnIndex(Constants.NOTES_CONTENT_NAME)));
                note.setReminder(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Constants.NOTES_REMINDER))));
                note.setReminderTime(cursor.getString(cursor.getColumnIndex(Constants.NOTES_REMINDER_TIME)));
                note.setDateTime(cursor.getLong(cursor.getColumnIndex(Constants.NOTES_CREATED_AT_NAME)));
                // save note object in the array list
                notesArrayList.add(note);
            }
        }
        if (cursor != null) {
            // close cursor to free up the resources
            cursor.close();
        }
        // close database connection
        db.close();
        // return array list to the request method
        return notesArrayList;
    }

}
