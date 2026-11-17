package poe1;

/**
 * One message as it is kept in the Part 3 arrays and saved to the JSON file.
 * The flag is "Sent", "Stored" or "Disregard".
 */
public class MessageRecord {

    private String messageID;
    private int messageNumber;
    private String sender;
    private String recipient;
    private String text;
    private String flag;
    private String hash;

    public MessageRecord(String messageID, int messageNumber, String sender,
                         String recipient, String text, String flag, String hash) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
        this.flag = flag;
        this.hash = hash;
    }

    public String getMessageID()  { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getSender()     { return sender; }
    public String getRecipient()  { return recipient; }
    public String getText()       { return text; }
    public String getFlag()       { return flag; }
    public String getHash()       { return hash; }
}
