package objects;

import java.sql.*;
import java.util.ArrayList;

public class Group {
    private String gName;
    private float gCostPerHour;
    private int gNumber;
    private String[] gGames;
    private boolean isRunning = false;
    private int minutesPassed = 0;
    private float gTotalPrice;
    private ArrayList<User> gPlayers = new ArrayList<>();

    public Group(String gName, float gCostPerHour, int gNumber, String[] gGames){
        this.gName = gName;
        this.gCostPerHour = gCostPerHour;
        this.gNumber = gNumber;
        this.gGames = gGames;
        new timePassed().start();
    }

    public String getName() {
        return gName;
    }

    public float getCostPerHour() {
        return gCostPerHour;
    }

    public int getNumber() {
        return gNumber;
    }

    public String[] getGames() {
        return gGames;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getMinutesPassed() {
        return minutesPassed;
    }

    public float getgTotalPrice() {
        return gTotalPrice;
    }

    public void setRunning(boolean running){
        this.isRunning = running;
    }

    public ArrayList<User> getgPlayers() {
        return gPlayers;
    }

    public void addPlayer(User user){
        gPlayers.add(user);
    }

    public void removePlayer(User user) {
        gPlayers.remove(user);
    }

    public static void makeNewGroup(String name, float cph, int number, String[] games) throws ClassNotFoundException, SQLException {
        //creating database
        Class.forName("org.sqlite.JDBC");
        Connection localConn = DriverManager.getConnection("jdbc:sqlite:setting.db");
        Statement stat = localConn.createStatement();
        stat.executeUpdate("create table if not exists gp (name, cph, num, games);");
        //insert new game info
        PreparedStatement prep = localConn.prepareStatement("insert into gp values (?, ?, ?, ?);");
        prep.setString(1, name);
        prep.setString(2, String.valueOf(cph));
        prep.setString(3, String.valueOf(number));
        String gamesNames = "";
        for (int i = 0; i < games.length; i++) {
            gamesNames += games[i] + ",";
        }
        prep.setString(4, gamesNames);
        prep.addBatch();
        localConn.setAutoCommit(false);
        prep.executeBatch();
        localConn.setAutoCommit(true);
        //test
        ResultSet rs = stat.executeQuery("select * from gp;");
        System.out.println("------------------------------------------------------------------------");
        while (rs.next()) {
            System.out.println("name = " + rs.getString("name"));
            System.out.println("cph = " + rs.getString("cph"));
            System.out.println("number = " + rs.getString("num"));
            System.out.println("games = " + rs.getString("games"));
            System.out.println("------------------------------------");
        }
        rs.close();
        localConn.close();
    }

    class timePassed extends Thread{
        @Override
        public void run() {
            while (isRunning){
                minutesPassed++;
                gTotalPrice = (minutesPassed/60) * gCostPerHour;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startTime(){
        new timePassed().start();
    }

}
