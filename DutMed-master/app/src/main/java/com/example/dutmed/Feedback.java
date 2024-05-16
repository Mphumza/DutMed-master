package com.example.dutmed;

public class Feedback {

    private final int userId;
    private final int entryId;
    private final String feedbackText;
    private final String suggestions;
    private final String dateSubmitted;

    public Feedback(int userId, int entryId, String feedbackText, String suggestions, String dateSubmitted) {

        this.userId = userId;
        this.entryId = entryId;
        this.feedbackText = feedbackText;
        this.suggestions = suggestions;
        this.dateSubmitted = dateSubmitted;
    }

    // Getters and possibly setters
    public int getUser_Id() { return userId; }
    public int getEntryId() { return entryId; }
    public String getFeedbackText() {
        return feedbackText;
    }
    public String getSuggestions() { return suggestions;}
    public String getDate() {
        return dateSubmitted;
    }

}

