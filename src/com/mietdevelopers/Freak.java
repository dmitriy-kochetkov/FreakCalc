package com.mietdevelopers.FreakCalc;

/**
 * Created by dmitriy on 08.12.15.
 */
public class Freak {

    private String name;
    private float payment;

    public Freak(String name, float payment) {
        this.name = name;
        this.payment = payment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPayment(float payment) {
        this.payment = payment;
    }

    public String getName() {
        return name;
    }

    public float getPayment() {
        return payment;
    }

    public String toString() {
        return name;
    }
}
