import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {
    private BorderPane registerLayout;
    private AnchorPane profileLayout;
    private AnchorPane gamesLayout;
    private AnchorPane costsLayout;
    private Scene registerScene;
    private Scene profileScene;
    private Scene gamesScene;
    private TabPane mainPane = new TabPane();
    private Tab registerTab = new Tab("Register");
    private Tab profileTab = new Tab("Profiles");
    private Tab gamesTab = new Tab("Games");
    private Tab costsTab = new Tab("Costs");

    @Override
    public void start(Stage primaryStage) throws IOException, SQLException, ClassNotFoundException {
        primaryStage.setTitle("CRM");

        mainPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        registerTab.setContent(getRightStage());
        profileTab.setContent(getMiddleStage());
        gamesTab.setContent(getLeftStage());
        costsTab.setContent(getCostsLayout());
        mainPane.getTabs().add(registerTab);
        mainPane.getTabs().add(profileTab);
        mainPane.getTabs().add(gamesTab);
        mainPane.getTabs().add(costsTab);
        Scene main = new Scene(mainPane);
        primaryStage.setScene(main);
        primaryStage.show();
        /*Group.makeNewGroup("biliard", (float) 20.0, 2, new String[]{"shuffer", "snooker", "8 ball"});
        Group.makeNewGroup("ps4", (float) 8.0, 6, new String[]{"fifa 20", "pes 2020", "Call Of Duty: Black Ops"
        , "Call Of Duty: MW3"});
        Group.makeNewGroup("airHockey", (float) 15.0, 1, new String[]{"airHockey"});
        Group.makeNewGroup("table", (float) 20.0, 4, new String[]{"pantomim", "Risk", "Secret Hitler", "Mafia"
        , "Spi"});*/

    }

    private Node getRightStage() throws IOException {
        //RIGHT
        FXMLLoader rightLoader = new FXMLLoader();
        rightLoader.setLocation(Main.class.getResource("javaFX/fxml/right/right.fxml"));
        registerLayout = rightLoader.load();
        //RIGHT-TOP
        FXMLLoader topRightLoader = new FXMLLoader();
        topRightLoader.setLocation(Main.class.getResource("javaFX/fxml/right/register.fxml"));
        BorderPane topRightLayout = topRightLoader.load();
        registerLayout.setTop(topRightLayout);
        //RIGHT-BUTTOM
        FXMLLoader bottomRightLoader = new FXMLLoader();
        bottomRightLoader.setLocation(Main.class.getResource("javaFX/fxml/right/login.fxml"));
        BorderPane bottomRightLayout = bottomRightLoader.load();
        registerLayout.setBottom(bottomRightLayout);
        //Make Scene
        return registerLayout;
    }

    private Node getMiddleStage() throws IOException {
        FXMLLoader middleLoader = new FXMLLoader();
        middleLoader.setLocation(Main.class.getResource("javaFX/fxml/middle.fxml"));
        return middleLoader.load();
    }

    private Node getLeftStage() throws IOException {
        FXMLLoader leftLoader = new FXMLLoader();
        leftLoader.setLocation(Main.class.getResource("javaFX/fxml/left/games.fxml"));
        return leftLoader.load();
    }

    private Node getCostsLayout() throws  IOException {
        FXMLLoader costsLoader = new FXMLLoader();
        costsLoader.setLocation(Main.class.getResource("javaFX/fxml/costsList.fxml"));
        return costsLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
