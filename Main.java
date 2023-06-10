package us.senal;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        String enteredUsername = "username";
        String enteredPassword = "password";

        // Verify user credentials and retrieve the role of the user, if valid
        try (ResultSet userInfo = Access.userLogin(enteredUsername, enteredPassword)) {

            if (userInfo.next()) {
                System.out.println("Checking Role Access...");
                //System.out.println("Valid User Info? " + userInfo.next());

                String role = userInfo.getString("role");
                System.out.println("Role: " + role);

                // Connect to Admin, Instructor, or Student module depending on verified user's role
                Access.roleAccess(enteredUsername, role);
            }
        }
    }
}