package javaFx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import objects.Costs;
import objects.UserLogin;
import objects.UserOrder;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class costsListControl implements Initializable {

    //table
    @FXML
    private TableView<Costs> costsTable;
    @FXML
    private TableColumn<Costs, String> nameColumn;
    @FXML
    private TableColumn<Costs, String> numberColumn;
    @FXML
    private TableColumn<Costs, String> vahedColumn;
    @FXML
    private TableColumn<Costs, String> totalPriceColumn;
    @FXML
    private TableColumn<Costs, String> dateColumn;
    @FXML
    private TableColumn<Costs,String> timeColumn;

    @FXML
    private ChoiceBox<String> costsChoiceBox;
    @FXML Label totalPriceLable;

    //user login table
    @FXML
    private TableView<UserLogin> usersLoginTable;
    @FXML
    private TableColumn<UserLogin, String> userLoginNameColumn;
    @FXML
    private TableColumn<UserLogin, String> userLoginFamilyNameColumn;
    @FXML
    private TableColumn<UserLogin, String> userLoginPhoneColumn;
    @FXML
    private TableColumn<UserLogin, String> userLoginPriceColumn;
    @FXML
    private TableColumn<UserLogin, String> userLoginTimeColumn;
    @FXML
    private TableColumn<UserLogin, String> userLogoutTimeColumn;
    @FXML
    private Label totalLoginIncomLabel;

    //user order table
    @FXML
    private TableView<UserOrder> usersOrdersTable;
    @FXML
    private TableColumn<UserOrder, String> userOrderNameColumn;
    @FXML
    private TableColumn<UserOrder, String> userOrderFamilyNameColumn;
    @FXML
    private TableColumn<UserOrder, String> userOrderPhoneColumn;
    @FXML
    private TableColumn<UserOrder, String> userOrderProductNameColumn;
    @FXML
    private TableColumn<UserOrder, String> userOrderPriceColumn;
    @FXML
    private TableColumn<UserOrder, String> userOrderTimeColumn;
    @FXML
    private Label totalOrdersIncomeLabel;

    @FXML
    private Label totalIncomeLabel;

    private Connection localConn;
    private Statement stat;
    private int totalCostPrice = 0;
    private int totalLoginPrice = 0;
    private int totalOrderPrice = 0;
    private int totalIncomePrice = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> costsChoiceBoxList = FXCollections.observableArrayList();
        costsChoiceBoxList.add("روز");
        costsChoiceBoxList.add("ماه");
        costsChoiceBoxList.add("سال");
        costsChoiceBox.setItems(costsChoiceBoxList);
        costsChoiceBox.getSelectionModel().selectFirst();
        costsChoiceBox.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            try {
                updateCostsTable();
                updateLoginsTable();
                updateOrdersTable();
                totalIncomePrice = totalOrderPrice + totalLoginPrice;
                totalIncomeLabel.setText(String.valueOf(totalIncomePrice));
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        });
        try {
            Class.forName("org.sqlite.JDBC");
            localConn = DriverManager.getConnection("jdbc:sqlite:cost&income.db");
            stat = localConn.createStatement();
            stat.executeUpdate("create table if not exists costs (name, number, vahed, totalPrice, date, time);");
            stat.executeUpdate("create table if not exists userLogin (phone, inTime, outTime, price);");
            stat.executeUpdate("create table if not exists usersOrders (phone, name, price, time, date);");
            updateCostsTable();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        vahedColumn.setCellValueFactory(new PropertyValueFactory<>("vahed"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        userLoginNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userLoginFamilyNameColumn.setCellValueFactory(new PropertyValueFactory<>("familyName"));
        userLoginPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        userLoginPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        userLoginTimeColumn.setCellValueFactory(new PropertyValueFactory<>("logInTime"));
        userLogoutTimeColumn.setCellValueFactory(new PropertyValueFactory<>("logOutTime"));

        userOrderNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userOrderFamilyNameColumn.setCellValueFactory(new PropertyValueFactory<>("familyName"));
        userOrderPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        userOrderProductNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        userOrderPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        userOrderTimeColumn.setCellValueFactory(new PropertyValueFactory<>("orderTime"));

        try {
            updateCostsTable();
            updateLoginsTable();
            updateOrdersTable();
            totalIncomePrice = totalOrderPrice + totalLoginPrice;
            totalIncomeLabel.setText(String.valueOf(totalIncomePrice));
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        userLoginTimeColumn.setComparator(dateColumn.getComparator().reversed());
        userOrderTimeColumn.setComparator(dateColumn.getComparator().reversed());
        dateColumn.setComparator(dateColumn.getComparator().reversed());
        timeColumn.setComparator(timeColumn.getComparator().reversed());
    }

    @FXML
    public void addCostBtn() throws IOException {

        //making stage
        Stage costAdd = new Stage();
        AnchorPane costAddPane = new AnchorPane();
        costAddPane.setPrefSize(400, 300);

        //main label
        Label mainLabel = new Label("لطفا اطلاعات مورد نظر را وارد نمایید :");
        mainLabel.setFont(new Font(14));
        AnchorPane.setTopAnchor(mainLabel, 20.0);
        AnchorPane.setRightAnchor(mainLabel, 20.0);
        costAddPane.getChildren().add(mainLabel);

        //name label
        Label nameLabel = new Label("نام :");
        AnchorPane.setTopAnchor(nameLabel,64.0);
        AnchorPane.setRightAnchor(nameLabel, 20.0);
        costAddPane.getChildren().add(nameLabel);
        //name
        TextField costAddNameTField = new TextField();
        costAddNameTField.setPromptText("نام");
        AnchorPane.setTopAnchor(costAddNameTField, 60.0);
        AnchorPane.setRightAnchor(costAddNameTField, 100.0);
        costAddNameTField.setAlignment(Pos.CENTER_RIGHT);
        costAddPane.getChildren().add(costAddNameTField);

        //number label
        Label numberLabel = new Label("تعداد / مقدار :");
        AnchorPane.setTopAnchor(numberLabel,104.0);
        AnchorPane.setRightAnchor(numberLabel, 20.0);
        costAddPane.getChildren().add(numberLabel);
        //number
        TextField costNumberTField = new TextField();
        costNumberTField.setPromptText("تعداد / مقدار");
        AnchorPane.setTopAnchor(costNumberTField, 100.0);
        AnchorPane.setRightAnchor(costNumberTField, 100.0);
        costNumberTField.setAlignment(Pos.CENTER_RIGHT);
        costAddPane.getChildren().add(costNumberTField);

        //choice box
        ChoiceBox<String> costAddChoiceBox = new ChoiceBox<>();
        ObservableList<String> costAddList = FXCollections.observableArrayList();
        costAddList.add("عدد");
        costAddList.add("کیلو");
        costAddList.add("گرم");
        costAddList.add("لیتر");
        costAddChoiceBox.setItems(costAddList);
        AnchorPane.setTopAnchor(costAddChoiceBox, 100.0);
        AnchorPane.setRightAnchor(costAddChoiceBox, 290.0);
        costAddPane.getChildren().add(costAddChoiceBox);

        //price label
        Label priceLabel = new Label("قیمت :");
        AnchorPane.setTopAnchor(priceLabel,144.0);
        AnchorPane.setRightAnchor(priceLabel, 20.0);
        costAddPane.getChildren().add(priceLabel);
        //price
        TextField costPriceTField = new TextField();
        costPriceTField.setPromptText("قیمت");
        AnchorPane.setTopAnchor(costPriceTField, 140.0);
        AnchorPane.setRightAnchor(costPriceTField, 100.0);
        costPriceTField.setAlignment(Pos.CENTER_RIGHT);
        costAddPane.getChildren().add(costPriceTField);

        //warning label
        Label warningLabel = new Label("لطفا اطلاعات را تکمیل نمایید !");
        warningLabel.setTextFill(Color.web("#cf4c4c"));
        AnchorPane.setTopAnchor(warningLabel, 180.0);
        AnchorPane.setRightAnchor(warningLabel, 20.0);
        warningLabel.setVisible(false);
        costAddPane.getChildren().add(warningLabel);

        //submit btn
        Button submitBtn = new Button("ثبت");
        submitBtn.setFont(new Font(18));
        AnchorPane.setBottomAnchor(submitBtn, 45.0);
        AnchorPane.setRightAnchor(submitBtn, 173.0);
        costAddPane.getChildren().add(submitBtn);
        submitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (costAddNameTField.getText().trim().isEmpty() || costNumberTField.getText().trim().isEmpty() ||
                        costPriceTField.getText().trim().isEmpty() || costAddChoiceBox.getSelectionModel().selectedItemProperty().get().trim().isEmpty()) {
                    warningLabel.setVisible(true);
                } else {
                    DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");
                    DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    LocalDateTime now = LocalDateTime.now();
                    Costs costs = new Costs(costAddNameTField.getText(),Float.parseFloat(costNumberTField.getText()), costAddChoiceBox.getSelectionModel().getSelectedItem(),
                            Integer.parseInt(costPriceTField.getText()),
                            date.format(now),time.format(now));
                    try {
                        addCost(costs);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    warningLabel.setVisible(true);
                    warningLabel.setTextFill(Color.web("#4d893c"));
                    warningLabel.setText("اطلاعات شما با موفقیت ذخیره شد !");
                    try {
                        updateCostsTable();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
        Scene costAddScene = new Scene(costAddPane);
        costAdd.setScene(costAddScene);
        costAdd.show();

    }

    @FXML
    public void removeCostBtn() throws SQLException {
        if (costsTable.getSelectionModel().getSelectedItem() != null) {
            removeCost(costsTable.getSelectionModel().getSelectedItem());
            updateCostsTable();
        }
    }

    @FXML
    public void editCostBtn() throws SQLException{
        if (costsTable.getSelectionModel().getSelectedItem() != null) {
            //making stage
            Stage costAdd = new Stage();
            AnchorPane costAddPane = new AnchorPane();
            costAddPane.setPrefSize(400, 300);

            //main label
            Label mainLabel = new Label("لطفا اطلاعات مورد نظر را وارد نمایید :");
            mainLabel.setFont(new Font(14));
            AnchorPane.setTopAnchor(mainLabel, 20.0);
            AnchorPane.setRightAnchor(mainLabel, 20.0);
            costAddPane.getChildren().add(mainLabel);

            //name label
            Label nameLabel = new Label("نام :");
            AnchorPane.setTopAnchor(nameLabel,64.0);
            AnchorPane.setRightAnchor(nameLabel, 20.0);
            costAddPane.getChildren().add(nameLabel);
            //name
            TextField costAddNameTField = new TextField();
            costAddNameTField.setPromptText("نام");
            costAddNameTField.setText(costsTable.getSelectionModel().getSelectedItem().getName());
            AnchorPane.setTopAnchor(costAddNameTField, 60.0);
            AnchorPane.setRightAnchor(costAddNameTField, 100.0);
            costAddNameTField.setAlignment(Pos.CENTER_RIGHT);
            costAddPane.getChildren().add(costAddNameTField);

            //number label
            Label numberLabel = new Label("تعداد / مقدار :");
            AnchorPane.setTopAnchor(numberLabel,104.0);
            AnchorPane.setRightAnchor(numberLabel, 20.0);
            costAddPane.getChildren().add(numberLabel);
            //number
            TextField costNumberTField = new TextField();
            costNumberTField.setPromptText("تعداد / مقدار");
            costNumberTField.setText(String.valueOf(costsTable.getSelectionModel().getSelectedItem().getNumber()));
            AnchorPane.setTopAnchor(costNumberTField, 100.0);
            AnchorPane.setRightAnchor(costNumberTField, 100.0);
            costNumberTField.setAlignment(Pos.CENTER_RIGHT);
            costAddPane.getChildren().add(costNumberTField);

            //choice box
            ChoiceBox<String> costAddChoiceBox = new ChoiceBox<>();
            ObservableList<String> costAddList = FXCollections.observableArrayList();
            costAddList.add("عدد");
            costAddList.add("کیلو");
            costAddList.add("گرم");
            costAddList.add("لیتر");
            costAddChoiceBox.setItems(costAddList);
            costAddChoiceBox.getSelectionModel().select(costsTable.getSelectionModel().getSelectedItem().getVahed());
            AnchorPane.setTopAnchor(costAddChoiceBox, 100.0);
            AnchorPane.setRightAnchor(costAddChoiceBox, 290.0);
            costAddPane.getChildren().add(costAddChoiceBox);

            //price label
            Label priceLabel = new Label("قیمت :");
            AnchorPane.setTopAnchor(priceLabel,144.0);
            AnchorPane.setRightAnchor(priceLabel, 20.0);
            costAddPane.getChildren().add(priceLabel);
            //price
            TextField costPriceTField = new TextField();
            costPriceTField.setPromptText("قیمت");
            costPriceTField.setText(String.valueOf(costsTable.getSelectionModel().getSelectedItem().getTotalPrice()));
            AnchorPane.setTopAnchor(costPriceTField, 140.0);
            AnchorPane.setRightAnchor(costPriceTField, 100.0);
            costPriceTField.setAlignment(Pos.CENTER_RIGHT);
            costAddPane.getChildren().add(costPriceTField);

            //warning label
            Label warningLabel = new Label("لطفا اطلاعات را تکمیل نمایید !");
            warningLabel.setTextFill(Color.web("#cf4c4c"));
            AnchorPane.setTopAnchor(warningLabel, 180.0);
            AnchorPane.setRightAnchor(warningLabel, 20.0);
            warningLabel.setVisible(false);
            costAddPane.getChildren().add(warningLabel);

            //submit btn
            Button submitBtn = new Button("ثبت");
            submitBtn.setFont(new Font(18));
            AnchorPane.setBottomAnchor(submitBtn, 45.0);
            AnchorPane.setRightAnchor(submitBtn, 173.0);
            costAddPane.getChildren().add(submitBtn);
            submitBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    String date = costsTable.getSelectionModel().getSelectedItem().getDate();
                    String time = costsTable.getSelectionModel().getSelectedItem().getTime();
                    try {
                        removeCost(costsTable.getSelectionModel().getSelectedItem());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    if (costAddNameTField.getText().trim().isEmpty() || costNumberTField.getText().trim().isEmpty() ||
                            costPriceTField.getText().trim().isEmpty() || costAddChoiceBox.getSelectionModel().selectedItemProperty().get().trim().isEmpty()) {
                        warningLabel.setVisible(true);
                    } else {
                        Costs costs = new Costs(costAddNameTField.getText(),Float.parseFloat(costNumberTField.getText()), costAddChoiceBox.getSelectionModel().getSelectedItem(),
                                Integer.parseInt(costPriceTField.getText()),
                                date,time);
                        try {
                            addCost(costs);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        warningLabel.setVisible(true);
                        warningLabel.setTextFill(Color.web("#4d893c"));
                        warningLabel.setText("اطلاعات شما با موفقیت ذخیره شد !");
                    }
                    costAdd.close();
                }
            });
            Scene costAddScene = new Scene(costAddPane);
            costAdd.setScene(costAddScene);
            costAdd.show();
        }
    }

    private void updateCostsTable() throws SQLException {

        //getting info from db
        ResultSet rs = stat.executeQuery("select * from costs;");
        ObservableList<Costs> list = FXCollections.observableArrayList();
        totalCostPrice = 0;

        //setting time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter day = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter month = DateTimeFormatter.ofPattern("yyyy/MM");
        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
        while (rs.next()){
            if (costsChoiceBox.getSelectionModel().getSelectedItem() == "روز"){
                if (!rs.getString("date").equals(day.format(now))){
                    continue;
                }
            } else if (costsChoiceBox.getSelectionModel().getSelectedItem() == "ماه"){
                String s = rs.getString("date").split("/")[0] + "/" + rs.getString("date").split("/")[1];
                if (!s.equals(month.format(now))){
                    continue;
                }
            } else if (costsChoiceBox.getSelectionModel().getSelectedItem() == "سال"){
                if (!rs.getString("date").split("/")[0].equals(year.format(now))){
                    continue;
                }
            }

            //making object
            Costs costs = new Costs(rs.getString("name"),Float.parseFloat(rs.getString("number")), rs.getString("vahed"),
                    Integer.parseInt(rs.getString("totalPrice")),
                    rs.getString("date"), rs.getString("time"));
            list.add(costs);
            totalCostPrice += Float.parseFloat(rs.getString("totalPrice"));
        }
        totalPriceLable.setText(String.valueOf(totalCostPrice) + " تومان");
        FilteredList<Costs> filteredData = new FilteredList<>(list, b -> true);
        SortedList<Costs> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(costsTable.comparatorProperty());
        costsTable.setItems(sortedData);
        costsTable.getSortOrder().add(dateColumn);
        costsTable.getSortOrder().add(timeColumn);
        costsTable.sort();
    }

    private void addCost(Costs costs) throws SQLException {
        PreparedStatement prep = localConn.prepareStatement("insert into costs values (?, ?, ?, ?, ?, ?);");
        prep.setString(1, costs.getName());
        prep.setString(2, String.valueOf(costs.getNumber()));
        prep.setString(3, costs.getVahed());
        prep.setString(4, String.valueOf(costs.getTotalPrice()));
        prep.setString(5, costs.getDate());
        prep.setString(6, costs.getTime());
        prep.addBatch();
        localConn.setAutoCommit(false);
        prep.executeBatch();
        localConn.setAutoCommit(true);
        updateCostsTable();
    }

    private void removeCost(Costs costs) throws SQLException {
        PreparedStatement prep = localConn.prepareStatement("delete from costs where name = ? and number = ? and vahed = ?" +
                " and totalPrice = ? and date = ? and time = ?");
        prep.setString(1, costs.getName());
        prep.setString(2, String.valueOf(costs.getNumber()));
        prep.setString(3, costs.getVahed());
        prep.setString(4, String.valueOf(costs.getTotalPrice()));
        prep.setString(5, costs.getDate());
        prep.setString(6, costs.getTime());
        prep.addBatch();
        localConn.setAutoCommit(false);
        prep.executeBatch();
        localConn.setAutoCommit(true);
    }

    private void updateLoginsTable() throws SQLException, ClassNotFoundException {
        totalLoginPrice = 0;
        ResultSet rs = stat.executeQuery("select * from userLogin;");
        ObservableList<UserLogin> list = FXCollections.observableArrayList();
        //setting time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter day = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter month = DateTimeFormatter.ofPattern("yyyy/MM");
        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
        while (rs.next()){
            if (costsChoiceBox.getSelectionModel().getSelectedItem() == "روز"){
                if (!rs.getString("inTime").split("-")[0].trim().equals(day.format(now))){
                    continue;
                }
            } else if (costsChoiceBox.getSelectionModel().getSelectedItem() == "ماه"){
                String s = rs.getString("inTime").split("-")[0].trim().split("/")[0] + "/" + rs.getString("inTime").split("-")[0].trim().split("/")[1];
                if (!s.equals(month.format(now))){
                    continue;
                }
            } else if (costsChoiceBox.getSelectionModel().getSelectedItem() == "سال"){
                if (!rs.getString("inTime").split("-")[0].trim().split("/")[0].equals(year.format(now))){
                    continue;
                }
            }

            Class.forName("org.sqlite.JDBC");
            Connection localConn2 = DriverManager.getConnection("jdbc:sqlite:main.db");
            Statement stat2 = localConn2.createStatement();
            ResultSet rs2 = stat2.executeQuery("select * from users where phone = '"+rs.getString("phone")+"';");
            String name = "", fName = "";
            while (rs2.next()){
                name = rs2.getString("name");
                fName = rs2.getString("familyName");
            }
            //making object
            if (!rs.getString("price").equals("?")) {
                UserLogin userLogin = new UserLogin(name, fName, rs.getString("phone"),
                        Integer.parseInt(rs.getString("price")),
                        rs.getString("inTime"), rs.getString("outTime"));
                list.add(userLogin);
                totalLoginPrice += Float.parseFloat(rs.getString("price"));
            } else {
                UserLogin userLogin = new UserLogin(name, fName, rs.getString("phone"),
                        0,
                        rs.getString("inTime"), rs.getString("outTime"));
                list.add(userLogin);
            }
        }
        totalLoginIncomLabel.setText(String.valueOf(totalLoginPrice));
        FilteredList<UserLogin> filteredData = new FilteredList<>(list, b -> true);
        SortedList<UserLogin> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(usersLoginTable.comparatorProperty());
        usersLoginTable.setItems(sortedData);
        usersLoginTable.getSortOrder().add(userLoginTimeColumn);
        usersLoginTable.sort();
    }

    private void updateOrdersTable() throws SQLException, ClassNotFoundException {
        totalOrderPrice = 0;
        ResultSet rs = stat.executeQuery("select * from usersOrders;");
        ObservableList<UserOrder> list = FXCollections.observableArrayList();
        //setting time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter day = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter month = DateTimeFormatter.ofPattern("yyyy/MM");
        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
        while (rs.next()){
            if (costsChoiceBox.getSelectionModel().getSelectedItem() == "روز"){
                if (!rs.getString("date").equals(day.format(now))){
                    continue;
                }
            } else if (costsChoiceBox.getSelectionModel().getSelectedItem() == "ماه"){
                String s = rs.getString("date").split("/")[0] + "/" + rs.getString("date").split("/")[1];
                if (!s.equals(month.format(now))){
                    continue;
                }
            } else if (costsChoiceBox.getSelectionModel().getSelectedItem() == "سال"){
                if (!rs.getString("date").split("/")[0].equals(year.format(now))){
                    continue;
                }
            }

            Class.forName("org.sqlite.JDBC");
            Connection localConn2 = DriverManager.getConnection("jdbc:sqlite:main.db");
            Statement stat2 = localConn2.createStatement();
            ResultSet rs2 = stat2.executeQuery("select * from users where phone = '"+rs.getString("phone")+"';");
            String name = "", fName = "";
            while (rs2.next()){
                name = rs2.getString("name");
                fName = rs2.getString("familyName");
            }
            //making object
             UserOrder userOrder = new UserOrder(name, fName, rs.getString("phone"),
                 rs.getString("name"),
                 Integer.parseInt(rs.getString("price")), rs.getString("date") + " - " + rs.getString("time"));
             list.add(userOrder);
             totalOrderPrice += Float.parseFloat(rs.getString("price"));
        }
        totalOrdersIncomeLabel.setText(String.valueOf(totalOrderPrice));
        FilteredList<UserOrder> filteredData = new FilteredList<>(list, b -> true);
        SortedList<UserOrder> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(usersOrdersTable.comparatorProperty());
        usersOrdersTable.setItems(sortedData);
        usersOrdersTable.getSortOrder().add(userOrderTimeColumn);
        usersOrdersTable.sort();
    }
}
