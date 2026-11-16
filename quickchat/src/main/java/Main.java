import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/** QuickChat - console menu for Parts 1, 2 and 3. */
public class Main {

    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        Login login = register();
        if (!doLogin(login)) {
            return;
        }

        System.out.println("\nWelcome to QuickChat.");

        MessageManager manager = new MessageManager(Paths.get("storedMessages.json"));
        try {
            int loaded = manager.loadStoredMessages();
            if (loaded > 0) {
                System.out.println(loaded + " stored message(s) loaded from storedMessages.json");
            }
        } catch (IOException e) {
            System.out.println("Could not load stored messages: " + e.getMessage());
        }

        int numMessages = readInt("How many messages do you want to enter? ", 0, Integer.MAX_VALUE);
        int entered = 0;

        boolean running = true;
        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            int option = readInt("Choose an option: ", 1, 4);

            switch (option) {
                case 1:
                    if (entered >= numMessages) {
                        System.out.println("You have already entered all " + numMessages + " messages.");
                    } else {
                        entered = sendMessages(manager, login, numMessages, entered);
                        System.out.println("\nTotal messages sent: " + manager.returnTotalMessages());
                    }
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    running = false;
                    break;
                case 4:
                    storedMessagesMenu(manager);
                    break;
            }
        }
        System.out.println("Goodbye.");
    }

    // ------------------------------------------------------------------
    // Part 1
    // ------------------------------------------------------------------

    private static Login register() {
        System.out.println("=== Register ===");
        System.out.print("First name: ");
        String first = in.nextLine().trim();
        System.out.print("Last name: ");
        String last = in.nextLine().trim();
        Login login = new Login(first, last);

        while (true) {
            System.out.print("Username: ");
            String username = in.nextLine().trim();
            System.out.print("Password: ");
            String password = in.nextLine();
            System.out.print("Cell phone number (e.g. +27838968976): ");
            String cell = in.nextLine().trim();

            String result = login.registerUser(username, password, cell);
            System.out.println(result);
            if (result.endsWith("successfully added.")) {
                return login;
            }
        }
    }

    private static boolean doLogin(Login login) {
        System.out.println("\n=== Login ===");
        for (int attempt = 0; attempt < 3; attempt++) {
            System.out.print("Username: ");
            String username = in.nextLine().trim();
            System.out.print("Password: ");
            String password = in.nextLine();
            boolean ok = login.loginUser(username, password);
            System.out.println(login.returnLoginStatus(ok));
            if (ok) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Part 2 - send / disregard / store
    // ------------------------------------------------------------------

    private static int sendMessages(MessageManager manager, Login login, int numMessages, int entered) {
        while (entered < numMessages) {
            System.out.println("\n--- Message " + (entered + 1) + " of " + numMessages + " ---");

            String recipient;
            while (true) {
                System.out.print("Recipient cell number (with international code): ");
                recipient = in.nextLine().trim();
                Message check = new Message("0000000000", 0, "", recipient, "x", "New");
                String result = check.checkRecipientCell();
                System.out.println(result);
                if (result.startsWith("Cell phone number successfully")) {
                    break;
                }
            }

            String text;
            while (true) {
                System.out.print("Message (max 250 characters): ");
                text = in.nextLine().trim();
                if (text.isEmpty()) {
                    System.out.println("Please enter a message.");
                    continue;
                }
                String result = manager.checkMessageLength(text);
                System.out.println(result);
                if (result.equals("Message ready to send.")) {
                    break;
                }
            }

            Message message = manager.createMessage(login.getUsername(), recipient, text);

            System.out.println("\n1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            int choice = readInt("Choose an option: ", 1, 3);

            String result = manager.sentMessage(message, choice);
            System.out.println(result);

            if (choice == 2) {
                // Keep asking until the user presses 0 to delete the message.
                while (readInt("", 0, 9) != 0) {
                    System.out.println("Press 0 to delete the message.");
                }
                manager.disregardMessage(message);
                System.out.println("Message disregarded.");
            } else if (choice == 1) {
                System.out.println("\n" + message.toDetails());
            }
            entered++;
        }
        return entered;
    }

    // ------------------------------------------------------------------
    // Part 3 - Stored Messages menu
    // ------------------------------------------------------------------

    private static void storedMessagesMenu(MessageManager manager) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("1) Display the sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages for a recipient");
            System.out.println("5) Delete a message using its hash");
            System.out.println("6) Display a report of all the stored messages");
            System.out.println("7) Display a report of all the sent messages");
            System.out.println("8) Back");
            int option = readInt("Choose an option: ", 1, 8);

            switch (option) {
                case 1:
                    System.out.println(manager.displaySenderAndRecipient());
                    break;
                case 2:
                    Message longest = manager.longestStoredMessage();
                    System.out.println(longest == null
                            ? "There are no stored messages."
                            : "Longest stored message: " + longest.getText());
                    break;
                case 3:
                    System.out.print("Enter the message ID: ");
                    Message found = manager.searchByMessageID(in.nextLine().trim());
                    System.out.println(found == null
                            ? "No message with that ID was found."
                            : "Recipient: " + found.getRecipient() + "\nMessage: " + found.getText());
                    break;
                case 4:
                    System.out.print("Enter the recipient number: ");
                    List<Message> list = manager.searchByRecipient(in.nextLine().trim());
                    if (list.isEmpty()) {
                        System.out.println("No messages found for that recipient.");
                    }
                    for (Message m : list) {
                        System.out.println("\"" + m.getText() + "\"");
                    }
                    break;
                case 5:
                    System.out.print("Enter the message hash: ");
                    System.out.println(manager.deleteByHash(in.nextLine().trim().toUpperCase()));
                    break;
                case 6:
                    System.out.println(manager.displayStoredReport());
                    break;
                case 7:
                    System.out.println(manager.displayReport());
                    break;
                case 8:
                    back = true;
                    break;
            }
        }
    }

    // ------------------------------------------------------------------

    /** Keeps asking until the user enters a whole number between min and max. */
    private static int readInt(String prompt, int min, int max) {
        while (true) {
            if (!prompt.isEmpty()) {
                System.out.print(prompt);
            }
            String line = in.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.println("Please enter a number between " + min + " and "
                    + (max == Integer.MAX_VALUE ? "a larger number" : String.valueOf(max)) + ".");
        }
    }
}
