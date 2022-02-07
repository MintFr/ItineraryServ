/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mint.itineraries;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe de connexion aux bases de données
 *
 * @author Mathieu Rey-Herme & Emmanuel Bocquillon
 */
public final class ConnectionDB {

    private Connection connect;

    /**
     * Connect to database containing datas of the chosen mean of transportation
     *
     * @param transportation
     */
    public ConnectionDB(int transportation) {
        this.connectToDB(transportation);
    }

    /**
     * Constructor without parameters
     */
    public ConnectionDB() {
    }

    /**
     * Connect to database depending on the requested mean of transportation
     *
     * @param transportation
     */
    public void connectToDB(int transportation) {
        try {
            Class.forName("org.postgresql.Driver");

            Properties config = new Properties();
            config.load(this.getClass().getResourceAsStream("/credentials.properties"));

            var username = config.getProperty("user");
            var password = config.getProperty("pwd");
            var address = config.getProperty("address");
            var port = config.getProperty("port");

            String link = "jdbc:postgresql://";

            switch (transportation) {
                case 3:
                    link += address + ":" + port + "/" + "routing_pedestrian";
                    //System.out.println("case0");
                    break;
                case 2:
                    link += address + ":" + port + "/" + "routing_bicycles";
                    //System.out.println("case1");
                    break;
                case 0:
                    link += address + ":" + port + "/" + "routing_cars";
                    //System.out.println("casedefault");
                    break;
                default:
                    link += address + ":" + port + "/" + "routing_cars";
                    //System.out.println("casedefault");
                    break;

            };
            //System.out.println(link);

            this.connect = DriverManager.getConnection(link, username, password);

        } catch (java.lang.ClassNotFoundException e) {
            System.err.println("ClassNotFoundException : " + e.getMessage());
        } catch (SQLException ex) {
            System.err.println("SQLException : " + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            this.connect.close();
        } catch (SQLException ex) {
            System.err.println("SQLException : " + ex.getMessage());
        }
    }

    /**
     *
     * @return
     */
    public Connection getConnect() {
        return connect;
    }
}
