package com.example.dutmed;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import java.util.ArrayList;


public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {
    private List<Appointment> mData;
    private List<Appointment> mDataFiltered;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView emailTextView;
        private final TextView campusTextView;
        private final TextView dateTextView;
        private final TextView timeTextView;

        public ViewHolder(View view) {
            super(view);
            emailTextView = view.findViewById(R.id.emailTextView);
            campusTextView = view.findViewById(R.id.campusTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
            timeTextView = view.findViewById(R.id.timeTextView);
        }
    }

    public AppointmentsAdapter(List<Appointment> data) {
        mData = data;
        mDataFiltered = new ArrayList<>(data); // copy full data
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.appointments, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Appointment appointment = mDataFiltered.get(position);
        holder.emailTextView.setText(appointment.getEmail());
        holder.campusTextView.setText(appointment.getCampus());
        holder.dateTextView.setText(appointment.getDate());
        holder.timeTextView.setText(appointment.getTime());
    }

    public void setAppointments(List<Appointment> appointments) {
        this.mData = appointments;
        notifyDataSetChanged(); // Notify the adapter that the underlying data has changed
    }

    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

    public void updateData(List<Appointment> newData) {
        mData = new ArrayList<>(newData);
        mDataFiltered = new ArrayList<>(newData);
        notifyDataSetChanged();
    }
}
