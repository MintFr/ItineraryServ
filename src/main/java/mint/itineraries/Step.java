/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mint.itineraries;

/**
 *
 */
public class Step {
    private String addressStep;
    private int lengthStep;
    private int numberOfEdges; // number of edges that compose the step (for real time itinerary purpose)

    public Step(String addressStep, int lengthStep,int numberOfEdges) {
        this.addressStep = addressStep;
        this.lengthStep = lengthStep;
        this.numberOfEdges = numberOfEdges; 
    }
    public Step(String addressStep, int lengthStep) {
        this.addressStep = addressStep;
        this.lengthStep = lengthStep;
        this.numberOfEdges = 1;
    }

    public Step() {
        this.addressStep = new String();
        this.lengthStep = 0;
        this.numberOfEdges = 0;
    }

    public String getAddressStep() {
        return addressStep;
    }

    public void setAddressStep(String addressStep) {
        this.addressStep = addressStep;
    }

    public int getLengthStep() {
        return lengthStep;
    }

    public void setLengthStep(int lengthStep) {
        this.lengthStep = lengthStep;
    }
    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    public void setNumberOfEdges(int numberOfEdges) {
        this.numberOfEdges = numberOfEdges;
    }
    @Override
    public String toString() {
        return "{" + "addressStep: " + addressStep + ", lengthStep: " + lengthStep +",numberOfEdges: "+numberOfEdges+ '}';
    }   
}
