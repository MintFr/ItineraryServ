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
 *
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
     * This method gets the ids of the nearest ways from the point we want to
     * start.
     *
     * @param connect : Connection object to query the database
     * @param lon : longitude of the point we want to find the nearest way
     * @param lat : latitude of the point we want to find the nearest way
     * @return
     */
    public int getIDQuery(Connection connect, double lon, double lat) {
        ResultSet res;
        String query = "SELECT source FROM ways ORDER BY the_geom <-> ST_SetSRID(ST_Point (?,?),4326) limit 1;";
        try ( PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setDouble(1, lon);
            stmt.setDouble(2, lat);
            res = stmt.executeQuery();
            res.next();

            return res.getInt(1);

        } catch (SQLException ex) {
            System.err.println("SQLException : " + ex.getMessage());
            return -1;
        }
    }
    
    /**
     * Method that calculate the distance between a real point, and the clother
     * points in the database. Used for the first and last step of itinerary.
     * @param connect
     * @param lon
     * @param lat
     * @return 
     */
    public double getDistanceToPoint(Connection connect, double lon, double lat) {
        ResultSet res;
        String query = 
                "SELECT 1000*ST_Distance("
                + "ST_SetSRID(ST_Point (?, ?), 4326),"
                + " dist.the_geom) as distance"
                + " FROM ("
                + "SELECT * FROM ways ORDER BY"
                + " the_geom <-> ST_SetSRID(ST_Point (?,?),4326) limit 1"
                + ") as dist;";
        try ( PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setDouble(1, lon);
            stmt.setDouble(2, lat);
            
            stmt.setDouble(3, lon);
            stmt.setDouble(4, lat);
            res = stmt.executeQuery();
            res.next();

            return res.getDouble("distance");

        } catch (SQLException ex) {
            System.err.println("SQLException : " + ex.getMessage());
            return -1.;
        }
    }

    /**
     * Method that calculate an itinerary from start point, end point and other
     * options specified in the url
     *
     * @param transportation
     * @param lonStart
     * @param latStart
     * @param lonEnd
     * @param latEnd
     * @param criteria
     */
    public void getItinerary(int transportation, double lonStart, double latStart, double lonEnd, double latEnd, int criteria) {
        // connection to db
        ConnectionDB co = new ConnectionDB(transportation);
        switch (transportation) {
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
        
        double distStart = getDistanceToPoint(co.getConnect(), lonStart, latStart);
        double distEnd = getDistanceToPoint(co.getConnect(), lonEnd, latEnd);

        String query = "";
        switch (criteria) {
            case 0:
                query = "SELECT x1, y1, x2, y2, the_geom, length_m as length, maxspeed_forward as speed, name, conc_no2 as c"
                        + "       FROM ("
                        + "           SELECT edge"
                        + "           FROM pgr_dijkstra('SELECT gid as id, source, target, cost_fast as cost FROM ways_with_pol', ?, ?, false) "
                        + "       )"
                        + "AS route, ways_with_pol "
                        + "WHERE route.edge =  ways_with_pol.gid";
                break;
            case 1:
                query = "SELECT x1, y1, x2, y2, the_geom, length_m as length, maxspeed_forward as speed, name, conc_no2 as c"
                        + "       FROM ("
                        + "           SELECT edge"
                        + "           FROM pgr_dijkstra('SELECT gid as id, source, target, cost_health_off as cost FROM ways_with_pol', ?, ?, false) "
                        + "       )"
                        + "AS route, ways_with_pol "
                        + "WHERE route.edge = ways_with_pol.gid";
                break;
            case 2:
                query = "SELECT x1, y1, x2, y2, the_geom, length_m as length, maxspeed_forward as speed, name, conc_no2 as c"
                        + "       FROM ("
                        + "           SELECT edge"
                        + "           FROM pgr_dijkstra('SELECT gid as id, source, target, cost_health_peak as cost FROM ways_with_pol', ?, ?, false) "
                        + "       )"
                        + "AS route, ways_with_pol "
                        + "WHERE route.edge =  ways_with_pol.gid";
                break;
        }
        // query to get the itinerary

        try ( PreparedStatement stmt = co.getConnect().prepareStatement(query)) {
            stmt.setInt(1, wayStart);
            stmt.setInt(2, wayEnd);
            ResultSet itinerary = stmt.executeQuery();

            // get itinerary attributes
            this.HofStart = true;
            this.time = "00:00";
            //this.stepsLength = new ArrayList<>();
            this.distance = 0;
            this.duration = 0;
            this.exposition = 0;
            this.pointsItinerary = new ArrayList<>();
            this.pointsItinerary.add(new Coordonnees(lonStart, latStart));
            this.geomsItinerary = new ArrayList<>();
            this.details = new ArrayList<>();

            this.details.add(new Step("Rejoignez : ", 16, 1));

            // treatment of the first two edges
            itinerary.next();
            System.out.println(itinerary.getDouble("c"));
            incrExposition(itinerary.getDouble("c"));
            incrDistanceAndDuration(transportation, itinerary.getDouble("length"), itinerary.getDouble("speed"));
            //this.stepsLength.add((int)Math.round(itinerary.getDouble("length")));
            if (itinerary.getString("name") != null) {
                this.details.add(new Step(itinerary.getString("name"), (int) itinerary.getDouble("length")));
            } else {
                this.details.add(new Step("", (int) itinerary.getDouble("length")));
            }

            double[] firstEdge = new double[]{itinerary.getDouble("x1"), itinerary.getDouble("y1"),
                itinerary.getDouble("x2"), itinerary.getDouble("y2")};
            itinerary.next();
            incrExposition(itinerary.getDouble("c"));
            incrDistanceAndDuration(transportation, itinerary.getDouble("length"), itinerary.getDouble("speed"));

            if (itinerary.getString("name") != null) {
                this.details.add(new Step(itinerary.getString("name"), (int) itinerary.getDouble("length")));
            } else {
                this.details.add(new Step("", (int) itinerary.getDouble("length")));
            }

            double[] secondEdge = new double[]{itinerary.getDouble("x1"), itinerary.getDouble("y1"),
                itinerary.getDouble("x2"), itinerary.getDouble("y2")};
            addNodesFirstTwoEdges(firstEdge, secondEdge);

            // treatment of the rest
            while (itinerary.next()) {
                incrExposition(itinerary.getDouble("c"));
                incrDistanceAndDuration(transportation, itinerary.getDouble("length"), itinerary.getDouble("speed"));
                if (itinerary.getString("name") != null) {
                    this.details.add(new Step(itinerary.getString("name"), (int) itinerary.getDouble("length")));
                } else {
                    this.details.add(new Step("", (int) itinerary.getDouble("length")));
                }
                addNode(itinerary.getDouble("x1"), itinerary.getDouble("y1"), itinerary.getDouble("x2"), itinerary.getDouble("y2"));
                this.geomsItinerary.add(itinerary.getString("the_geom"));
            }

            this.pointsItinerary.add(new Coordonnees(lonEnd, latEnd));

            this.cleanDetails();
            this.cleanUnnamedDetails();

            this.details.add(new Step("Vous arrivez : ", 16, 1));

        } catch (SQLException ex) {
            Logger.getLogger(Itinerary.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            co.closeConnection();
        }

    }

    /**
     * Increase exposition's value
     *
     * @param exp
     */
    public void incrExposition(double exp) {
        this.exposition += exp;
    }

    /**
     * Increase distance's and duration's value
     *
     * @param transportation
     * @param length
     * @param carSpeed
     */
    public void incrDistanceAndDuration(int transportation, double length, double carSpeed) {
        this.distance += length;
        double speed;
        switch (transportation) {
            case 3:
                speed = 5 / 3.6; // 5 km/h in m/s
                break;
            case 2:
                speed = 15 / 3.6; // 15 km/h in m/s
                break;
            case 0:
                speed = carSpeed / 3.6; // car speed in km/h;
                break;
            default:
                speed = carSpeed / 3.6; // car speed in km/h;
                break;
        };
        this.duration += length / speed;
    }

    /**
     * add the first node to itinerary
     *
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
     *
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
    public void cleanDetails() {
        ArrayList<Step> newDetails = new ArrayList<>();
        String tempAddress = this.getDetails().get(0).getAddressStep();
        int tempLength = this.getDetails().get(0).getLengthStep();
        int nbPoint = 1;
        for (int j = 1; j < this.getDetails().size(); j++) {
            if (this.getDetails().get(j).getAddressStep().equals(tempAddress)) {
                //System.out.println(true);
                tempLength += this.getDetails().get(j).getLengthStep();
                nbPoint += 1;
            } else {
                //System.out.println(false);
                newDetails.add(new Step(tempAddress, tempLength, nbPoint));
                tempAddress = this.getDetails().get(j).getAddressStep();
                tempLength = this.getDetails().get(j).getLengthStep();
                nbPoint = 1;

            }
        } 
       newDetails.add(new Step(tempAddress, tempLength, nbPoint));
        this.setDetails(newDetails);
    }

    /**
     * method that fuses steps when 2 steps of the same name are separed by one
     * unnamed one
     */
    public void cleanUnnamedDetails() {
        //First we create an array of integer that will contain the index of the step that it has to be fused with
        int[] indexTofuse = new int[this.getDetails().size()];
        for (int k = 0; k < this.getDetails().size() - 1; k++) {
            indexTofuse[k] = k;
        }
        int j = 1;

        //We have an array of indexes that will correspond to the index the step should have when fused
        while (j < this.getDetails().size() - 1) {

            String tempAddress = this.getDetails().get(j).getAddressStep();
            if (tempAddress.equals("")
                    & (this.getDetails().get(j - 1).getAddressStep().equals(
                            this.getDetails().get(j + 1).getAddressStep()
                    ))) {
                indexTofuse[j] = indexTofuse[j - 1];
                indexTofuse[j + 1] = indexTofuse[j - 1];
                j++;
            } else {
                indexTofuse[j] = indexTofuse[j - 1] + 1;
            }
            j++;
        }

        //We create newDetails to update the details in the itinerary
        ArrayList<Step> newDetails = new ArrayList<>();
        newDetails.add(new Step(this.getDetails().get(0).getAddressStep(), this.getDetails().get(0).getLengthStep(), this.getDetails().get(0).getNumberOfEdges()));
        //We check the index in the array index to fuse and we add a new one if not fused with the precedent one and if needed to be fused we do it
        for (int i = 1; i < this.getDetails().size(); i++) {
            if (indexTofuse[i] != indexTofuse[i - 1]) {
                newDetails.add(new Step(this.getDetails().get(i).getAddressStep(), this.getDetails().get(i).getLengthStep(), this.getDetails().get(i).getNumberOfEdges()));
            } else {
                newDetails.set(indexTofuse[i], new Step(newDetails.get(indexTofuse[i]).getAddressStep(),
                         newDetails.get(indexTofuse[i]).getLengthStep() + this.getDetails().get(i).getLengthStep(),
                         newDetails.get(indexTofuse[i]).getNumberOfEdges() + this.getDetails().get(i).getNumberOfEdges()));
            }
        }
        //we set the new details in the itinerary
        this.setDetails(newDetails);
    }

    /**
     * Connect two itineraries
     *
     * @param itinerary2
     */
    public void joinItinerary(Itinerary itinerary2) {
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
     *
     * @return
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Get duration
     *
     * @return
     */
    public double getDuration() {
        return duration;
    }

    /**
     * Get points of the itinerary
     *
     * @return
     */
    public ArrayList<Coordonnees> getPointsItinerary() {
        return pointsItinerary;
    }

    /**
     * Get geometries of the segment
     *
     * @return
     */
    public ArrayList<String> getGeomsItinerary() {
        return geomsItinerary;
    }

    /**
     * Get exposition
     *
     * @return
     */
    public double getExposition() {
        return exposition;
    }

    /**
     * Set exposition
     *
     * @param exposition
     */
    public void setExposition(double exposition) {
        this.exposition = exposition;
    }

    /**
     * Get transport
     *
     * @return
     */
    public String getTransport() {
        return transport;
    }

    /**
     * Set transport
     *
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
     *
     * @param HofStart
     */
    public void setHofStart(boolean HofStart) {
        this.HofStart = HofStart;
    }

    /**
     * Set time
     *
     * @param time
     */
    public void setTime(String time) {
        //System.out.println(time);
        this.time = time;
    }

    /**
     * Return if there is a start hour defined
     *
     * @return
     */
    public boolean isHofStart() {
        return HofStart;
    }

    /**
     * Get time
     *
     * @return
     */
    public String getTime() {
        return time;
    }

    /**
     * Get itinerary details
     *
     * @return
     */
    public ArrayList<Step> getDetails() {
        return details;
    }

    /**
     * Set itinerary's details
     *
     * @param details
     */
    public void setDetails(ArrayList<Step> details) {
        this.details = details;
    }

    /**
     * Return if the itinerary has a step
     *
     * @return
     */
    public boolean isHasStep() {
        return hasStep;
    }

    /**
     * Set if there is a step
     *
     * @param hasStep
     */
    public void setHasStep(boolean hasStep) {
        this.hasStep = hasStep;
    }

    /**
     * Get list of steps
     *
     * @return
     */
    public ArrayList<Double> getStep() {
        return step;
    }

    /**
     * Set list of step
     *
     * @param step
     */
    public void setStep(ArrayList<Double> step) {
        this.step = step;
    }

    @Override
    public String toString() {
        String response = "";
        response += "Itinerary : \n";
        response += "Hour of start : " + this.HofStart + "\n";
        response += "time : " + this.time + "\n";
        response += "distance : " + this.getDistance() + "\n";
        response += "duration : " + this.getDuration() + "\n";
        response += "exposition : " + this.getExposition() + "\n";
        response += "transport : " + this.transport + "\n";
        response += "Details :  \n";
        for (int i = 0; i < this.details.size(); i++) {
            response += "\t " + this.getDetails().get(i).toString() + "\n";
        }
        response += "End details } \n";
        response += "Points itinerary :  \n";
        for (int i = 0; i < this.pointsItinerary.size(); i++) {
            response += "\t " + this.getPointsItinerary().get(i).toString() + "\n";
        }
        response += "End points } \n";
        response += "hasStep : " + hasStep + "\n step : " + step + "\n }";
        return response;
    }
}
