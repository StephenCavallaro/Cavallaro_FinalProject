package csc4360.finalproject.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import csc4360.finalproject.R;
import csc4360.finalproject.model.Note;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteAdapter extends ArrayAdapter<Note> {

    private ArrayList<Note> data;
    private SparseBooleanArray mSelectedItemsIds;

    public NoteAdapter(Context context, int resource, ArrayList<Note> data) {
        super(context, resource, data);
        this.data = data;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;
        Note note = data.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.custom_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.mContent.setText(note.getContent());

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        String dateTime = dateFormat.format(new Date(note.getDateTime()).getTime());

        holder.mDate.setText(dateTime);
        if (note.getReminder() == 1) {
            holder.mNotification.setVisibility(View.VISIBLE);
        } else {
            holder.mNotification.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.content) TextView mContent;
        @BindView(R.id.date) TextView mDate;
        @BindView(R.id.notification)
        ImageView mNotification;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void remove(Note object) {
        data.remove(object);
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    private void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
