package com.geeksdeck.firebasesample;

/**
 * Created by syron on 13.06.16.
 */
public class ChatMessage {

     String message;
     String name;

    // Required default constructor for Firebase object mapping
    private ChatMessage() {
    }

    ChatMessage(String message, String author) {
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
