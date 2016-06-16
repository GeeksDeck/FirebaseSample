package com.geeksdeck.firebasesample.model;

public class ChatMessage {

    private String message;
    private String name;

    // Required default constructor for Firebase object mapping
    private ChatMessage() {
    }

    public ChatMessage(String message, String author) {
        this.message = message;
        this.name = author;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }
}
