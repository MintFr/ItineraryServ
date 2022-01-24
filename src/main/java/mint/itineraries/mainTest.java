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
    static double lonStart =  -1.569431;
    static double lonEnd = -1.562239;
    static double latStart = 47.250334;
    static double latEnd = 47.235706;
    static Itinerary itinerary1;
    


    public static void main(String[] args){
        itinerary1 = new Itinerary();
        
        itinerary1.getItinerary(1, -1.569431, 47.250334, -1.562239, 47.235706, 0);
        
        System.out.println(itinerary1);
        
    }
    
}
