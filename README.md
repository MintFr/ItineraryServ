# Itinerary Serv Project
This folder is the back-end of the application, and espacially the calculation of itineraries. Indeed, this project is a service deployed on an Apache Tomcat. The service uses the SpringBoot framework.

## Project architecture
The calculation is realized through a PostGreSQL database, with the `pgr_dijkstra` algorithm. The database comes from OpenStreetMap open-data, and the pollution data from our model, put in our database through the MintServ folder.

### 1) Connection to the database
We have a config file that ables to connect to our databases. A template is written in our resources.
We have 4 different databases, depending on the transportation (routing_cars, routing_pedestrians...). You have to create a `credentials.properties` with the same structure as the `credentials.properties.template`.

### 2) Data structure
We use a class called Itinerary, which gathers all the necessary information concerning an itinerary. We then have a method called `getItinerary`, which creates an itinerary (with SQL request) given the transportation, the latitudes and longitudes of start and end point, and a criteria (faster or healthier).

## How to deploy it
### 1) Build your project with maven
You have to build the project with maven, to create a target folder, where a war file is created in.

### 2) Deploy it on Tomcat
To do it, you have to go to the manager home of your Tomcat server. Then, you can either drop your war file (currently named `itineraries-pollution.war`) and deploy it, or enter information manually with the name you want to call it, the URL etc...