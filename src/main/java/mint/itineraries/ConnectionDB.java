/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mint.itineraries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe de connexion aux bases de données
 * @author Mathieu Rey-Herme & Emmanuel Bocquillon
 */
public final class ConnectionDB {
    private Connection connect;
    
    static Properties readConfigurationFile(File configFile) {
    try (FileInputStream contents = new FileInputStream(configFile)) {
        var properties = new Properties();
        properties.load(contents);
        return properties;
    }   catch (FileNotFoundException ex) {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
}
    
    /**
     * Connect to database containing datas of the chosen mean of transportation
     * @param transportation 
     */
    public ConnectionDB(int transportation){
        this.connectToDB(transportation);
    }
    
    /**
     * Constructor without parameters
     */
    public ConnectionDB() {
    }
    
    /**
     * Connect to database depending on the requested mean of transportation
     * @param transportation 
     */
    public void connectToDB(int transportation){
        try {
            Class.forName("org.postgresql.Driver");
            
            File configFile = new File("credentials.properties");
            
            Properties config = readConfigurationFile(configFile);
            
            var username = config.getProperty("user");
            var password = config.getProperty("pwd");
            var address = config.getProperty("address");
            
            // ResourceBundle parameters = ResourceBundle.getBundle("credentials");
            // String user = getResourceElement(parameters, "user");
            // String pwd = getResourceElement(parameters, "pwd");
            // String address = getResourceElement(parameters, "address");
            
            String lien = "jdbc:postgresql://";
            //System.out.println(transportation);
            //System.out.println(transportation==0);
            switch (transportation){
                case 3 :
                    lien += address + "routing_pedestrian";
                    //System.out.println("case0");
                    break;
                case 2 :
                    lien += address + "routing_bicycles";
                    //System.out.println("case1");
                    break;
                case 0 : 
                    lien += address + "routing_cars";
                    //System.out.println("casedefault");
                    break;
                default :
                    lien += address + "routing_cars";
                    //System.out.println("casedefault");
                    break;

            };
            //System.out.println(lien);
            
            this.connect = DriverManager.getConnection(lien, username, password);
            
        }catch(java.lang.ClassNotFoundException e) {
            System.err.println("ClassNotFoundException : " + e.getMessage()) ;
        }
        catch(SQLException ex) {
            System.err.println("SQLException : " + ex.getMessage()) ;
        }
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            this.connect.close();
        } catch(SQLException ex) {
            System.err.println("SQLException : " + ex.getMessage());
        }
    }
    
    /**
     * 
     * @param res
     * @param element
     * @return 
     */
    private String getResourceElement(ResourceBundle res, String element) {
        String newValue;
        String returnValue = "";
        if (res != null) {
            try {
                newValue = res.getString(element);
                if (!newValue.equals("")) {
                    returnValue = newValue;
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return returnValue;
    }

    /**
     * 
     * @return 
     */
    public Connection getConnect() {
        return connect;
    }
}
