/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poe1;
import java.nio.file.Paths;
import java.util.Random;
import javax.swing.JOptionPane;

public class MESSAGE {

    //PART 3: holds the arrays (sent, disregarded, stored, hashes, IDs) and the JSON file
    private final MessageManager manager = new MessageManager(Paths.get("StoreMessage.json"));

    public MessageManager getManager() {
        return manager;
    }

     //PART 3: messageNumber and sender were added so the message can be saved in the arrays
     public String SendMessage(String MessageId, String Message, String MessageHash, String recepient, int messageNumber, String sender) {
       //a value returning function

         String[] StoreOptions ={"Send Message", "Disregard Message","Store Message to send Later"};

        int Sendoptions=JOptionPane.showOptionDialog(
                null,
                "Please select an option", "Status options", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, StoreOptions, 0);

        //closing the dialog returns -1, which used to crash the program; it now counts as disregard
        if(Sendoptions<0){
            Sendoptions=1;
        }
        String task = StoreOptions[Sendoptions];
        //instead of using a switch i opt for a if statement
               if(Sendoptions==0){
                   //PART 3: add to the sent array
                   manager.addMessage(new MessageRecord(MessageId, messageNumber, sender, recepient, Message, "Sent", MessageHash));
                   JOptionPane.showMessageDialog(null, "Message is sent successfully");
                  String display = DisplayMessage(MessageId,Message,MessageHash,recepient);
                  JOptionPane.showMessageDialog(null, display);
               }else if(Sendoptions==1){
                    //PART 3: add to the disregarded array
                    manager.addMessage(new MessageRecord(MessageId, messageNumber, sender, recepient, Message, "Disregard", MessageHash));
                    JOptionPane.showMessageDialog(null, "Message is disregarded");
               }else if(Sendoptions==2){

                StoreUserMessages(new MessageRecord(MessageId, messageNumber, sender, recepient, Message, "Stored", MessageHash));
                JOptionPane.showMessageDialog(null, "Message is successfully stored");
               }

        return task;
    }

    //created a method that creates 10 random numbers
   public String CreateRandomMessageID() {
    Random rand = new Random();
    StringBuilder build = new StringBuilder();

    //using a for loop to generate 10 numbers
    for (int i = 0; i < 10; i++) {
        int digit = rand.nextInt(10); // Random digit from 0–9
        build.append(digit);
    }

    return build.toString();
}
   //a method that checks if the MessageId length is valid or not
   public boolean CheckMessageIDLength(String messageID) {
    boolean isValid = messageID.length() == 10;

    if (isValid) {
        JOptionPane.showMessageDialog(null, "Message ID is valid.\nMessage Id" + messageID);
    } else {
        JOptionPane.showMessageDialog(null, "Invalid Message ID. It must be exactly 10 digits.");
    }

    return isValid;
}
//created a method that generatesa message hash
public String generateMessageHash(String messageID, int messageNumber, String message) {
    // storing the First 2 characters of MessageID
    String prefix = messageID.substring(0, 2);

    // Split the message into words
    String[] words = message.trim().split("\\s+");
    //storing the first word into as the first element of an array
    //punctuation is removed so "tonight?" gives TONIGHT (e.g. 00:0:HITONIGHT)
    String firstWord = words[0].replaceAll("[^A-Za-z0-9]", "");
    String lastWord = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");

    // Build the hash
    String messageHash = (prefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();

    return messageHash;
}


    public boolean CheckMessageComplexity(String Message){
    boolean validate=false;

    //the brief allows messages of up to 250 characters (this was 50)
    if(Message.length()<=250){
       JOptionPane.showMessageDialog(null,"Message is successfully captured: ");
       validate =true;
    }else{

       JOptionPane.showMessageDialog(null,"Message is not successfully captured, please ensure that the message does not exceed 250 characters: ");
        validate =false;
    }

    return validate;
    }
   public String DisplayMessage(String messageId, String message, String messageHash, String recipient) {
    String display =
            "=========================\n" +
            "      Message Details    \n" +
            "=========================\n" +
            "Message ID     : " + messageId + "\n" +
            "Message Hash   : " + messageHash + "\n" +
            "Recipient No.  : " + recipient + "\n" +
            "User Message   : " + message + "\n" +
            "=========================";

    return display;
}



   //PART 3: adds the message to the stored array and saves the stored array to the JSON file
   //(before, this method only wrote an empty line to the file)
   public void StoreUserMessages(MessageRecord storedMessage) {
    manager.addMessage(storedMessage);
    JOptionPane.showMessageDialog(null, "Message stored successfully in file.");
}

    //method that validates the recipient number
   public int checkRecipientCell(String phone_number) {
    boolean isValid = phone_number != null
                      && phone_number.length() == 12
                      && phone_number.startsWith("+27")
                      && !phone_number.matches(".*[A-Za-z!@#$%^&*().].*");

    if (isValid) {
        return 1;
    } else {
        return 0;
    }
}
}
