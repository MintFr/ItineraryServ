/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mint.itineraries;

/**
 *
 * @author Kheira
 */
public class Step {
    private String addressStep;
    private int lengthStep;
    private int numberOfPoints;

    public Step(String addressStep, int lengthStep,int numberOfPoints) {
        this.addressStep = addressStep;
        this.lengthStep = lengthStep;
        this.numberOfPoints = numberOfPoints;
    }
    public Step(String addressStep, int lengthStep) {
        this.addressStep = addressStep;
        this.lengthStep = lengthStep;
        this.numberOfPoints = 1;
    }

    public Step() {
        this.addressStep = new String();
        this.lengthStep = 0;
        this.numberOfPoints = 0;
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
    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }
    @Override
    public String toString() {
        return "{" + "addressStep: " + addressStep + ", lengthStep: " + lengthStep +",numberOfPoints: "+numberOfPoints+ '}';
    }   
}
