package javaFx.controllers;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import connectivity.ConnectionClass;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

//import javax.swing.text.html.ImageView;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.chrono.*;

public class registerControl {

    @FXML private TextField regNameTField;
    @FXML private TextField regFNameTField;
    @FXML private Label regWarning;
    @FXML private TextField regReferenceTField;
    @FXML private TextField regPhoneTField;
    @FXML private TextField regCodeTField;
    @FXML private ImageView regImage;
    @FXML private TextField regBDay;
    @FXML private TextField regBMonth;
    @FXML private TextField regBYear;

    private Webcam webcam;
    private boolean isRunning = true;
    private Image image = null;
    private BufferedImage bufferedImage = null;

    @FXML
    public void regSubmitBtn(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        //---------------local database---------------
        Class.forName("org.sqlite.JDBC");
        Connection localConn = DriverManager.getConnection("jdbc:sqlite:main.db");
        Statement stat = localConn.createStatement();
        stat.executeUpdate("create table if not exists users (name, familyName, reference, phone, birthDay, imgUrl);");
        PreparedStatement prep = localConn.prepareStatement("insert into users values (?, ?, ?, ?, ?, ?);");
        //check for the information
        if ((regFNameTField.getText().trim().isEmpty()) || (regNameTField.getText().trim().isEmpty()) ||
        regPhoneTField.getText().trim().isEmpty() || regBDay.getText().trim().isEmpty() ||
        regBMonth.getText().trim().isEmpty() || regBYear.getText().trim().isEmpty()) {
            regWarning.setText("لطفا اطلاعات مورد نیاز را کامل نمایید.");
            regWarning.setTextFill(Color.web("#cf4c4c"));
            regWarning.setVisible(true);
        } else {
            //adding user
            if (!stat.executeQuery("select * from users where phone = '" + regPhoneTField.getText() + "';").next()) {
                regWarning.setVisible(false);
                prep.setString(1, regNameTField.getText());
                prep.setString(2, regFNameTField.getText());
                prep.setString(3, regReferenceTField.getText());
                prep.setString(4, regPhoneTField.getText());
                prep.setString(5, regBYear.getText() + "/" + regBMonth.getText() + "/" + regBDay.getText());
                String imgUrl = "img" + regPhoneTField.getText() + ".jpg";
                prep.setString(6,imgUrl);
                prep.addBatch();
                //adding pic
                if (bufferedImage == null){
                    try {
                        ImageIO.write(webcam.getImage(), "JPG", new File(imgUrl));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        ImageIO.write(bufferedImage, "JPG", new File(imgUrl));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                regWarning.setText("کاربر جدید با موفقیت ثبت شد");
                regWarning.setTextFill(Color.web("#4d893c"));
                regWarning.setVisible(true);
            }
            else {
                //phone num already exists
                regWarning.setText("شماره موبایل تکراری می باشد !");
                regWarning.setTextFill(Color.web("#cf4c4c"));
                regWarning.setVisible(true);
            }
        }

        localConn.setAutoCommit(false);
        prep.executeBatch();
        localConn.setAutoCommit(true);
        //test
        //printing users info
        ResultSet rs = stat.executeQuery("select * from users;");
        System.out.println("------------------------------------------------------------------------");
        while (rs.next()) {
            System.out.println("name = " + rs.getString("name"));
            System.out.println("family name = " + rs.getString("familyName"));
            System.out.println("reference = " + rs.getString("reference"));
            System.out.println("phone number = " + rs.getString("phone"));
            System.out.println("------------------------------------");
        }
        rs.close();
        localConn.close();
    }

    @FXML
    public void regPhotoSubmitCode(ActionEvent actionEvent) throws IOException {
        if (isRunning){
            bufferedImage = webcam.getImage();
            isRunning = false;
        } else {
            isRunning = true;
            bufferedImage = null;
            new videoFeedTaker().start();
        }
    }

    public registerControl(){
        webcam = Webcam.getDefault();
        webcam.open();
        new videoFeedTaker().start();
    }

    class videoFeedTaker extends Thread {
        @Override
        public void run() {
            while (isRunning) {
                image = SwingFXUtils.toFXImage(webcam.getImage(), null);
                Platform.runLater(()-> regImage.setImage(image));

                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}



/*        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        String sqlCode = "INSERT INTO USER VALUES('ppp')";
        Statement statement=connection.createStatement();
        statement.executeUpdate(sqlCode);
        label.setText(text.getText()); */
