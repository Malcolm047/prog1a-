package poe1;
import java.util.Scanner;
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
    }
    
    
    
}
