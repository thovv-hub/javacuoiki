package org.example.cuoikijava.model;

import jakarta.persistence.*;

@Entity
@Table(name = "messages")

public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;

    private String sender;

    private String receiver;
    private String senderRole;

    private String receiverRole;

    private String content;

    private String time;

    public Message() {
    }

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReceiverRole() {
        return receiverRole;
    }

    public void setReceiverRole(String receiverRole) {
        this.receiverRole = receiverRole;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }
}