package com.example.dutmed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class NotifyActivity extends AppCompatActivity {

    EditText editTextSubject, editTextContent, editTextContentEmail;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notify);
        button =findViewById((R.id.btnSend));



        editTextSubject =findViewById(R.id.subject);
        editTextContent = findViewById(R.id.content);
        editTextContentEmail =findViewById(R.id.email);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject,content,email;
                subject = editTextSubject.getText().toString();
                content = editTextContent.getText().toString();
                email = editTextContentEmail.getText().toString();

                if (subject.isEmpty() && content.isEmpty() && email.isEmpty()) {
                    Toast.makeText(NotifyActivity.this,"All fields are required", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendEmail(subject, content,email);
                }

            }
        });
    }

    public void sendEmail(String subject,String content, String email){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT,content);
        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent,"Choose email client:"));

    }

}