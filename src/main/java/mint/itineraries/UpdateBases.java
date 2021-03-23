/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mint.itineraries;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe de mise à jour des coûts d'une base.
 * @author Mathieu Rey-Herme & Emmanuel Bocquillon
 */
public class UpdateBases {
    //TODO : writes metrics for pollution
    public void updateOne(int base) {
        ConnectionDB co = new ConnectionDB(base);

        String query; //cost-adapt est la colonne qui détermine le coût
        switch (base) {
            case 0 :
                query = "ALTER TABLE ways ADD IF NOT EXISTS cost_adapt float;\n" +
                "UPDATE ways SET cost_adapt = length_m * 3.6/5;"; //attribut pour chaque voie de circulation : pour l'instant c'est longueur x vitesse. 
                break;
            case 1 :
                query = "ALTER TABLE ways ADD IF NOT EXISTS cost_adapt float;\n" +
                "UPDATE ways SET cost_adapt = length_m * 3.6/15;";
                break;
            default :
                query = "ALTER TABLE ways ADD IF NOT EXISTS cost_adapt float;\n" +
                "UPDATE ways SET cost_adapt = length_m * 3.6/maxspeed_forward;";
                break;
        }; //Mettre le coût du trajet pour les piétons
        //Mettre le coût du trajet pour les cyclistes
        //Mettre le coût du trajet pour les automobilistes
        
        try (PreparedStatement stmt = co.getConnect().prepareStatement(query)) {
            stmt.executeUpdate();
            System.out.println("Base actualisée.");
            
        } catch (SQLException ex) {    
            Logger.getLogger(Itinerary.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            co.closeConnection();
        }
    }
}
