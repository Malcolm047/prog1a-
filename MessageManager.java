package poe1;

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

/**
 * Part 3 only: the five arrays, the JSON file and the Stored Messages features.
 *
 * Arrays: sent messages, disregarded messages, stored messages, message hashes
 * and message IDs. Stored messages are written to (and read back from) a JSON file.
 */
public class MessageManager {

    private final ArrayList<MessageRecord> sentMessages = new ArrayList<>();
    private final ArrayList<MessageRecord> disregardedMessages = new ArrayList<>();
    private final ArrayList<MessageRecord> storedMessages = new ArrayList<>();
    private final ArrayList<String> messageHashes = new ArrayList<>();
    private final ArrayList<String> messageIDs = new ArrayList<>();

    private int totalSent = 0;

    private final Path storageFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public MessageManager(Path storageFile) {
        this.storageFile = storageFile;
    }

    // ------------------------------------------------------------------
    // 1. Populating the arrays (no hard-coding: everything goes through here)
    // ------------------------------------------------------------------

    /** Puts the message in the right array using its flag and records its hash and ID. */
    public void addMessage(MessageRecord message) {
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

    public int returnTotalMessages() {
        return totalSent;
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
            MessageRecord[] loaded = gson.fromJson(reader, MessageRecord[].class);
            if (loaded == null) {
                return 0;
            }
            for (MessageRecord m : loaded) {
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
    // 2. Stored Messages menu
    // ------------------------------------------------------------------

    /** a. Sender and recipient of all stored messages. */
    public String displaySenderAndRecipient() {
        if (storedMessages.isEmpty()) {
            return "There are no stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (MessageRecord m : storedMessages) {
            sb.append("Sender: ").append(m.getSender())
              .append(" | Recipient: ").append(m.getRecipient()).append("\n");
        }
        return sb.toString().trim();
    }

    /** b. The longest stored message (null if there are none). */
    public MessageRecord longestStoredMessage() {
        MessageRecord longest = null;
        for (MessageRecord m : storedMessages) {
            if (longest == null || m.getText().length() > longest.getText().length()) {
                longest = m;
            }
        }
        return longest;
    }

    /** c. Search for a message ID (sent or stored). Null if not found. */
    public MessageRecord searchByMessageID(String id) {
        for (MessageRecord m : sentAndStored()) {
            if (m.getMessageID().equals(id)) {
                return m;
            }
        }
        return null;
    }

    /** d. All messages (sent or stored) for one recipient. */
    public List<MessageRecord> searchByRecipient(String recipient) {
        List<MessageRecord> found = new ArrayList<>();
        for (MessageRecord m : sentAndStored()) {
            if (m.getRecipient().equals(recipient)) {
                found.add(m);
            }
        }
        return found;
    }

    /** e. Delete a message using its hash. */
    public String deleteByHash(String hash) {
        for (ArrayList<MessageRecord> list : List.of(sentMessages, storedMessages)) {
            for (int i = 0; i < list.size(); i++) {
                MessageRecord m = list.get(i);
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
        for (MessageRecord m : sentMessages) {
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
        for (MessageRecord m : storedMessages) {
            sb.append("Message ID: ").append(m.getMessageID()).append("\n")
              .append("Message Hash: ").append(m.getHash()).append("\n")
              .append("Sender: ").append(m.getSender()).append("\n")
              .append("Recipient: ").append(m.getRecipient()).append("\n")
              .append("Message: ").append(m.getText()).append("\n\n");
        }
        return sb.toString().trim();
    }

    private List<MessageRecord> sentAndStored() {
        List<MessageRecord> all = new ArrayList<>(sentMessages);
        all.addAll(storedMessages);
        return all;
    }

    // ------------------------------------------------------------------
    // Access to the arrays (used by the unit tests)
    // ------------------------------------------------------------------

    public ArrayList<MessageRecord> getSentMessages()        { return sentMessages; }
    public ArrayList<MessageRecord> getDisregardedMessages() { return disregardedMessages; }
    public ArrayList<MessageRecord> getStoredMessages()      { return storedMessages; }
    public ArrayList<String> getMessageHashes()              { return messageHashes; }
    public ArrayList<String> getMessageIDs()                 { return messageIDs; }
}
