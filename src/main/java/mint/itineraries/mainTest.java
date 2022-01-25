/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mint.itineraries;

/**
 *
 * @author mathis
 */
public class mainTest {
    static double lonStart =  -1.586;
    static double lonEnd = -1.598;
    static double latStart = 47.219;
    static double latEnd = 47.278;
    static Itinerary itinerary1;
    


    public static void main(String[] args){
        itinerary1 = new Itinerary();
        
        itinerary1.getItinerary(1, -1.586, 47.219, -1.598, 47.278, 0);
        
        System.out.println(itinerary1);
        
    }
    
}
