package com.example.dutmed;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dutmed.DutMedDbHelper;
import com.example.dutmed.HealthResource;

public class AddHealthTopicsActivity extends AppCompatActivity {

    private DutMedDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_health_topics);

        final EditText titleInput = findViewById(R.id.editTextTitle);
        final EditText contentInput = findViewById(R.id.editTextContent);
        final EditText typeInput = findViewById(R.id.editTextType);
        final EditText imageUrlInput = findViewById(R.id.editTextImageUrl);
        Button addButton = findViewById(R.id.buttonAddResource);

        addButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String content = contentInput.getText().toString();
            String type = typeInput.getText().toString();
            String imageUrl = imageUrlInput.getText().toString();

            HealthResource resource = new HealthResource(-1, title, content, type, imageUrl);
            // Assuming you have a method to add to database or list
            dbHelper.addHealthResource(title,content,type,imageUrl);
        });
    }
}
