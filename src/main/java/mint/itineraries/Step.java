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

    public Step(String addressStep, int lengthStep) {
        this.addressStep = addressStep;
        this.lengthStep = lengthStep;
    }

    public Step() {
        this.addressStep = new String();
        this.lengthStep = 0;
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
    
    
    
    
}
