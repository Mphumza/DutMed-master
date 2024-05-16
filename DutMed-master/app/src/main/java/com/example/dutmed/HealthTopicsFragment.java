package com.example.dutmed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HealthTopicsFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_topics, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewHealthTopics);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the database helper
        DutMedDbHelper dbHelper = new DutMedDbHelper(getContext());
        // Fetch the list of Health Resources from the database
        List<HealthResource> healthResources = dbHelper.getAllHealthResources();

        // Correctly create the adapter instance
        HealthTopicsAdapter adapter = new HealthTopicsAdapter(healthResources);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
