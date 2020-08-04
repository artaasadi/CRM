package javaFx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import objects.User;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
//import org.controlsfx.control.textfield.*;

public class loginControl implements Initializable {

    @FXML private TextField loginSearchField;
    @FXML private TableView<User> loginSearchTable;
    @FXML private TableColumn<User, String> name;
    @FXML private TableColumn<User, String> familyName;
    @FXML private TableColumn<User, String> phoneNum;
    @FXML private ChoiceBox<String> loginKind;
    private final ObservableList<User> dataList = FXCollections.observableArrayList();
    private static HashMap<User, LocalDateTime> logedInUsers = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> loginKindList = FXCollections.observableArrayList();
        tableMaker();
    }

    @FXML
    public void loginRefresh() {
        tableMaker();
    }

    @FXML
    public void loginSubmitCode() throws ClassNotFoundException, SQLException {
        if (!logedInUsers.containsKey(loginSearchTable.getSelectionModel().getSelectedItem()) && !loginSearchTable.getSelectionModel().isEmpty()) {
            DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");
            DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime now = LocalDateTime.now();
            String t = date.format(now) + " - " + time.format(now);
            logedInUsers.put(loginSearchTable.getSelectionModel().getSelectedItem(), now);
            Class.forName("org.sqlite.JDBC");
            Connection localConn = DriverManager.getConnection("jdbc:sqlite:cost&income.db");
            Statement stat = localConn.createStatement();
            stat.executeUpdate("create table if not exists userLogin (phone, inTime, outTime, price);");
            PreparedStatement prep = localConn.prepareStatement("insert into userLogin values (?, ?, ?, ?);");
            prep.setString(1, loginSearchTable.getSelectionModel().getSelectedItem().getPhoneNum());
            prep.setString(2, t);
            prep.setString(3, "?");
            prep.setString(4, "20000");
            prep.addBatch();
            localConn.setAutoCommit(false);
            prep.executeBatch();
            localConn.setAutoCommit(true);
        }
    }

    private void tableMaker(){
        dataList.clear();
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        familyName.setCellValueFactory(new PropertyValueFactory<>("familyName"));
        phoneNum.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));

        //adding Users from database
        try {
            //add sqlite file
            Class.forName("org.sqlite.JDBC");
            Connection localConn = DriverManager.getConnection("jdbc:sqlite:main.db");
            Statement stat = localConn.createStatement();
            stat.executeUpdate("create table if not exists users (name, familyName, reference, phone, birthDay, imgUrl);");
            ResultSet rs = stat.executeQuery("select * from users;");
            //adding Users
            while (rs.next()){
                User emp = new User(rs.getString("name"), rs.getString("familyName"), rs.getString("reference")
                        ,rs.getString("phone"), rs.getString("birthDay"), rs.getString("imgUrl"));
                dataList.add(emp);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        // Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<User> filteredData = new FilteredList<>(dataList, b -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        loginSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                // If filter text is empty, display all persons.

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (user.getName().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
                    return true; // Filter matches first name.
                } else if (user.getFamilyName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches last name.
                }
                else if (user.getPhoneNum().indexOf(lowerCaseFilter)!=-1)
                    return true;
                else
                    return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<User> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        // 	  Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(loginSearchTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        loginSearchTable.setItems(sortedData);
    }

    public static HashMap<User, LocalDateTime> getLogedInUsers(){
        return logedInUsers;
    }

    public static void removeUser(User user) {
        logedInUsers.remove(user);
    }

}
