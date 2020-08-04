package javaFx.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import objects.Foods;
import objects.Orders;
import objects.User;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class profileControl {

    @FXML
    private Label profileName;
    @FXML
    private Label profileFName;
    @FXML
    private Label profileBDay;
    @FXML
    private Label profilePhoneNum;
    @FXML
    private Label profileReference;
    @FXML
    private ImageView profileImage;
    @FXML
    private ChoiceBox<String> usersList;
    @FXML
    private Label loginTimeLabel;
    @FXML
    private Label timePassedLabel;
    @FXML
    private Label profileTotalPrice;
    @FXML
    private Button logOut;
    @FXML
    private Button foodSubmitBtn;
    @FXML
    TableView<Orders> ordersTableView;
    @FXML
    TableColumn<Orders, String> orderNameColumn;
    @FXML
    TableColumn<Orders, String> orderPriceColumn;
    @FXML
    TableColumn<Orders, String> orderTimeColumn;

    private final ObservableList<String> dataList = FXCollections.observableArrayList();
    private boolean listActionAdded = false;
    private HashMap<User, LocalDateTime> logedInUsersTime;
    private HashMap<String, User> logedInUsersHash = new HashMap<>();
    private User currentUser;

    @FXML
    public void profileRefreshCode() {
        logedInUsersTime = loginControl.getLogedInUsers();
        if (!dataList.isEmpty()) {
            dataList.clear();
        }
        if (!logedInUsersHash.isEmpty()) {
            logedInUsersHash.clear();
        }
        for (User user : logedInUsersTime.keySet()) {
            dataList.add(new String(user.getName() + " - " + user.getFamilyName()));
            logedInUsersHash.put(user.getName() + " - " + user.getFamilyName(), user);
        }
        usersList.setItems(dataList);
        if (!listActionAdded) {
            usersList.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
                try {
                    currentUser = logedInUsersHash.get(newValue);
                    profileSetValue();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
            listActionAdded = true;
        }
    }

    @FXML
    public void logOutBtn() throws SQLException, ClassNotFoundException {
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentUserLoginTime = logedInUsersTime.get(currentUser);
        String currentUserTimeS = date.format(currentUserLoginTime) + " - " + time.format(currentUserLoginTime);
        String t = date.format(now) + " - " + time.format(now);
        long timeDifferenceHour = ChronoUnit.HOURS.between(logedInUsersTime.get(currentUser), LocalDateTime.now());
        long timeDifferenceMin = ChronoUnit.MINUTES.between(logedInUsersTime.get(currentUser), LocalDateTime.now());
        double timeDifference = (timeDifferenceHour * 60 + timeDifferenceMin);
        System.out.println(timeDifference);
        Class.forName("org.sqlite.JDBC");
        Connection localConn = DriverManager.getConnection("jdbc:sqlite:cost&income.db");
        Statement stat = localConn.createStatement();
        stat.executeUpdate("UPDATE userLogin SET outTime = '" + t + "' WHERE inTime = '" + currentUserTimeS + "';");
        if (timeDifference <= 60) {
            stat.executeUpdate("UPDATE userLogin SET price = '20000' WHERE inTime = '" + currentUserTimeS + "';");
        } else {
            stat.executeUpdate("UPDATE userLogin SET price = '40000' WHERE inTime = '" + currentUserTimeS + "';");
        }
        loginControl.removeUser(currentUser);
        profileRefreshCode();
        currentUser = null;
    }

    @FXML
    public void foodSubmitFunc() throws SQLException, ClassNotFoundException {
        //making stage
        Stage foodChoose = new Stage();
        AnchorPane foodChoosepane = new AnchorPane();
        foodChoosepane.setPrefSize(600, 380);

        //------creating tableView
        TableView<Foods> foodsTableView = new TableView<>();
        foodsTableView.setEditable(true);
        foodsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        foodsTableView.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        TableColumn<Foods, String> foodNameColumn = new TableColumn<>("نام محصول");
        TableColumn<Foods, String> foodPriceColumn = new TableColumn<>("قیمت");
        foodNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        foodPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        foodsTableView.getColumns().addAll(foodNameColumn, foodPriceColumn);
        foodsTableView.setPrefSize(450, 200);
        AnchorPane.setRightAnchor(foodsTableView, 75.0);
        AnchorPane.setTopAnchor(foodsTableView, 40.0);
        foodChoosepane.getChildren().add(foodsTableView);

        //creating or initializing database
        Class.forName("org.sqlite.JDBC");
        Connection localConn = DriverManager.getConnection("jdbc:sqlite:setting.db");
        Statement stat = localConn.createStatement();
        stat.executeUpdate("create table if not exists foods (name, price);");

        //add name text field
        TextField addNameTField = new TextField();
        addNameTField.setPromptText("نام محصول");
        addNameTField.setAlignment(Pos.CENTER_RIGHT);
        addNameTField.setPrefWidth(200);
        AnchorPane.setTopAnchor(addNameTField, 265.0);
        AnchorPane.setRightAnchor(addNameTField, 75.0);
        foodChoosepane.getChildren().add(addNameTField);

        //add price text field
        TextField addPriceTField = new TextField();
        addPriceTField.setPromptText("قیمت");
        addPriceTField.setAlignment(Pos.CENTER_RIGHT);
        addPriceTField.setPrefWidth(100);
        AnchorPane.setTopAnchor(addPriceTField, 265.0);
        AnchorPane.setRightAnchor(addPriceTField, 300.0);
        foodChoosepane.getChildren().add(addPriceTField);

        //add button
        Button addFoodBtn = new Button("افزودن به لیست");
        addFoodBtn.setPrefWidth(100);
        AnchorPane.setTopAnchor(addFoodBtn, 265.0);
        AnchorPane.setRightAnchor(addFoodBtn, 425.0);
        foodChoosepane.getChildren().add(addFoodBtn);


        //------creating search field
        TextField foodSearchTField = new TextField();
        foodSearchTField.setPromptText("جست و جو");
        foodSearchTField.setAlignment(Pos.CENTER_RIGHT);
        foodSearchTField.setPrefWidth(200);
        AnchorPane.setTopAnchor(foodSearchTField, 310.0);
        AnchorPane.setRightAnchor(foodSearchTField, 75.0);
        foodChoosepane.getChildren().add(foodSearchTField);

        try {
            updateFoodAddTable(foodSearchTField, foodsTableView);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        addFoodBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    PreparedStatement prep = localConn.prepareStatement("insert into foods values (?, ?);");
                    prep.setString(1, addNameTField.getText());
                    prep.setString(2, addPriceTField.getText());
                    prep.addBatch();
                    localConn.setAutoCommit(false);
                    prep.executeBatch();
                    localConn.setAutoCommit(true);
                    try {
                        updateFoodAddTable(foodSearchTField, foodsTableView);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        //submit button
        Button submitFoodBtn = new Button("ثبت سفارش");
        submitFoodBtn.setPrefWidth(100);
        AnchorPane.setTopAnchor(submitFoodBtn, 310.0);
        AnchorPane.setRightAnchor(submitFoodBtn, 425.0);
        foodChoosepane.getChildren().add(submitFoodBtn);

        submitFoodBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    addOrder(new Foods(foodsTableView.getSelectionModel().getSelectedItem().getName(), foodsTableView.getSelectionModel().getSelectedItem().getPrice()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        Scene costAddScene = new Scene(foodChoosepane);
        foodChoose.setScene(costAddScene);
        foodChoose.show();
    }

    private void updateFoodAddTable(TextField textField, TableView<Foods> tableView) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        Connection localConn = DriverManager.getConnection("jdbc:sqlite:setting.db");
        Statement stat = localConn.createStatement();

        //finding data
        ObservableList<Foods> foodsList = FXCollections.observableArrayList();
        ResultSet rs = stat.executeQuery("select * from foods;");
        while (rs.next()) {
            foodsList.add(new Foods(rs.getString("name"), Integer.parseInt(rs.getString("price"))));
        }
        FilteredList<Foods> filteredData = new FilteredList<>(foodsList, b -> true);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(food -> {
                // If filter text is empty, display all persons.

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (food.getName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches first name.
                } else
                    return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Foods> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        // 	  Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tableView.setItems(sortedData);
    }

    private void profileSetValue() throws FileNotFoundException {
        if (currentUser != null) {
            logOut.setDisable(false);
            foodSubmitBtn.setDisable(false);
            profileName.setText(currentUser.getName());
            profileFName.setText(currentUser.getFamilyName());
            profilePhoneNum.setText(currentUser.getPhoneNum());
            profileReference.setText(currentUser.getReference());
            Image img = new Image(new FileInputStream(currentUser.getImgUrl()));
            profileBDay.setText(currentUser.getBirthDay());
            profileImage.setImage(img);
            DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");
            DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime currentUserLoginTime = logedInUsersTime.get(currentUser);
            String currentUserTimeS = date.format(currentUserLoginTime) + " - " + time.format(currentUserLoginTime);
            loginTimeLabel.setText(currentUserTimeS);
            new updateTime().start();
            updateOrdersTable();
        } else {
            logOut.setDisable(true);
            foodSubmitBtn.setDisable(true);
            profileName.setText("");
            profileFName.setText("");
            profilePhoneNum.setText("");
            profileReference.setText("");
            profileBDay.setText("");
            profileImage.setImage(null);
            loginTimeLabel.setText("");
            timePassedLabel.setText("");
            profileTotalPrice.setText("");
            ordersTableView.setItems(null);
        }
    }

    private void addOrder(Foods foods) throws ClassNotFoundException, SQLException {

        Class.forName("org.sqlite.JDBC");
        Connection localConn = DriverManager.getConnection("jdbc:sqlite:cost&income.db");
        Statement stat = localConn.createStatement();
        stat.executeUpdate("create table if not exists usersOrders (phone, name, price, time, date);");
        PreparedStatement prep = localConn.prepareStatement("insert into usersOrders values (?, ?, ?, ?, ?);");
        prep.setString(1, currentUser.getPhoneNum());
        prep.setString(2, foods.getName());
        prep.setString(3, String.valueOf(foods.getPrice()));
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String t = time.format(now);
        String d = date.format(now);
        prep.setString(4, t);
        prep.setString(5, d);
        prep.addBatch();
        localConn.setAutoCommit(false);
        prep.executeBatch();
        localConn.setAutoCommit(true);
        currentUser.addOrder(new Orders(foods, t, d));
        ResultSet rs = stat.executeQuery("select * from usersOrders;");
        while (rs.next()){
            System.out.println(rs.getString("name") + " -- " + rs.getString("phone") + " -- " + rs.getString("time"));
        }
        System.out.println(currentUser.getOrders().size());
        updateOrdersTable();
    }

    private void updateOrdersTable() {

        ObservableList<Orders> userOrdersList = FXCollections.observableArrayList();
        for (int i = 0; i < currentUser.getOrders().size(); i++) {
            userOrdersList.add(currentUser.getOrders().get(i));

        }System.out.println(userOrdersList.size());
        orderNameColumn.setCellValueFactory(new PropertyValueFactory<Orders, String>("foodName"));
        orderPriceColumn.setCellValueFactory(new PropertyValueFactory<Orders, String>("foodPrice"));
        orderTimeColumn.setCellValueFactory(new PropertyValueFactory<Orders, String>("time"));
        ordersTableView.setItems(userOrdersList);
    }

    private void calculateTotalPrice(){

    }

    class updateTime extends Thread {
        @Override
        public void run() {

            while (true) {
                Platform.runLater(() -> {
                    if (currentUser != null) {
                        long timeDifferenceHour = ChronoUnit.HOURS.between(logedInUsersTime.get(currentUser), LocalDateTime.now());
                        long timeDifferenceMin = ChronoUnit.MINUTES.between(logedInUsersTime.get(currentUser), LocalDateTime.now());
                        long timeDifferenceSec = ChronoUnit.SECONDS.between(logedInUsersTime.get(currentUser), LocalDateTime.now());
                        String t = "" + timeDifferenceHour + ":" + timeDifferenceMin + ":" + timeDifferenceSec;
                        timePassedLabel.setText(t);

                        //price set
                        double timeDifference = (timeDifferenceHour * 60 + timeDifferenceMin);
                        if (timeDifference <= 60) {
                            currentUser.setTotalPrice(20000);
                            for (int i = 0; i < currentUser.getOrders().size(); i++) {
                                currentUser.setTotalPrice(currentUser.getTotalPrice() + currentUser.getOrders().get(i).getFood().getPrice());
                            }
                        } else {
                            currentUser.setTotalPrice(40000);
                            for (int i = 0; i < currentUser.getOrders().size(); i++) {
                                currentUser.setTotalPrice(currentUser.getTotalPrice() + currentUser.getOrders().get(i).getFood().getPrice());
                            }
                        }
                        profileTotalPrice.setText(currentUser.getTotalPrice() + " تومان");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

