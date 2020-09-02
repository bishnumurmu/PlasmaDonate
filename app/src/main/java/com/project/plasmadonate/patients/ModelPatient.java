package com.project.plasmadonate.patients;

public class ModelPatient {

    private String name;
    //private int sets;
    private String city;
    private String contact;

    public ModelPatient(){
        //for firebase
    }

    public ModelPatient(String name, String city, String contact) {
        this.name = name;
        this.city = city;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }


}

