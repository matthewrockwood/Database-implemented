package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Person;
import service.MyLogger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
public class DbConnectivityClass {
    final static String DB_NAME="CSC311_Matthew_Rockwood";
        MyLogger lg= new MyLogger();
        final static String SQL_SERVER_URL = "jdbc:mysql://rockwoodcsc311server.mysql.database.azure.com";//update this server name
        final static String DB_URL = "jdbc:mysql://rockwoodcsc311server.mysql.database.azure.com/"+DB_NAME;//update this database name
        final static String USERNAME = "rockwoodadmin";// update this username
        final static String PASSWORD = "Catinthehat19!";// update this password


        private final ObservableList<Person> data = FXCollections.observableArrayList();

        // Method to retrieve all data from the database and store it into an observable list to use in the GUI tableview.


        public  ObservableList<Person> getData() {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT * FROM users ";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (!resultSet.isBeforeFirst()) {
                    lg.makeLog("No data");
                }
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String department = resultSet.getString("department");
                    String major = resultSet.getString("major");
                    String email = resultSet.getString("email");
                    String imageURL = resultSet.getString("imageURL");
                    data.add(new Person(id, first_name, last_name, department, major, email, imageURL));
                }
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return data;
        }


    public boolean connectToDatabase() {
        boolean hasRegisteredUsers = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // First, connect to MYSQL server and create the database if not created
            try (Connection conn = DriverManager.getConnection(SQL_SERVER_URL, USERNAME, PASSWORD);
                 Statement statement = conn.createStatement()) {
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            }

            // Second, connect to the database and create the tables if not created
            try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 Statement statement = conn.createStatement()) {

                // Create 'users' table
                String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT(10) NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                        "first_name VARCHAR(200) NOT NULL," +
                        "last_name VARCHAR(200) NOT NULL," +
                        "department VARCHAR(200)," +
                        "major VARCHAR(200)," +
                        "email VARCHAR(200) NOT NULL UNIQUE," +
                        "imageURL VARCHAR(200))";
                statement.executeUpdate(createUsersTable);

                // Create 'registered_users' table
                String createRegisteredUsersTable = "CREATE TABLE IF NOT EXISTS registered_users (" +
                        "username VARCHAR(200) NOT NULL PRIMARY KEY," +
                        "password VARCHAR(200) NOT NULL," +
                        "email VARCHAR(200) NOT NULL UNIQUE)";
                statement.executeUpdate(createRegisteredUsersTable);

                // Check if there are any registered users in 'users'
                ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");
                if (resultSet.next()) {
                    int numUsers = resultSet.getInt(1);
                    if (numUsers > 0) {
                        hasRegisteredUsers = true;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasRegisteredUsers;
    }
    public void insertRegisteredUser(String username, String password, String email) {
        connectToDatabase();
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            // Check if the email already exists in the 'registered_users' table
            String checkEmailSql = "SELECT COUNT(*) FROM registered_users WHERE email = ?";
            PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSql);
            checkEmailStmt.setString(1, email);
            ResultSet resultSet = checkEmailStmt.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                // If email exists, log a message and return
                lg.makeLog("The email " + email + " is already registered.");
                return;
            }

            // If email doesn't exist, proceed with the insertion
            String sql = "INSERT INTO registered_users (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email);
            int row = preparedStatement.executeUpdate();
            if (row > 0) {
                lg.makeLog("A new registered user was inserted successfully.");
            }

            // After inserting into the database, write the username, password, and email to 'preference.txt'
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("preference.txt", true))) {
                writer.write("Username: " + username + "\n");
                writer.write("Password: " + password + "\n");
                writer.write("Email: " + email + "\n");
                writer.write("--------------------------\n");
            } catch (IOException e) {
                e.printStackTrace();
                lg.makeLog("Error writing to preference.txt");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void printRegisteredUsersTable() {
        connectToDatabase();
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM registered_users";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("Registered Users Table:");
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");

                System.out.println("Username: " + username + ", Password: " + password + ", Email: " + email);
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean login(String username, String password) {
        String query = "SELECT 1 FROM registered_users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Return true if a match is found, otherwise false
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs
        }
    }




    public void queryUserByLastName(String name) {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT * FROM users WHERE last_name = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, name);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String major = resultSet.getString("major");
                    String department = resultSet.getString("department");

                    lg.makeLog("ID: " + id + ", Name: " + first_name + " " + last_name + " "
                            + ", Major: " + major + ", Department: " + department);
                }
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void listAllUsers() {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT * FROM users ";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String department = resultSet.getString("department");
                    String major = resultSet.getString("major");
                    String email = resultSet.getString("email");

                    lg.makeLog("ID: " + id + ", Name: " + first_name + " " + last_name + " "
                            + ", Department: " + department + ", Major: " + major + ", Email: " + email);
                }

                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void insertUser(Person person) {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "INSERT INTO users (first_name, last_name, department, major, email, imageURL) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, person.getFirstName());
                preparedStatement.setString(2, person.getLastName());
                preparedStatement.setString(3, person.getDepartment());
                preparedStatement.setString(4, person.getMajor());
                preparedStatement.setString(5, person.getEmail());
                preparedStatement.setString(6, person.getImageURL());
                int row = preparedStatement.executeUpdate();
                if (row > 0) {
                    lg.makeLog("A new user was inserted successfully.");
                }
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void editUser(int id, Person p) {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "UPDATE users SET first_name=?, last_name=?, department=?, major=?, email=?, imageURL=? WHERE id=?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, p.getFirstName());
                preparedStatement.setString(2, p.getLastName());
                preparedStatement.setString(3, p.getDepartment());
                preparedStatement.setString(4, p.getMajor());
                preparedStatement.setString(5, p.getEmail());
                preparedStatement.setString(6, p.getImageURL());
                preparedStatement.setInt(7, id);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public void deleteRecord(Person person) {
            int id = person.getId();
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "DELETE FROM users WHERE id=?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        //Method to retrieve id from database where it is auto-incremented.
        public int retrieveId(Person p) {
            connectToDatabase();
            int id;
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT id FROM users WHERE email=?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, p.getEmail());

                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                id = resultSet.getInt("id");
                preparedStatement.close();
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            lg.makeLog(String.valueOf(id));
            return id;
        }
    }