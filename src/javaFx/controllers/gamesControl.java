package javaFx.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import objects.Group;
import objects.User;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class gamesControl implements Initializable {
    @FXML private ChoiceBox<String> groupChoose;
    @FXML private ChoiceBox<Integer> groupNum;
    @FXML private Button startGame;
    @FXML private Button stopGame;
    @FXML private Label gameCph;
    @FXML private Label gameTimePassed;
    @FXML private Label gameTotalPrice;
    @FXML private TableView<User> groupPlayersTable;
    @FXML private TableColumn<User, String> playerPhoneNum;
    @FXML private TableColumn<User, String> playerFamilyName;
    @FXML private TableColumn<User, String> playerName;

    private final ObservableList<String> groupsNameList = FXCollections.observableArrayList();
    private ArrayList<Group> groups;
    private HashMap<String, Group> groupsHash = new HashMap<>();
    private HashMap<String, Integer> groupCount = new HashMap<>();
    private Group selectedGroup;

    @FXML
    public void groupAddUser(){
        //make stage
        Stage userAdd = new Stage();
        BorderPane userAddPane = new BorderPane();
            //making table
            TableView<User> usersTable = new TableView<>();
            usersTable.setEditable(true);
            usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            TableColumn<User, String> name = new TableColumn<>("نام");
            TableColumn<User, String> familyName = new TableColumn<>("نام خانوادگی");
            TableColumn<User, String> phoneNum = new TableColumn<>("شماره");
            name.setCellValueFactory(new PropertyValueFactory<>("name"));
            familyName.setCellValueFactory(new PropertyValueFactory<>("familyName"));
            phoneNum.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
            usersTable.getColumns().addAll(name, familyName, phoneNum);
        //get data
        ObservableList<User> dataList = FXCollections.observableArrayList();
        dataList.addAll(loginControl.getLogedInUsers().keySet());
        makeUsersTable(usersTable, dataList);
            /*
            this button is going to
            add selected user from the table
            to the group players
            and updates the list
             */
            //making button
            Button addButton = new Button("افزودن");
            //adding action
            addButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (!selectedGroup.getgPlayers().contains(usersTable.getSelectionModel().getSelectedItem()))
                        selectedGroup.addPlayer(usersTable.getSelectionModel().getSelectedItem());
                    ObservableList<User> data = FXCollections.observableArrayList();
                    data.addAll(selectedGroup.getgPlayers());
                    makeUsersTable(groupPlayersTable, data);
                }
            });
        //show stage
        userAddPane.setCenter(usersTable);
        userAddPane.setBottom(addButton);
        Scene userAddScene = new Scene(userAddPane);
        userAdd.setScene(userAddScene);
        userAdd.show();
    }

    @FXML
    public void groupRemoveUser(){
        selectedGroup.removePlayer(groupPlayersTable.getSelectionModel().getSelectedItem());
        ObservableList<User> data = FXCollections.observableArrayList();
        data.addAll(selectedGroup.getgPlayers());
        makeUsersTable(groupPlayersTable, data);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        groups = new ArrayList<>();

        //initializing columns
        playerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        playerFamilyName.setCellValueFactory(new PropertyValueFactory<>("familyName"));
        playerPhoneNum.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));

        //add sqlite file
        try {

            //add sqlite file
            Class.forName("org.sqlite.JDBC");
            Connection localConn = DriverManager.getConnection("jdbc:sqlite:setting.db");
            Statement stat = localConn.createStatement();
            stat.executeUpdate("create table if not exists gp (name, cph, num, games);");
            ResultSet rs = stat.executeQuery("select * from gp;");

            //clearing data
            if (!groupsNameList.isEmpty())
                groupsNameList.clear();
            if (!groupsHash.isEmpty())
                groupsHash.clear();
            if (!groupCount.isEmpty())
                groupCount.clear();
            //adding Group
            while (rs.next()){
                groupsNameList.add(rs.getString("name"));
                groupCount.put(rs.getString("name"), Integer.parseInt(rs.getString("num")));
                for (int i = 1; i <= Integer.parseInt(rs.getString("num")); i++) {
                    Group gp = new Group(rs.getString("name"), Float.parseFloat(rs.getString("cph")),
                            i, rs.getString("games").split(","));
                    groups.add(gp);
                }
            }
            for (Group group : groups) {
                groupsHash.put(group.getName() + "_" + group.getNumber(), group);
            }

            //setting choose list
            groupChoose.setItems(groupsNameList);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        //groupsListAction
        groupChoose.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            setGroupName(newValue);
        });
        startGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                selectedGroup.setRunning(true);
                stopGame.setDisable(false);
                startGame.setDisable(true);
                selectedGroup.startTime();
                new updateTime().start();
            }
        });

        stopGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                selectedGroup.setRunning(false);
                stopGame.setDisable(true);
                startGame.setDisable(false);
            }
        });
    }

    private void setGroupName(String name){
        ObservableList<Integer> groupsNumList = FXCollections.observableArrayList();
        for (int i = 1; i <= groupCount.get(name); i++) {
            groupsNumList.add(i);
        }
        groupNum.setItems(groupsNumList);
        groupNum.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) ->
        {
            setGroupNum(name, newValue);
        });
        setGroupNum(name, 1);
        groupNum.setValue(1);
    }

    private void setGroupNum(String name, int num){
        setSelectedGroup(name + "_" + num);
    }

    private void setSelectedGroup(String id){
        Group gp = groupsHash.get(id);
        selectedGroup = gp;
        if (gp.isRunning()){
            stopGame.setDisable(false);
            startGame.setDisable(true);
        } else {
            stopGame.setDisable(true);
            startGame.setDisable(false);
        }
        gameCph.setText(String.valueOf(selectedGroup.getCostPerHour()) + " تومان");
        ObservableList<User> dataList = FXCollections.observableArrayList();
        dataList.addAll(selectedGroup.getgPlayers());
        makeUsersTable(groupPlayersTable, dataList);
    }

    class updateTime extends Thread{
        @Override
        public void run() {

            while (true){
                Platform.runLater(() -> {
                    gameTimePassed.setText(selectedGroup.getMinutesPassed()/60 + ":" + selectedGroup.getMinutesPassed()%60);
                    gameTotalPrice.setText(String.valueOf(selectedGroup.getgTotalPrice()));
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void makeUsersTable(TableView tableView, ObservableList list){
        FilteredList<User> filteredData = new FilteredList<>(list, b -> true);
        SortedList<User> sortedData = new SortedList<>(filteredData);
        tableView.setItems(sortedData);
    }

}
