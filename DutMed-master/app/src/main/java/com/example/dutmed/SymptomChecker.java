package com.example.dutmed;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;
import android.text.Html;
import android.text.Spanned;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SymptomChecker extends AppCompatActivity {
    EditText mSymptomEdit;
    Button CheckBUtton;
    TextView ResultsTextViewing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_symptom_checker);


        //initialize UI Elements
        mSymptomEdit = findViewById(R.id.symptomEditText);
        CheckBUtton = findViewById(R.id.checkButton);
        ResultsTextViewing = findViewById(R.id.resultsTextView);

        //set onClickListener for CheckButton
        CheckBUtton.setOnClickListener(v -> checkSymptoms());
    }
    private String getRemediesForConditions(String conditions) {
        Map<String, String> conditionToRemedyMap = new HashMap<>();
        conditionToRemedyMap.put("Flu, Common Cold", "Stay hydrated, rest, and take over-the-counter cold remedies.");
        conditionToRemedyMap.put("Migraine, Tension Headache", "Apply a cold or warm compress to your head, use essential oils, ensure adequate hydration.");
        conditionToRemedyMap.put("Fever - may require further diagnosis", "Drink plenty of fluids, use acetaminophen or ibuprofen, and rest.");
        conditionToRemedyMap.put("Sinusitis", "Use a humidifier, stay hydrated, and consider nasal irrigation or decongestants.");
        conditionToRemedyMap.put("Possible COVID-19 infection - please seek medical advice for proper testing and diagnosis", "Follow local health guidelines, self-isolate, and get tested for COVID-19.");
        conditionToRemedyMap.put("Conjunctivitis (Pink Eye)", "Use antibiotic or antihistamine eye drops if prescribed, and maintain good eye hygiene.");

        StringBuilder remedies = new StringBuilder();

        // Split the conditions string into individual conditions
        String[] splitConditions = conditions.split("; ");
        for (String condition : splitConditions) {
            if (conditionToRemedyMap.containsKey(condition.trim())) {
                remedies.append(conditionToRemedyMap.get(condition.trim())).append("; ");
            }
        }

        return remedies.toString();
    }


    public void checkSymptoms() {
        String userInput = mSymptomEdit.getText().toString().toLowerCase(); // Get user input and convert to lower case for comparison
        boolean hasHeadache = userInput.contains("headache");
        boolean hasFever = userInput.contains("fever");
        boolean hasSinus = userInput.contains("sinus");
        boolean hasCovid = userInput.contains("covid");
        boolean hasPinkEye = userInput.contains("pink eye");
        // You can add more symptoms checks here

        // Check symptoms against known conditions
        StringBuilder conditions = new StringBuilder();

        if (hasHeadache && hasFever) {
            conditions.append("Flu, Common Cold; ");
        }
        if (hasHeadache) {
            conditions.append("Migraine, Tension Headache; ");
        }
        if (hasFever) {
            conditions.append("Fever - may require further diagnosis; ");
        }
        if (hasSinus) {
            conditions.append("Sinusitis; ");
        }
        if (hasCovid) {
            conditions.append("Possible COVID-19 infection - please seek medical advice for proper testing and diagnosis; ");
        }
        if (hasPinkEye) {
            conditions.append("Conjunctivitis (Pink Eye); ");
        }
        if (conditions.length() == 0) {
            conditions.append("No specific condition detected based on the entered symptoms.");
        }
        String conditionString = conditions.toString();
        String remedies = getRemediesForConditions(conditionString);

        displayPossibleConditions(conditionString, remedies);

    }

// No longer need separate methods for getting user input for headache and fever

    //Example: Display possible conditions
    @SuppressLint("SetTextI18n")
    private void displayPossibleConditions(String conditions, String remedies) {
        String formattedText = "<b>Possible conditions:</b><br>" + conditions + "<br><br><b>Remedies:</b><br>" + remedies;
        Spanned styledText;

        styledText = Html.fromHtml(formattedText, Html.FROM_HTML_MODE_LEGACY);

        ResultsTextViewing.setText(styledText);
    }
}
