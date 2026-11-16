import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for Part 3, using the test data from the brief. */
public class MessageManagerTest {

    private static final String MSG2 = "Where are you? You are late! I have asked you to be on time.";

    private Path jsonFile;
    private MessageManager manager;
    private Message m1, m2, m3, m4, m5;

    @BeforeEach
    public void setUp() throws IOException {
        jsonFile = Files.createTempFile("storedMessages", ".json");
        Files.deleteIfExists(jsonFile);          // start with no file
        manager = new MessageManager(jsonFile);

        // Test data from the brief. Message 4's developer-supplied ID is 0838884567.
        // The brief does not give message 4 a recipient, so a different number is used
        // (it must not be +27838884567, or the recipient search would return it too).
        m1 = new Message(manager.generateMessageID(), 0, "Developer", "+27834557896", "Did you get the cake?", "Sent");
        m2 = new Message(manager.generateMessageID(), 1, "Developer", "+27838884567", MSG2, "Stored");
        m3 = new Message(manager.generateMessageID(), 2, "Developer", "+27834484567", "Yohoooo, I am at your gate.", "Disregard");
        m4 = new Message("0838884567", 3, "Developer", "+27838884560", "It is dinner time!", "Sent");
        m5 = new Message(manager.generateMessageID(), 4, "Developer", "+27838884567", "Ok, I am leaving without you.", "Stored");

        // Populate the arrays through the same method the program uses.
        for (Message m : Arrays.asList(m1, m2, m3, m4, m5)) {
            manager.addMessage(m);
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(jsonFile);
    }

    // ---------------- Part 3 tests from the brief ----------------

    @Test
    public void sentMessagesArrayCorrectlyPopulated() {
        List<String> expected = Arrays.asList("Did you get the cake?", "It is dinner time!");
        List<String> actual = new ArrayList<>();
        for (Message m : manager.getSentMessages()) {
            actual.add(m.getText());
        }
        assertEquals(expected, actual);
    }

    @Test
    public void disregardedAndStoredArraysCorrectlyPopulated() {
        assertEquals(1, manager.getDisregardedMessages().size());
        assertEquals("Yohoooo, I am at your gate.", manager.getDisregardedMessages().get(0).getText());
        assertEquals(2, manager.getStoredMessages().size());
    }

    @Test
    public void hashAndIdArraysContainEveryMessage() {
        assertEquals(5, manager.getMessageHashes().size());
        assertEquals(5, manager.getMessageIDs().size());
        assertTrue(manager.getMessageIDs().contains("0838884567"));
    }

    @Test
    public void displayTheLongestMessage() {
        assertEquals(MSG2, manager.longestStoredMessage().getText());
    }

    @Test
    public void searchForMessageID() {
        Message found = manager.searchByMessageID("0838884567");
        assertNotNull(found);
        assertEquals("It is dinner time!", found.getText());
    }

    @Test
    public void searchForMessageIDNotFound() {
        assertEquals(null, manager.searchByMessageID("9999999999"));
    }

    @Test
    public void searchAllMessagesForARecipient() {
        List<String> expected = Arrays.asList(MSG2, "Ok, I am leaving without you.");
        List<String> actual = new ArrayList<>();
        for (Message m : manager.searchByRecipient("+27838884567")) {
            actual.add(m.getText());
        }
        assertEquals(expected, actual);
    }

    @Test
    public void deleteAMessageUsingTheMessageHash() {
        String result = manager.deleteByHash(m2.getHash());
        assertEquals("Message: \"" + MSG2 + "\" successfully deleted.", result);
        assertEquals(1, manager.getStoredMessages().size());
        assertFalse(manager.getMessageHashes().contains(m2.getHash()));
        assertFalse(manager.getMessageIDs().contains(m2.getMessageID()));
    }

    @Test
    public void deleteWithUnknownHash() {
        assertEquals("No message with the hash NOPE was found.", manager.deleteByHash("NOPE"));
    }

    @Test
    public void displayReportListsAllSentMessages() {
        String expected =
                "Message Hash: " + m1.getHash() + "\n"
                + "Recipient: +27834557896\n"
                + "Message: Did you get the cake?\n\n"
                + "Message Hash: " + m4.getHash() + "\n"
                + "Recipient: +27838884560\n"
                + "Message: It is dinner time!";
        assertEquals(expected, manager.displayReport());
    }

    // ---------------- JSON storage ----------------

    @Test
    public void storedMessagesAreWrittenToJsonAndReadBack() throws IOException {
        assertTrue(Files.exists(jsonFile));

        MessageManager fresh = new MessageManager(jsonFile);
        assertEquals(2, fresh.loadStoredMessages());
        assertEquals(MSG2, fresh.getStoredMessages().get(0).getText());
        assertEquals("Ok, I am leaving without you.", fresh.getStoredMessages().get(1).getText());
        assertEquals(m2.getHash(), fresh.getStoredMessages().get(0).getHash());
        assertEquals(2, fresh.getMessageHashes().size());
    }

    @Test
    public void deletedMessageIsRemovedFromJson() throws IOException {
        manager.deleteByHash(m2.getHash());
        MessageManager fresh = new MessageManager(jsonFile);
        assertEquals(1, fresh.loadStoredMessages());
    }

    @Test
    public void loadingWithNoFileGivesAnEmptyArray() throws IOException {
        Files.deleteIfExists(jsonFile);
        assertEquals(0, new MessageManager(jsonFile).loadStoredMessages());
    }

    // ---------------- Part 2 behaviour (kept working) ----------------

    @Test
    public void messageLengthSuccessAndFailure() {
        assertEquals("Message ready to send.", manager.checkMessageLength("Hi Mike"));
        String tooLong = "a".repeat(255);
        assertEquals("Message exceeds 250 characters by 5, please reduce the size.",
                manager.checkMessageLength(tooLong));
    }

    @Test
    public void recipientNumberFormat() {
        Message good = new Message("1234567890", 0, "s", "+27718693002", "Hi", "Sent");
        Message bad = new Message("1234567890", 0, "s", "08575975889", "Hi", "Sent");
        assertEquals("Cell phone number successfully captured.", good.checkRecipientCell());
        assertEquals("Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.",
                bad.checkRecipientCell());
    }

    @Test
    public void messageHashIsCorrect() {
        Message m = new Message("0012345678", 0, "s", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", "Sent");
        assertEquals("00:0:HITONIGHT", m.createMessageHash());
    }

    @Test
    public void messageIDIsCreatedAndNoLongerThanTen() {
        String id = manager.generateMessageID();
        assertEquals(10, id.length());
        assertTrue(new Message(id, 0, "s", "+27718693002", "Hi", "Sent").checkMessageID());
    }

    @Test
    public void sendDisregardAndStoreMessages() {
        Message a = manager.createMessage("Developer", "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        Message b = manager.createMessage("Developer", "+27718693002", "Hi Keegan, did you receive the payment?");
        Message c = manager.createMessage("Developer", "+27718693002", "Stored for later");
        int sentBefore = manager.returnTotalMessages();

        assertEquals("Message successfully sent.", manager.sentMessage(a, 1));
        assertEquals("Press 0 to delete the message.", manager.sentMessage(b, 2));
        manager.disregardMessage(b);
        assertEquals("Message successfully stored.", manager.sentMessage(c, 3));

        assertEquals(sentBefore + 1, manager.returnTotalMessages());
        assertTrue(manager.getDisregardedMessages().contains(b));
    }
}
