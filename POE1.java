package poe1;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;
public class POE1 {
    public static void main(String[] args) {
        // TODO code application logic here
        //Scanner object
        Scanner Angels = new Scanner (System.in);

        LOGIN Ye = new LOGIN ();
        //Prompting the user
        System.out.println("Welcome to Malcoms World");
        System.out.println("Enter your First Name:");
        String FirstName = Angels.nextLine();
        Ye.setFirstName(FirstName);

        System.out.println("Enter your Last Name:");
        String LastName = Angels.nextLine();
        Ye.setLastName(LastName);

        System.out.println("Enter your UserName:");
        String UserName = Angels.nextLine();
        Ye.setUserName(UserName);

        //Checking is the username meets the requirements
        if (Ye.CheckUsername(UserName)){
            System.out.println("Username succssefully entered");
        }else {
            System.out.println("Username is not succsseflly captured(Username must contain _, and must not be more than 5 letters)");
        }

        System.out.println("Enter your Password:");
        String Password = Angels.nextLine();
        Ye.setPassword(Password);

        //Checking if the password meets the requirements
        if (Ye.CheckPasswordComplesity(Password)){
            System.out.println("Password succssefully entered");
        }else{
            System.out.println("Password unsuccssefully entered(Password must contain Digits, Special characters, Uppercase and Length should be atleast 8 letters)");
        }

        System.out.println("Enter your Phonenumber:");
        String Phonenumber = Angels.nextLine();
        Ye.setPhoneNumber(Phonenumber);

        //Checking if the Phonenumber meets the requirements
        if (Ye.CheckPhonenumber(Phonenumber)){
            System.out.println("Phonenumber succssefully entered");
        }else{
            System.out.println("Phonenumber unsuccssefully entered (Phonenumber must start with +27, must contain numbers only and must have 12 numbers)");
        }


        //calling the register method to check if the user has registered

       String register = Ye.RegisterUser(UserName, Password);
        System.out.println("Message:  " + register);

        //Prompting the user to Login
        System.out.println("***************************\n Login \n***************************");

        System.out.println("Enter Username:");
        String LoginUsername = Angels.nextLine();

        System.out.println("Enter Password:");
        String LoginPassword = Angels.nextLine();

        boolean isLogin = Ye.LoginUser(LoginUsername, LoginPassword);
        String LoginMessage = Ye.ReturnLoginStatus(isLogin);

        if(isLogin){
            System.out.println(LoginMessage);
        }else{
            System.out.println("Login unsuccessful");
        }

        //part 2 starts only when the uuser has logged successfully
        //(this block used to sit outside main, which is why the file did not compile)
        if(isLogin){
            JOptionPane.showMessageDialog(null, "-----------------------------\nWelcome To QuickChat \n-----------------------------");

            //one MESSAGE object for the whole program so the Part 3 arrays are kept between menu choices
            MESSAGE message = new MESSAGE ();

            //PART 3: read the stored messages from the JSON file into the stored array
            try {
                int loaded = message.getManager().loadStoredMessages();
                if (loaded > 0) {
                    JOptionPane.showMessageDialog(null, loaded + " stored message(s) loaded from the JSON file.");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Could not load the stored messages: " + e.getMessage());
            }

            String sender = Ye.getFirstName() + " " + Ye.getLastName();

            //declaring
            int Menu=0;

            while(Menu !=3){
                //PART 3: option 4 was added
                Menu = Integer.parseInt(JOptionPane.showInputDialog("Please choose an option from 1-4: \n1:Send Messages \n2:Show recently sent Messages \n3:Exit Application \n4:Stored Messages"));

                switch(Menu){
                    case 1: {
                        int num_of_tasks = Integer.parseInt(JOptionPane.showInputDialog("How many messages do you want to send: "));

                        //declaring arrays to store variables
                        String [] recipient = new String [num_of_tasks];
                        String [] Message_Id = new String [num_of_tasks];
                        String [] UserMessages = new String [num_of_tasks];
                        String [] Message_Hash = new String [num_of_tasks];
                        String [] Message_Options = new String [num_of_tasks];

                        int CheckNum=0;
                        //for loop that will iterate based on the user input

                        for(int Messagecount =0; Messagecount <num_of_tasks ;Messagecount++){

                            do{
                            recipient [Messagecount] =JOptionPane.showInputDialog("Please enter the cell number:");
                            CheckNum=message.checkRecipientCell( recipient [Messagecount]);

                            if(CheckNum==0){
                                 JOptionPane.showMessageDialog(null, "Invalid recipient num " );
                            }
                            }while(CheckNum==0);
                            do{
                            UserMessages [Messagecount] = JOptionPane.showInputDialog("Please enter your message: ");
                            }while(!message.CheckMessageComplexity(UserMessages [Messagecount]));

                            Message_Id [Messagecount] = message.CreateRandomMessageID();
                            JOptionPane.showMessageDialog(null, "Message Id: \n"  + Message_Id[Messagecount]);


                            Message_Hash [Messagecount] = message.generateMessageHash(Message_Id[Messagecount], Messagecount, UserMessages[Messagecount]);
                             JOptionPane.showMessageDialog(null, "Message Hash for mesage \n" +   Message_Hash[Messagecount]);

                            Message_Options[Messagecount]= message.SendMessage(Message_Id[Messagecount],  UserMessages[Messagecount], Message_Hash [Messagecount],
                            recipient [Messagecount], Messagecount, sender);

                        }
                        //the total is now the number of messages that were actually sent
                        //(it used to add up the loop counter, which gave the wrong number)
                        JOptionPane.showMessageDialog(null, "The total number of messages sent " + message.getManager().returnTotalMessages());

                        break;
                    }
                    case 2:
                        JOptionPane.showMessageDialog(null, "Coming Soon.");
                        break;
                    case 4:
                        storedMessagesMenu(message);
                        break;
                }
            }
        }
    }

    //PART 3: the Stored Messages menu (main menu option 4)
    private static void storedMessagesMenu(MESSAGE message) {
        MessageManager manager = message.getManager();
        boolean back = false;

        while(!back){
            String input = JOptionPane.showInputDialog(
                    "Stored Messages - choose an option from 1-8: \n"
                    + "1:Display the sender and recipient of all stored messages \n"
                    + "2:Display the longest stored message \n"
                    + "3:Search for a message ID \n"
                    + "4:Search for all messages for a recipient \n"
                    + "5:Delete a message using the message hash \n"
                    + "6:Display a report of all the stored messages \n"
                    + "7:Display a report of all the sent messages \n"
                    + "8:Back");
            if(input == null){
                return;   //Cancel goes back to the main menu
            }

            int choice;
            try {
                choice = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a number from 1 to 8.");
                continue;
            }

            switch(choice){
                case 1:
                    JOptionPane.showMessageDialog(null, manager.displaySenderAndRecipient());
                    break;
                case 2: {
                    MessageRecord longest = manager.longestStoredMessage();
                    JOptionPane.showMessageDialog(null, longest == null
                            ? "There are no stored messages."
                            : "Longest stored message: \n" + longest.getText());
                    break;
                }
                case 3: {
                    String id = JOptionPane.showInputDialog("Enter the message ID:");
                    if(id == null){
                        break;
                    }
                    MessageRecord found = manager.searchByMessageID(id.trim());
                    JOptionPane.showMessageDialog(null, found == null
                            ? "No message with that ID was found."
                            : "Recipient: " + found.getRecipient() + "\nMessage: " + found.getText());
                    break;
                }
                case 4: {
                    String recipient = JOptionPane.showInputDialog("Enter the recipient's cell number:");
                    if(recipient == null){
                        break;
                    }
                    List<MessageRecord> list = manager.searchByRecipient(recipient.trim());
                    if(list.isEmpty()){
                        JOptionPane.showMessageDialog(null, "No messages found for that recipient.");
                    }else{
                        StringBuilder sb = new StringBuilder();
                        for(MessageRecord m : list){
                            sb.append("\"").append(m.getText()).append("\"\n");
                        }
                        JOptionPane.showMessageDialog(null, sb.toString());
                    }
                    break;
                }
                case 5: {
                    String hash = JOptionPane.showInputDialog("Enter the message hash:");
                    if(hash == null){
                        break;
                    }
                    JOptionPane.showMessageDialog(null, manager.deleteByHash(hash.trim().toUpperCase()));
                    break;
                }
                case 6:
                    JOptionPane.showMessageDialog(null, manager.displayStoredReport());
                    break;
                case 7:
                    JOptionPane.showMessageDialog(null, manager.displayReport());
                    break;
                case 8:
                    back = true;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Please enter a number from 1 to 8.");
            }
        }
    }
}
