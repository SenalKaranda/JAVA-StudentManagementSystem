package us.senal.student;

import us.senal.Access;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Student {

    public static int getStudentID(String username) throws SQLException {
        Connection connection = Access.getConnection();
        String query = "SELECT idstudents FROM students WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("idstudents");
        }
        else {
            return -1;
        }
    }
    public static ResultSet[] getStudentClasses(int studentid) throws SQLException {
        Connection connection = Access.getConnection();
        // Retrieve the relevant class tables for the student
        String query = "SELECT idclasses FROM classes";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet tableResultSet = statement.executeQuery();

        ArrayList<ResultSet> resultSetList = new ArrayList<>();

        while (tableResultSet.next()) {
            String tableName = "class_" + tableResultSet.getInt("idclasses");
            //System.out.println(tableName);

            // Construct the query to retrieve class information for the student from each table
            String classQuery = "SELECT * FROM " + tableName + " WHERE studentid = ?";
            PreparedStatement classStatement = connection.prepareStatement(classQuery);
            classStatement.setInt(1, studentid);
            ResultSet classResultSet = classStatement.executeQuery();

            resultSetList.add(classResultSet);
        }

        // Convert the list of result sets to an array
        ResultSet[] resultSetArray = new ResultSet[resultSetList.size()];
        resultSetList.toArray(resultSetArray);
        return resultSetArray;
    }
    public static void OpenStudentPanel(int id) throws SQLException {
        ResultSet[] studentResults = getStudentClasses(id);
        System.out.println("Opened Student Panel");
        JFrame frame = new JFrame("Student Panel: ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 750);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        //contentPanel.setBackground(Color.LIGHT_GRAY);
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.setContentPane(contentPanel);

        for (ResultSet studentResult : studentResults) {
            if (studentResult.next()) {
                JPanel classPanel = new JPanel();
                classPanel.setLayout(new BoxLayout(classPanel, BoxLayout.Y_AXIS));
                classPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                //classPanel.setBackground(Color.CYAN);
                classPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(classPanel);

                JLabel classLabel = new JLabel();
                classLabel.setText(studentResult.getString("classname"));
                classLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
                Font originalFont = classLabel.getFont();
                Font resizedFont = originalFont.deriveFont(16f); // Change the font size as desired
                classLabel.setFont(resizedFont);
                classPanel.add(classLabel);


                JPanel assignmentPanel = new JPanel();
                assignmentPanel.setLayout(new BoxLayout(assignmentPanel, BoxLayout.X_AXIS));
                assignmentPanel.setBorder(new EmptyBorder(10, 25, 10, 25));
                //assignmentPanel.setBackground(Color.ORANGE);
                assignmentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(assignmentPanel);

                JLabel assignmentLabel = new JLabel();
                assignmentLabel.setText(studentResult.getString("assignment"));
                assignmentPanel.add(assignmentLabel);

                JLabel separator = new JLabel();
                separator.setText("     ");
                assignmentPanel.add(separator);

                JLabel gradeLabel = new JLabel();
                gradeLabel.setText(studentResult.getString("grade"));
                assignmentPanel.add(gradeLabel);

                while (studentResult.next()) {
                    assignmentPanel = new JPanel();
                    assignmentPanel.setLayout(new BoxLayout(assignmentPanel, BoxLayout.X_AXIS));
                    assignmentPanel.setBorder(new EmptyBorder(10, 25, 10, 25));
                    //assignmentPanel.setBackground(Color.ORANGE);
                    assignmentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    contentPanel.add(assignmentPanel);

                    assignmentLabel = new JLabel();
                    assignmentLabel.setText(studentResult.getString("assignment"));
                    assignmentPanel.add(assignmentLabel);

                    separator = new JLabel();
                    separator.setText("     ");
                    assignmentPanel.add(separator);

                    gradeLabel = new JLabel();
                    gradeLabel.setText(studentResult.getString("grade"));
                    assignmentPanel.add(gradeLabel);
                }
            }

        }

        frame.setVisible(true);
    }
}


