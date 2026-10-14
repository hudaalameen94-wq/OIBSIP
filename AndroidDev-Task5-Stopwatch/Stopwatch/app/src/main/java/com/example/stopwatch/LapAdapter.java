package com.example.stopwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/** Binds Lap objects to the rows of the scrollable lap list. */
public class LapAdapter extends ArrayAdapter<Lap> {

    public LapAdapter(@NonNull Context context, @NonNull List<Lap> laps) {
        super(context, 0, laps);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.item_lap, parent, false);
        }
        Lap lap = getItem(position);
        if (lap != null) {
            ((TextView) row.findViewById(R.id.lapNumber))
                    .setText(getContext().getString(R.string.lap_label, lap.number));
            ((TextView) row.findViewById(R.id.lapSplit))
                    .setText("+" + MainActivity.formatFull(lap.splitMs));
            ((TextView) row.findViewById(R.id.lapTotal))
                    .setText(MainActivity.formatFull(lap.totalMs));
        }
        return row;
    }
}