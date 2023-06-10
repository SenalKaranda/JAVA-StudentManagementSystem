package us.senal.instructor;

import us.senal.Access;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Instructor {

    public static int getInstructorID(String username) throws SQLException {
        Connection connection = Access.getConnection();
        String query = "SELECT idinstructors FROM instructors WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("idinstructors");
        }
        else {
            return -1;
        }
    }
    public static int getClassID(String className) throws SQLException {
        Connection connection = Access.getConnection();
        String query = "SELECT idclasses FROM classes WHERE classname = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, className);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("idclasses");
        }
        else {
            return -1;
        }
    }
    public static ResultSet[] getInstructorClasses(int instructorid) throws SQLException {
        Connection connection = Access.getConnection();
        // Retrieve the relevant class tables for the student
        String query = "SELECT idclasses FROM classes WHERE instructorid = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, instructorid);
        ResultSet tableResultSet = statement.executeQuery();

        ArrayList<ResultSet> resultSetList = new ArrayList<>();

        while (tableResultSet.next()) {
            String tableName = "class_" + tableResultSet.getInt("idclasses");
            //System.out.println(tableName);

            // Construct the query to retrieve class information for the instructor from each table
            String classQuery = "SELECT * FROM " + tableName;
            PreparedStatement classStatement = connection.prepareStatement(classQuery);
            ResultSet classResultSet = classStatement.executeQuery();

            resultSetList.add(classResultSet);
        }

        // Convert the list of result sets to an array
        ResultSet[] resultSetArray = new ResultSet[resultSetList.size()];
        resultSetList.toArray(resultSetArray);
        return resultSetArray;
    }
    public static void OpenInstructorPanel(int id) throws SQLException {
        ResultSet[] instructorResults = getInstructorClasses(id);

        System.out.println("Opened Instructor Panel");
        JFrame frame = new JFrame("Instructor Panel: ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 750);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.setContentPane(contentPanel);

        for (ResultSet instructorResult : instructorResults) {
            if (instructorResult.next()) {
                JPanel classPanel = new JPanel();
                classPanel.setLayout(new BoxLayout(classPanel, BoxLayout.Y_AXIS));
                classPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                classPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(classPanel);

                JPanel classTitlePanel = new JPanel();
                classTitlePanel.setLayout(new BoxLayout(classTitlePanel, BoxLayout.X_AXIS));
                classTitlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                classTitlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                classPanel.add(classTitlePanel);

                JLabel classLabel = new JLabel();
                classLabel.setText(instructorResult.getString("classname"));
                classLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
                Font originalFont = classLabel.getFont();
                Font resizedFont = originalFont.deriveFont(16f);
                classLabel.setFont(resizedFont);
                classTitlePanel.add(classLabel);

                JLabel separator = new JLabel();
                separator.setText("     ");
                classTitlePanel.add(separator);

                JLabel classIDLabel = new JLabel();
                classIDLabel.setText("ID: " + Integer.toString(getClassID(instructorResult.getString("classname"))));
                classIDLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
                originalFont = classIDLabel.getFont();
                resizedFont = originalFont.deriveFont(16f);
                classIDLabel.setFont(resizedFont);
                classTitlePanel.add(classIDLabel);

                JPanel assignmentPanel = new JPanel(new FlowLayout());
                assignmentPanel.setLayout(new BoxLayout(assignmentPanel, BoxLayout.X_AXIS));
                assignmentPanel.setBorder(new EmptyBorder(10, 25, 10, 25));
                assignmentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(assignmentPanel);

                JLabel assignmentLabel = new JLabel();
                assignmentLabel.setText(instructorResult.getString("assignment"));
                assignmentPanel.add(assignmentLabel);

                separator = new JLabel();
                separator.setText("     ");
                assignmentPanel.add(separator);

                JLabel studentLabel = new JLabel();
                studentLabel.setText(instructorResult.getString("studentname"));
                assignmentPanel.add(studentLabel);

                separator = new JLabel();
                separator.setText("     ");
                assignmentPanel.add(separator);

                JTextField gradeLabel = new JTextField();
                gradeLabel.setName(instructorResult.getString("assignment") + "," + instructorResult.getString("studentname"));
                gradeLabel.setText(instructorResult.getString("grade"));
                gradeLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                assignmentPanel.add(gradeLabel);

                while (instructorResult.next()) {
                    assignmentPanel = new JPanel(new FlowLayout());
                    assignmentPanel.setLayout(new BoxLayout(assignmentPanel, BoxLayout.X_AXIS));
                    assignmentPanel.setBorder(new EmptyBorder(10, 25, 10, 25));
                    assignmentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    assignmentPanel.setName("assignmentPanel");
                    contentPanel.add(assignmentPanel);

                    assignmentLabel = new JLabel();
                    assignmentLabel.setText(instructorResult.getString("assignment"));
                    assignmentPanel.add(assignmentLabel);

                    separator = new JLabel();
                    separator.setText("     ");
                    assignmentPanel.add(separator);

                    studentLabel = new JLabel();
                    studentLabel.setText(instructorResult.getString("studentname"));
                    assignmentPanel.add(studentLabel);

                    separator = new JLabel();
                    separator.setText("     ");
                    assignmentPanel.add(separator);

                    gradeLabel = new JTextField();
                    gradeLabel.setName(instructorResult.getString("assignment") + "," + instructorResult.getString("studentname"));
                    gradeLabel.setText(instructorResult.getString("grade"));
                    gradeLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                    assignmentPanel.add(gradeLabel);
                }

                JButton submitButton = new JButton();
                submitButton.setText("Submit");
                submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                submitButton.setBorder(new EmptyBorder(10, 10, 10, 10));
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            Access.updateGrades(id, contentPanel);
                            System.out.println("Finished attempting to update grades.");
                        } catch (SQLException ex) {
                            System.out.println("Failed to update grades.");
                            throw new RuntimeException(ex);
                        }
                    }
                });
                contentPanel.add(submitButton);
            }

        }

        frame.setVisible(true);
    }

}
