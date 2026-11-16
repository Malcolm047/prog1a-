import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Holds the five arrays from Part 3 and everything that works on them.
 *
 * Sent / Disregarded / Stored messages, plus one array of all message hashes
 * and one of all message IDs. Stored messages are written to (and read back
 * from) a JSON file.
 */
public class MessageManager {

    private final ArrayList<Message> sentMessages = new ArrayList<>();
    private final ArrayList<Message> disregardedMessages = new ArrayList<>();
    private final ArrayList<Message> storedMessages = new ArrayList<>();
    private final ArrayList<String> messageHashes = new ArrayList<>();
    private final ArrayList<String> messageIDs = new ArrayList<>();

    private int totalSent = 0;
    private int messageCounter = 0;

    private final Path storageFile;
    private final Random random = new Random();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public MessageManager(Path storageFile) {
        this.storageFile = storageFile;
    }

    // ------------------------------------------------------------------
    // Creating messages
    // ------------------------------------------------------------------

    /** Random ten-digit ID that has not been used yet. */
    public String generateMessageID() {
        String id;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                sb.append(random.nextInt(10));
            }
            id = sb.toString();
        } while (messageIDs.contains(id));
        return id;
    }

    public String checkMessageLength(String text) {
        int over = text.length() - Message.MAX_LENGTH;
        if (over > 0) {
            return "Message exceeds 250 characters by " + over + ", please reduce the size.";
        }
        return "Message ready to send.";
    }

    public Message createMessage(String sender, String recipient, String text) {
        return new Message(generateMessageID(), messageCounter++, sender, recipient, text, "New");
    }

    // ------------------------------------------------------------------
    // Populating the arrays
    // ------------------------------------------------------------------

    /**
     * The user chooses to send (1), disregard (2) or store (3) the message.
     * Returns the message the brief asks for.
     */
    public String sentMessage(Message message, int choice) {
        switch (choice) {
            case 1:
                message.setFlag("Sent");
                addMessage(message);
                return "Message successfully sent.";
            case 2:
                // The message is only removed once the user confirms with 0
                // (see disregardMessage).
                return "Press 0 to delete the message.";
            case 3:
                message.setFlag("Stored");
                addMessage(message);
                return "Message successfully stored.";
            default:
                return "Invalid option. Please choose 1, 2 or 3.";
        }
    }

    public void disregardMessage(Message message) {
        message.setFlag("Disregard");
        addMessage(message);
    }

    /**
     * Puts a message in the correct array according to its flag and records
     * its hash and ID. This is what the test data is loaded through too, so
     * nothing is hard-coded.
     */
    public void addMessage(Message message) {
        String flag = message.getFlag() == null ? "" : message.getFlag().toLowerCase();
        switch (flag) {
            case "sent":
                sentMessages.add(message);
                totalSent++;
                break;
            case "stored":
                storedMessages.add(message);
                break;
            case "disregard":
            case "disregarded":
                disregardedMessages.add(message);
                break;
            default:
                throw new IllegalArgumentException("Unknown message flag: " + message.getFlag());
        }
        messageHashes.add(message.getHash());
        messageIDs.add(message.getMessageID());

        if (flag.equals("stored")) {
            try {
                saveStoredMessages();
            } catch (IOException e) {
                System.out.println("Could not write the JSON file: " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------
    // JSON storage
    // ------------------------------------------------------------------

    /** Writes the whole stored-messages array to the JSON file. */
    public void saveStoredMessages() throws IOException {
        try (Writer writer = Files.newBufferedWriter(storageFile)) {
            gson.toJson(storedMessages, writer);
        }
    }

    /**
     * Reads the JSON file into the stored-messages array (called when the
     * program starts). Returns how many messages were loaded.
     */
    public int loadStoredMessages() throws IOException {
        if (!Files.exists(storageFile)) {
            return 0;
        }
        try (Reader reader = Files.newBufferedReader(storageFile)) {
            Message[] loaded = gson.fromJson(reader, Message[].class);
            if (loaded == null) {
                return 0;
            }
            for (Message m : loaded) {
                storedMessages.add(m);
                messageHashes.add(m.getHash());
                messageIDs.add(m.getMessageID());
            }
            return loaded.length;
        } catch (JsonParseException e) {
            throw new IOException("The JSON file is not valid: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    // Part 2 style helpers
    // ------------------------------------------------------------------

    /** All the messages sent while the program is running. */
    public String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.toDetails()).append("\n\n");
        }
        return sb.toString().trim();
    }

    public int returnTotalMessages() {
        return totalSent;
    }

    // ------------------------------------------------------------------
    // Part 3 - Stored Messages menu
    // ------------------------------------------------------------------

    /** a. Sender and recipient of all stored messages. */
    public String displaySenderAndRecipient() {
        if (storedMessages.isEmpty()) {
            return "There are no stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append("Sender: ").append(m.getSender())
              .append(" | Recipient: ").append(m.getRecipient()).append("\n");
        }
        return sb.toString().trim();
    }

    /** b. The longest stored message (null if there are none). */
    public Message longestStoredMessage() {
        Message longest = null;
        for (Message m : storedMessages) {
            if (longest == null || m.getText().length() > longest.getText().length()) {
                longest = m;
            }
        }
        return longest;
    }

    /** c. Search for a message ID (sent or stored). Null if not found. */
    public Message searchByMessageID(String id) {
        for (Message m : sentAndStored()) {
            if (m.getMessageID().equals(id)) {
                return m;
            }
        }
        return null;
    }

    /** d. All messages (sent or stored) for one recipient. */
    public List<Message> searchByRecipient(String recipient) {
        List<Message> found = new ArrayList<>();
        for (Message m : sentAndStored()) {
            if (m.getRecipient().equals(recipient)) {
                found.add(m);
            }
        }
        return found;
    }

    /** e. Delete a message using its hash. */
    public String deleteByHash(String hash) {
        for (ArrayList<Message> list : List.of(sentMessages, storedMessages)) {
            for (int i = 0; i < list.size(); i++) {
                Message m = list.get(i);
                if (m.getHash().equals(hash)) {
                    list.remove(i);
                    messageHashes.remove(m.getHash());
                    messageIDs.remove(m.getMessageID());
                    if (list == storedMessages) {
                        try {
                            saveStoredMessages();
                        } catch (IOException e) {
                            System.out.println("Could not update the JSON file: " + e.getMessage());
                        }
                    }
                    return "Message: \"" + m.getText() + "\" successfully deleted.";
                }
            }
        }
        return "No message with the hash " + hash + " was found.";
    }

    /** Report of all the sent messages: hash, recipient and message. */
    public String displayReport() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append("Message Hash: ").append(m.getHash()).append("\n")
              .append("Recipient: ").append(m.getRecipient()).append("\n")
              .append("Message: ").append(m.getText()).append("\n\n");
        }
        return sb.toString().trim();
    }

    /** f. Full details of all the stored messages. */
    public String displayStoredReport() {
        if (storedMessages.isEmpty()) {
            return "There are no stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append("Message ID: ").append(m.getMessageID()).append("\n")
              .append("Message Hash: ").append(m.getHash()).append("\n")
              .append("Sender: ").append(m.getSender()).append("\n")
              .append("Recipient: ").append(m.getRecipient()).append("\n")
              .append("Message: ").append(m.getText()).append("\n\n");
        }
        return sb.toString().trim();
    }

    private List<Message> sentAndStored() {
        List<Message> all = new ArrayList<>(sentMessages);
        all.addAll(storedMessages);
        return all;
    }

    // ------------------------------------------------------------------
    // Access to the arrays (used by the unit tests)
    // ------------------------------------------------------------------

    public ArrayList<Message> getSentMessages()         { return sentMessages; }
    public ArrayList<Message> getDisregardedMessages()  { return disregardedMessages; }
    public ArrayList<Message> getStoredMessages()       { return storedMessages; }
    public ArrayList<String> getMessageHashes()         { return messageHashes; }
    public ArrayList<String> getMessageIDs()            { return messageIDs; }
}
