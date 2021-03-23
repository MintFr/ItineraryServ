/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mint.itineraries;

/**
 * Class representing spatial coordinates, in latitude and longitude
 * @author Kheira
 */
public class Coordonnees {
    private Double latitude;
    private Double longitude;

    /**
     * Constructor
     * @param longitude
     * @param latitude 
     */
    public Coordonnees(Double longitude, Double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Get latitude
     * @return 
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Set latitude
     * @param latitude 
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Get longitude
     * @return 
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Set longitude
     * @param longitude 
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
}
