package connectivity;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionClass {
    Connection connection;

    public Connection getConnection() {
        String dbName = "datamsof_crm_datamfamily";
        String userName = "datamsof_family";
        String passWord = "mstB#~b;FpC3";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://144.76.168.187:3306/" + dbName + "?user=" + userName + "&password=" + passWord + "&useUnicode=true&characterEncoding=UTF-8");
            //"jdbc:mysql://144.76.168.187:2082/" + dbName ,userName,passWord
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
