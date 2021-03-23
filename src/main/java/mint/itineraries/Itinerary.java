/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mint.itineraries;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe de calcul d'itinéraire
 * @author Mathieu Rey-Herme & Emmanuel Bocquillon
 */
public class Itinerary {
    private ArrayList<Step> details;
    private boolean HofStart;
    private String time;
    private double distance; //meters
    private double duration; // seconds
    private double exposition;
    private String transport;
    //private ArrayList<Integer> stepsLength;
    private ArrayList<Coordonnees> pointsItinerary;
    private ArrayList<String> geomsItinerary;
    private boolean hasStep;
    private ArrayList<Double> step;

    /**
     * Constructor
     */
    public Itinerary() {
    }
    
    /**
     * This method gets the ids of the nearest ways from the point we want to start.
     * @param connect : Connection object to query the database
     * @param lon : longitude of the point we want to find the nearest way
     * @param lat : latitude of the point we want to find the nearest way
     * @return 
     */
    public int getIDQuery(Connection connect, double lon, double lat) {
        ResultSet res;
        String query = "SELECT source FROM ways ORDER BY the_geom <-> ST_SetSRID(ST_Point (?,?),4326) limit 1;";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setDouble(1, lon) ;
            stmt.setDouble(2, lat);
            res = stmt.executeQuery();
            res.next();
            
            return res.getInt(1);
            
        } catch(SQLException ex) {
            System.err.println("SQLException : " + ex.getMessage());
            return -1;
        } 
    }
    
    /**
     * Method that calculate an itinerary from start point, end point and other options specified in the url
     * @param transportation
     * @param lonStart
     * @param latStart
     * @param lonEnd
     * @param latEnd
     * @param criteria 
     */
    public void getItinerary(int transportation, double lonStart, double latStart, double lonEnd, double latEnd, int criteria){
        // connection to db
        ConnectionDB co = new ConnectionDB(transportation);
        switch(transportation){
            case 3:
                this.setTransport("Piéton");
                break;
            case 2:
                this.setTransport("Vélo");
                break;
            case 0:
                this.setTransport("Voiture");
                break;
            default:
                this.setTransport("Voiture");
                break;

                

        }
        // get way id for start and end
        int wayStart = getIDQuery(co.getConnect(), lonStart, latStart); //recupère les indices des aretes qui correspondent aux coordonnées
        int wayEnd = getIDQuery(co.getConnect(), lonEnd, latEnd);
        
        String query = "";
        switch(criteria){
            case 0: 
            query =  "SELECT x1, y1, x2, y2, the_geom, length_m as length, maxspeed_forward as speed, name, conc_no2 as c" +
                        "       FROM (" + 
                        "           SELECT edge" +
                        "           FROM pgr_dijkstra('SELECT gid as id, source, target, cost_fast as cost FROM ways_with_pol', ?, ?, false) " +
                        "       )" +
                        "AS route, ways_with_pol " + 
                        "WHERE route.edge =  ways_with_pol.gid";
                break;
            case 1 : 
                query =  "SELECT x1, y1, x2, y2, the_geom, length_m as length, maxspeed_forward as speed, name, conc_no2 as c" +
                        "       FROM (" + 
                        "           SELECT edge" +
                        "           FROM pgr_dijkstra('SELECT gid as id, source, target, cost_health_off as cost FROM ways_with_pol', ?, ?, false) " +
                        "       )" +
                        "AS route, ways_with_pol " + 
                        "WHERE route.edge = ways_with_pol.gid";
                break; 
            case 2 : 
                query =  "SELECT x1, y1, x2, y2, the_geom, length_m as length, maxspeed_forward as speed, name, conc_no2 as c" +
                        "       FROM (" + 
                        "           SELECT edge" +
                        "           FROM pgr_dijkstra('SELECT gid as id, source, target, cost_health_peak as cost FROM ways_with_pol', ?, ?, false) " +
                        "       )" +
                        "AS route, ways_with_pol " + 
                        "WHERE route.edge =  ways_with_pol.gid";
                break;
        }
        // query to get the itinerary
        
        try (PreparedStatement stmt = co.getConnect().prepareStatement(query)) {
            stmt.setInt(1, wayStart);
            stmt.setInt(2, wayEnd);
            ResultSet itinerary = stmt.executeQuery();
            
            // get itinerary attributes
            this.HofStart=true;
            this.time="00:00";
            //this.stepsLength = new ArrayList<>();
            this.distance = 0;
            this.duration = 0;
            this.exposition = 0;
            this.pointsItinerary = new ArrayList<>();
            this.pointsItinerary.add(new Coordonnees(lonStart, latStart));
            this.geomsItinerary = new ArrayList<>();
            this.details = new ArrayList<>();
            

            
            // treatment of the first two edges
            itinerary.next();
            System.out.println(itinerary.getDouble("c"));
            incrExposition(itinerary.getDouble("c"));
            incrDistanceAndDuration(transportation, itinerary.getDouble("length"), itinerary.getDouble("speed"));
            //this.stepsLength.add((int)Math.round(itinerary.getDouble("length")));
            //System.out.println(itinerary.getString("name"));
            if(itinerary.getString("name")!=null){
                this.details.add(new Step(itinerary.getString("name"), (int)itinerary.getDouble("length")));
            }            
            double[] firstEdge = new double[] {itinerary.getDouble("x1"), itinerary.getDouble("y1"), 
                                               itinerary.getDouble("x2"), itinerary.getDouble("y2")};
            itinerary.next();
            incrExposition(itinerary.getDouble("c"));
            incrDistanceAndDuration(transportation, itinerary.getDouble("length"), itinerary.getDouble("speed"));
            //this.stepsLength.add((int)Math.round(itinerary.getDouble("length")));
            
            if(itinerary.getString("name")!=null){
                this.details.add(new Step(itinerary.getString("name"), (int)itinerary.getDouble("length")));
            }
            
            
            double[] secondEdge = new double[] {itinerary.getDouble("x1"), itinerary.getDouble("y1"), 
                                               itinerary.getDouble("x2"), itinerary.getDouble("y2")};
            addNodesFirstTwoEdges(firstEdge, secondEdge);
            
            // treatment of the rest
            while (itinerary.next()) {
                incrExposition(itinerary.getDouble("c"));
                incrDistanceAndDuration(transportation, itinerary.getDouble("length"), itinerary.getDouble("speed"));
                //this.stepsLength.add((int)Math.round(itinerary.getDouble("length")));
                if(itinerary.getString("name")!=null){
                this.details.add(new Step(itinerary.getString("name"), (int)itinerary.getDouble("length")));
            }                addNode(itinerary.getDouble("x1"), itinerary.getDouble("y1"), itinerary.getDouble("x2"), itinerary.getDouble("y2"));
                this.geomsItinerary.add(itinerary.getString("the_geom"));
            }

            //this.pointsItinerary.add(new Coordonnees(latEnd, lonEnd));
            
            this.pointsItinerary.add(new Coordonnees(lonEnd, latEnd));
            //System.out.println(stepsLength);

            this.cleanDetails();

        } catch (SQLException ex) {    
            Logger.getLogger(Itinerary.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            co.closeConnection();
        }

    }
    
    /**
     * Increase exposition's value
     * @param exp 
     */
    public void incrExposition(double exp){
        this.exposition += exp;
    }
    
    
    /**
     * Increase distance's and duration's value
     * @param transportation
     * @param length
     * @param carSpeed 
     */
    public void incrDistanceAndDuration(int transportation, double length, double carSpeed) {
        this.distance += length;
        double speed;
        switch (transportation) {
                    case 3 :
                        speed = 5 / 3.6; // 5 km/h in m/s
                        break;
                    case 2 :
                        speed = 15 / 3.6; // 15 km/h in m/s
                        break;
                    case 0 :
                        speed = carSpeed / 3.6; // car speed in km/h;
                        break;
                    default :
                        speed = carSpeed / 3.6; // car speed in km/h;
                        break;
                };
        this.duration += length / speed;
    }

    /**
     * add the first node to itinerary
     * @param firstEdge
     * @param secondEdge 
     */
    public void addNodesFirstTwoEdges(double[] firstEdge, double[] secondEdge) {
        if ((firstEdge[0] == secondEdge[0]) && (firstEdge[1] == secondEdge[1])) {
            // point 1 of first edge equals point 1 of second edge
            this.pointsItinerary.add(new Coordonnees(firstEdge[2], firstEdge[3])); // add point 2 first edge
            this.pointsItinerary.add(new Coordonnees(firstEdge[0], firstEdge[1])); // add point 1 first edge (point 1 second edge)
            this.pointsItinerary.add(new Coordonnees(secondEdge[2], secondEdge[3])); // add point 2 second edge
            
        } else if ((firstEdge[0] == secondEdge[2]) && (firstEdge[1] == secondEdge[3])) {
            // point 1 of first edge equals point 2 of second edge
            this.pointsItinerary.add(new Coordonnees(firstEdge[2], firstEdge[3])); // add point 2 first edge
            this.pointsItinerary.add(new Coordonnees(firstEdge[0], firstEdge[1])); // add point 1 first edge (point 2 second edge)
            this.pointsItinerary.add(new Coordonnees(secondEdge[0], secondEdge[1])); // add point 1 second edge
            
        } else if ((firstEdge[2] == secondEdge[0]) && (firstEdge[3] == secondEdge[1])) {
            // point 2 of first edge equals point 1 of second edge
            this.pointsItinerary.add(new Coordonnees(firstEdge[0], firstEdge[1])); // add point 1 first edge
            this.pointsItinerary.add(new Coordonnees(firstEdge[2], firstEdge[3])); // add point 2 first edge (point 1 second edge)
            this.pointsItinerary.add(new Coordonnees(secondEdge[2], secondEdge[3])); // add point 2 second edge
            
        } else if ((firstEdge[2] == secondEdge[2]) && (firstEdge[3] == secondEdge[3])) {
            // point 2 of first edge equals point 2 of second edge
            this.pointsItinerary.add(new Coordonnees(firstEdge[0], firstEdge[1])); // add point 1 first edge
            this.pointsItinerary.add(new Coordonnees(firstEdge[2], firstEdge[3])); // add point 2 first edge (point 2 second edge)
            this.pointsItinerary.add(new Coordonnees(secondEdge[0], secondEdge[1])); // add point 1 second edge
            
        }
    }
    
    /**
     * Add node to the itinerary
     * @param x1
     * @param y1
     * @param x2
     * @param y2 
     */
    public void addNode(double x1, double y1, double x2, double y2) {
        double lastX = this.pointsItinerary.get(this.pointsItinerary.size() - 1).getLongitude();
        double lastY = this.pointsItinerary.get(this.pointsItinerary.size() - 1).getLatitude();
        
        if ((x1 == lastX) && (y1 == lastY)) {
            this.pointsItinerary.add(new Coordonnees(x2, y2));
        } else {
            this.pointsItinerary.add(new Coordonnees(x1, y1));
        }
    }
    
    /**
     * Agregate details with the same street name
     */
    public void cleanDetails(){
        ArrayList<Step> newDetails = new ArrayList<>();
        String tempAddress = this.getDetails().get(0).getAddressStep();
        int tempLength = this.getDetails().get(0).getLengthStep();
        for(int j = 1; j<this.getDetails().size();j++){
            if(this.getDetails().get(j).getAddressStep().equals(tempAddress)){
                //System.out.println(true);
                tempLength += this.getDetails().get(j).getLengthStep();
            }
            else{
                //System.out.println(false);
                newDetails.add(new Step(tempAddress, tempLength));
                tempAddress = this.getDetails().get(j).getAddressStep();
                tempLength = this.getDetails().get(j).getLengthStep();
            }
        }
        this.setDetails(newDetails);
    }
    
    /**
     * Connect two itineraries 
     * @param itinerary2 
     */
    public void joinItinerary(Itinerary itinerary2){
        this.details.addAll(itinerary2.getDetails());
        //this.time += itinerary2.getTime();
        this.distance += itinerary2.getDistance();
        this.duration += itinerary2.getDuration();
        this.exposition += itinerary2.getExposition();
        this.pointsItinerary.addAll(itinerary2.getPointsItinerary());
        this.geomsItinerary.addAll(itinerary2.getGeomsItinerary());
    }
    
    /**
     * Get distance
     * @return 
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Get duration
     * @return 
     */
    public double getDuration() {
        return duration;
    }

    /**
     * Get points of the itinerary
     * @return 
     */
    public ArrayList<Coordonnees> getPointsItinerary() {
        return pointsItinerary;
    }

    /**
     * Get geometries of the segment
     * @return 
     */
    public ArrayList<String> getGeomsItinerary() {
        return geomsItinerary;
    }
    
    /**
     * Get exposition
     * @return 
     */
    public double getExposition() {
        return exposition;
    }

    /**
     * Set exposition
     * @param exposition 
     */
    public void setExposition(double exposition) {
        this.exposition = exposition;
    }

    /**
     * Get transport
     * @return 
     */
    public String getTransport() {
        return transport;
    }

    /**
     * Set transport
     * @param transport 
     */
    public void setTransport(String transport) {
        this.transport = transport;
    }
    
//    public ArrayList<Integer> getStepsLength() {
//        return stepsLength;
//    }
//
//    public void setStepsLength(ArrayList<Integer> stepsLength) {
//        this.stepsLength = stepsLength;
//    }

    /**
     * Set if there is a an start hour defined
     * @param HofStart 
     */
    public void setHofStart(boolean HofStart) {
        this.HofStart = HofStart;
    }

    /**
     * Set time
     * @param time 
     */
    public void setTime(String time) {
        //System.out.println(time);
        this.time = time;
    }

    /**
     * Return if there is a start hour defined
     * @return 
     */
    public boolean isHofStart() {
        return HofStart;
    }

    /**
     * Get time
     * @return 
     */
    public String getTime() {
        return time;
    }

    /**
     * Get itinerary details
     * @return 
     */
    public ArrayList<Step> getDetails() {
        return details;
    }

    /**
     * Set itinerary's details
     * @param details 
     */
    public void setDetails(ArrayList<Step> details) {
        this.details = details;
    }

    /**
     * Return if the itinerary has a step
     * @return 
     */
    public boolean isHasStep() {
        return hasStep;
    }

    /**
     * Set if there is a step
     * @param hasStep 
     */
    public void setHasStep(boolean hasStep) {
        this.hasStep = hasStep;
    }

    /**
     * Get list of steps
     * @return 
     */
    public ArrayList<Double> getStep() {
        return step;
    }

    /**
     * Set list of step
     * @param step 
     */
    public void setStep(ArrayList<Double> step) {
        this.step = step;
    }
    
    
    
    


    
    
    
    
    
    
}