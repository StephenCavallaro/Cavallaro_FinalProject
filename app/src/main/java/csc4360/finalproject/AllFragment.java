package csc4360.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import csc4360.finalproject.adapters.NoteAdapter;
import csc4360.finalproject.data.DatabaseHelper;
import csc4360.finalproject.model.Note;
import csc4360.finalproject.custom.ExTextView;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;


// Fragment sub class
public class AllFragment extends Fragment {

    public static ArrayList<Note> notesArrayList = new ArrayList<>();

    private static DatabaseHelper databaseHelper;
    private static NoteAdapter noteAdapter;

    @BindView(R.id.notes_list) ListView notesList;
    @BindView(android.R.id.empty) ExTextView tvEmpty;

    public AllFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all, container, false);
        ButterKnife.bind(this, view);
        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editNote(position);
            }
        });
        return view;
    }

    // edit note. parameter position is note index.
    private void editNote(int position) {
        // Forward note data to the Update Note Activity through intent
        Intent intent = new Intent(getActivity(), NoteActivity.class);
        intent.putExtra("action", "update");
        intent.putExtra("Id", notesArrayList.get(position).getId());
        intent.putExtra("Content", notesArrayList.get(position).getContent());
        intent.putExtra("Reminder", notesArrayList.get(position).getReminder());
        intent.putExtra("ReminderTime", notesArrayList.get(position).getReminderTime());
        intent.putExtra("Date", notesArrayList.get(position).getDateTime());
        startActivity(intent);
    }

    // Populate the list view
    public void populateList() {
        // Clear array list
        notesArrayList.clear();
        // initialize database helper class
        databaseHelper = new DatabaseHelper(getActivity());
        // Get all notes
        notesArrayList = databaseHelper.getAllNotes();
        // initialize adapter: (used to populate list, grid view etc)
        noteAdapter = new NoteAdapter(getActivity(), R.layout.custom_list, notesArrayList);
        notesList.setAdapter(noteAdapter);
        // If there are no notes then show this view
        // Which holds a warning message
        notesList.setEmptyView(tvEmpty);
        // Acts like checkboxes
        // Set the listView mode to select multiple: so now we can select multiple items
        // in the list view at the same time
        notesList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        notesList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // get the number of selected items count from the listView
                final int checkedCount = notesList.getCheckedItemCount();
                // show the number of selected items in the listview
                mode.setTitle(checkedCount + " Selected");
                // Toggle the select and unselect state of the item in the listview
                noteAdapter.toggleSelection(position);
                // set the mode in the main activity so latter we can clear it
                // if we move to the next fragment
                MainActivity.actionMode = mode;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // show menu on selecting item(s) in the listview
                mode.getMenuInflater().inflate(R.menu.list_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setTitle("WARNING")
                                .setMessage("Are you sure you want to say goodbye to these items?")
                                .setCancelable(true)
                                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Calls getSelectedIds method from ListViewAdapter Class
                                        SparseBooleanArray selected = noteAdapter.getSelectedIds();
                                        // Captures all selected ids with a loop
                                        for (int i = (selected.size() - 1); i >= 0; i--) {
                                            if (selected.valueAt(i)) {
                                                Note selectedItem = (Note) noteAdapter.getItem(selected.keyAt(i));
                                                // Remove it from the database
                                                deleteNote(selectedItem);
                                                // Remove selected items following the ids
                                                noteAdapter.remove(selectedItem);
                                            }
                                        }
                                        mode.finish();
                                        Toast.makeText(getActivity(), "Congrats! All Selected Notes Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton("Naw", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).create();
                        dialog.show();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                noteAdapter.removeSelection();
            }
        });
        noteAdapter.notifyDataSetChanged();
    }

    // Delete note. Parameter note is the user note
    private void deleteNote(Note note) {
        Boolean result = databaseHelper.deleteNote(note);
    }
    @Override
    public void onResume() {
        super.onResume();
        // Re populate the list
        populateList();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (MainActivity.actionMode != null) {
            MainActivity.actionMode.finish();
        }
    }
}
