/**
 * A single QuickChat message (Part 2 methods).
 *
 * The flag is one of: "Sent", "Stored" or "Disregard".
 */
public final class Message {

    public static final int MAX_LENGTH = 250;

    private String messageID;      // 10-digit unique ID
    private int messageNumber;     // running number of the message (starts at 0)
    private String sender;
    private String recipient;
    private String text;
    private String flag;
    private String hash;

    public Message(String messageID, int messageNumber, String sender,
                   String recipient, String text, String flag) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
        this.flag = flag;
        this.hash = createMessageHash();
    }

    /** Message ID may not be more than ten characters. */
    public boolean checkMessageID() {
        return messageID != null && !messageID.isEmpty() && messageID.length() <= 10;
    }

    /**
     * Recipient must start with an international code (+) followed by no more
     * than ten digits.
     * Regex idea based on the E.164 phone number format - add your own
     * reference here (author, year, website, access date) as the brief requires.
     */
    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("^\\+\\d{1,3}\\d{1,10}$")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.";
    }

    /**
     * First two characters of the ID : message number : first and last word
     * of the message, all in capitals. e.g. 00:0:HITHANKS
     */
    public String createMessageHash() {
        String idPart = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        String[] words = text.trim().split("\\s+");
        String first = clean(words[0]);
        String last = clean(words[words.length - 1]);
        return (idPart + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    /** Removes punctuation such as the "?" in "tonight?". */
    private static String clean(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    /** Full details in the order the brief asks for. */
    public String toDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + hash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + text;
    }

    public String getMessageID()  { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getSender()     { return sender; }
    public String getRecipient()  { return recipient; }
    public String getText()       { return text; }
    public String getFlag()       { return flag; }
    public String getHash()       { return hash; }

    public void setFlag(String flag) { this.flag = flag; }
}
