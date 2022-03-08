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

    /**
     * Return itinerary
     *
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
            @RequestParam(value = "transportation") List<Integer> transportation) {
        //car, common transportation, bike, pedestrian
        List<Itinerary> response = new ArrayList<>();

        if (hasStep == false) {
            int i = 0;
            for (int j : transportation) {
                Itinerary itineraryFast = new Itinerary();
                Itinerary itineraryHealth = new Itinerary();
                // TODO: Here we get the data from captation
                double random = Math.random() * 100;
                itineraryFast.setHofStart(hourStart);
                itineraryHealth.setHofStart(hourStart);
                if (j != 0) {
                    itineraryFast.getItinerary(i, start.get(1), start.get(0), end.get(1), end.get(0), 0);
                    itineraryFast.setHofStart(hourStart);
                    itineraryFast.setTime(time);
                    itineraryFast.setHasStep(false);
                    response.add(itineraryFast);
                    itineraryHealth.getItinerary(i, start.get(1), start.get(0), end.get(1), end.get(0), 2);
                    itineraryHealth.setHofStart(hourStart);
                    itineraryHealth.setTime(time);
                    itineraryHealth.setHasStep(false);
                    response.add(itineraryHealth);

                }
                i++;
            }
            return response;
        } else {
            List<Itinerary> itineraries1 = new ArrayList<>();
            List<Itinerary> itineraries2 = new ArrayList<>();
            ArrayList<Double> stepArray = new ArrayList<>();
            stepArray.add(step.get(0));
            stepArray.add(step.get(1));
            int i = 0;
            for (int j : transportation) {
                Itinerary itinerary1Fast = new Itinerary();
                double random = Math.random() * 100;
                itinerary1Fast.setHofStart(hourStart);
                Itinerary itinerary1Health = new Itinerary();
                itinerary1Health.setHofStart(hourStart);
                if (j != 0) {
                    itinerary1Fast.getItinerary(i, start.get(1), start.get(0), step.get(1), step.get(0), 0);
                    itinerary1Fast.setHofStart(hourStart);
                    itinerary1Fast.setTime(time);
                    itinerary1Fast.setHasStep(true);
                    itinerary1Fast.setStep(stepArray);
                    itineraries1.add(itinerary1Fast);
                    itinerary1Health.getItinerary(i, start.get(1), start.get(0), step.get(1), step.get(0), 2);
                    itinerary1Health.setHofStart(hourStart);
                    itinerary1Health.setTime(time);
                    itinerary1Health.setHasStep(true);
                    itinerary1Health.setStep(stepArray);
                    itineraries1.add(itinerary1Health);
                }
                Itinerary itinerary2Fast = new Itinerary();
                double random2 = Math.random() * 100;
                itinerary2Fast.setHofStart(hourStart);
                Itinerary itinerary2Health = new Itinerary();
                itinerary2Health.setHofStart(hourStart);
                if (j != 0) {
                    itinerary2Fast.getItinerary(i, step.get(1), step.get(0), end.get(1), end.get(0), 0);
                    itinerary2Fast.setHofStart(hourStart);
                    itineraries2.add(itinerary2Fast);
                    itinerary2Health.getItinerary(i, step.get(1), step.get(0), end.get(1), end.get(0), 2);
                    itinerary2Health.setHofStart(hourStart);
                    itineraries2.add(itinerary2Health);
                }
                i++;
            }
            for (int j = 0; j < itineraries1.size(); j++) {
                itineraries1.get(j).joinItinerary(itineraries2.get(j));
                response.add(itineraries1.get(j));
            }
        }
        return response;
    }

    /**
     * Return tan's map
     *
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
     *
     * @param file
     * @return
     * @throws IOException
     */
    private String readStream(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try ( BufferedReader r = new BufferedReader(new FileReader(file))) {
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

}
