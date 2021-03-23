package mint.itineraries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author Kheira
 */
@SpringBootApplication
@RestController
public class MintApplication {

/**
 * 
 * @param args 
 */
public static void main(String[] args) {
SpringApplication.run(MintApplication.class, args);
}

//@GetMapping("/itinerary")
//public List<Coordonnees> itinerary(@RequestParam(value = "pdd") Double pdd, @RequestParam(value="pda") Double pda){
//    List<Coordonnees> response = new ArrayList<>();
//    response.add(new Coordonnees(1.55, 2.57));
//    response.add(new Coordonnees(pdd, pda));
//    response.add(new Coordonnees(pda, pdd));
//    System.out.println("je suis la");
//    return response;
//}

//@GetMapping("/itinerary")
//public List<Coordonnees> itinerary(@RequestParam(value = "pddLat") Double pddLat, 
//                                    @RequestParam(value = "pddLong") Double pddLong,
//                                    @RequestParam(value = "pdaLat") Double pdaLat,
//                                    @RequestParam(value = "pdaLong") Double pdaLong){
//    //System.out.println("je suis la");
//    List<Coordonnees> response = new ArrayList<>();
//    response.add(new Coordonnees(pddLat, pddLong));
//    response.add(new Coordonnees(pdaLat, pdaLong));
//    return response;
//}
//
//@RequestMapping("/itinerary2")
//public List<Itinerarybis> itinerary2(@RequestParam(value = "pddLat") Double pddLat, 
//                                    @RequestParam(value = "pddLong") Double pddLong,
//                                    @RequestParam(value = "pdaLat") Double pdaLat,
//                                    @RequestParam(value = "pdaLong") Double pdaLong,
//                                    @RequestParam(value = "transports") List<Integer> transports){
//    
//    
//    List<Itinerarybis> response = new ArrayList<>();
//    for (Integer i:transports){
//        Itinerarybis itinerary1;
//        if (i==0){
//            Coordonnees c0 = new Coordonnees(47.205461, -1.559122);
//            Coordonnees c1 = new Coordonnees(47.205559, -1.558233);
//            Coordonnees c2 = new Coordonnees(47.206165, -1.558373);
//            Coordonnees c3 = new Coordonnees(47.206220, -1.557744);
//            Coordonnees[] steps = new Coordonnees[4];
//            steps[0] = c0;
//            steps[1] = c1;
//            steps[2] = c2;
//            steps[3] = c3;
//            itinerary1 = new Itinerarybis(0, 3600, 50, steps);
//            response.add(itinerary1);
//        }
//        if (i==1){
//            Coordonnees c0 = new Coordonnees(47.205461, -1.559122);
//            Coordonnees c1 = new Coordonnees(47.206058, -1.559250);
//            Coordonnees c2 = new Coordonnees(47.206220, -1.557744);
//            Coordonnees[] steps = new Coordonnees[3];
//            steps[0] = c0;
//            steps[1] = c1;
//            steps[2] = c2;
//            itinerary1 = new Itinerarybis(1, 2500, 10, steps);
//            response.add(itinerary1);
//        }
//        if (i==2){
//            Coordonnees c0 = new Coordonnees(47.205461, -1.559122);
//            Coordonnees c1 = new Coordonnees(47.206220, -1.557744);
//            Coordonnees c2 = new Coordonnees(47.206220, -1.557744);
//            Coordonnees[] steps = new Coordonnees[3];
//            steps[0] = c0;
//            steps[1] = c1;
//            steps[2] = c2;
//            itinerary1 = new Itinerarybis(2, 1000, 100, steps);
//            response.add(itinerary1);
//        }
//    }
//    
//    return response;
//}
//@GetMapping("/itinerary3")
//public List<Itinerarybis> itinerary3(@RequestParam(value = "pddLat") Double pddLat, 
//                                    @RequestParam(value = "pddLong") Double pddLong,
//                                    @RequestParam(value = "pdaLat") Double pdaLat,
//                                    @RequestParam(value = "pdaLong") Double pdaLong,
//                                    @RequestParam(value = "i") int i){
//    
//    List<Itinerarybis> response = new ArrayList<>();
//    //for (int i:transports){
//        Itinerarybis itinerary1;
//        if (i==0){
//            Coordonnees c0 = new Coordonnees(47.205461, -1.559122);
//            Coordonnees c1 = new Coordonnees(47.205559, -1.558233);
//            Coordonnees c2 = new Coordonnees(47.206165, -1.558373);
//            Coordonnees c3 = new Coordonnees(47.206220, -1.557744);
//            Coordonnees[] steps = new Coordonnees[4];
//            steps[0] = c0;
//            steps[1] = c1;
//            steps[2] = c2;
//            steps[3] = c3;
//            itinerary1 = new Itinerarybis(0, 3600, 50, steps);
//            response.add(itinerary1);
//        }
//        if (i==1){
//            Coordonnees c0 = new Coordonnees(47.205461, -1.559122);
//            Coordonnees c1 = new Coordonnees(47.206058, -1.559250);
//            Coordonnees c2 = new Coordonnees(47.206220, -1.557744);
//            Coordonnees[] steps = new Coordonnees[3];
//            steps[0] = c0;
//            steps[1] = c1;
//            steps[2] = c2;
//            itinerary1 = new Itinerarybis(1, 2500, 10, steps);
//            response.add(itinerary1);
//        }
//        if (i==2){
//            Coordonnees c0 = new Coordonnees(47.205461, -1.559122);
//            Coordonnees c1 = new Coordonnees(47.206220, -1.557744);
//            Coordonnees c2 = new Coordonnees(47.206220, -1.557744);
//            Coordonnees[] steps = new Coordonnees[3];
//            steps[0] = c0;
//            steps[1] = c1;
//            steps[2] = c2;
//            itinerary1 = new Itinerarybis(2, 1000, 100, steps);
//            response.add(itinerary1);
//        }
//            return response;
//
//    }
//
//@GetMapping("/itinerary4")
//public List<Itinerary> itinerary4(@RequestParam(value = "pddLat") Double pddLat, 
//                                    @RequestParam(value = "pddLong") Double pddLong,
//                                    @RequestParam(value = "pdaLat") Double pdaLat,
//                                    @RequestParam(value = "pdaLong") Double pdaLong,
//                                    @RequestParam(value = "transportation") List<Integer> transportation){
//    //voiture, transport en commun, vélo, piéton
//    List<Itinerary> response = new ArrayList<>();
//    
//    //System.out.println(transportation);
//    int i = 0;
//    for (int j:transportation){
//       Itinerary itinerary = new Itinerary();
//       double random = Math.random()*100;
//       itinerary.setExposition(random);
//       if (j!=0){ 
//            //System.out.println(i);
//            itinerary.getItinerary(i, pddLong, pddLat, pdaLong, pdaLat, 0);
//            response.add(itinerary);
//       }
//       i++;
//       }
//    return response;
//}
//    
//@GetMapping("/itinerary5")
//public List<Itinerary> itinerary5(@RequestParam(value = "start") List<Double> start, 
//                                    @RequestParam(value = "end") List<Double> end,
//                                    @RequestParam(value = "hourStart") boolean hourStart,
//                                    @RequestParam(value = "time") String time,
//                                    @RequestParam(value = "transportation") List<Integer> transportation){
//    //voiture, transport en commun, vélo, piéton
//    List<Itinerary> response = new ArrayList<>();
//    
//    //System.out.println(time);
//    int i = 0;
//    for (int j:transportation){
//       Itinerary itinerary = new Itinerary();
//       double random = Math.random()*100;
//       itinerary.setExposition(random);
//       itinerary.setHofStart(hourStart);
//       //itinerary.setTime(time);
//       if (j!=0){ 
//            //System.out.println(i);
//            itinerary.getItinerary(i, start.get(1), start.get(0), end.get(1), end.get(0),0);
//            itinerary.setExposition(random);
//            itinerary.setHofStart(hourStart);
//            itinerary.setTime(time);
//            response.add(itinerary);
//            
//       }
//       i++;
//       }
//    return response;
//}

/**
 * Return itinerary 
 * @param start
 * @param end
 * @param hourStart
 * @param time
 * @param hasStep
 * @param step
 * @param transportation
 * @return 
 */
@GetMapping("/itineraryPollution")
public List<Itinerary> itinerary6(@RequestParam(value = "start") List<Double> start, 
                                    @RequestParam(value = "end") List<Double> end,
                                    @RequestParam(value = "hourStart") boolean hourStart,
                                    @RequestParam(value = "time") String time,
                                    @RequestParam(value = "hasStep") boolean hasStep,
                                    @RequestParam(value = "step", required = false) List<Double> step,
                                    @RequestParam(value = "transportation") List<Integer> transportation){
    //voiture, transport en commun, vélo, piéton
    List<Itinerary> response = new ArrayList<>();

    //System.out.println(transportation);
    if (hasStep == false){
    //List<Itinerary> itineraries = new ArrayList<>();
    int i = 0;
    for (int j:transportation){
       Itinerary itineraryFast = new Itinerary();
       Itinerary itineraryHealth = new Itinerary();
       double random = Math.random()*100;
       //itineraryFast.setExposition(random);
       itineraryFast.setHofStart(hourStart);
       itineraryHealth.setExposition(random);
       itineraryHealth.setHofStart(hourStart);
       //itinerary.setTime(time);
       if (j!=0){ 
            //System.out.println(i);
            itineraryFast.getItinerary(i, start.get(1), start.get(0), end.get(1), end.get(0), 0);
            //itineraryFast.setExposition(random);
            itineraryFast.setHofStart(hourStart);
            itineraryFast.setTime(time);
            itineraryFast.setHasStep(false);
            response.add(itineraryFast);
            itineraryHealth.getItinerary(i, start.get(1), start.get(0), end.get(1), end.get(0), 2);
            //itineraryHealth.setExposition(random);
            itineraryHealth.setHofStart(hourStart);
            itineraryHealth.setTime(time);
            itineraryHealth.setHasStep(false);
            response.add(itineraryHealth);
            
       }
       i++;
       }
    return response;
}
    else{
        List<Itinerary> itineraries1 = new ArrayList<>();
        List<Itinerary> itineraries2 = new ArrayList<>();
        ArrayList<Double> stepArray = new ArrayList<>();
        stepArray.add(step.get(0));
        stepArray.add(step.get(1));
        int i = 0;
        for (int j:transportation){
            Itinerary itinerary1Fast = new Itinerary();
            double random = Math.random()*100;
            itinerary1Fast.setExposition(random);
            itinerary1Fast.setHofStart(hourStart);
            Itinerary itinerary1Health = new Itinerary();
            itinerary1Health.setExposition(random);
            itinerary1Health.setHofStart(hourStart);
            //itinerary1.setTime(time);
            if (j!=0){ 
                //System.out.println(i);
                itinerary1Fast.getItinerary(i, start.get(1), start.get(0), step.get(1), step.get(0),0);
                //itinerary1Fast.setExposition(random);
                itinerary1Fast.setHofStart(hourStart);
                itinerary1Fast.setTime(time);
                itinerary1Fast.setHasStep(true);
                itinerary1Fast.setStep(stepArray);
                itineraries1.add(itinerary1Fast);
                itinerary1Health.getItinerary(i, start.get(1), start.get(0), step.get(1), step.get(0),2);
                //itinerary1Health.setExposition(random);
                itinerary1Health.setHofStart(hourStart);
                itinerary1Health.setTime(time);
                itinerary1Health.setHasStep(true);
                itinerary1Health.setStep(stepArray);
                itineraries1.add(itinerary1Health);
            }
            Itinerary itinerary2Fast = new Itinerary();
            double random2 = Math.random()*100;
            //itinerary2Fast.setExposition(random2);
            itinerary2Fast.setHofStart(hourStart);
            Itinerary itinerary2Health = new Itinerary();
            //itinerary2Health.setExposition(random2);
            itinerary2Health.setHofStart(hourStart);
            if (j!=0){ 
            //System.out.println(i);
                itinerary2Fast.getItinerary(i, step.get(1), step.get(0), end.get(1), end.get(0),0);
                //itinerary2Fast.setExposition(random);
                itinerary2Fast.setHofStart(hourStart);
                //itinerary2.setTime(time);
                itineraries2.add(itinerary2Fast);
                itinerary2Health.getItinerary(i, step.get(1), step.get(0), end.get(1), end.get(0), 2);
                //itinerary2Health.setExposition(random);
                itinerary2Health.setHofStart(hourStart);
                //itinerary2.setTime(time);
                itineraries2.add(itinerary2Health);
            }
            i++;
        }
        for(int j=0; j<itineraries1.size(); j++){
            itineraries1.get(j).joinItinerary(itineraries2.get(j));
            response.add(itineraries1.get(j));
        }
    }
    return response;}
        
        

    /**
     * Return tan's map
     * @param map
     * @return 
     */
    @GetMapping("/map")
    public String Map(@RequestParam(value = "map", defaultValue = "tan") String map) {
        try {
            ResourceBundle config = ResourceBundle.getBundle("config");
            //String fileName = config.getString("fileName");
            //String path = config.getString("path");
            //File file = new File(path+fileName);
            String fileBis = config.getString("file");
            //System.out.println(fileBis);
            File file = new File(fileBis);
            String json = readStream(file);
            return json;
        } catch (MissingResourceException | IOException e) {
            Logger.getLogger(MintApplication.class.getName()).log(Level.SEVERE, null, e);
            return "error 404";
        }
    }
    
    /**
     * Read Stream
     * @param file
     * @return
     * @throws IOException 
     */
    private String readStream(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

}

