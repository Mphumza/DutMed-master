package com.example.dutmed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dutmed.HealthResource;

import java.util.List;

public class HealthTopicsAdapter extends RecyclerView.Adapter<HealthTopicsAdapter.ViewHolder> {

    private List<HealthResource> healthResources;

    public HealthTopicsAdapter(List<HealthResource> healthResources) {
        this.healthResources = healthResources;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_health_resources, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HealthResource resource = healthResources.get(position);
        holder.title.setText(resource.getTitle());
        holder.content.setText(resource.getContent());
        holder.type.setText(resource.getType());
        // Load image using library like Glide or Picasso
        // Glide.with(holder.itemView.getContext()).load(resource.getImageUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return healthResources.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, content, type;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewTitle);
            content = itemView.findViewById(R.id.textViewContent);
            type = itemView.findViewById(R.id.textViewType);
            image = itemView.findViewById(R.id.imageViewResource);
        }
    }
}
