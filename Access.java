package us.senal;
import us.senal.admin.Admin;
import us.senal.instructor.Instructor;
import us.senal.student.Student;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Access {
    private static final String DB_URL = "jdbc:mysql://ip:3306/student_management_system";
    private static final String DB_USERNAME = "username";
    private static final String DB_PASSWORD = "password";

    public static Connection getConnection() {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;

    }
    public static ResultSet userLogin(String username, String password) throws SQLException {
        Connection connection = getConnection();
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, password);

        return statement.executeQuery();
    }

    public static void roleAccess(String username, String role) throws SQLException {
        switch (role) {
            case "student" -> {
                int id = Student.getStudentID(username);
                Student.OpenStudentPanel(id);
            }
            case "instructor" -> {
                int id = Instructor.getInstructorID(username);
                Instructor.OpenInstructorPanel(id);
            }
            case "admin" -> Admin.OpenAdminPanel();
            default -> {
            }
        }
    }
    public static void updateGrades(int id, JPanel contentPanel) throws SQLException {
        ResultSet[] instructorResults = Instructor.getInstructorClasses(id);
        for (ResultSet instructorResult : instructorResults) {
            if (instructorResult.next()) {
                String className = instructorResult.getString("classname");
                int classId = Instructor.getClassID(instructorResult.getString("classname"));

                for (Component component : contentPanel.getComponents()) {
                    if (component instanceof JPanel assignmentPanel && component.getName() != null && component.getName().equals("assignmentPanel")) {
                        for (Component subComponent : assignmentPanel.getComponents()) {
                            if (subComponent instanceof JTextField gradeField) {
                                String[] assignmentAndName = gradeField.getName().split(",");
                                String assignment = assignmentAndName[0];
                                String grade = gradeField.getText();
                                String name = assignmentAndName[1];

                                String updateQuery = "UPDATE class_" + classId + " SET grade = ? WHERE assignment = ? AND studentname = ?";
                                PreparedStatement statement = getConnection().prepareStatement(updateQuery);
                                statement.setDouble(1, Double.parseDouble(grade));
                                statement.setString(2, assignment);
                                statement.setString(3, name);
                                statement.executeUpdate();

                                System.out.println("Attempted to update " + className + ", " + assignment + " with new grade of " + grade);
                            }
                        }
                    }
                }
            }
        }
    }
}
