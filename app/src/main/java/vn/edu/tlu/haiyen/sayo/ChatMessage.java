package vn.edu.tlu.haiyen.sayo;

public class ChatMessage {
    private String senderId;
    private String receiverId;
    private String messageText;
    private long timestamp;

    // Constructor rỗng cần cho Firebase
    public ChatMessage() {
    }

    public ChatMessage(String senderId, String receiverId, String messageText, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    // Getters
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getMessageText() { return messageText; }
    public long getTimestamp() { return timestamp; }
}